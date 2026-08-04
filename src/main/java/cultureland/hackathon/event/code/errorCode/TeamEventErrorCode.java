package cultureland.hackathon.event.code.errorCode;

import cultureland.hackathon.global.code.BaseErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum TeamEventErrorCode implements BaseErrorCode {

    TEAM_EVENT_NOT_FOUND(HttpStatus.NOT_FOUND, "TEAM_EVENT_404_1", "팀 일정을 찾을 수 없습니다."),
    INVALID_DATE_RANGE(HttpStatus.BAD_REQUEST, "TEAM_EVENT_400_1", "종료 시각이 시작 시각보다 빠를 수 없습니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

}
