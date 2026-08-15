package cultureland.hackathon.milestone.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Getter
@NoArgsConstructor
public class MilestoneRecommendationRequestDto {

    @NotBlank(message = "Project type is required.")
    @Size(max = 100, message = "Project type must not exceed 100 characters.")
    private String projectType;

    @NotNull(message = "Due date and time are required.")
    private Instant dueDateTime;
}
