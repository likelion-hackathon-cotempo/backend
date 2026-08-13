package cultureland.hackathon.milestone.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;

@Getter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class MilestoneAiResponseDto {

    // AI가 추천한 마일스톤 목록
    private List<Recommendation> recommendations;

    @Getter
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Recommendation {
            private String title;
            private Instant dueDateTime;
            private String description;
    }
}
