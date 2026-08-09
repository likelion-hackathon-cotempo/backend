package cultureland.hackathon.member.service;

import cultureland.hackathon.global.exception.GeneralException;
import cultureland.hackathon.member.code.MemberErrorCode;
import cultureland.hackathon.member.dto.MemberResponseDto;
import cultureland.hackathon.member.dto.UpdateMemberRequestDto;
import cultureland.hackathon.member.entity.Member;
import cultureland.hackathon.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DateTimeException;
import java.time.ZoneId;

@Service
@Transactional
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;

    @Transactional(readOnly = true)
    public MemberResponseDto getMemberInfo(Long memberId) {
        Member member = findMemberById(memberId);

        return MemberResponseDto.of(
                member.getMemberId(),
                member.getEmail(),
                member.getName(),
                member.getCountry(),
                member.getTimezone()
        );
    }

    public MemberResponseDto updateMemberInfo(Long memberId, UpdateMemberRequestDto requestDto) {
        validateUpdateRequest(requestDto);
        validateTimezone(requestDto.getTimezone());

        Member member = findMemberById(memberId);

        member.updateInfo(
                requestDto.getName(),
                requestDto.getCountry(),
                requestDto.getTimezone()
        );

        return MemberResponseDto.of(
                member.getMemberId(),
                member.getEmail(),
                member.getName(),
                member.getCountry(),
                member.getTimezone()
        );
    }

    private Member findMemberById(Long memberId) {
            return memberRepository.findById(memberId)
                .orElseThrow(() -> new GeneralException(MemberErrorCode.MEMBER_NOT_FOUND));
    }

    private void validateUpdateRequest(UpdateMemberRequestDto requestDto) {
        if (
                requestDto.getName() == null
                && requestDto.getCountry() == null
                && requestDto.getTimezone() == null
        ) {
            throw new GeneralException(MemberErrorCode.MISSING_FIELDS);
        }
    }

    private void validateTimezone(String timezone) {
        if (timezone == null) {
            return;
        }

        try {
            ZoneId.of(timezone);
        } catch (DateTimeException exception) {
            throw new GeneralException(MemberErrorCode.INVALID_TIMEZONE);
        }
    }
}
