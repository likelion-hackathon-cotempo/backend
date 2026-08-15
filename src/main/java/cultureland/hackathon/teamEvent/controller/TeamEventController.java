package cultureland.hackathon.teamEvent.controller;

import cultureland.hackathon.teamEvent.dto.TeamEventCreateRequestDto;
import cultureland.hackathon.teamEvent.dto.TeamEventResponseDto;
import cultureland.hackathon.teamEvent.dto.TeamEventUpdateRequestDto;
import cultureland.hackathon.teamEvent.service.TeamEventService;
import cultureland.hackathon.global.api.ApiResponse;
import cultureland.hackathon.global.config.SwaggerConfig;
import cultureland.hackathon.global.security.AuthMember;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Team Event", description = "팀 일정 등록 · 수정 · 삭제 API")
@SecurityRequirement(name = SwaggerConfig.SECURITY_SCHEME_NAME)
@RestController
@RequestMapping("/api/v1/teams/{teamId}/events")
@RequiredArgsConstructor
public class TeamEventController {

    private final TeamEventService teamEventService;

    @Operation(
            summary = "팀 일정 등록",
            description = "팀 일정을 등록합니다. 해당 팀의 멤버면 누구나 등록할 수 있습니다. "
                    + "시작·종료 시각은 UTC 기준 ISO-8601 형식으로 전달합니다. (예: 2026-08-12T04:00:00Z)"
    )
    @PostMapping
    public ApiResponse<TeamEventResponseDto> createTeamEvent(
            @Parameter(hidden = true) @AuthenticationPrincipal AuthMember authMember,
            @Parameter(description = "팀 ID", example = "1") @PathVariable Long teamId,
            @RequestBody @Valid TeamEventCreateRequestDto requestDto
    ) {
        TeamEventResponseDto result =
                teamEventService.createTeamEvent(authMember.getMemberId(), teamId, requestDto);

        return ApiResponse.onSuccess("Team event created successfully.", result);
    }

    @Operation(
            summary = "팀 일정 수정",
            description = "전달된 필드만 수정합니다. 최소 한 개 이상의 필드를 전달해야 합니다. "
                    + "해당 팀의 멤버면 누구나 수정할 수 있습니다."
    )
    @PatchMapping("/{eventId}")
    public ApiResponse<TeamEventResponseDto> updateTeamEvent(
            @Parameter(hidden = true) @AuthenticationPrincipal AuthMember authMember,
            @Parameter(description = "팀 ID", example = "1") @PathVariable Long teamId,
            @Parameter(description = "팀 일정 ID", example = "1") @PathVariable Long eventId,
            @RequestBody @Valid TeamEventUpdateRequestDto requestDto
    ) {
        TeamEventResponseDto result =
                teamEventService.updateTeamEvent(
                        authMember.getMemberId(), teamId, eventId, requestDto);

        return ApiResponse.onSuccess("Team event updated successfully.", result);
    }

    @Operation(
            summary = "팀 일정 삭제",
            description = "팀 일정을 삭제합니다. 해당 팀의 멤버면 누구나 삭제할 수 있습니다."
    )
    @DeleteMapping("/{eventId}")
    public ApiResponse<Void> deleteTeamEvent(
            @Parameter(hidden = true) @AuthenticationPrincipal AuthMember authMember,
            @Parameter(description = "팀 ID", example = "1") @PathVariable Long teamId,
            @Parameter(description = "팀 일정 ID", example = "1") @PathVariable Long eventId
    ) {
        teamEventService.deleteTeamEvent(authMember.getMemberId(), teamId, eventId);

        return ApiResponse.onSuccess("Team event deleted successfully.");
    }
}