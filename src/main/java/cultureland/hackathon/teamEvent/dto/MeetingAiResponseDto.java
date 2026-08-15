package cultureland.hackathon.teamEvent.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class MeetingAiResponseDto {

    // AI가 추천한 회의 시간 목록
    private List<Recommendation> recommendations;

    @Getter
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Recommendation {
        private Integer candidateId;
        private String description;
    }
}
