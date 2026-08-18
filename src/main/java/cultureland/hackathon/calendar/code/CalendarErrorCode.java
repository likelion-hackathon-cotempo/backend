package cultureland.hackathon.calendar.code;

import cultureland.hackathon.global.code.BaseErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum CalendarErrorCode implements BaseErrorCode {

    UNSUPPORTED_CALENDAR_PERIOD(
            HttpStatus.BAD_REQUEST,
            "CALENDAR_400_1",
            "Calendar lookup is only supported from August to October 2026."
    );

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
