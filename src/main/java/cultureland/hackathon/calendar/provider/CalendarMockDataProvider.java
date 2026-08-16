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
            // TODO: 국가별 2026 8-9월 공휴일 및 학사 일정 추가
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
