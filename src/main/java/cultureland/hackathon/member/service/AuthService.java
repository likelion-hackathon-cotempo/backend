package cultureland.hackathon.member.service;

import cultureland.hackathon.global.exception.GeneralException;
import cultureland.hackathon.global.security.JwtTokenProvider;
import cultureland.hackathon.member.code.MemberErrorCode;
import cultureland.hackathon.member.dto.LoginRequestDto;
import cultureland.hackathon.member.dto.SignupRequestDto;
import cultureland.hackathon.member.entity.Member;
import cultureland.hackathon.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class AuthService {
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    public void signup(SignupRequestDto requestDto) {
        if (memberRepository.existsByEmail(requestDto.getEmail())) {
            throw new GeneralException(MemberErrorCode.DUPLICATE_EMAIL);
        }
        String encodedPassword = passwordEncoder.encode(requestDto.getPassword());

        Member member = Member.create(
                requestDto.getEmail(),
                encodedPassword,
                requestDto.getName(),
                requestDto.getCountry(),
                requestDto.getTimezone()
        );

        memberRepository.save(member);
    }

    @Transactional(readOnly = true)
    public String login(LoginRequestDto requestDto) {
        Member member = memberRepository.findByEmail(requestDto.getEmail())
                .orElseThrow(() -> new GeneralException(MemberErrorCode.INVALID_CREDENTIALS));

        if(!passwordEncoder.matches(requestDto.getPassword(), member.getPassword())){
            throw new GeneralException(MemberErrorCode.INVALID_CREDENTIALS);
        }

        return jwtTokenProvider.createAccessToken(member.getMemberId());
    }
}
