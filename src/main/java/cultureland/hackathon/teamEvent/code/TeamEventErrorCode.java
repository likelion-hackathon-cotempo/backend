package cultureland.hackathon.teamEvent.code;

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
    ),

    INVALID_MEETING_DURATION(
            HttpStatus.BAD_REQUEST,
            "TEAM_EVENT_400_3",
            "Meeting duration must be 30, 60, 90, or 120 minutes."
    ),
    SEARCH_PERIOD_TOO_SHORT(
            HttpStatus.BAD_REQUEST,
            "TEAM_EVENT_400_4",
            "The search period must be longer than the meeting duration."
    ),
    SEARCH_PERIOD_TOO_LONG(
            HttpStatus.BAD_REQUEST,
            "TEAM_EVENT_400_5",
            "The search period must not exceed 90 days."
    ),
    MEETING_CANDIDATE_NOT_FOUND(
            HttpStatus.UNPROCESSABLE_CONTENT,
            "TEAM_EVENT_422_1",
            "No available meeting time was found."
    ),
    INVALID_MEETING_RECOMMENDATION(
            HttpStatus.BAD_GATEWAY,
            "TEAM_EVENT_502_1",
            "The AI generated an invalid meeting recommendation."
    );

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

}
