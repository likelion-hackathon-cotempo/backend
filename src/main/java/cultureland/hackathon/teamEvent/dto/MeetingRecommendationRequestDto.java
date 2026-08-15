package cultureland.hackathon.teamEvent.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Getter
@NoArgsConstructor
public class MeetingRecommendationRequestDto {

    @NotNull(message = "Start date and time are required.")
    private Instant startDateTime;

    @NotNull(message = "End date and time are required.")
    private Instant endDateTime;

    // 30, 60, 90, 120분 허용
    @NotNull(message = "Meeting duration is required.")
    private Integer durationMinutes;
}
