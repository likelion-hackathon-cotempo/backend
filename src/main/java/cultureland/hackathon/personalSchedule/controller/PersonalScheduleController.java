package cultureland.hackathon.personalSchedule.controller;

import cultureland.hackathon.global.api.ApiResponse;
import cultureland.hackathon.global.config.SwaggerConfig;
import cultureland.hackathon.global.security.AuthMember;
import cultureland.hackathon.personalSchedule.dto.PersonalScheduleCreateRequestDto;
import cultureland.hackathon.personalSchedule.dto.PersonalScheduleResponseDto;
import cultureland.hackathon.personalSchedule.dto.PersonalScheduleUpdateRequestDto;
import cultureland.hackathon.personalSchedule.service.PersonalScheduleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Tag(
        name = "개인 일정",
        description = "개인 일정 등록·수정·삭제 API"
)
@SecurityRequirement(
        name = SwaggerConfig.SECURITY_SCHEME_NAME
)
@RestController
@RequestMapping("/api/v1/schedules")
@RequiredArgsConstructor
public class PersonalScheduleController {
    private final PersonalScheduleService personalScheduleService;

    @Operation(
            summary = "개인 일정 등록",
            description = """
                로그인한 회원의 개인 일정을 등록합니다.
                시작·종료 시각은 UTC 기준 ISO-8601 형식으로 전달합니다.
                가중치는 1부터 3까지 선택할 수 있습니다.
                """
    )
    @PostMapping
    public ApiResponse<PersonalScheduleResponseDto> createPersonalSchedule(
            @Parameter(hidden = true)
            @AuthenticationPrincipal AuthMember authMember,
            @RequestBody @Valid PersonalScheduleCreateRequestDto requestDto
    ) {
        Long memberId = authMember.getMemberId();
        PersonalScheduleResponseDto response =
                personalScheduleService.createPersonalSchedule(
                        memberId,
                        requestDto
                );
        return ApiResponse.onSuccess("Personal schedule created successfully.", response);
    }

    @Operation(
            summary = "개인 일정 수정",
            description = """
                로그인한 회원 본인의 개인 일정을 수정합니다.
                전달된 필드만 수정되며 최소 한 개 이상의 필드가 필요합니다.
                """
    )
    @PatchMapping("/{personalScheduleId}")
    public ApiResponse<PersonalScheduleResponseDto> updatePersonalSchedule(
            @Parameter(hidden = true)
            @AuthenticationPrincipal AuthMember authMember,

            @Parameter(description = "개인 일정 ID", example = "1")
            @PathVariable Long personalScheduleId,

            @RequestBody @Valid PersonalScheduleUpdateRequestDto requestDto
    ) {
        Long memberId = authMember.getMemberId();
        PersonalScheduleResponseDto response =
                personalScheduleService.updatePersonalSchedule(
                        memberId,
                        personalScheduleId,
                        requestDto
                );
        return ApiResponse.onSuccess("Personal schedule updated successfully.", response);
    }

    @Operation(
            summary = "개인 일정 삭제",
            description = "로그인한 회원 본인의 개인 일정을 삭제합니다."
    )
    @DeleteMapping("/{personalScheduleId}")
    public ApiResponse<Void> deletePersonalSchedule(
            @Parameter(hidden = true)
            @AuthenticationPrincipal AuthMember authMember,

            @Parameter(description = "개인 일정 ID", example = "1")
            @PathVariable Long personalScheduleId
    ) {
        Long memberId = authMember.getMemberId();
        personalScheduleService.deletePersonalSchedule(memberId, personalScheduleId);
        return ApiResponse.onSuccess("Personal schedule deleted successfully.");
    }
}
