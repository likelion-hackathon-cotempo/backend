package cultureland.hackathon.personalSchedule.code;

import cultureland.hackathon.global.code.BaseErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum PersonalScheduleErrorCode implements BaseErrorCode {

    PERSONAL_SCHEDULE_NOT_FOUND(
            HttpStatus.NOT_FOUND,
            "PERSONAL_SCHEDULE_404_1",
            "Schedule Not Found."
    ),
    INVALID_DATE_RANGE(
            HttpStatus.BAD_REQUEST,
            "PERSONAL_SCHEDULE_400_1",
            "End time must be after start time."
    ),
    INVALID_WEIGHT(
            HttpStatus.BAD_REQUEST,
            "PERSONAL_SCHEDULE_400_2",
            "Weight must be between 1 and 3."
    ),
    MISSING_FIELDS(
            HttpStatus.BAD_REQUEST,
            "PERSONAL_SCHEDULE_400_3",
            "At least one field must be provided."
    );

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

}
