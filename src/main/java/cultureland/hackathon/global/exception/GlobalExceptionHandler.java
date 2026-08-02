package cultureland.hackathon.global.exception;

import cultureland.hackathon.global.api.ApiResponse;
import cultureland.hackathon.global.code.BaseErrorCode;
import cultureland.hackathon.global.code.GeneralErrorCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(GeneralException.class)
    public ResponseEntity<ApiResponse<Void>> handleGeneralException(
            GeneralException e
    ) {
        BaseErrorCode errorCode = e.getErrorCode();

        return ResponseEntity
                .status(errorCode.getHttpStatus())
                .body(ApiResponse.onFailure(errorCode, null));
    }

    // 그 외의 정의되지 않은 모든 예외 처리
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleException(
            Exception ex
    ) {
        BaseErrorCode code = GeneralErrorCode.INTERNAL_SERVER_ERROR;

        return ResponseEntity
                .status(code.getHttpStatus())
                .body(ApiResponse.onFailure(code, null));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Void>> handleValidationException(
            MethodArgumentNotValidException e
    ) {
        Map<String, String> errors = new HashMap<>();

        e.getBindingResult()
                .getFieldErrors()
                .forEach(error -> {
                    errors.putIfAbsent(
                            error.getField(),
                            error.getDefaultMessage()
                    );
                });

        BaseErrorCode code = GeneralErrorCode.BAD_REQUEST;

        return ResponseEntity
                .status(code.getHttpStatus())
                .body(ApiResponse.onFailure(code, errors));
    }
}
