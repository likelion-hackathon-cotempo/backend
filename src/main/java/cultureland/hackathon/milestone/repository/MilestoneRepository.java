package cultureland.hackathon.milestone.repository;

import cultureland.hackathon.milestone.entity.Milestone;
import cultureland.hackathon.team.entity.Team;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MilestoneRepository extends JpaRepository<Milestone, Long> {

    List<Milestone> findAllByTeamOrderByDueDateAsc(Team team);

}
