package cultureland.hackathon.milestone.code.errorCode;

import cultureland.hackathon.global.code.BaseErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum MilestoneErrorCode implements BaseErrorCode {

    MILESTONE_NOT_FOUND(HttpStatus.NOT_FOUND, "MILESTONE_404_1", "마일스톤을 찾을 수 없습니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

}
