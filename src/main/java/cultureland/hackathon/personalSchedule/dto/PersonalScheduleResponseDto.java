package cultureland.hackathon.personalSchedule.dto;

import cultureland.hackathon.personalSchedule.entity.PersonalSchedule;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.Instant;
import java.time.ZoneOffset;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder(access = AccessLevel.PRIVATE)
public class PersonalScheduleResponseDto {
    private Long personalScheduleId;
    private String title;
    private Instant startDateTime;
    private Instant endDateTime;
    private int weight;

    public static PersonalScheduleResponseDto from(
            PersonalSchedule personalSchedule
    ) {
        return PersonalScheduleResponseDto.builder()
                .personalScheduleId(personalSchedule.getPersonalScheduleId())
                .title(personalSchedule.getTitle())
                .startDateTime(
                        personalSchedule.getStartDateTime()
                                .toInstant(ZoneOffset.UTC)
                )
                .endDateTime(
                        personalSchedule.getEndDateTime()
                                .toInstant(ZoneOffset.UTC)
                )
                .weight(personalSchedule.getWeight())
                .build();
    }
}
