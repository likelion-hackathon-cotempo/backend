package cultureland.hackathon.team.dto;

import cultureland.hackathon.team.entity.Team;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class TeamCreateResponseDto {

    private final Long teamId;

    private final String name;

    private final String inviteCode;    // 생성 직후 바로 공유 가능하도록 포함

    public static TeamCreateResponseDto from(Team team) {
        return TeamCreateResponseDto.builder()
                .teamId(team.getTeamId())
                .name(team.getName())
                .inviteCode(team.getInviteCode())
                .build();
    }

}
