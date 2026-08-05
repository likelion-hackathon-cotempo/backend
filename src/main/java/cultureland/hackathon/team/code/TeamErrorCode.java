package cultureland.hackathon.team.code;

import cultureland.hackathon.global.code.BaseErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum TeamErrorCode implements BaseErrorCode {

    TEAM_NOT_FOUND(HttpStatus.NOT_FOUND, "TEAM_404_1", "팀을 찾을 수 없습니다."),
    INVALID_INVITE_CODE(HttpStatus.NOT_FOUND, "TEAM_404_2", "유효하지 않은 초대 코드입니다."),
    ALREADY_JOINED(HttpStatus.CONFLICT, "TEAM_409_1", "이미 참여 중인 팀입니다."),
    NOT_TEAM_MEMBER(HttpStatus.FORBIDDEN, "TEAM_403_1", "해당 팀의 멤버가 아닙니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

}
