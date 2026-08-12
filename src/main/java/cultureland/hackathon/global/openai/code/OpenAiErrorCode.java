package cultureland.hackathon.global.openai.code;

import cultureland.hackathon.global.code.BaseErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum OpenAiErrorCode implements BaseErrorCode {

    OPENAI_AUTHENTICATION_FAILED(
            HttpStatus.BAD_GATEWAY,
            "OPENAI_502_1",
            "AI service authentication failed."
    ),
    OPENAI_REQUEST_FAILED(
            HttpStatus.BAD_GATEWAY,
            "OPENAI_502_2",
            "Failed to request the AI service."
    ),
    INVALID_OPENAI_RESPONSE(
            HttpStatus.BAD_GATEWAY,
            "OPENAI_502_3",
            "Received an invalid response from the AI service."
    ),
    OPENAI_RATE_LIMIT_EXCEEDED(
            HttpStatus.SERVICE_UNAVAILABLE,
            "OPENAI_503_1",
            "AI service request limit exceeded."
    );

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
