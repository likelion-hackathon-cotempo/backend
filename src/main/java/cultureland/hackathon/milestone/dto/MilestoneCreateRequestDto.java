package cultureland.hackathon.milestone.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Getter
@NoArgsConstructor
public class MilestoneCreateRequestDto {

    @NotBlank(message = "Title is required.")
    @Size(max = 100, message = "Title must not exceed 100 characters.")
    private String title;

    // UTC 기준 ISO-8601 (예: 2026-08-12T04:00:00Z)
    @NotNull(message = "Due time is required.")
    private Instant dueDateTime;

}
