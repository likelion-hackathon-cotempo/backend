package cultureland.hackathon.teamEvent.controller;

import cultureland.hackathon.global.api.ApiResponse;
import cultureland.hackathon.global.config.SwaggerConfig;
import cultureland.hackathon.global.security.AuthMember;
import cultureland.hackathon.teamEvent.dto.MeetingRecommendationRequestDto;
import cultureland.hackathon.teamEvent.dto.MeetingRecommendationResponseDto;
import cultureland.hackathon.teamEvent.service.MeetingRecommendationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(
        name = "회의 시간 추천",
        description = "팀원들의 일정과 현지 시각을 고려한 AI 회의 시간 추천 API"
)
@SecurityRequirement(
        name = SwaggerConfig.SECURITY_SCHEME_NAME
)
@RestController
@RequestMapping("/api/v1/teams/{teamId}/events/recommendations")
@RequiredArgsConstructor
public class MeetingRecommendationController {

    private final MeetingRecommendationService meetingRecommendationService;

    // 회의 시간 추천
    @Operation(
            summary = "회의 시간 추천",
            description = """
                요청 기간 내에서 팀 일정, 공휴일 및 학사 일정과 겹치는 시간을 제외하고
                팀원들의 개인 일정 가중치와 현지 시각을 고려하여
                AI가 최대 3개의 회의 시간을 추천합니다.

                회의 길이는 30분, 60분, 90분, 120분 중 하나를 입력해야 합니다.
                추천 결과는 자동으로 팀 일정에 등록되지 않습니다.
                """
    )
    @PostMapping
    public ApiResponse<List<MeetingRecommendationResponseDto>> recommendMeetings(
            @Parameter(hidden = true)
            @AuthenticationPrincipal AuthMember authMember,
            @PathVariable Long teamId,
            @RequestBody @Valid MeetingRecommendationRequestDto requestDto
    ) {
        Long memberId = authMember.getMemberId();

        List<MeetingRecommendationResponseDto> response =
                meetingRecommendationService.recommendMeetings(
                        memberId, teamId, requestDto
                );

        return ApiResponse.onSuccess("Meeting times recommended successfully.", response);
    }
}
