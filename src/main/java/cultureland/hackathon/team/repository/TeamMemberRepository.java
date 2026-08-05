package cultureland.hackathon.team.repository;

import cultureland.hackathon.member.entity.Member;
import cultureland.hackathon.team.entity.Team;
import cultureland.hackathon.team.entity.TeamMember;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TeamMemberRepository extends JpaRepository<TeamMember, Long> {

    List<TeamMember> findAllByTeam(Team team);

    List<TeamMember> findAllByMember(Member member);

    boolean existsByTeamAndMember(Team team, Member member);

}
