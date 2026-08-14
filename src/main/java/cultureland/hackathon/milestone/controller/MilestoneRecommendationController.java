package cultureland.hackathon.milestone.controller;

import cultureland.hackathon.global.api.ApiResponse;
import cultureland.hackathon.global.security.AuthMember;
import cultureland.hackathon.milestone.dto.MilestoneBulkCreateRequestDto;
import cultureland.hackathon.milestone.dto.MilestoneRecommendationRequestDto;
import cultureland.hackathon.milestone.dto.MilestoneRecommendationResponseDto;
import cultureland.hackathon.milestone.dto.MilestoneResponseDto;
import cultureland.hackathon.milestone.service.MilestoneRecommendationService;
import cultureland.hackathon.milestone.service.MilestoneService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/teams/{teamId}/milestones/recommendations")
@RequiredArgsConstructor
public class MilestoneRecommendationController {

    private final MilestoneRecommendationService milestoneRecommendationService;
    private final MilestoneService milestoneService;

    // 마일스톤 추천
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
