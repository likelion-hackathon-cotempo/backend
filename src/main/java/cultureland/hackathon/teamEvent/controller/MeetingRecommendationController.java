package cultureland.hackathon.teamEvent.controller;

import cultureland.hackathon.global.api.ApiResponse;
import cultureland.hackathon.global.security.AuthMember;
import cultureland.hackathon.teamEvent.dto.MeetingRecommendationRequestDto;
import cultureland.hackathon.teamEvent.dto.MeetingRecommendationResponseDto;
import cultureland.hackathon.teamEvent.service.MeetingRecommendationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/teams/{teamId}/events/recommendations")
@RequiredArgsConstructor
public class MeetingRecommendationController {

    private final MeetingRecommendationService meetingRecommendationService;

    // 회의 시간 추천
    @PostMapping
    public ApiResponse<List<MeetingRecommendationResponseDto>> recommendMeetings(
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
