package cultureland.hackathon.team.repository;

import cultureland.hackathon.member.entity.Member;
import cultureland.hackathon.team.entity.Team;
import cultureland.hackathon.team.entity.TeamMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface TeamMemberRepository extends JpaRepository<TeamMember, Long> {

    // 팀 참여 시 중복 참여 여부 확인
    boolean existsByTeamAndMember(Team team, Member member);

    // 팀 소속 검증 + 해당 팀에서의 역할 조회
    Optional<TeamMember> findByTeamAndMember(Team team, Member member);

    // 참여중인 팀 목록 조회
    @Query("""
            SELECT tm FROM TeamMember tm
            JOIN FETCH tm.team
            WHERE tm.member = :member
            ORDER BY tm.teamMemberId ASC
            """)
    List<TeamMember> findAllByMemberWithTeam(@Param("member") Member member);

    // 팀원 목록 조회
    @Query("""
            SELECT tm FROM TeamMember tm
            JOIN FETCH tm.member
            WHERE tm.team = :team
            ORDER BY tm.teamMemberId ASC
            """)
    List<TeamMember> findAllByTeamWithMember(@Param("team") Team team);

}
