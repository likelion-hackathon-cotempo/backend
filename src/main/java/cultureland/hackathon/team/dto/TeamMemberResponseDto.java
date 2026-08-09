package cultureland.hackathon.team.dto;

import cultureland.hackathon.member.entity.Member;
import cultureland.hackathon.team.entity.TeamMember;
import cultureland.hackathon.team.entity.TeamRole;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class TeamMemberResponseDto {

    private final Long memberId;

    private final String name;

    // KR, US, VN, ...
    private final String country;

    private final String timezone;

    private final String position;

    private final TeamRole role;

    public static TeamMemberResponseDto from(TeamMember teamMember) {

        Member member = teamMember.getMember();

        return TeamMemberResponseDto.builder()
                .memberId(member.getMemberId())
                .name(member.getName())
                .country(member.getCountry())
                .timezone(member.getTimezone())
                .position(teamMember.getPosition())
                .role(teamMember.getRole())
                .build();

    }

}
