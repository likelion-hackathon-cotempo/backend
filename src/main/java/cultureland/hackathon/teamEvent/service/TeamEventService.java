package cultureland.hackathon.teamEvent.service;

import cultureland.hackathon.teamEvent.code.TeamEventErrorCode;
import cultureland.hackathon.teamEvent.dto.TeamEventCreateRequestDto;
import cultureland.hackathon.teamEvent.dto.TeamEventResponseDto;
import cultureland.hackathon.teamEvent.dto.TeamEventUpdateRequestDto;
import cultureland.hackathon.teamEvent.entity.TeamEvent;
import cultureland.hackathon.teamEvent.repository.TeamEventRepository;
import cultureland.hackathon.global.exception.GeneralException;
import cultureland.hackathon.team.entity.Team;
import cultureland.hackathon.team.service.TeamService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Service
@Transactional
@RequiredArgsConstructor
public class TeamEventService {

    private final TeamEventRepository teamEventRepository;
    private final TeamService teamService;

    // 팀 일정 등록 — 해당 팀의 멤버면 누구나 가능
    public TeamEventResponseDto createTeamEvent(Long memberId, Long teamId,
                                                TeamEventCreateRequestDto requestDto) {
        Team team = teamService.getJoinedTeam(memberId, teamId);

        TeamEvent teamEvent = teamEventRepository.save(
                TeamEvent.create(
                        team,
                        requestDto.getTitle(),
                        toUtcDateTime(requestDto.getStartDateTime()),
                        toUtcDateTime(requestDto.getEndDateTime())
                )
        );

        return TeamEventResponseDto.from(teamEvent);
    }

    // 팀 일정 수정 — 전달된 필드만 반영
    public TeamEventResponseDto updateTeamEvent(Long memberId, Long teamId, Long teamEventId,
                                                TeamEventUpdateRequestDto requestDto) {
        validateUpdateRequest(requestDto);

        Team team = teamService.getJoinedTeam(memberId, teamId);
        TeamEvent teamEvent = getTeamEvent(teamEventId, team);

        teamEvent.update(
                requestDto.getTitle(),
                toUtcDateTime(requestDto.getStartDateTime()),
                toUtcDateTime(requestDto.getEndDateTime())
        );

        return TeamEventResponseDto.from(teamEvent);
    }

    // 팀 일정 삭제 — 해당 팀의 멤버면 누구나 가능
    public void deleteTeamEvent(Long memberId, Long teamId, Long teamEventId) {
        Team team = teamService.getJoinedTeam(memberId, teamId);
        TeamEvent teamEvent = getTeamEvent(teamEventId, team);

        teamEventRepository.delete(teamEvent);
    }


    private TeamEvent getTeamEvent(Long teamEventId, Team team) {
        return teamEventRepository.findByTeamEventIdAndTeam(teamEventId, team)
                .orElseThrow(() -> new GeneralException(TeamEventErrorCode.TEAM_EVENT_NOT_FOUND));
    }

    private void validateUpdateRequest(TeamEventUpdateRequestDto requestDto) {
        if (
                requestDto.getTitle() == null
                        && requestDto.getStartDateTime() == null
                        && requestDto.getEndDateTime() == null
        ) {
            throw new GeneralException(TeamEventErrorCode.MISSING_FIELDS);
        }
    }

    // 요청은 UTC 기준 Instant, 저장은 UTC 기준 LocalDateTime
    private LocalDateTime toUtcDateTime(Instant instant) {
        return instant != null
                ? LocalDateTime.ofInstant(instant, ZoneOffset.UTC)
                : null;
    }

}
