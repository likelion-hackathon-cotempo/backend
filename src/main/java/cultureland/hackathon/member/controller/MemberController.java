package cultureland.hackathon.member.controller;

import cultureland.hackathon.global.api.ApiResponse;
import cultureland.hackathon.global.config.SwaggerConfig;
import cultureland.hackathon.global.security.AuthMember;
import cultureland.hackathon.member.dto.MemberResponseDto;
import cultureland.hackathon.member.dto.UpdateMemberRequestDto;
import cultureland.hackathon.member.service.MemberService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/members/me")
@RequiredArgsConstructor
@SecurityRequirement(name = SwaggerConfig.SECURITY_SCHEME_NAME)
@Tag(
        name = "회원",
        description = "회원 정보 조회 및 수정 API"
)
public class MemberController {
    private final MemberService memberService;

    @Operation(
            summary = "내 정보 조회",
            description = "현재 로그인한 회원의 정보를 조회합니다."
    )
    @GetMapping
    public ApiResponse<MemberResponseDto> getMemberInfo(
            @AuthenticationPrincipal AuthMember authMember
    ) {
        Long memberId = authMember.getMemberId();
        MemberResponseDto response = memberService.getMemberInfo(memberId);
        return ApiResponse.onSuccess("Member information retrieved successfully.", response);
    }

    @Operation(
            summary = "내 정보 수정",
            description = "현재 로그인한 회원의 이름, 국가 및 시간대를 부분 수정합니다."
    )
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
