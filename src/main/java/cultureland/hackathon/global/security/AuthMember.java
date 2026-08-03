package cultureland.hackathon.global.security;

import lombok.Getter;

@Getter
public class AuthMember {

    // JWT 인증이 완료된 사용자의 식별자 (인증 이후에는 비밀번호 필요 없으므로 memberId만 보관)
    // 추후 컨트롤러에서 @AuthenticationPrincipal로 꺼내 사용
    private final Long memberId;

    public AuthMember(Long memberId) {
        this.memberId = memberId;
    }
}
