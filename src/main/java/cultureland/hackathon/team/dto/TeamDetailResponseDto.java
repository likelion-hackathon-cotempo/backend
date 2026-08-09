package cultureland.hackathon.team.dto;

import cultureland.hackathon.team.entity.Team;
import cultureland.hackathon.team.entity.TeamRole;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class TeamDetailResponseDto {

    private final Long teamId;

    private final String name;

    private final String inviteCode;

    private final TeamRole myRole;

    private final List<TeamMemberResponseDto> members;

    public static TeamDetailResponseDto of(Team team, TeamRole myRole,
                                           List<TeamMemberResponseDto> members) {
        return TeamDetailResponseDto.builder()
                .teamId(team.getTeamId())
                .name(team.getName())
                .inviteCode(team.getInviteCode())
                .myRole(myRole)
                .members(members)
                .build();

    }

}
