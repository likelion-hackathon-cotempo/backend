package cultureland.hackathon.calendar.controller;

import cultureland.hackathon.calendar.dto.CalendarEventDto;
import cultureland.hackathon.calendar.service.CalendarService;
import cultureland.hackathon.global.api.ApiResponse;
import cultureland.hackathon.global.config.SwaggerConfig;
import cultureland.hackathon.global.security.AuthMember;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(
        name = "통합 캘린더",
        description = "개인 및 팀의 통합 캘린더 조회 API"
)
@SecurityRequirement(
        name = SwaggerConfig.SECURITY_SCHEME_NAME
)
@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class CalendarController {

    private final CalendarService calendarService;

    // 개인 통합 캘린더 조회
    @Operation(
            summary = "개인 통합 캘린더 조회",
            description = """
                로그인한 회원의 개인 일정, 참여 중인 팀의 팀 일정,
                회원 국가의 공휴일과 학사 일정을 월별로 조회합니다.

                캘린더 조회는 2026년 8월부터 10월까지만 지원합니다.
                """
    )
    @GetMapping("/dashboard")
    public ApiResponse<List<CalendarEventDto>> getPersonalCalendar(
            @Parameter(hidden = true)
            @AuthenticationPrincipal AuthMember authMember,
            @RequestParam(name = "year") int year,
            @RequestParam(name = "month") int month
    ) {
        Long memberId = authMember.getMemberId();

        List<CalendarEventDto> response =
                calendarService.getPersonalCalendar(memberId, year, month);

        return ApiResponse.onSuccess(
                "Personal calendar retrieved successfully.", response
        );
    }

    // 팀 통합 캘린더 조회
    @Operation(
            summary = "팀 통합 캘린더 조회",
            description = """
                해당 팀의 팀원 개인 일정, 팀 일정, 마일스톤,
                팀원 국가별 공휴일과 학사 일정을 월별로 조회합니다.

                팀에 참여 중인 회원만 조회할 수 있으며,
                캘린더 조회는 2026년 8월부터 10월까지만 지원합니다.
                """
    )
    @GetMapping("/teams/{teamId}/calendar")
    public ApiResponse<List<CalendarEventDto>> getTeamCalendar(
            @Parameter(hidden = true)
            @AuthenticationPrincipal AuthMember authMember,
            @PathVariable Long teamId,
            @RequestParam(name = "year") int year,
            @RequestParam(name = "month") int month
    ) {
        Long memberId = authMember.getMemberId();

        List<CalendarEventDto> response =
                calendarService.getTeamCalendar(
                        memberId, teamId, year, month
                );

        return ApiResponse.onSuccess(
                "Team calendar retrieved successfully.", response
        );
    }
}
