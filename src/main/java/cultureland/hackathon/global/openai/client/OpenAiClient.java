package cultureland.hackathon.global.openai.client;

import cultureland.hackathon.global.exception.GeneralException;
import cultureland.hackathon.global.openai.code.OpenAiErrorCode;
import cultureland.hackathon.global.openai.dto.OpenAiRequestDto;
import cultureland.hackathon.global.openai.dto.OpenAiResponseDto;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClientResponseException;

import java.util.Objects;

@Component
public class OpenAiClient {

    private final RestClient openAiRestClient;
    private final String model;

    public OpenAiClient(
            // OpenAiConfig의 openAiRestClient Bean을 지정해서 주입
            @Qualifier("openAiRestClient")
            RestClient openAiRestClient,

            // 사용할 OpenAI 모델명을 환경설정에서 주입
            @Value("${openai.model}")
            String model
    ) {
        this.openAiRestClient = openAiRestClient;
        this.model = model;
    }

    // 전달 받은 프롬프트를 OpenAI에 보내고 모델이 생성한 텍스트만 반환
    public String generate(String prompt) {

        // 설정된 모델명과 프롬프트로 OpenAI 요청 본문 생성
        OpenAiRequestDto requestDto =
                OpenAiRequestDto.of(model, prompt);

        OpenAiResponseDto responseDto;

        try {
            responseDto = openAiRestClient
                    // OpenAI Responses API 호출
                    .post()
                    .uri("/responses")

                    // 요청 DTO를 JSON 본문으로 변환하여 전달
                    .body(requestDto)

                    // HTTP 요청을 실행하고 응답 가져옴
                    .retrieve()

                    // 응답 JSON을 OpenAiResponseDto로 변환
                    .body(OpenAiResponseDto.class);
        } catch (RestClientResponseException e) {
            // OpenAI 서버가 4xx 또는 5xx 상태 코드로 응답한 경우
            throw convertHttpException(e);
        } catch (ResourceAccessException e) {
            // 연결 실패, 응답 시간 초과 등
            throw new GeneralException(
                    OpenAiErrorCode.OPENAI_REQUEST_FAILED
            );
        } catch (RestClientException e) {
            // 그 외 HTTP 요청 또는 응답 변환 과정에서 발생한 오류
            throw new GeneralException(
                    OpenAiErrorCode.OPENAI_REQUEST_FAILED
            );
        }

        // 정상 응답에서 실제 output_text만 추출
        return extractOutputText(responseDto);
    }

    // OpenAI의 HTTP 상태 코드를 서비스에서 사용하는 공통 에러코드로 변환
    private GeneralException convertHttpException(
            RestClientResponseException e
    ) {
        int statusCode = e.getStatusCode().value();

        // API 키 누락, 잘못된 키 또는 권한 부족
        if (statusCode == 401 || statusCode == 403) {
            return new GeneralException(
                    OpenAiErrorCode.OPENAI_AUTHENTICATION_FAILED
            );
        }

        // OpenAI 요청 횟수 또는 사용량 제한 초과
        if (statusCode == 429) {
            return new GeneralException(
                    OpenAiErrorCode.OPENAI_RATE_LIMIT_EXCEEDED
            );
        }

        // 잘못된 요청, 존재하지 않는 모델, OpenAI 서버 오류 등
        return new GeneralException(
                OpenAiErrorCode.OPENAI_REQUEST_FAILED
        );
    }

    // Responses API의 중첩 응답 구조에서 type이 output_text인 실제 응답 텍스트 추출
    private String extractOutputText(OpenAiResponseDto responseDto) {

        if (
                responseDto == null || responseDto.getOutput() == null
        ) {
            throw new GeneralException(
                    OpenAiErrorCode.INVALID_OPENAI_RESPONSE
            );
        }

        String result = responseDto.getOutput().stream()
                // output 목록에 null이 들어오는 상황 방지
                .filter(Objects::nonNull)

                // 실제 답변에 해당하는 message 타입만 선택
                .filter(output ->
                        "message".equals(output.getType())
                )

                // content가 없는 output 항목 제외
                .filter(output ->
                        output.getContent() != null
                )

                // 각 output 안의 content 목록을 하나의 흐름으로 펼침
                .flatMap(output ->
                        output.getContent().stream()
                )

                // content 목록의 null 값 제외
                .filter(Objects::nonNull)

                // 실제 텍스트에 해당하는 output_text 타입만 선택
                .filter(content ->
                        "output_text".equals(content.getType())
                )

                // 실제 텍스트 값만 꺼냄
                .map(OpenAiResponseDto.Content::getText)

                // null이거나 공백으로만 구성된 텍스트 제외
                .filter(text ->
                        text != null && !text.isBlank()
                )

                // 텍스트가 여러 조각이면 줄바꿈으로 연결
                .reduce(
                        (first, second) ->
                                first + System.lineSeparator() + second
                )

                // 유효한 텍스트가 없으면 예외 발생
                .orElseThrow(() ->
                        new GeneralException(
                                OpenAiErrorCode.INVALID_OPENAI_RESPONSE
                        )
                );

        return result;
    }
}
