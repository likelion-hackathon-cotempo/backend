package cultureland.hackathon.schedule.entity;

import cultureland.hackathon.member.entity.Member;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Table(name = "schedule")
@Builder(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Schedule {

    // 추후 수정 가능
    private static final int MAX_WEIGHT = 5;
    private static final int MIN_WEIGHT = 1;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long scheduleId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Column(nullable = false, length = 100)
    private String title;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private ScheduleType type;

    // UTC 기준
    @Column(name = "start_date_time", nullable = false)
    private LocalDateTime startDateTime;

    // UTC 기준
    @Column(name = "end_date_time", nullable = false)
    private LocalDateTime endDateTime;

    @Column(nullable = false)
    private int weight;

    // 온보딩 시 국가 기준으로 자동 생성 — 가중치는 항상 최대값
    public static Schedule createNationalHoliday(Member member, String title,
                                                 LocalDateTime startDateTime,
                                                 LocalDateTime endDateTime) {
        return Schedule.builder()
                .member(member)
                .title(title)
                .type(ScheduleType.NATIONAL_HOLIDAY)
                .startDateTime(startDateTime)
                .endDateTime(endDateTime)
                .weight(MAX_WEIGHT)
                .build();
    }

    // 체감 휴무 기간 — 공휴일보다 낮고 개인 일정보다 높은 가중치
    public static Schedule createCulturalBreak(Member member, String title,
                                               LocalDateTime startDateTime,
                                               LocalDateTime endDateTime,
                                               int weight) {
        return Schedule.builder()
                .member(member)
                .title(title)
                .type(ScheduleType.CULTURAL_BREAK)
                .startDateTime(startDateTime)
                .endDateTime(endDateTime)
                .weight(weight)
                .build();
    }

    // 사용자가 직접 등록하는 개인 일정 (시험기간 등)
    public static Schedule createPersonal(Member member, String title,
                                          LocalDateTime startDateTime,
                                          LocalDateTime endDateTime,
                                          int weight) {
        return Schedule.builder()
                .member(member)
                .title(title)
                .type(ScheduleType.PERSONAL)
                .startDateTime(startDateTime)
                .endDateTime(endDateTime)
                .weight(weight)
                .build();
    }

}
