package cultureland.hackathon.schedule.controller;

import cultureland.hackathon.global.api.ApiResponse;
import cultureland.hackathon.global.security.AuthMember;
import cultureland.hackathon.schedule.dto.PersonalScheduleCreateRequestDto;
import cultureland.hackathon.schedule.dto.PersonalScheduleResponseDto;
import cultureland.hackathon.schedule.dto.PersonalScheduleUpdateRequestDto;
import cultureland.hackathon.schedule.service.PersonalScheduleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/schedules")
@RequiredArgsConstructor
public class PersonalScheduleController {
    private final PersonalScheduleService personalScheduleService;

    @PostMapping
    public ApiResponse<PersonalScheduleResponseDto> createPersonalSchedule(
            @AuthenticationPrincipal AuthMember authMember,
            @RequestBody @Valid PersonalScheduleCreateRequestDto requestDto
    ) {
        Long memberId = authMember.getMemberId();
        PersonalScheduleResponseDto response =  personalScheduleService.createPersonalSchedule(memberId, requestDto);
        return ApiResponse.onSuccess("Personal schedule created successfully", response);
    }

    @PatchMapping("/{personalScheduleId}")
    public ApiResponse<PersonalScheduleResponseDto> updatePersonalSchedule(
            @AuthenticationPrincipal AuthMember authMember,
            @PathVariable Long personalScheduleId,
            @RequestBody @Valid PersonalScheduleUpdateRequestDto requestDto
    ) {
        Long memberId = authMember.getMemberId();
        PersonalScheduleResponseDto response = personalScheduleService.updatePersonalSchedule(memberId, personalScheduleId, requestDto);
        return ApiResponse.onSuccess("Personal schedule updated successfully", response);
    }

    @DeleteMapping("/{personalScheduleId}")
    public ApiResponse<Void> deletePersonalSchedule(
            @AuthenticationPrincipal AuthMember authMember,
            @PathVariable Long personalScheduleId
    ) {
        Long memberId = authMember.getMemberId();
        personalScheduleService.deletePersonalSchedule(memberId, personalScheduleId);
        return ApiResponse.onSuccess("Personal schedule deleted successfully.");
    }
}
