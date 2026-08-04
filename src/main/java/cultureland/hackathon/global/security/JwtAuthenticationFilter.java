package cultureland.hackathon.global.security;

import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
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
import java.util.Collections;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";

    private final JwtTokenProvider jwtTokenProvider;
    private final AuthenticationEntryPoint authenticationEntryPoint;

    // 모든 HTTP 요청마다 실행되는 필터
    // Authorization Header에 담긴 JWT 검증 -> 인증된 사용자 정보 SecurityContext에 저장
    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        // 1. 요청 Header에서 Authorization 값 꺼냄
        String authorizationHeader = request.getHeader(AUTHORIZATION_HEADER);

        // 2. Authorization Header 없거나 Bearer 형식 아니면 인증 처리 x -> 다음 필터로 넘김
        if (authorizationHeader == null || !authorizationHeader.startsWith(BEARER_PREFIX)) {
            filterChain.doFilter(request, response);
            return;
        }

        // 3. Bearer 접두사 제거 후 순수 JWT 문자열만 추출
        String token = authorizationHeader.substring(BEARER_PREFIX.length());

        // 4. 토큰 검증
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
}
