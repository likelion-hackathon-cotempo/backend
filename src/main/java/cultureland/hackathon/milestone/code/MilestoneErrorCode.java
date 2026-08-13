package cultureland.hackathon.milestone.code;

import cultureland.hackathon.global.code.BaseErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum MilestoneErrorCode implements BaseErrorCode {

    MISSING_FIELDS(
            HttpStatus.BAD_REQUEST,
            "MILESTONE_400_1",
            "At least one field must be provided."
    ),
    MILESTONE_NOT_FOUND(
            HttpStatus.NOT_FOUND,
            "MILESTONE_404_1",
            "Milestone not found."
    ),

    INVALID_PROJECT_DEADLINE(
            HttpStatus.BAD_REQUEST,
            "MILESTONE_400_1",
            "The project deadline must be in the future."
    ),
    MILESTONE_RECOMMENDATION_NOT_FOUND(
            HttpStatus.UNPROCESSABLE_CONTENT,
            "MILESTONE_422_1",
            "Unable to generate milestone recommendations for the given project."
    ),
    INVALID_MILESTONE_RECOMMENDATION(
            HttpStatus.BAD_GATEWAY,
            "MILESTONE_502_1",
            "The AI generated an invalid milestone recommendation."
    );

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

}
