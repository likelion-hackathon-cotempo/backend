package cultureland.hackathon.global.openai.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class OpenAiRequestDto {

    private String model;
    private String input;

    public static OpenAiRequestDto of(
            String model,
            String input
    ) {
        return new OpenAiRequestDto(model, input);
    }
}
