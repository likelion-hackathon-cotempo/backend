package cultureland.hackathon.personalSchedule.dto;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Getter
@NoArgsConstructor
public class PersonalScheduleUpdateRequestDto {

    @Size(max = 100, message = "Title must not exceed 100 characters.")
    @Pattern(
            regexp = ".*\\S.*",
            message = "Title must not be blank."
    )
    private String title;

    private Instant startDateTime;

    private Instant endDateTime;

    @Min(value = 1, message = "Weight must be between 1 and 3.")
    @Max(value = 3, message = "Weight must be between 1 and 3.")
    private Integer weight;
}
