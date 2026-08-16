package cultureland.hackathon.calendar.provider;

import cultureland.hackathon.calendar.dto.CalendarEventDto;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
public class CalendarMockDataProvider {

    // 공휴일과 학사 일정은 특정 시각이 아닌 현지 날짜 전체를 차지하므로 LocalDate로 관리함
    // 프로토타입이므로 목데이터로 관리하지만 추후 API 개발 시 교체 가능

    private static final List<CalendarEventDto> EVENTS = List.of(

            // ==================== KR ====================

            // 홍익대학교 2학기 개강
            CalendarEventDto.ofAllDayEvent(
                    "Fall Semester Begins",
                    "KR",
                    LocalDate.of(2026, 9, 1),
                    LocalDate.of(2026, 9, 1),
                    CalendarEventDto.Type.ACADEMIC
            ),

            // 홍익대학교 시험기간
            CalendarEventDto.ofAllDayEvent(
                    "Examination Period",
                    "KR",
                    LocalDate.of(2026, 10, 19),
                    LocalDate.of(2026, 10, 23),
                    CalendarEventDto.Type.ACADEMIC
            ),

            // 광복절
            CalendarEventDto.ofAllDayEvent(
                    "Liberation Day",
                    "KR",
                    LocalDate.of(2026, 8, 15),
                    LocalDate.of(2026, 8, 15),
                    CalendarEventDto.Type.HOLIDAY
            ),

            // 광복절 대체공휴일
            CalendarEventDto.ofAllDayEvent(
                    "Liberation Day (Substitute Holiday)",
                    "KR",
                    LocalDate.of(2026, 8, 17),
                    LocalDate.of(2026, 8, 17),
                    CalendarEventDto.Type.HOLIDAY
            ),

            // 추석 연휴
            CalendarEventDto.ofAllDayEvent(
                    "Chuseok Holiday",
                    "KR",
                    LocalDate.of(2026, 9, 24),
                    LocalDate.of(2026, 9, 24),
                    CalendarEventDto.Type.HOLIDAY
            ),

            // 추석
            CalendarEventDto.ofAllDayEvent(
                    "Chuseok (Korean Thanksgiving)",
                    "KR",
                    LocalDate.of(2026, 9, 25),
                    LocalDate.of(2026, 9, 25),
                    CalendarEventDto.Type.HOLIDAY
            ),

            // 추석 연휴
            CalendarEventDto.ofAllDayEvent(
                    "Chuseok Holiday",
                    "KR",
                    LocalDate.of(2026, 9, 26),
                    LocalDate.of(2026, 9, 26),
                    CalendarEventDto.Type.HOLIDAY
            ),

            // 개천절
            CalendarEventDto.ofAllDayEvent(
                    "National Foundation Day",
                    "KR",
                    LocalDate.of(2026, 10, 3),
                    LocalDate.of(2026, 10, 3),
                    CalendarEventDto.Type.HOLIDAY
            ),

            // 개천절 대체공휴일
            CalendarEventDto.ofAllDayEvent(
                    "National Foundation Day (Substitute Holiday)",
                    "KR",
                    LocalDate.of(2026, 10, 5),
                    LocalDate.of(2026, 10, 5),
                    CalendarEventDto.Type.HOLIDAY
            ),

            // 한글날
            CalendarEventDto.ofAllDayEvent(
                    "Hangeul Day",
                    "KR",
                    LocalDate.of(2026, 10, 9),
                    LocalDate.of(2026, 10, 9),
                    CalendarEventDto.Type.HOLIDAY
            ),


            // ==================== US ====================

            // UC Berkeley 가을학기 시작
            CalendarEventDto.ofAllDayEvent(
                    "Fall Semester Begins",
                    "US",
                    LocalDate.of(2026, 8, 19),
                    LocalDate.of(2026, 8, 19),
                    CalendarEventDto.Type.ACADEMIC
            ),

            // UC Berkeley 수업 시작
            CalendarEventDto.ofAllDayEvent(
                    "Instruction Begins",
                    "US",
                    LocalDate.of(2026, 8, 26),
                    LocalDate.of(2026, 8, 26),
                    CalendarEventDto.Type.ACADEMIC
            ),

            // 노동절
            CalendarEventDto.ofAllDayEvent(
                    "Labor Day",
                    "US",
                    LocalDate.of(2026, 9, 7),
                    LocalDate.of(2026, 9, 7),
                    CalendarEventDto.Type.HOLIDAY
            ),

            // 콜럼버스 데이
            CalendarEventDto.ofAllDayEvent(
                    "Columbus Day",
                    "US",
                    LocalDate.of(2026, 10, 12),
                    LocalDate.of(2026, 10, 12),
                    CalendarEventDto.Type.HOLIDAY
            ),


            // ==================== VN ====================

            // UEH 2026년 마지막 학기 시작
            CalendarEventDto.ofAllDayEvent(
                    "Final Semester Begins",
                    "VN",
                    LocalDate.of(2026, 8, 10),
                    LocalDate.of(2026, 8, 10),
                    CalendarEventDto.Type.ACADEMIC
            ),

            /*
             * 2026년 8월 22일은 대체 근무일이므로 휴일로 등록하지 않는다.
             * 회의 시간 추천에서도 차단 날짜로 처리하지 않는다.
             */

            // 건국기념일 연휴 시작
            CalendarEventDto.ofAllDayEvent(
                    "National Day Holiday",
                    "VN",
                    LocalDate.of(2026, 8, 29),
                    LocalDate.of(2026, 8, 29),
                    CalendarEventDto.Type.HOLIDAY
            ),

            // 건국기념일 연휴
            CalendarEventDto.ofAllDayEvent(
                    "National Day Holiday",
                    "VN",
                    LocalDate.of(2026, 8, 30),
                    LocalDate.of(2026, 8, 30),
                    CalendarEventDto.Type.HOLIDAY
            ),

            // 건국기념일 연휴
            CalendarEventDto.ofAllDayEvent(
                    "National Day Holiday",
                    "VN",
                    LocalDate.of(2026, 8, 31),
                    LocalDate.of(2026, 8, 31),
                    CalendarEventDto.Type.HOLIDAY
            ),

            // 건국기념일 연휴
            CalendarEventDto.ofAllDayEvent(
                    "National Day Holiday",
                    "VN",
                    LocalDate.of(2026, 9, 1),
                    LocalDate.of(2026, 9, 1),
                    CalendarEventDto.Type.HOLIDAY
            ),

            // 건국기념일
            CalendarEventDto.ofAllDayEvent(
                    "National Day",
                    "VN",
                    LocalDate.of(2026, 9, 2),
                    LocalDate.of(2026, 9, 2),
                    CalendarEventDto.Type.HOLIDAY
            )
    );

    // 요청 기간과 겹치는 공휴일 및 학사 일정 반환
    public List<CalendarEventDto> findByCountryAndPeriod(
            String country,
            LocalDate startDate,
            LocalDate endDate
    ) {
        return EVENTS.stream()
                .filter(event ->
                        event.getCountry().equalsIgnoreCase(country)
                )
                .filter(event ->
                        !event.getEndDate().isBefore(startDate)
                            && !event.getStartDate().isAfter(endDate)
                )
                .toList();
    }
}
