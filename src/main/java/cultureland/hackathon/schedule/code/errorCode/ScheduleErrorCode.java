package cultureland.hackathon.schedule.code.errorCode;

import cultureland.hackathon.global.code.BaseErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ScheduleErrorCode implements BaseErrorCode {

    SCHEDULE_NOT_FOUND(HttpStatus.NOT_FOUND, "SCHEDULE_404_1", "일정을 찾을 수 없습니다."),
    INVALID_DATE_RANGE(HttpStatus.BAD_REQUEST, "SCHEDULE_400_1", "종료 시각이 시작 시각보다 빠를 수 없습니다."),
    INVALID_WEIGHT(HttpStatus.BAD_REQUEST, "SCHEDULE_400_2", "가중치는 1에서 5 사이여야 합니다."),
    NOT_SCHEDULE_OWNER(HttpStatus.FORBIDDEN, "SCHEDULE_403_1", "본인의 일정만 수정할 수 있습니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

}
