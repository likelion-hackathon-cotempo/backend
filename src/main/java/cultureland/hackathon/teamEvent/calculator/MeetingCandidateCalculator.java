package cultureland.hackathon.teamEvent.calculator;

import cultureland.hackathon.calendar.dto.CalendarEventDto;
import cultureland.hackathon.calendar.provider.CalendarMockDataProvider;
import cultureland.hackathon.member.entity.Member;
import cultureland.hackathon.personalSchedule.entity.PersonalSchedule;
import cultureland.hackathon.teamEvent.dto.MeetingCandidateDto;
import cultureland.hackathon.teamEvent.entity.TeamEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class MeetingCandidateCalculator {

    // 회의 후보 30분 간격으로 생성
    private static final int SLOT_INTERVAL_MINUTES = 30;

    // AI에게 전달할 최대 후보 개수
    private static final int MAX_CANDIDATES = 5;

    private final CalendarMockDataProvider calendarMockDataProvider;

    // 팀원별 선호 회의 시간 범위
    private static final LocalTime PREFERRED_HOURS_START = LocalTime.of(8, 0);
    private static final LocalTime PREFERRED_HOURS_END = LocalTime.of(22, 0);

    // 요청 기간 내 회의 후보 계산
    // 팀 일정, 공휴일, 학사 일정과 겹치는 시간은 완전히 제외 후 개인 일정과 겹치는 시간은 가중치를 점수로 변환
    public List<MeetingCandidateDto> calculate(
            Instant searchStart,
            Instant searchEnd,
            int durationMinutes,
            List<Member> members,
            List<TeamEvent> teamEvents,
            List<PersonalSchedule> personalSchedules
    ) {
        // 회원별 개인 일정 미리 묶어서 후보마다 전체 목록 반복 조회하지 않게 함
        Map<Long, List<PersonalSchedule>> schedulesByMemberId =
                personalSchedules.stream()
                        .collect(Collectors.groupingBy(
                                schedule ->
                                        schedule.getMember().getMemberId()
                        ));
        List<ScoredCandidate> scoredCandidates = new ArrayList<>();

        // 요청 시작 시각을 다음 30분 단위로 맞춤
        Instant candidateStart = alignToNextSlot(searchStart);

        while (true) {
            Instant candidateEnd =
                    candidateStart.plus(
                            Duration.ofMinutes(durationMinutes)
                    );

            // 회의 종료 시각이 요청 범위를 벗어나면 후보 생성 종료
            if (candidateEnd.isAfter(searchEnd)) {
                break;
            }

            // 기존 팀 일정과 겹치면 회의 후보에서 제외
            if (overlapsAnyTeamEvent(
                    candidateStart,
                    candidateEnd,
                    teamEvents
            )) {
                candidateStart = moveToNextSlot(candidateStart);
                continue;
            }

            // 팀원 한명이라도 현지 공휴일 또는 학사 일정에 해당하면 제외
            if (overlapsAnyAllDayEvent(
                    candidateStart,
                    candidateEnd,
                    members
            )) {
                candidateStart = moveToNextSlot(candidateStart);
                continue;
            }

            // 개인 일정 충돌 점수와 팀원별 현지 시각 정보 계산
            ScoredCandidate scoredCandidate =
                    scoreCandidate(
                            candidateStart,
                            candidateEnd,
                            members,
                            schedulesByMemberId
                    );

            scoredCandidates.add(scoredCandidate);
            candidateStart = moveToNextSlot(candidateStart);
        }

        // 선호 시간대를 벗어난 팀원 수가 적은 순으로 정렬하고 그 이후 개인 일정 충돌 점수와 시작 시각 기준 정렬
        scoredCandidates.sort(
                Comparator
                        .comparingInt(ScoredCandidate::outsidePreferredHoursMemberCount)
                        .thenComparingInt(ScoredCandidate::totalConflictScore)
                        .thenComparing(ScoredCandidate::startDateTime)
        );

        List<ScoredCandidate> selectedCandidates =
                selectNonOverlappingCandidates(scoredCandidates);

        List<MeetingCandidateDto> result = new ArrayList<>();

        for (int index = 0; index < selectedCandidates.size(); index++) {
            ScoredCandidate candidate = selectedCandidates.get(index);

            result.add(
                    MeetingCandidateDto.of(
                            index + 1,
                            candidate.startDateTime(),
                            candidate.endDateTime(),
                            candidate.totalConflictScore(),
                            candidate.members()
                    )
            );
        }

        return result;
    }

    // 후보 시간과 각 회원의 개인 일정 비교해 충돌 점수 계산
    private ScoredCandidate scoreCandidate(
            Instant candidateStart,
            Instant candidateEnd,
            List<Member> members,
            Map<Long, List<PersonalSchedule>> schedulesByMemberId
    ) {
        int totalConflictScore = 0;
        int outsidePreferredHoursMemberCount = 0;

        List<MeetingCandidateDto.MemberContext> memberContexts = new ArrayList<>();

        for (Member member : members) {
            List<PersonalSchedule> memberSchedules =
                    schedulesByMemberId.getOrDefault(member.getMemberId(), List.of());

            int memberConflictScore = memberSchedules.stream()
                    .filter(schedule ->
                            overlaps(
                                    candidateStart,
                                    candidateEnd,
                                    toInstant(schedule.getStartDateTime()),
                                    toInstant(schedule.getEndDateTime())
                            )
                    )
                    .mapToInt(schedule ->
                            convertWeightToScore(schedule.getWeight())
                    )
                    .sum();

            totalConflictScore += memberConflictScore;

            ZoneId memberZoneId = ZoneId.of(member.getTimezone());

            LocalDateTime localStartDateTime =
                    LocalDateTime.ofInstant(
                            candidateStart,
                            memberZoneId
                    );

            LocalDateTime localEndDateTime =
                    LocalDateTime.ofInstant(
                            candidateEnd,
                            memberZoneId
                    );

            if (isOutsidePreferredHours(
                    localStartDateTime,
                    localEndDateTime
            )) {
                outsidePreferredHoursMemberCount++;
            }

            memberContexts.add(
                    MeetingCandidateDto.MemberContext.of(
                            member.getMemberId(),
                            member.getName(),
                            member.getCountry(),
                            member.getTimezone(),
                            localStartDateTime,
                            localEndDateTime,
                            memberConflictScore > 0,
                            memberConflictScore
                    )
            );
        }

        return new ScoredCandidate(
                candidateStart,
                candidateEnd,
                totalConflictScore,
                outsidePreferredHoursMemberCount,
                List.copyOf(memberContexts)
        );
    }

    // 후보가 기존 팀 일정과 겹치는지 확인
    private boolean overlapsAnyTeamEvent(
            Instant candidateStart,
            Instant candidateEnd,
            List<TeamEvent> teamEvents
    ) {
        return teamEvents.stream()
                .anyMatch(teamEvent ->
                        overlaps(
                                candidateStart,
                                candidateEnd,
                                toInstant(teamEvent.getStartDateTime()),
                                toInstant(teamEvent.getEndDateTime())
                        )
                );
    }

    // 후보 시간이 팀원의 현지 공휴일 또는 학사 일정과 겹치는지 확인
    private boolean overlapsAnyAllDayEvent(
            Instant candidateStart,
            Instant candidateEnd,
            List<Member> members
    ) {
        for (Member member : members) {
            ZoneId memberZoneId = ZoneId.of(member.getTimezone());

            LocalDateTime localStartDateTime =
                    LocalDateTime.ofInstant(candidateStart, memberZoneId);

            LocalDateTime localEndDateTime =
                    LocalDateTime.ofInstant(candidateEnd, memberZoneId);

            LocalDate candidateStartDate = localStartDateTime.toLocalDate();
            LocalDate candidateEndDate =
                    localEndDateTime
                            .minusNanos(1)
                            .toLocalDate();

            List<CalendarEventDto> blockedEvents =
                    calendarMockDataProvider
                            .findByCountryAndPeriod(
                                    member.getCountry(),
                                    candidateStartDate,
                                    candidateEndDate
                            );

            // 팀원 한명에게라도 공휴일 또는 학사 일정이면 후보 전체 제외
            if (!blockedEvents.isEmpty()) {
                return true;
            }
        }
        return false;
    }

    // 두 시간 구간 겹치는지 확인 (종료와 시작이 같은 경우는 겹침으로 보지 않음)
    private boolean overlaps(
            Instant firstStart,
            Instant firstEnd,
            Instant secondStart,
            Instant secondEnd
    ) {
        return firstStart.isBefore(secondEnd)
                && firstEnd.isAfter(secondStart);
    }

    // 개인 일정 가중치 점수로 변환
    private int convertWeightToScore(int weight) {
        return switch (weight) {
            case 1 -> 10;
            case 2 -> 20;
            case 3 -> 40;
            default -> throw new IllegalArgumentException(
                    "Personal schedule weight must be between 1 and 3."
            );
        };
    }

    // UTC 기준 LocalDateTime을 Instant로 변환
    private Instant toInstant(LocalDateTime dateTime) {
        return dateTime.toInstant(ZoneOffset.UTC);
    }

    // 요청 시작 시각을 다음 30분 단위로 올림
    private Instant alignToNextSlot(Instant instant) {
        long slotMillis =
                Duration.ofMinutes(SLOT_INTERVAL_MINUTES).toMillis();

        long instantMillis = instant.toEpochMilli();

        long alignedMillis =
                ((instantMillis +slotMillis - 1) / slotMillis) * slotMillis;

        return Instant.ofEpochMilli(alignedMillis);
    }

    // 현재 후보에서 다음 30분 후보로 이동
    private Instant moveToNextSlot(Instant candidateStart) {
        return candidateStart.plus(
                Duration.ofMinutes(SLOT_INTERVAL_MINUTES)
        );
    }

    // 정렬 전 후보의 계산 결과 잠시 보관하는 내부 객체
    private record ScoredCandidate(
            Instant startDateTime,
            Instant endDateTime,
            int totalConflictScore,
            int outsidePreferredHoursMemberCount,
            List<MeetingCandidateDto.MemberContext> members
    ) {
    }

    // 회의 전체가 같은 날짜의 오전 8시 ~ 오후 10시 안에 포함되는지 확인
    private boolean isOutsidePreferredHours(
            LocalDateTime localStart,
            LocalDateTime localEnd
    ) {
        if (!localStart.toLocalDate().equals(localEnd.toLocalDate())
        ) {
            return true;
        }

        return localStart.toLocalTime()
                .isBefore(PREFERRED_HOURS_START)
                || localEnd.toLocalTime()
                .isAfter(PREFERRED_HOURS_END);
    }

    // 후보 정렬 후 겹치지 않는 후보만 남김
    private List<ScoredCandidate> selectNonOverlappingCandidates(
            List<ScoredCandidate> sortedCandidates
    ) {
        List<ScoredCandidate> selectedCandidates = new ArrayList<>();

        for (ScoredCandidate candidate : sortedCandidates) {
            boolean overlapsSelectedCandidate = selectedCandidates.stream()
                    .anyMatch(selected -> overlaps(
                            candidate.startDateTime(),
                            candidate.endDateTime(),
                            selected.startDateTime(),
                            selected.endDateTime()
                    ));

            if (overlapsSelectedCandidate) {
                continue;
            }

            selectedCandidates.add(candidate);

            if (selectedCandidates.size() == MAX_CANDIDATES) {
                break;
            }
        }

        return selectedCandidates;
    }
}
