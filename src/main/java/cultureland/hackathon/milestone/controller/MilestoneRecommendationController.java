package cultureland.hackathon.milestone.controller;

import cultureland.hackathon.global.api.ApiResponse;
import cultureland.hackathon.global.config.SwaggerConfig;
import cultureland.hackathon.global.security.AuthMember;
import cultureland.hackathon.milestone.dto.MilestoneBulkCreateRequestDto;
import cultureland.hackathon.milestone.dto.MilestoneRecommendationRequestDto;
import cultureland.hackathon.milestone.dto.MilestoneRecommendationResponseDto;
import cultureland.hackathon.milestone.dto.MilestoneResponseDto;
import cultureland.hackathon.milestone.service.MilestoneRecommendationService;
import cultureland.hackathon.milestone.service.MilestoneService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "마일스톤 추천", description = "AI 기반 마일스톤 추천 및 추천 결과 전체 등록 API")
@SecurityRequirement(name = SwaggerConfig.SECURITY_SCHEME_NAME)
@RestController
@RequestMapping("/api/v1/teams/{teamId}/milestones/recommendations")
@RequiredArgsConstructor
public class MilestoneRecommendationController {

    private final MilestoneRecommendationService milestoneRecommendationService;
    private final MilestoneService milestoneService;

    // 마일스톤 추천
    @Operation(
            summary = "AI 마일스톤 추천",
            description = """
                    프로젝트 유형과 최종 마감 시각을 기반으로
                    AI가 최대 6개의 마일스톤을 추천합니다.
                    
                    추천 결과에는 제목, 권장 마감 시각 및 간단한 설명이 포함됩니다.
                    추천 결과는 자동으로 저장되지 않습니다.
                    """
    )
    @PostMapping
    public ApiResponse<List<MilestoneRecommendationResponseDto>> getMilestoneRecommendations(
            @AuthenticationPrincipal AuthMember authMember,
            @PathVariable Long teamId,
            @RequestBody @Valid MilestoneRecommendationRequestDto requestDto
    ) {
        Long memberId = authMember.getMemberId();

        List<MilestoneRecommendationResponseDto> result =
                milestoneRecommendationService.recommendMilestones(memberId, teamId, requestDto);

        return ApiResponse.onSuccess("Milestone recommendations retrieved successfully.", result);
    }

    // 마일스톤 추천 후 결과 전체 등록
    @Operation(
            summary = "추천 마일스톤 전체 등록",
            description = """
                    AI 추천 모달에 표시된 마일스톤을 모두 등록합니다.
                    
                    추천 결과의 제목과 마감 시각만 저장되며,
                    추천 설명은 데이터베이스에 저장되지 않습니다.
                    최소 1개부터 최대 6개까지 한 번에 등록할 수 있습니다.
                    """
    )
    @PostMapping("/bulk")
    public ApiResponse<List<MilestoneResponseDto>> createRecommendedMilestones(
            @AuthenticationPrincipal AuthMember authMember,
            @PathVariable Long teamId,
            @RequestBody @Valid MilestoneBulkCreateRequestDto requestDto
    ) {
        Long memberId = authMember.getMemberId();

        List<MilestoneResponseDto> result =
                milestoneService.createMilestonesInBulk(memberId, teamId, requestDto);

        return ApiResponse.onSuccess("Recommended milestones created successfully.", result);
    }
}
