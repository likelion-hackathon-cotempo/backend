package cultureland.hackathon.event.code;

import cultureland.hackathon.global.code.BaseErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum TeamEventErrorCode implements BaseErrorCode {

    INVALID_DATE_RANGE(
            HttpStatus.BAD_REQUEST,
            "TEAM_EVENT_400_1",
            "End time must be after start time."
    ),
    MISSING_FIELDS(
            HttpStatus.BAD_REQUEST,
            "TEAM_EVENT_400_2",
            "At least one field must be provided."
    ),
    TEAM_EVENT_NOT_FOUND(
            HttpStatus.NOT_FOUND,
            "TEAM_EVENT_404_1",
            "Team event not found."
    );

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

}
