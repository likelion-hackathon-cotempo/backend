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
    );

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

}
