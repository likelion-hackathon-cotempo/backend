package cultureland.hackathon.teamEvent.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.Instant;

@Getter
@Builder(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class MeetingRecommendationResponseDto {

    private Instant startDateTime;
    private Instant endDateTime;
    private String description;

    public static MeetingRecommendationResponseDto of(
            Instant startDateTime,
            Instant endDateTime,
            String description
    ) {
        return MeetingRecommendationResponseDto.builder()
                .startDateTime(startDateTime)
                .endDateTime(endDateTime)
                .description(description)
                .build();
    }
}
