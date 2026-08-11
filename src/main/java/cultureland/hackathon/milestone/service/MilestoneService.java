package cultureland.hackathon.milestone.service;

import cultureland.hackathon.global.exception.GeneralException;
import cultureland.hackathon.milestone.code.MilestoneErrorCode;
import cultureland.hackathon.milestone.dto.MilestoneCreateRequestDto;
import cultureland.hackathon.milestone.dto.MilestoneResponseDto;
import cultureland.hackathon.milestone.dto.MilestoneUpdateRequestDto;
import cultureland.hackathon.milestone.entity.Milestone;
import cultureland.hackathon.milestone.repository.MilestoneRepository;
import cultureland.hackathon.team.entity.Team;
import cultureland.hackathon.team.service.TeamService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class MilestoneService {

    private final MilestoneRepository milestoneRepository;
    private final TeamService teamService;

    // 마일스톤 목록 조회 - 마감 시각 오름차순
    @Transactional(readOnly = true)
    public List<MilestoneResponseDto> getMilestones(Long memberId, Long teamId) {
        Team team = teamService.getJoinedTeam(memberId, teamId);

        return milestoneRepository.findAllByTeamOrderByDueDateTimeAsc(team).stream()
                .map(MilestoneResponseDto::from)
                .toList();
    }

    // 마일스톤 등록 - 해당 팀의 멤버 누구나 가능
    public MilestoneResponseDto createMilestone(Long memberId, Long teamId,
                                                MilestoneCreateRequestDto requestDto) {
        Team team = teamService.getJoinedTeam(memberId, teamId);

        Milestone milestone = milestoneRepository.save(
                Milestone.create(
                        team,
                        requestDto.getTitle(),
                        toUtcDateTime(requestDto.getDueDateTime())
                )
        );

        return MilestoneResponseDto.from(milestone);
    }

    // 마일스톤 수정 - 전달된 필드만 반영 (completed 포함)
    public MilestoneResponseDto updateMilestone(Long memberId, Long teamId, Long milestoneId,
                                                MilestoneUpdateRequestDto requestDto) {
        validateUpdateRequest(requestDto);

        Team team = teamService.getJoinedTeam(memberId, teamId);
        Milestone milestone = getMilestone(milestoneId,team);

        milestone.update(
                requestDto.getTitle(),
                toUtcDateTime(requestDto.getDueDateTime()),
                requestDto.getCompleted()
        );

        return MilestoneResponseDto.from(milestone);
    }

    // 마일스톤 삭제 - 해당 팀의 멤버 누구나 가능
    public void deleteMilestone(Long memberId, Long teamId, Long milestoneId) {
        Team team = teamService.getJoinedTeam(memberId, teamId);
        Milestone milestone = getMilestone(milestoneId, team);

        milestoneRepository.delete(milestone);
    }


    private Milestone getMilestone(Long milestoneId, Team team) {
        return milestoneRepository.findByMilestoneIdAndTeam(milestoneId, team)
                .orElseThrow(() -> new GeneralException(MilestoneErrorCode.MILESTONE_NOT_FOUND));
    }

    private void validateUpdateRequest(MilestoneUpdateRequestDto requestDto) {
        if (
                requestDto.getTitle() == null
                        && requestDto.getDueDateTime() == null
                        && requestDto.getCompleted() == null
        ) {
            throw new GeneralException(MilestoneErrorCode.MISSING_FIELDS);
        }
    }

    // 요청은 UTC 기준 Instant, 저장은 UTC 기준 LocalDateTime
    private LocalDateTime toUtcDateTime(Instant instant) {
        return instant != null
                ? LocalDateTime.ofInstant(instant, ZoneOffset.UTC)
                : null;
    }

}
