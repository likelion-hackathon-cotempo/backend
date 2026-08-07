package cultureland.hackathon.team.entity;

import cultureland.hackathon.member.entity.Member;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Table(
        name = "team_member",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_team_member",
                columnNames = {"team_id", "member_id"}
        )
)
@Builder(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class TeamMember {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long teamMemberId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TeamRole role;

    @Column(nullable = false, length = 30)
    private String positon;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "team_id", nullable = false)
    private Team team;

    public static TeamMember create(TeamRole role, String positon, Member member, Team team) {
        return TeamMember.builder()
                .role(role)
                .positon(positon)
                .member(member)
                .team(team)
                .build();
    }

}
