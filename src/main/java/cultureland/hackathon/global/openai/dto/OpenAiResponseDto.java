package cultureland.hackathon.global.openai.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

import java.util.List;

@Getter
@NoArgsConstructor
// OpenAI 응답 중 DTO에 선언하지 않은 필드는 무시
@JsonIgnoreProperties(ignoreUnknown = true)
public class OpenAiResponseDto {

    // OpenAI가 생성한 출력 항목 목록
    private List<Output> output;

    @Getter
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Output {
        private String type;
        private List<Content> content;
    }

    @Getter
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Content {
        private String type;
        private String text;
    }
}
