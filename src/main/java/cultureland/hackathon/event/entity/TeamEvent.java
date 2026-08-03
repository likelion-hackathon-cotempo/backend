package cultureland.hackathon.event.entity;

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
        return TeamEvent.builder()
                .team(team)
                .title(title)
                .startDateTime(startDateTime)
                .endDateTime(endDateTime)
                .build();
    }
}
