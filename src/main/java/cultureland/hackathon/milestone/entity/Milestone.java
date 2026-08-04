package cultureland.hackathon.milestone.entity;

import cultureland.hackathon.team.entity.Team;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Getter
@Table(name = "milestone")
@Builder(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Milestone {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long milestoneId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "team_id", nullable = false)
    private Team team;

    @Column(nullable = false, length = 100)
    private String title;

    @Column(name = "due_date_time", nullable = false)
    private LocalDate dueDateTime;

    public static Milestone create(Team team, String title, LocalDate dueDateTime) {
        return Milestone.builder()
                .team(team)
                .title(title)
                .dueDateTime(dueDateTime)
                .build();
    }
}
