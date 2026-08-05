package cultureland.hackathon.schedule.repository;

import cultureland.hackathon.member.entity.Member;
import cultureland.hackathon.schedule.entity.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface ScheduleRepository extends JpaRepository<Schedule, Long> {

    List<Schedule> findAllByMember(Member member);

    /*
      여러 회원의 일정 중 주어진 기간과 겹치는 것들을 조회
      겹침 판정: 일정 시작 < 기간 끝 AND 일정 끝 > 기간 시작
      경계가 맞닿는 경우는 충돌로 보지 않음
     */
    @Query("""
            SELECT s FROM Schedule s
            WHERE s.member IN :members
              AND s.startDateTime < :end
              AND s.endDateTime > :start
            """)
    List<Schedule> findOverlapping(@Param("members") List<Member> members,
                                   @Param("start") LocalDateTime start,
                                   @Param("end") LocalDateTime end);

}
