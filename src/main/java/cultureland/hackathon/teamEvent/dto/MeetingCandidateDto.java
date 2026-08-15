package cultureland.hackathon.teamEvent.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class MeetingCandidateDto {

    // AI가 후보 식별 시 사용하는 번호, 새로운 시간 생성하지 않도록 막기 위함
    private final int candidateId;
    private final Instant startDateTime;
    private final Instant endDateTime;

    // 이 시간대와 겹치는 모든 개인 일정의 점수 합계
    // 팀 일정, 공휴일, 학사 일정과 겹치는 후보는 완전히 제외 후 DTO로 만듦
    private final int totalConflictScore;

    // 후보 시간에 대한 팀원별 현지 시각 및 개인 일정 충돌 정보
    private final List<MemberContext> members;

    public static MeetingCandidateDto of(
            int candidateId,
            Instant startDateTime,
            Instant endDateTime,
            int totalConflictScore,
            List<MemberContext> members
    ) {
        return MeetingCandidateDto.builder()
                .candidateId(candidateId)
                .startDateTime(startDateTime)
                .endDateTime(endDateTime)
                .totalConflictScore(totalConflictScore)
                .members(members)
                .build();
    }

    @Getter
    @Builder(access = AccessLevel.PRIVATE)
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    public static class MemberContext {

        // 팀원 식별자
        private final Long memberId;

        // 추천 설명에 사용할 팀원 이름
        private final String memberName;

        // 공휴일 판단 등에 사용할 국가 코드
        private final String country;

        // 현지 시각 변환 기준
        private final String timezone;

        // 후보 시작 시각을 해당 팀원의 타임존으로 변환한 값
        private final LocalDateTime localStartDateTime;
        // 후보 종료 시각을 해당 팀원의 타임존으로 변환한 값
        private final LocalDateTime localEndDateTime;

        // 해당 팀원의 개인 일정과 겹치는지 여부
        private final boolean hasConflict;

        // 해당 팀원의 개인 일정 충돌 점수
        private final int conflictScore;

        public static MemberContext of(
                Long memberId,
                String memberName,
                String country,
                String timezone,
                LocalDateTime localStartDateTime,
                LocalDateTime localEndDateTime,
                boolean hasConflict,
                int conflictScore
        ) {
            return MemberContext.builder()
                    .memberId(memberId)
                    .memberName(memberName)
                    .country(country)
                    .timezone(timezone)
                    .localStartDateTime(localStartDateTime)
                    .localEndDateTime(localEndDateTime)
                    .hasConflict(hasConflict)
                    .conflictScore(conflictScore)
                    .build();
        }
    }
}
