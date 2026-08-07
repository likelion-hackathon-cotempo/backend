package cultureland.hackathon.team.service;

import cultureland.hackathon.global.exception.GeneralException;
import cultureland.hackathon.member.code.MemberErrorCode;
import cultureland.hackathon.member.entity.Member;
import cultureland.hackathon.member.repository.MemberRepository;
import cultureland.hackathon.team.code.TeamErrorCode;
import cultureland.hackathon.team.dto.TeamCreateRequestDto;
import cultureland.hackathon.team.dto.TeamCreateResponseDto;
import cultureland.hackathon.team.dto.TeamJoinRequestDto;
import cultureland.hackathon.team.dto.TeamSummaryResponseDto;
import cultureland.hackathon.team.entity.Team;
import cultureland.hackathon.team.entity.TeamMember;
import cultureland.hackathon.team.entity.TeamRole;
import cultureland.hackathon.team.repository.TeamMemberRepository;
import cultureland.hackathon.team.repository.TeamRepository;
import cultureland.hackathon.team.util.InviteCodeGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class TeamService {

    private final TeamRepository teamRepository;
    private final TeamMemberRepository teamMemberRepository;
    private final MemberRepository memberRepository;
    private final InviteCodeGenerator inviteCodeGenerator;

    // 팀 생성 - 생성자는 자동으로 OWNER 참여
    // 초대 코드 중복은 team.invite_code의 unique 제약으로 처리
    @Transactional
    public TeamCreateResponseDto createTeam(Long memberId, TeamCreateRequestDto requestDto) {
        Member member = getMember(memberId);

        Team team = teamRepository.save(
                Team.create(requestDto.getName(), inviteCodeGenerator.generate())
        );

        teamMemberRepository.save(
                TeamMember.create(TeamRole.OWNER, requestDto.getPosition(), member, team)
        );

        return TeamCreateResponseDto.from(team);
    }

    // 초대 코드로 팀 참여 - 자동으로 MEMBER 참여
    @Transactional
    public TeamSummaryResponseDto joinTeam(Long memberId, TeamJoinRequestDto requestDto) {
        Member member = getMember(memberId);

        Team team = teamRepository.findByInviteCode(requestDto.getInviteCode())
                .orElseThrow(() -> new GeneralException(TeamErrorCode.INVALID_INVITE_CODE));

        if (teamMemberRepository.existsByTeamAndMember(team, member)) {
            throw new GeneralException(TeamErrorCode.ALREADY_JOINED);
        }

        TeamMember teamMember = teamMemberRepository.save(
                TeamMember.create(TeamRole.MEMBER, requestDto.getPosition(), member, team)
        );

        return TeamSummaryResponseDto.from(teamMember);
    }

    private Member getMember(Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new GeneralException(MemberErrorCode.MEMBER_NOT_FOUND));

}
