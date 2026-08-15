package cultureland.hackathon.teamEvent.entity;

import cultureland.hackathon.teamEvent.code.TeamEventErrorCode;
import cultureland.hackathon.global.exception.GeneralException;
import cultureland.hackathon.team.entity.Team;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Table(name = "team_event")
@Builder(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class TeamEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long teamEventId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "team_id", nullable = false)
    private Team team;

    @Column(nullable = false, length = 100)
    private String title;

    // UTC 기준
    @Column(name = "start_date_time", nullable = false)
    private LocalDateTime startDateTime;

    // UTC 기준
    @Column(name = "end_date_time", nullable = false)
    private LocalDateTime endDateTime;

    public static TeamEvent create(Team team, String title,
                                   LocalDateTime startDateTime,
                                   LocalDateTime endDateTime) {
        validate(startDateTime, endDateTime);
        return TeamEvent.builder()
                .team(team)
                .title(title)
                .startDateTime(startDateTime)
                .endDateTime(endDateTime)
                .build();
    }

    private static void validate(LocalDateTime start, LocalDateTime end) {
        if (!start.isBefore(end)) {
            throw new GeneralException(TeamEventErrorCode.INVALID_DATE_RANGE);
        }
    }

    public void update(String title, LocalDateTime startDateTime, LocalDateTime endDateTime) {
        // 부분 수정이므로 전달되지 않은 값은 기존 값을 유지한 뒤 최종 값으로 검증
        LocalDateTime newStartDateTime =
                startDateTime != null ? startDateTime : this.startDateTime;
        LocalDateTime newEndDateTime =
                endDateTime != null ? endDateTime : this.endDateTime;

        validate(newStartDateTime, newEndDateTime);

        if (title != null) {
            this.title = title;
        }

        this.startDateTime = newStartDateTime;
        this.endDateTime = newEndDateTime;
    }

}
