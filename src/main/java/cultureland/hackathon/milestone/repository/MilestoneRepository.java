package cultureland.hackathon.milestone.repository;

import cultureland.hackathon.milestone.entity.Milestone;
import cultureland.hackathon.team.entity.Team;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface MilestoneRepository extends JpaRepository<Milestone, Long> {

    List<Milestone> findAllByTeamOrderByDueDateTimeAsc(Team team);

    // 경로의 teamId와 마일스톤의 소속 팀이 일치하는 경우에만 조회
    Optional<Milestone> findByMilestoneIdAndTeam(Long milestoneId, Team team);

    // 조회 기간 안에 마감되는 팀 마일스톤 조회
    @Query("""
        SELECT m FROM Milestone m
        WHERE m.team = :team
          AND m.dueDateTime >= :start
          AND m.dueDateTime < :end
        ORDER BY m.dueDateTime ASC
        """)
    List<Milestone> findByTeamAndPeriod(
            @Param("team") Team team,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end
    );
}
