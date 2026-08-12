package cultureland.hackathon.personalSchedule.repository;

import cultureland.hackathon.member.entity.Member;
import cultureland.hackathon.personalSchedule.entity.PersonalSchedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface PersonalScheduleRepository extends JpaRepository<PersonalSchedule, Long> {

    Optional<PersonalSchedule> findByPersonalScheduleIdAndMember(
            Long personalScheduleId,
            Member member
    );

    // 추후 통합 캘린더에서 호출
    /*
      여러 회원의 일정 중 주어진 기간과 겹치는 것들을 조회
      겹침 판정: 일정 시작 < 기간 끝 AND 일정 끝 > 기간 시작
      경계가 맞닿는 경우는 충돌로 보지 않음
     */
    @Query("""
            SELECT s FROM PersonalSchedule s
            WHERE s.member IN :members
              AND s.startDateTime < :end
              AND s.endDateTime > :start
            """)
    List<PersonalSchedule> findOverlapping(
            @Param("members") List<Member> members,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end
    );

}
