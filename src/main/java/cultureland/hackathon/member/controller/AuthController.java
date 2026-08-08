package cultureland.hackathon.member.controller;

import cultureland.hackathon.global.api.ApiResponse;
import cultureland.hackathon.member.dto.LoginRequestDto;
import cultureland.hackathon.member.dto.SignupRequestDto;
import cultureland.hackathon.member.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Tag(
        name = "인증",
        description = "회원가입, 로그인, 로그아웃 API"
)
public class AuthController {
    private static final String ACCESS_TOKEN_COOKIE = "accessToken";
    private static final long COOKIE_MAX_AGE = 3600L;

    private final AuthService authService;

    // 회원가입
    @Operation(
            summary = "회원가입",
            description = "회원 정보를 입력받아 새로운 계정을 생성합니다."
    )
    @PostMapping("/signup")
    public ApiResponse<Void> signup(
            @RequestBody @Valid SignupRequestDto requestDto
    ) {
        authService.signup(requestDto);
        return ApiResponse.onSuccess("Signup completed successfully.");
    }

    // 로그인
    @Operation(
            summary = "로그인",
            description = "이메일과 비밀번호를 검증하고 Access Token을 HttpOnly 쿠키로 발급합니다."
    )
    @PostMapping("/login")
    public ApiResponse<Void> login(
            @RequestBody @Valid LoginRequestDto requestDto,
            HttpServletResponse response
    ) {
        String accessToken = authService.login(requestDto);

        ResponseCookie cookie = ResponseCookie
                .from(ACCESS_TOKEN_COOKIE, accessToken)
                .httpOnly(true)
                .secure(false)
                .sameSite("Lax")
                .path("/")
                .maxAge(COOKIE_MAX_AGE)
                .build();

        response.addHeader(
                HttpHeaders.SET_COOKIE,
                cookie.toString()
        );

        return ApiResponse.onSuccess("Login completed successfully.");
    }

    // 로그아웃
    @Operation(
            summary = "로그아웃",
            description = "Access Token 쿠키를 만료시켜 로그아웃합니다."
    )
    @PostMapping("/logout")
    public ApiResponse<Void> logout(
            HttpServletResponse response
    ) {
        ResponseCookie cookie = ResponseCookie
                .from(ACCESS_TOKEN_COOKIE, "")
                .httpOnly(true)
                // 로컬 http 개발용, https로 배포 시 수정 필요
                .secure(false)
                .sameSite("Lax")
                .path("/")
                .maxAge(0)
                .build();

        response.addHeader(
                HttpHeaders.SET_COOKIE,
                cookie.toString()
        );

        return ApiResponse.onSuccess("Logout completed successfully.");
    }
}
