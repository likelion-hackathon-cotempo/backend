package cultureland.hackathon.member.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Table(name = "member")
@Builder(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long memberId;

    @Column(nullable = false, unique = true, length = 100)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false, length = 30)
    private String name;

    // KR, VN, US, ...
    @Column(nullable = false, length = 2)
    private String country;

    // Asia/Seoul, Asia/Ho_Chi_Minh, ...
    @Column(nullable = false, length = 50)
    private String timezone;

    public static Member create(String email, String password, String name,
                                String country, String timezone) {
        return Member.builder()
                .email(email)
                .password(password)
                .name(name)
                .country(country)
                .timezone(timezone)
                .build();
    }

    public void reviseInfo(String name, String country, String timezone) {
        if (name != null) {
            this.name = name;
        }

        if (country != null) {
            this.country = country;
        }

        if (timezone != null) {
            this.timezone = timezone;
        }
    }

}
