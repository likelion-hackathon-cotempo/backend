package cultureland.hackathon.milestone.service;

import cultureland.hackathon.global.exception.GeneralException;
import cultureland.hackathon.global.openai.client.OpenAiClient;
import cultureland.hackathon.global.openai.code.OpenAiErrorCode;
import cultureland.hackathon.member.code.MemberErrorCode;
import cultureland.hackathon.member.entity.Member;
import cultureland.hackathon.member.repository.MemberRepository;
import cultureland.hackathon.milestone.code.MilestoneErrorCode;
import cultureland.hackathon.milestone.dto.MilestoneAiResponseDto;
import cultureland.hackathon.milestone.dto.MilestoneRecommendationRequestDto;
import cultureland.hackathon.milestone.dto.MilestoneRecommendationResponseDto;
import cultureland.hackathon.milestone.prompt.MilestoneRecommendationPrompt;
import cultureland.hackathon.team.code.TeamErrorCode;
import cultureland.hackathon.team.entity.Team;
import cultureland.hackathon.team.repository.TeamMemberRepository;
import cultureland.hackathon.team.repository.TeamRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;

import java.time.Instant;
import java.util.*;

@Service
@RequiredArgsConstructor
public class MilestoneRecommendationService {

    private static final int MAX_RECOMMENDATIONS = 6;

    private final MemberRepository memberRepository;
    private final TeamRepository teamRepository;
    private final TeamMemberRepository teamMemberRepository;
    private final OpenAiClient openAiClient;
    private final ObjectMapper objectMapper;

    // 로그인 회원이 속한 팀에 대해 AI가 마일스톤 추천을 생성, DB에 저장하지 않고 후보 목록으로 반환
    public List<MilestoneRecommendationResponseDto> recommendMilestones(
            Long memberId,
            Long teamId,
            MilestoneRecommendationRequestDto requestDto
    ) {
        // 추천 요청 시각을 시작 시간으로 고려
        Instant startDateTime = Instant.now();

        // 마감 시간이 현재보다 미래인지 검증
        validateDeadline(
                startDateTime,
                requestDto.getDueDateTime()
        );

        // 로그인 회원과 요청 대상 팀이 존재하는지 검증
        Member member = findMember(memberId);
        Team team = findTeam(teamId);

        // 해당 팀에 참여 중인 경우에만 추천 기능 사용할 수 있도록 검증
        validateTeamMember(team, member);

        // OpenAI 프롬프트 생성
        String prompt = MilestoneRecommendationPrompt.create(
                requestDto.getProjectType(),
                startDateTime,
                requestDto.getDueDateTime()
        );

        // 모델이 생성한 텍스트 반환
        String aiResponseText = openAiClient.generate(prompt);

        // 반환된 JSON 문자열을 Java DTO로 변환
        MilestoneAiResponseDto aiResponse =
                parseAiResponse(aiResponseText);

        // AI가 반환한 결과의 개수, 필수값, 날짜 범위 검증
        validateAiResponse(
                aiResponse,
                startDateTime,
                requestDto.getDueDateTime()
        );

        // 서버에서 날짜순으로 다시 정렬 후 프론트에 반환할 API 응답 DTO로 반환
        return aiResponse.getRecommendations().stream()
                .sorted(
                        Comparator.comparing(
                                MilestoneAiResponseDto.Recommendation::getDueDateTime
                        )
                )
                .map(recommendation ->
                        MilestoneRecommendationResponseDto.of(
                                recommendation.getTitle(),
                                recommendation.getDueDateTime(),
                                recommendation.getDescription()
                        )
                ).toList();
    }

    // 로그인 회원 조회
    private Member findMember(Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() ->
                        new GeneralException(
                            MemberErrorCode.MEMBER_NOT_FOUND
                        )
                );
    }

    // 추천 요청 대상 팀 조회
    private Team findTeam(Long teamId) {
        return teamRepository.findById(teamId)
                .orElseThrow(() ->
                        new GeneralException(
                                TeamErrorCode.TEAM_NOT_FOUND
                        )
                );
    }

    // 로그인 회원이 해당 팀에 참여하고 있는지 검증
    private void validateTeamMember(
            Team team,
            Member member
    ) {
        if (!teamMemberRepository.existsByTeamAndMember(team, member)) {
            throw new GeneralException(
                    TeamErrorCode.NOT_TEAM_MEMBER
            );
        }
    }

    // 프로젝트 최종 마감 시각이 추천 요청 시각보다 미래인지 검증
    private void validateDeadline(Instant startDateTime, Instant dueDateTime) {
        if (!dueDateTime.isAfter(startDateTime)) {
            throw new GeneralException(
                    MilestoneErrorCode.INVALID_PROJECT_DEADLINE
            );
        }
    }

    // OpenAI가 반환한 JSON 문자열을 AI 응답 DTO로 변환
    private MilestoneAiResponseDto parseAiResponse(String aiResponseText) {
        try {
            return objectMapper.readValue(aiResponseText, MilestoneAiResponseDto.class);
        } catch (JacksonException e) {
            throw new GeneralException(
                    OpenAiErrorCode.INVALID_OPENAI_RESPONSE
            );
        }
    }

    // AI가 프롬프트 규칙 지켰는지 서버에서 재검증
    private void validateAiResponse(
            MilestoneAiResponseDto aiResponse,
            Instant startDateTime,
            Instant finalDueDateTime
    ) {
        // 추천 목록 자체가 없는 경우
        if (
                aiResponse == null || aiResponse.getRecommendations() == null
        ) {
            throw new GeneralException(
                    OpenAiErrorCode.INVALID_OPENAI_RESPONSE
            );
        }

        List<MilestoneAiResponseDto.Recommendation> recommendations =
                aiResponse.getRecommendations();

        // 요청은 성공했지만 추천 결과 비어 있는 경우
        if (recommendations.isEmpty()) {
            throw new GeneralException(
                    MilestoneErrorCode.MILESTONE_RECOMMENDATION_NOT_FOUND
            );
        }

        // 최대 개수 초과한 경우
        if (recommendations.size() > MAX_RECOMMENDATIONS) {
            throw new GeneralException(
                    MilestoneErrorCode.INVALID_MILESTONE_RECOMMENDATION
            );
        }

        // 제목 소문자로 정규화
        Set<String> titles = new HashSet<>();

        for (
                MilestoneAiResponseDto.Recommendation recommendation
                : recommendations
        ) {
            validateRecommendation(
                    recommendation,
                    startDateTime,
                    finalDueDateTime,
                    titles
            );
        }
    }

    // AI가 반환한 마일스톤 한 건 검증
    private void validateRecommendation(
            MilestoneAiResponseDto.Recommendation recommendation,
            Instant startDateTime,
            Instant finalDueDateTime,
            Set<String> titles
    ) {
        // 추천 목록 내에 null이 존재하는 경우
        if (recommendation == null) {
            throw new GeneralException(
                    MilestoneErrorCode.INVALID_MILESTONE_RECOMMENDATION
            );
        }

        String title = recommendation.getTitle();
        String description = recommendation.getDescription();
        Instant dueDateTime = recommendation.getDueDateTime();

        // 화면에 필요한 필수 결과 누락된 경우
        if (
                title == null
                || title.isBlank()
                || description == null
                || description.isBlank()
                || dueDateTime == null
        ) {
            throw new GeneralException(
                    MilestoneErrorCode.INVALID_MILESTONE_RECOMMENDATION
            );
        }

        // 추천 시각은 요청 시점 이후, 마감 시각 이하여야 함
        if (
                !dueDateTime.isAfter(startDateTime)
                || dueDateTime.isAfter(finalDueDateTime)
        ) {
            throw new GeneralException(
                    MilestoneErrorCode.INVALID_MILESTONE_RECOMMENDATION
            );
        }

        // 앞뒤 공백과 대소문자 차이 제거 후 제목 중복 확인
        String normalizedTitle = title.trim().toLowerCase(Locale.ROOT);

        if (!titles.add(normalizedTitle)) {
            throw new GeneralException(
                    MilestoneErrorCode.INVALID_MILESTONE_RECOMMENDATION
            );
        }
    }

}
