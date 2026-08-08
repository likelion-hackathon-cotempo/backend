package cultureland.hackathon.member.code;

import cultureland.hackathon.global.code.BaseErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum MemberErrorCode implements BaseErrorCode {

    INVALID_CREDENTIALS(
            HttpStatus.UNAUTHORIZED,
            "MEMBER_401_1",
            "Invalid email or password."),
    MEMBER_NOT_FOUND(
            HttpStatus.NOT_FOUND,
            "MEMBER_404_1",
            "Member not found."
    ),
    DUPLICATE_EMAIL(
            HttpStatus.CONFLICT,
            "MEMBER_409_1",
            "An account with this email already exists.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

}
