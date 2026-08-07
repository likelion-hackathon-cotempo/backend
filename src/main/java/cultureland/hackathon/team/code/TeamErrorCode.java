package cultureland.hackathon.team.code;

import cultureland.hackathon.global.code.BaseErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum TeamErrorCode implements BaseErrorCode {

    NOT_TEAM_MEMBER(
            HttpStatus.FORBIDDEN,
            "TEAM_403_1",
            "You are not a member of this team."
    ),
    TEAM_NOT_FOUND(
            HttpStatus.NOT_FOUND,
            "TEAM_404_1",
            "Team not found."
    ),
    INVALID_INVITE_CODE(
            HttpStatus.NOT_FOUND,
            "TEAM_404_2",
            "Invalid invite code."
    ),
    ALREADY_JOINED(
            HttpStatus.CONFLICT,
            "TEAM_409_1",
            "You have already joined this team."
    ),
    INVITE_CODE_GENERATION_FAILED(
            HttpStatus.INTERNAL_SERVER_ERROR,
            "TEAM_500_1",
            "Failed to generate a unique invite code."
    );

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

}
