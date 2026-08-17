package cultureland.hackathon.teamEvent.repository;

import cultureland.hackathon.teamEvent.entity.TeamEvent;
import cultureland.hackathon.team.entity.Team;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface TeamEventRepository extends JpaRepository<TeamEvent, Long> {

    List<TeamEvent> findAllByTeamOrderByStartDateTimeAsc(Team team);

    List<TeamEvent> findAllByTeamAndStartDateTimeBetween(Team team,
                                                         LocalDateTime start,
                                                         LocalDateTime end);

    Optional<TeamEvent> findByTeamEventIdAndTeam(Long teamEventId, Team team);

    @Query("""
            SELECT e FROM TeamEvent e
            WHERE e.team = :team
              AND e.startDateTime < :end
              AND e.endDateTime > :start
            """)
    List<TeamEvent> findOverlapping(
            @Param("team") Team team,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end
    );
}
