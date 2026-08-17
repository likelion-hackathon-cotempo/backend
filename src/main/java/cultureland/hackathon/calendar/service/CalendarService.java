package cultureland.hackathon.calendar.service;

import cultureland.hackathon.calendar.code.CalendarErrorCode;
import cultureland.hackathon.calendar.dto.CalendarEventDto;
import cultureland.hackathon.calendar.provider.CalendarMockDataProvider;
import cultureland.hackathon.global.exception.GeneralException;
import cultureland.hackathon.member.code.MemberErrorCode;
import cultureland.hackathon.member.entity.Member;
import cultureland.hackathon.member.repository.MemberRepository;
import cultureland.hackathon.milestone.entity.Milestone;
import cultureland.hackathon.milestone.repository.MilestoneRepository;
import cultureland.hackathon.personalSchedule.entity.PersonalSchedule;
import cultureland.hackathon.personalSchedule.repository.PersonalScheduleRepository;
import cultureland.hackathon.team.entity.Team;
import cultureland.hackathon.team.entity.TeamMember;
import cultureland.hackathon.team.repository.TeamMemberRepository;
import cultureland.hackathon.team.service.TeamService;
import cultureland.hackathon.teamEvent.entity.TeamEvent;
import cultureland.hackathon.teamEvent.repository.TeamEventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.*;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CalendarService {

    // 캘린더 조회 범위
    private static final int SUPPORTED_YEAR = 2026;
    private static final int START_MONTH = 8;
    private static final int END_MONTH = 10;

    private final MemberRepository memberRepository;
    private final TeamMemberRepository teamMemberRepository;
    private final PersonalScheduleRepository personalScheduleRepository;
    private final TeamEventRepository teamEventRepository;
    private final MilestoneRepository milestoneRepository;
    private final CalendarMockDataProvider calendarMockDataProvider;
    private final TeamService teamService;

    // 개인 통합 캘린더 조회 : 개인 일정, 참여한 팀의 팀 일정, 설정 국가의 공휴일 및 학사 일정
    public List<CalendarEventDto> getPersonalCalendar(
            Long memberId,
            int year,
            int month
    ) {
        Member member = getMember(memberId);

        CalendarPeriod period =
                createCalendarPeriod(
                        year, month, member.getTimezone()
                );

        List<CalendarEventDto> events = new ArrayList<>();

        // 로그인 회원 본인의 개인 일정 조회
        List<PersonalSchedule> personalSchedules =
                personalScheduleRepository.findOverlapping(
                        List.of(member),
                        period.utcStart(),
                        period.utcEnd()
                );

        events.addAll(
                personalSchedules.stream()
                        .map(this::toPersonalScheduleEvent)
                        .toList()
        );

        // 로그인 회원이 참여 중인 팀 목록 조회
        List<Team> teams =
                teamMemberRepository
                        .findAllByMemberWithTeam(member)
                        .stream()
                        .map(TeamMember::getTeam)
                        .toList();

        // 참여 중인 팀 없는 경우 빈 리스트 사용
        List<TeamEvent> teamEvents =
                teams.isEmpty()
                ? List.of() : teamEventRepository
                        .findOverlappingByTeams(
                                teams, period.utcStart(), period.utcEnd()
                        );

        events.addAll(
                teamEvents.stream()
                        .map(this::toTeamEvent)
                        .toList()
        );

        // 로그인 회원 국가의 공휴일 및 학사 일정 조회
        events.addAll(
                calendarMockDataProvider
                        .findByCountryAndPeriod(
                                member.getCountry(),
                                period.startDate(),
                                period.endDate()
                        )
        );

        return sortEvents(
                events, period.viewerZoneId()
        );
    }

    // 팀 통합 캘린더 조회 : 팀원 전체의 개인 일정, 팀 일정, 마일스톤, 팀원 국가별 공휴일 및 학사 일정
    public List<CalendarEventDto> getTeamCalendar(
            Long memberId,
            Long teamId,
            int year,
            int month
    ) {
        // 로그인 회원의 팀 소속 여부 검증
        Team team = teamService.getJoinedTeam(memberId, teamId);

        Member viewer = getMember(memberId);

        CalendarPeriod period =
                createCalendarPeriod(year, month, viewer.getTimezone());

        // 팀원 엔티티 전체 조회
        List<Member> members =
                teamMemberRepository
                        .findAllByTeamWithMember(team)
                        .stream()
                        .map(TeamMember::getMember)
                        .toList();

        List<CalendarEventDto> events = new ArrayList<>();

        // 팀원들의 개인 일정 조회
        List<PersonalSchedule> personalSchedules =
                personalScheduleRepository.findOverlapping(
                        members,
                        period.utcStart(),
                        period.utcEnd()
                );

        events.addAll(
                personalSchedules.stream()
                        .map(this::toPersonalScheduleEvent)
                        .toList()
        );

        // 해당 팀의 팀 일정 조회
        List<TeamEvent> teamEvents =
                teamEventRepository.findOverlapping(
                        team,
                        period.utcStart(),
                        period.utcEnd()
                );

        events.addAll(
                teamEvents.stream()
                        .map(this::toTeamEvent)
                        .toList()
        );

        // 조회 기간 안에 마감되는 마일스톤 조회
        List<Milestone> milestones =
                milestoneRepository.findByTeamAndPeriod(
                        team,
                        period.utcStart(),
                        period.utcEnd()
                );

        events.addAll(
                milestones.stream()
                        .map(this::toMilestoneEvent)
                        .toList()
        );

        // 국가 코드 Set으로 구성해 중복 막음
        Set<String> countries =
                members.stream()
                        .map(Member::getCountry)
                        .collect(Collectors.toSet());

        for (String country : countries) {
            events.addAll(
                    calendarMockDataProvider
                            .findByCountryAndPeriod(
                                    country,
                                    period.startDate(),
                                    period.endDate()
                            )
            );
        }

        return sortEvents(events, period.viewerZoneId());
    }

    // 개인 일정을 통합 캘린더 이벤트로 변환
    private CalendarEventDto toPersonalScheduleEvent(
            PersonalSchedule personalSchedule
    ) {
        Member owner = personalSchedule.getMember();

        return CalendarEventDto.fromPersonalSchedule(
                personalSchedule.getPersonalScheduleId(),
                owner.getMemberId(),
                owner.getName(),
                personalSchedule.getTitle(),
                toInstant(personalSchedule.getStartDateTime()),
                toInstant(personalSchedule.getEndDateTime())
        );
    }

    // 팀 일정을 통합 캘린더 이벤트로 변환
    private CalendarEventDto toTeamEvent(TeamEvent teamEvent) {
        Team team = teamEvent.getTeam();

        return CalendarEventDto.fromTeamEvent(
                teamEvent.getTeamEventId(),
                team.getTeamId(),
                team.getName(),
                teamEvent.getTitle(),
                toInstant(teamEvent.getStartDateTime()),
                toInstant(teamEvent.getEndDateTime())
        );
    }

    // 마일스톤을 통합 캘린더 이벤트로 변환
    private CalendarEventDto toMilestoneEvent(Milestone milestone) {
        Team team = milestone.getTeam();

        return CalendarEventDto.fromMilestone(
                milestone.getMilestoneId(),
                team.getTeamId(),
                team.getName(),
                milestone.getTitle(),
                toInstant(milestone.getDueDateTime()),
                milestone.isCompleted()
        );
    }

    // 로그인 회원 조회
    private Member getMember(Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() ->
                        new GeneralException(
                                MemberErrorCode.MEMBER_NOT_FOUND
                        )
                );
    }

    // DB에 UTC 기준으로 저장된 LocalDateTime을 API 응답용 Instant로 변환
    private Instant toInstant(
            LocalDateTime dateTime
    ) {
        return dateTime.toInstant(
                ZoneOffset.UTC
        );
    }

    // 캘린더 조회 범위 생성
    private CalendarPeriod createCalendarPeriod(
            int year, int month, String timezone
    ) {
        validateCalendarPeriod(year, month);

        YearMonth yearMonth = YearMonth.of(year, month);

        ZoneId viewerZoneId = ZoneId.of(timezone);

        LocalDate startDate = yearMonth.atDay(1);

        LocalDate endDate = yearMonth.atEndOfMonth();

        Instant startInstant = startDate.atStartOfDay(viewerZoneId).toInstant();
        Instant endInstant = yearMonth.plusMonths(1)
                .atDay(1)
                .atStartOfDay(viewerZoneId)
                .toInstant();

        return new CalendarPeriod(
                startDate,
                endDate,
                LocalDateTime.ofInstant(startInstant, ZoneOffset.UTC),
                LocalDateTime.ofInstant(endInstant, ZoneOffset.UTC),
                viewerZoneId
        );
    }

    // 캘린더 기간 검증
    private void validateCalendarPeriod(
            int year, int month
    ) {
        if (
                year != SUPPORTED_YEAR
                || month < START_MONTH || month > END_MONTH
        ) {
            throw new GeneralException(
                    CalendarErrorCode.UNSUPPORTED_CALENDAR_PERIOD
            );
        }
    }

    // 하나의 기준 시각으로 변환 후 정렬
    private List<CalendarEventDto> sortEvents(
            List<CalendarEventDto> events,
            ZoneId viewerZoneId
    ) {
        return events.stream()
                .sorted(
                        Comparator.comparing(event ->
                                getSortTime(
                                        event, viewerZoneId
                                )
                        )
                )
                .toList();
    }

    private Instant getSortTime(
            CalendarEventDto event, ZoneId viewerZoneId
    ) {
        // 개인 일정 및 팀 일정
        if (event.getStartDateTime() != null) {
            return event.getStartDateTime();
        }

        // 마일스톤
        if (event.getDueDateTime() != null) {
            return event.getDueDateTime();
        }

        // 공휴일 및 학사 일정
        return event.getStartDate()
                .atStartOfDay(viewerZoneId)
                .toInstant();
    }

    // 캘린더 조회에 필요한 기간 값 함께 보관하는 내부 객체
    private record CalendarPeriod(
            LocalDate startDate,
            LocalDate endDate,
            LocalDateTime utcStart,
            LocalDateTime utcEnd,
            ZoneId viewerZoneId
    ) {
    }
}
