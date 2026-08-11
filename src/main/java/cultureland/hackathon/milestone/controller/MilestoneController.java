package cultureland.hackathon.milestone.controller;

import cultureland.hackathon.global.api.ApiResponse;
import cultureland.hackathon.global.config.SwaggerConfig;
import cultureland.hackathon.global.security.AuthMember;
import cultureland.hackathon.milestone.dto.MilestoneCreateRequestDto;
import cultureland.hackathon.milestone.dto.MilestoneResponseDto;
import cultureland.hackathon.milestone.dto.MilestoneUpdateRequestDto;
import cultureland.hackathon.milestone.service.MilestoneService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Milestone", description = "마일스톤 조회 · 등록 · 수정 · 삭제 API")
@SecurityRequirement(name = SwaggerConfig.SECURITY_SCHEME_NAME)
@RestController
@RequestMapping("/api/v1/teams/{teamId}/milestones")
@RequiredArgsConstructor
public class MilestoneController {

    private final MilestoneService milestoneService;

    @Operation(
            summary = "마일스톤 목록 조회",
            description = "팀의 마일스톤을 마감 시각 오름차순으로 조회합니다. "
                    + "해당 팀의 멤버만 조회할 수 있습니다."
    )
    @GetMapping
    public ApiResponse<List<MilestoneResponseDto>> getMilestones(
            @Parameter(hidden = true) @AuthenticationPrincipal AuthMember authMember,
            @Parameter(description = "팀 ID", example = "1") @PathVariable Long teamId
    ) {
        List<MilestoneResponseDto> result =
                milestoneService.getMilestones(authMember.getMemberId(), teamId);

        return ApiResponse.onSuccess("Milestone retrieved successfully.", result);
    }

    @Operation(
            summary = "마일스톤 등록",
            description = "마일스톤을 등록합니다. 해당 팀의 멤버면 누구나 등록할 수 있습니다. "
                    + "마감 시각은 UTC 기준 ISO-8601 형식으로 전달합니다. (예: 2026-08-12T04:00:00Z) "
                    + "등록 시 완료 여부는 항상 false로 설정됩니다."
    )
    @PostMapping
    public ApiResponse<MilestoneResponseDto> createMilestone(
            @Parameter(hidden = true) @AuthenticationPrincipal AuthMember authMember,
            @Parameter(description = "팀 ID", example = "1") @PathVariable Long teamId,
            @RequestBody @Valid MilestoneCreateRequestDto requestDto
    ) {
        MilestoneResponseDto result =
                milestoneService.createMilestone(authMember.getMemberId(), teamId, requestDto);

        return ApiResponse.onSuccess("Milestone created successfully.", result);
    }

    @Operation(
            summary = "마일스톤 수정",
            description = "전달된 필드만 수정합니다. 최소 한 개 이상의 필드를 전달해야 합니다. "
                    + "완료 체크 토글도 이 API로 처리합니다. (예: {\"completed\": true})"
    )
    @PatchMapping("/{milestoneId}")
    public ApiResponse<MilestoneResponseDto> updateMilestone(
            @Parameter(hidden = true) @AuthenticationPrincipal AuthMember authMember,
            @Parameter(description = "팀 ID", example = "1") @PathVariable Long teamId,
            @Parameter(description = "마일스톤 ID", example = "1") @PathVariable Long milestoneId,
            @RequestBody @Valid MilestoneUpdateRequestDto requestDto
    ) {
        MilestoneResponseDto result =
                milestoneService.updateMilestone(
                        authMember.getMemberId(), teamId, milestoneId, requestDto
                );

        return ApiResponse.onSuccess("Milestone updated successfully.", result);
    }

    @Operation(
            summary = "마일스톤 삭제",
            description = "마일스톤을 삭제합니다. 해당 팀의 멤버면 누구나 삭제할 수 있습니다."
    )
    @DeleteMapping("/{milestoneId}")
    public ApiResponse<Void> deleteMilestone(
            @Parameter(hidden = true) @AuthenticationPrincipal AuthMember authMember,
            @Parameter(description = "팀 ID", example = "1") @PathVariable Long teamId,
            @Parameter(description = "마일스톤 ID", example = "1") @PathVariable Long milestoneId
    ) {
        milestoneService.deleteMilestone(authMember.getMemberId(), teamId, milestoneId);

        return ApiResponse.onSuccess("Milestone deleted successfully.");
    }

}
