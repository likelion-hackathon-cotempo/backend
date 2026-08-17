package cultureland.hackathon.calendar.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.Instant;
import java.time.LocalDate;

@Getter
@Builder(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class CalendarEventDto {

    private final Long id;
    private final String title;
    private final Type type;

    // 공휴일, 학사 일정 적용 국가
    private final String country;

    // 공휴일, 학사 일정처럼 날짜 전체를 차지하는 일정
    private final LocalDate startDate;
    private final LocalDate endDate;

    // 개인 일정, 팀 일정처럼 시간이 있는 일정
    private final Instant startDateTime;
    private final Instant endDateTime;

    // 마일스톤처럼 마감 날짜 및 시간이 있는 일정
    private final Instant dueDateTime;

    // 현재 로그인 사용자가 수정할 수 있는 일정인지 표시
    private final boolean editable;

    // 개인 일정 소유 회원 정보
    private final Long memberId;
    private final String memberName;

    // 팀 일정 및 마일스톤의 소속 팀 정보
    private final Long teamId;
    private final String teamName;

    // 마일스톤 완료 여부
    private final Boolean completed;

    public enum Type {
        PERSONAL_SCHEDULE,
        TEAM_EVENT,
        MILESTONE,
        HOLIDAY,
        ACADEMIC
    }

    // 개인 일정
    public static CalendarEventDto fromPersonalSchedule(
            Long id,
            Long memberId,
            String memberName,
            String title,
            Instant startDateTime,
            Instant endDateTime
    ) {
        return CalendarEventDto.builder()
                .id(id)
                .memberId(memberId)
                .memberName(memberName)
                .title(title)
                .type(Type.PERSONAL_SCHEDULE)
                .startDateTime(startDateTime)
                .endDateTime(endDateTime)
                .editable(false)
                .build();
    }

    // 팀 일정
    public static CalendarEventDto fromTeamEvent(
            Long id,
            Long teamId,
            String teamName,
            String title,
            Instant startDateTime,
            Instant endDateTime
    ) {
        return CalendarEventDto.builder()
                .id(id)
                .teamId(teamId)
                .teamName(teamName)
                .title(title)
                .type(Type.TEAM_EVENT)
                .startDateTime(startDateTime)
                .endDateTime(endDateTime)
                .editable(true)
                .build();
    }

    // 마일스톤
    public static CalendarEventDto fromMilestone(
            Long id,
            Long teamId,
            String teamName,
            String title,
            Instant dueDateTime,
            boolean completed
    ) {
        return CalendarEventDto.builder()
                .id(id)
                .teamId(teamId)
                .teamName(teamName)
                .title(title)
                .type(Type.MILESTONE)
                .dueDateTime(dueDateTime)
                .completed(completed)
                .editable(true)
                .build();
    }

    // 공휴일, 학사 일정
    public static CalendarEventDto ofAllDayEvent(
            String title,
            String country,
            LocalDate startDate,
            LocalDate endDate,
            Type type
    ) {
        if (type != Type.HOLIDAY && type != Type.ACADEMIC) {
            throw new IllegalArgumentException(
                    "All-day event type must be HOLIDAY or ACADEMIC."
            );
        }
        if (startDate.isAfter(endDate)) {
            throw new IllegalArgumentException(
                    "All-day event start date must not be after end date."
            );
        }

        return CalendarEventDto.builder()
                .title(title)
                .country(country)
                .type(type)
                .startDate(startDate)
                .endDate(endDate)
                .editable(false)
                .build();
    }
}
