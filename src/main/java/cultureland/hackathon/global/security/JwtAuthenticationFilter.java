package cultureland.hackathon.global.security;

import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final String ACCESS_TOKEN_COOKIE =
            "accessToken";

    private final JwtTokenProvider jwtTokenProvider;
    private final AuthenticationEntryPoint authenticationEntryPoint;

    // 모든 HTTP 요청마다 실행되는 필터
    // accessToken 쿠키의 JWT를 검증하고 인증 정보를 SecurityContext에 저장
    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        String token = resolveToken(request);

        if (token == null || token.isBlank()) {
            filterChain.doFilter(request, response);
            return;
        }

        if (!jwtTokenProvider.validateToken(token)) {
            handleAuthenticationFailure(
                    request,
                    response,
                    "유효하지 않은 토큰입니다."
            );
            return;
        }

        Long memberId;

        try {
            memberId = jwtTokenProvider.getMemberId(token);
        } catch (JwtException | IllegalArgumentException exception) {
            handleAuthenticationFailure(
                    request,
                    response,
                    "토큰의 회원 정보가 올바르지 않습니다."
            );
            return;
        }
        AuthMember authMember = new AuthMember(memberId);
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(
                        authMember,
                        null,
                        Collections.emptyList()
                );
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        filterChain.doFilter(request, response);
    }

    private void handleAuthenticationFailure(
            HttpServletRequest request,
            HttpServletResponse response,
            String message
    ) throws IOException, ServletException {
        SecurityContextHolder.clearContext();

        authenticationEntryPoint.commence(
                request,
                response,
                new BadCredentialsException(message)
        );
    }

    private String resolveToken(
            HttpServletRequest request
    ) {
        Cookie[] cookies = request.getCookies();

        if (cookies == null) {
            return null;
        }

        return Arrays.stream(cookies)
                .filter(cookie ->
                        ACCESS_TOKEN_COOKIE.equals(
                                cookie.getName()
                        )
                )
                .map(Cookie::getValue)
                .findFirst()
                .orElse(null);
    }

    @Override
    protected boolean shouldNotFilter(
            HttpServletRequest request
    ) {
        String path = request.getServletPath();

        return path.equals("/api/members/login")
                || path.equals("/api/members/logout")
                || path.equals("/actuator/health");
    }
}
