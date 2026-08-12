package cultureland.hackathon.milestone.entity;

import cultureland.hackathon.team.entity.Team;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

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
    private LocalDateTime dueDateTime;

    @Column(nullable = false)
    private boolean completed;

    public static Milestone create(Team team, String title, LocalDateTime dueDateTime) {
        return Milestone.builder()
                .team(team)
                .title(title)
                .dueDateTime(dueDateTime)
                .completed(false)
                .build();
    }

    public void update(String title, LocalDateTime dueDateTime, Boolean completed) {
        if (title != null) {
            this.title = title;
        }

        if (dueDateTime != null) {
            this.dueDateTime = dueDateTime;
        }

        if (completed != null) {
            this.completed = completed;
        }
    }

}
