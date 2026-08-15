package cultureland.hackathon.milestone.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.Instant;

@Getter
@Builder(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class MilestoneRecommendationResponseDto {

    private final String title;
    private final Instant dueDateTime;
    private final String description;

    public static MilestoneRecommendationResponseDto of(
            String title,
            Instant dueDateTime,
            String description
    ) {
        return MilestoneRecommendationResponseDto.builder()
                .title(title)
                .dueDateTime(dueDateTime)
                .description(description)
                .build();
    }

}
