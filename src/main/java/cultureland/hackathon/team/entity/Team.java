package cultureland.hackathon.team.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Table(name = "team")
@Builder(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Team {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long teamId;

    @Column(nullable = false, length = 50)
    private String name;

    @Column(nullable = false, unique = true, length = 10)
    private String inviteCode;

    public static Team create(String name, String inviteCode) {
        return Team.builder()
                .name(name)
                .inviteCode(inviteCode)
                .build();
    }

}
