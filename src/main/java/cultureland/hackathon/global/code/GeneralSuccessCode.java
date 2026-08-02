package cultureland.hackathon.global.code;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
@Getter
public enum GeneralSuccessCode implements BaseSuccessCode {
    OK(HttpStatus.OK, "COMMON_200", "성공적으로 처리했습니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
