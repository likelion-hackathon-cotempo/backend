package cultureland.hackathon.calendar.controller;

import cultureland.hackathon.calendar.dto.CalendarEventDto;
import cultureland.hackathon.calendar.service.CalendarService;
import cultureland.hackathon.global.api.ApiResponse;
import cultureland.hackathon.global.security.AuthMember;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class CalendarController {

    private final CalendarService calendarService;

    // 개인 통합 캘린더 조회
    @GetMapping("/dashboard")
    public ApiResponse<List<CalendarEventDto>> getPersonalCalendar(
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
    @GetMapping("/teams/{teamId}/calendar")
    public ApiResponse<List<CalendarEventDto>> getTeamCalendar(
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
