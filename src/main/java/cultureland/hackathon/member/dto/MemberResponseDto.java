package cultureland.hackathon.member.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class MemberResponseDto {
    private Long memberId;
    private String email;
    private String name;
    private String country;
    private String timezone;

    public static MemberResponseDto of(Long memberId, String email, String name, String country, String timezone) {
        return MemberResponseDto.builder()
                .memberId(memberId)
                .email(email)
                .name(name)
                .country(country)
                .timezone(timezone)
                .build();
    }

}
