package cultureland.hackathon.milestone.repository;

import cultureland.hackathon.milestone.entity.Milestone;
import cultureland.hackathon.team.entity.Team;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MilestoneRepository extends JpaRepository<Milestone, Long> {

    List<Milestone> findAllByTeamOrderByDueDateTimeAsc(Team team);

    // 경로의 teamId와 마일스톤의 소속 팀이 일치하는 경우에만 조회
    Optional<Milestone> findByMilestoneIdAndTeam(Long milestoneId, Team team);

}
