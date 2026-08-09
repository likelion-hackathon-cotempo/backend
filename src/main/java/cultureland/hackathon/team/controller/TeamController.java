package cultureland.hackathon.team.controller;

import cultureland.hackathon.global.api.ApiResponse;
import cultureland.hackathon.global.config.SwaggerConfig;
import cultureland.hackathon.global.security.AuthMember;
import cultureland.hackathon.team.dto.*;
import cultureland.hackathon.team.service.TeamService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Team", description = "팀 생성 · 참여 · 조회 API")
@SecurityRequirement(name = SwaggerConfig.SECURITY_SCHEME_NAME)
@RestController
@RequestMapping("/api/v1/teams")
@RequiredArgsConstructor
public class TeamController {

    private final TeamService teamService;

    @Operation(
            summary = "팀 생성",
            description = "팀을 생성하고 생성자를 OWNER로 참여시킵니다. "
                    + "초대 코드는 서버에서 자동 생성되어 응답에 포함됩니다."
    )
    @PostMapping
    public ApiResponse<TeamCreateResponseDto> createTeam(
            @Parameter(hidden = true) @AuthenticationPrincipal AuthMember authMember,
            @RequestBody @Valid TeamCreateRequestDto requestDto
    ) {
        TeamCreateResponseDto result =
                teamService.createTeam(authMember.getMemberId(), requestDto);

        return ApiResponse.onSuccess("Team created successfully.", result);
    }

    @Operation(
            summary = "팀 참여",
            description = "초대 코드로 팀에 참여합니다. 참여자는 MEMBER 역할을 가지며, 직군 입력이 필수입니다."
    )
    @PostMapping("/join")
    public ApiResponse<TeamSummaryResponseDto> joinTeam(
            @Parameter(hidden = true) @AuthenticationPrincipal AuthMember authMember,
            @RequestBody @Valid TeamJoinRequestDto requestDto
    ) {
        TeamSummaryResponseDto result =
                teamService.joinTeam(authMember.getMemberId(), requestDto);

        return ApiResponse.onSuccess("Team joined successfully.", result);
    }

    @Operation(
            summary = "참여 중인 팀 목록 조회",
            description = "로그인한 회원이 참여 중인 팀을 참여한 순서대로 조회합니다."
    )
    @GetMapping
    public ApiResponse<List<TeamSummaryResponseDto>> getMyTeams(
            @Parameter(hidden = true) @AuthenticationPrincipal AuthMember authMember
    ) {
        List<TeamSummaryResponseDto> result =
                teamService.getMyTeams(authMember.getMemberId());

        return ApiResponse.onSuccess("Teams retrieved successfully.", result);
    }

    @Operation(
            summary = "팀 상세 조회",
            description = "팀 정보, 초대 코드, 팀원 목록을 조회합니다. 해당 팀의 멤버만 조회할 수 있습니다."
    )
    @GetMapping("/{teamId}")
    public ApiResponse<TeamDetailResponseDto> getTeamDetail(
            @Parameter(hidden = true) @AuthenticationPrincipal AuthMember authMember,
            @Parameter(description = "팀 ID", example = "1") @PathVariable Long teamId
    ) {
        TeamDetailResponseDto result =
                teamService.getTeamDetail(authMember.getMemberId(), teamId);

        return ApiResponse.onSuccess("Team retrieved successfully.", result);
    }

    @Operation(
            summary = "팀원 목록 조회",
            description = "팀원의 이름, 국가, 타임존, 직군을 조회합니다. "
                    + "해당 팀의 멤버만 조회할 수 있으며 OWNER가 가장 먼저 반환됩니다."
    )
    @GetMapping("/{teamId}/members")
    public ApiResponse<List<TeamMemberResponseDto>> getTeamMembers(
            @Parameter(hidden = true) @AuthenticationPrincipal AuthMember authMember,
            @Parameter(description = "팀 ID", example = "1") @PathVariable Long teamId
    ) {
        List<TeamMemberResponseDto> result =
                teamService.getTeamMembers(authMember.getMemberId(), teamId);

        return ApiResponse.onSuccess("Team members retrieved successfully.", result);
    }
}
