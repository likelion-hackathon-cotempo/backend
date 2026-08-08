package cultureland.hackathon.member.controller;

import cultureland.hackathon.global.api.ApiResponse;
import cultureland.hackathon.global.security.AuthMember;
import cultureland.hackathon.member.dto.MemberResponseDto;
import cultureland.hackathon.member.dto.UpdateMemberRequestDto;
import cultureland.hackathon.member.service.MemberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/members/me")
@RequiredArgsConstructor
public class MemberController {
    private final MemberService memberService;

    @GetMapping
    public ApiResponse<MemberResponseDto> getMemberInfo(
            @AuthenticationPrincipal AuthMember authMember
    ) {
        Long memberId = authMember.getMemberId();
        MemberResponseDto response = memberService.getMemberInfo(memberId);
        return ApiResponse.onSuccess("Member information retrieved successfully.", response);
    }

    @PatchMapping
    public ApiResponse<MemberResponseDto> updateMemberInfo(
            @AuthenticationPrincipal AuthMember authMember,
            @RequestBody @Valid UpdateMemberRequestDto requestDto
    ) {
        Long memberId = authMember.getMemberId();
        MemberResponseDto response = memberService.updateMemberInfo(memberId, requestDto);
        return ApiResponse.onSuccess("Member information updated successfully.", response);
    }
}
