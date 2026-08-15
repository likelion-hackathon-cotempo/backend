package cultureland.hackathon.teamEvent.repository;

import cultureland.hackathon.teamEvent.entity.TeamEvent;
import cultureland.hackathon.team.entity.Team;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface TeamEventRepository extends JpaRepository<TeamEvent, Long> {

    List<TeamEvent> findAllByTeamOrderByStartDateTimeAsc(Team team);

    List<TeamEvent> findAllByTeamAndStartDateTimeBetween(Team team,
                                                         LocalDateTime start,
                                                         LocalDateTime end);

    Optional<TeamEvent> findByTeamEventIdAndTeam(Long teamEventId, Team team);

}
