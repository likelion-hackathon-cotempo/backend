package cultureland.hackathon.team.dto;

import cultureland.hackathon.team.entity.Team;
import cultureland.hackathon.team.entity.TeamMember;
import cultureland.hackathon.team.entity.TeamRole;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class TeamSummaryResponseDto {

    private final Long teamId;

    private final String name;

    private final TeamRole myRole;

    public static TeamSummaryResponseDto from(TeamMember teamMember) {

        Team team = teamMember.getTeam();

        return TeamSummaryResponseDto.builder()
                .teamId(team.getTeamId())
                .name(team.getName())
                .myRole(teamMember.getRole())
                .build();

    }

}
