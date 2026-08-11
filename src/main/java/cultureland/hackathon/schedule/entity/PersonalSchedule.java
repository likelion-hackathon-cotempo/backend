package cultureland.hackathon.schedule.entity;

import cultureland.hackathon.global.exception.GeneralException;
import cultureland.hackathon.member.entity.Member;
import cultureland.hackathon.schedule.code.PersonalScheduleErrorCode;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Table(name = "personal_schedule")
@Builder(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class PersonalSchedule {

    // 추후 수정 가능
    private static final int MAX_WEIGHT = 3;
    private static final int MIN_WEIGHT = 1;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long personalScheduleId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Column(nullable = false, length = 100)
    private String title;

    // UTC 기준
    @Column(name = "start_date_time", nullable = false)
    private LocalDateTime startDateTime;

    // UTC 기준
    @Column(name = "end_date_time", nullable = false)
    private LocalDateTime endDateTime;

    @Column(nullable = false)
    private int weight;

    // 사용자가 직접 등록하는 개인 일정
    public static PersonalSchedule create(Member member, String title,
                                                  LocalDateTime startDateTime,
                                                  LocalDateTime endDateTime,
                                                  int weight) {
        validate(startDateTime, endDateTime, weight);
        return PersonalSchedule.builder()
                .member(member)
                .title(title)
                .startDateTime(startDateTime)
                .endDateTime(endDateTime)
                .weight(weight)
                .build();
    }

    private static void validate(LocalDateTime start, LocalDateTime end, int weight) {
        if (!start.isBefore(end)) {
            throw new GeneralException(PersonalScheduleErrorCode.INVALID_DATE_RANGE);
        }
        if (weight < MIN_WEIGHT || weight > MAX_WEIGHT) {
            throw new GeneralException(PersonalScheduleErrorCode.INVALID_WEIGHT);
        }
    }

    public void update(String title, LocalDateTime startDateTime, LocalDateTime endDateTime, Integer weight) {

        LocalDateTime newStartDateTime =
                startDateTime != null ? startDateTime : this.startDateTime;
        LocalDateTime newEndDateTime =
                endDateTime != null ? endDateTime : this.endDateTime;
        int newWeight = weight != null ? weight : this.weight;

        validate(newStartDateTime, newEndDateTime, newWeight);

        if (title != null) {
            this.title = title;
        }

        this.startDateTime = newStartDateTime;
        this.endDateTime = newEndDateTime;
        this.weight = newWeight;
    }

}
