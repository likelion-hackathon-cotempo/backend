package cultureland.hackathon.schedule.dto;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Getter
@NoArgsConstructor
public class PersonalScheduleCreateRequestDto {

    @NotBlank(message = "Title is required.")
    @Size(max = 100, message = "Title must not exceed 100 characters.")
    private String title;

    @NotNull(message = "Start date and time are required.")
    private Instant startDateTime;

    @NotNull(message = "End date and time are required.")
    private Instant endDateTime;

    @NotNull(message = "Schedule weight is required.")
    @Min(value = 1, message = "Weight must be between 1 and 3.")
    @Max(value = 3, message = "Weight must be between 1 and 3.")
    private Integer weight;
}
