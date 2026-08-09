package cultureland.hackathon.team.service;

import cultureland.hackathon.global.exception.GeneralException;
import cultureland.hackathon.member.code.MemberErrorCode;
import cultureland.hackathon.member.entity.Member;
import cultureland.hackathon.member.repository.MemberRepository;
import cultureland.hackathon.team.code.TeamErrorCode;
import cultureland.hackathon.team.dto.*;
import cultureland.hackathon.team.entity.Team;
import cultureland.hackathon.team.entity.TeamMember;
import cultureland.hackathon.team.entity.TeamRole;
import cultureland.hackathon.team.repository.TeamMemberRepository;
import cultureland.hackathon.team.repository.TeamRepository;
import cultureland.hackathon.team.util.InviteCodeGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class TeamService {

    private final TeamRepository teamRepository;
    private final TeamMemberRepository teamMemberRepository;
    private final MemberRepository memberRepository;
    private final InviteCodeGenerator inviteCodeGenerator;

    // 팀 생성 - 생성자는 자동으로 OWNER 참여
    // 초대 코드 중복은 team.invite_code의 unique 제약으로 처리
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

    // 참여 중인 팀 목록 조회
    @Transactional(readOnly = true)
    public List<TeamSummaryResponseDto> getMyTeams(Long memberId) {
        Member member = getMember(memberId);

        return teamMemberRepository.findAllByMemberWithTeam(member).stream()
                .map(TeamSummaryResponseDto::from)
                .toList();
    }

    // 팀 상세 조회 — 팀 멤버만 조회 가능
    @Transactional(readOnly = true)
    public TeamDetailResponseDto getTeamDetail(Long memberId, Long teamId) {
        Member member = getMember(memberId);
        Team team = getTeam(teamId);
        TeamMember teamMember = getTeamMember(team, member);

        return TeamDetailResponseDto.of(team, teamMember.getRole(), findTeamMembers(team));
    }

    // 팀원 목록 조회 — 팀 멤버만 조회 가능
    @Transactional(readOnly = true)
    public List<TeamMemberResponseDto> getTeamMembers(Long memberId, Long teamId) {
        Member member = getMember(memberId);
        Team team = getTeam(teamId);
        validateTeamMember(team, member);

        return teamMemberRepository.findAllByTeamWithMember(team).stream()
                .map(TeamMemberResponseDto::from)
                .toList();
    }

    private Member getMember(Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new GeneralException(MemberErrorCode.MEMBER_NOT_FOUND));
    }

    private Team getTeam(Long teamId) {
        return teamRepository.findById(teamId)
                .orElseThrow(() -> new GeneralException(TeamErrorCode.TEAM_NOT_FOUND));
    }

    private List<TeamMemberResponseDto> findTeamMembers(Team team) {
        return teamMemberRepository.findAllByTeamWithMember(team).stream()
                .map(TeamMemberResponseDto::from)
                .toList();
    }

    // 팀 소속 검증 + 해당 팀에서의 역할이 필요할 때
    private TeamMember getTeamMember(Team team, Member member) {
        return teamMemberRepository.findByTeamAndMember(team, member)
                .orElseThrow(() -> new GeneralException(TeamErrorCode.NOT_TEAM_MEMBER));
    }

    // 팀 소속 검증만 필요할 때
    private void validateTeamMember(Team team, Member member) {
        if (!teamMemberRepository.existsByTeamAndMember(team, member)) {
            throw new GeneralException(TeamErrorCode.NOT_TEAM_MEMBER);
        }
    }

}
