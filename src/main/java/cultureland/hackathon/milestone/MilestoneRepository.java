package cultureland.hackathon.milestone;

import cultureland.hackathon.milestone.entity.MileStone;
import cultureland.hackathon.team.entity.Team;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MilestoneRepository extends JpaRepository<MileStone, Long> {

    List<MileStone> findAllByTeamOrderByDueDateAsc(Team team);

}
