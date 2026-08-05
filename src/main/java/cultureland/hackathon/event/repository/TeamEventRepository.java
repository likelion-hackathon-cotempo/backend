package cultureland.hackathon.event.repository;

import cultureland.hackathon.event.entity.TeamEvent;
import cultureland.hackathon.team.entity.Team;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface TeamEventRepository extends JpaRepository<TeamEvent, Long> {

    List<TeamEvent> findAllByTeamOrderByStartDateTimeAsc(Team team);

    List<TeamEvent> findAllByTeamAndStartDateTimeBetween(Team team,
                                                         LocalDateTime start,
                                                         LocalDateTime end);

}
