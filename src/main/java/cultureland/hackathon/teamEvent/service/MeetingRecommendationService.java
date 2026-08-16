package cultureland.hackathon.teamEvent.service;

import cultureland.hackathon.global.exception.GeneralException;
import cultureland.hackathon.global.openai.client.OpenAiClient;
import cultureland.hackathon.global.openai.code.OpenAiErrorCode;
import cultureland.hackathon.member.entity.Member;
import cultureland.hackathon.personalSchedule.entity.PersonalSchedule;
import cultureland.hackathon.personalSchedule.repository.PersonalScheduleRepository;
import cultureland.hackathon.team.entity.Team;
import cultureland.hackathon.team.entity.TeamMember;
import cultureland.hackathon.team.repository.TeamMemberRepository;
import cultureland.hackathon.team.service.TeamService;
import cultureland.hackathon.teamEvent.calculator.MeetingCandidateCalculator;
import cultureland.hackathon.teamEvent.code.TeamEventErrorCode;
import cultureland.hackathon.teamEvent.dto.MeetingAiResponseDto;
import cultureland.hackathon.teamEvent.dto.MeetingCandidateDto;
import cultureland.hackathon.teamEvent.dto.MeetingRecommendationRequestDto;
import cultureland.hackathon.teamEvent.dto.MeetingRecommendationResponseDto;
import cultureland.hackathon.teamEvent.entity.TeamEvent;
import cultureland.hackathon.teamEvent.prompt.MeetingRecommendationPrompt;
import cultureland.hackathon.teamEvent.repository.TeamEventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.*;

import static java.util.stream.Collectors.toSet;

@Service
@RequiredArgsConstructor
public class MeetingRecommendationService {

    // 사용자가 선택할 수 있는 회의 길이
    private static final Set<Integer> ALLOWED_DURATIONS = Set.of(30, 60, 90, 120);

    private static final int MAX_RECOMMENDATIONS = 3;

    private final TeamService teamService;
    private final TeamMemberRepository teamMemberRepository;
    private final TeamEventRepository teamEventRepository;
    private final PersonalScheduleRepository personalScheduleRepository;
    private final MeetingCandidateCalculator meetingCandidateCalculator;
    private final OpenAiClient openAiClient;
    private final ObjectMapper objectMapper;

    // AI가 최종적으로 선택한 최대 3개의 후보를 반환
    public List<MeetingRecommendationResponseDto> recommendMeetings(
            Long memberId,
            Long teamId,
            MeetingRecommendationRequestDto requestDto
    ) {
        Instant searchStart = requestDto.getStartDateTime();
        Instant searchEnd = requestDto.getEndDateTime();
        Integer durationMinutes = requestDto.getDurationMinutes();

        // 요청 기간과 회의 길이 검증
        validateRequest(
                searchStart,
                searchEnd,
                durationMinutes
        );

        // 로그인 회원이 해당 팀에 참여 중인지 확인 후 팀 조회
        Team team = teamService.getJoinedTeam(memberId, teamId);

        // 팀에 참여 중인 회원 엔티티 목록 조회
        List<Member> members =
                teamMemberRepository
                        .findAllByTeamWithMember(team)
                        .stream()
                        .map(TeamMember::getMember)
                        .toList();

        LocalDateTime utcSearchStart = toUtcDateTime(searchStart);
        LocalDateTime utcSearchEnd = toUtcDateTime(searchEnd);

        // 요청 기간과 겹치는 기존 팀 일정 조회
        List<TeamEvent> teamEvents =
                teamEventRepository.findOverlapping(
                        team,
                        utcSearchStart,
                        utcSearchEnd
                );

        // 요청 기간과 겹치는 모든 팀원의 개인 일정 조회
        List<PersonalSchedule> personalSchedules =
                personalScheduleRepository.findOverlapping(
                        members,
                        utcSearchStart,
                        utcSearchEnd
                );

        // 규칙 기반으로 점수가 낮은 회의 후보 최대 5개 계산
        List<MeetingCandidateDto> candidates =
                meetingCandidateCalculator.calculate(
                        searchStart,
                        searchEnd,
                        durationMinutes,
                        members,
                        teamEvents,
                        personalSchedules
                );

        // 후보가 하나도 만들어지지 않은 경우
        if (candidates.isEmpty()) {
            throw new GeneralException(
                    TeamEventErrorCode.MEETING_CANDIDATE_NOT_FOUND
            );
        }

        // 후보 목록 JSON으로 변환해 프롬프트에 포함
        String candidatesJson = serializeCandidates(candidates);

        String prompt = MeetingRecommendationPrompt.create(candidatesJson);

        // OpenAI가 생성한 JSON 텍스트 반환
        String aiResponseText = openAiClient.generate(prompt);

        // AI 응답 JSON을 DTO로 변환
        MeetingAiResponseDto aiResponse =
                parseAiResponse(aiResponseText);

        // AI 응답의 후보 ID, 설명, 중복 여부 등 검증
        validateAiResponse(aiResponse, candidates);

        // 후보 ID로 원본 후보 빠르게 찾도록 Map 생성
        Map<Integer, MeetingCandidateDto> candidatesById = new HashMap<>();

        for (MeetingCandidateDto candidate : candidates) {
            candidatesById.put(
                    candidate.getCandidateId(),
                    candidate
            );
        }

        // AI가 반환한 순서를 추천 순서로 유지
        return aiResponse.getRecommendations().stream()
                .map(recommendation -> {
                        MeetingCandidateDto candidate =
                            candidatesById.get(
                                    recommendation.getCandidateId()
                            );

                        return MeetingRecommendationResponseDto.of(
                                candidate.getStartDateTime(),
                                candidate.getEndDateTime(),
                                recommendation.getDescription()
                        );
                })
                .toList();
    }

    // 검색 기간과 회의 길이 검증
    private void validateRequest(
            Instant searchStart,
            Instant searchEnd,
            Integer durationMinutes
    ) {
        // 종료 시각은 시작 시각보다 뒤여야 함
        if (
                searchStart == null
                        || searchEnd == null
                        || !searchStart.isBefore(searchEnd)
        ) {
            throw new GeneralException(
                    TeamEventErrorCode.INVALID_DATE_RANGE
            );
        }

        if (
                durationMinutes == null
                || !ALLOWED_DURATIONS.contains(durationMinutes)
        ) {
            throw new GeneralException(
                    TeamEventErrorCode.INVALID_MEETING_DURATION
            );
        }

        // 조회 기간 안에 최소 한번의 회의 배치할 수 있어야 함
        if (
                Duration.between(searchStart, searchEnd)
                        .compareTo(Duration.ofMinutes(durationMinutes)) < 0
        ) {
            throw new GeneralException(
                    TeamEventErrorCode.SEARCH_PERIOD_TOO_SHORT
            );
        }
    }

    // 후보 DTO 목록을 프롬프트에 넣을 JSON 문자열로 변환
    private String serializeCandidates(List<MeetingCandidateDto> candidates) {
        try {
            return objectMapper.writeValueAsString(candidates);
        } catch (JacksonException e) {
            throw new GeneralException(
                    OpenAiErrorCode.OPENAI_REQUEST_FAILED
            );
        }
    }

    // OpenAI가 반환한 JSON 문자열을 AI 응답 DTO로 변환
    private MeetingAiResponseDto parseAiResponse(String aiResponseText) {
        try {
            return objectMapper.readValue(aiResponseText, MeetingAiResponseDto.class);
        } catch (JacksonException e) {
            throw new GeneralException(
                    OpenAiErrorCode.INVALID_OPENAI_RESPONSE
            );
        }
    }

    // 프롬프트 규칙 지켰는지 재검증
    private void validateAiResponse(
            MeetingAiResponseDto aiResponse,
            List<MeetingCandidateDto> candidates
    ) {
        if (
                aiResponse == null
                || aiResponse.getRecommendations() == null
        ) {
            throw new GeneralException(
                    OpenAiErrorCode.INVALID_OPENAI_RESPONSE
            );
        }

        List<MeetingAiResponseDto.Recommendation> recommendations = aiResponse.getRecommendations();

        // AI가 선택할 수 있는 적절한 후보가 없다고 판단한 경우
        if (recommendations.isEmpty()) {
            throw new GeneralException(
                    TeamEventErrorCode.MEETING_CANDIDATE_NOT_FOUND
            );
        }

        // 프롬프트에서 지정한 최대 추천 개수 초과
        if (recommendations.size() > MAX_RECOMMENDATIONS) {
            throw new GeneralException(
                    TeamEventErrorCode.INVALID_MEETING_RECOMMENDATION
            );
        }

        Set<Integer> validCandidateIds =
                candidates.stream()
                        .map(MeetingCandidateDto::getCandidateId)
                        .collect(toSet());

        Set<Integer> selectedCandidateIds = new HashSet<>();

        for (
                MeetingAiResponseDto.Recommendation recommendation : recommendations
        ) {
            if (recommendation == null) {
                throw new GeneralException(
                        TeamEventErrorCode.INVALID_MEETING_RECOMMENDATION
                );
            }

            Integer candidateId = recommendation.getCandidateId();
            String description = recommendation.getDescription();

            // 필수 결과 누락되었는지 확인
            if (
                    candidateId == null
                    || description == null
                    || description.isBlank()
            ) {
                throw new GeneralException(
                        TeamEventErrorCode.INVALID_MEETING_RECOMMENDATION
                );
            }

            // 서버가 제공하지 않은 후보 ID를 AI가 반환한 경우
            if (!validCandidateIds.contains(candidateId)) {
                throw new GeneralException(
                        TeamEventErrorCode.INVALID_MEETING_RECOMMENDATION
                );
            }

            // 동일한 후보를 AI가 중복 반환한 경우
            if (!selectedCandidateIds.add(candidateId)) {
                throw new GeneralException(
                        TeamEventErrorCode.INVALID_MEETING_RECOMMENDATION
                );
            }
        }
    }

    // API에서 받은 UTC Instant -> 엔티티 조회용 UTC LocalDateTime
    private LocalDateTime toUtcDateTime(Instant instant) {
        return LocalDateTime.ofInstant(
                instant,
                ZoneOffset.UTC
        );
    }
}
