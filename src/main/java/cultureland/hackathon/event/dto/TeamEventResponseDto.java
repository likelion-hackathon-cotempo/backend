package cultureland.hackathon.event.dto;

import cultureland.hackathon.event.entity.TeamEvent;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.Instant;
import java.time.ZoneOffset;

@Getter
@Builder(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class TeamEventResponseDto {

    private final Long teamEventId;

    private final String title;

    // UTC 기준 ISO-8601
    private final Instant startDateTime;

    private final Instant endDateTime;

    public static TeamEventResponseDto from(TeamEvent teamEvent) {
        return TeamEventResponseDto.builder()
                .teamEventId(teamEvent.getTeamEventId())
                .title(teamEvent.getTitle())
                .startDateTime(teamEvent.getStartDateTime().toInstant(ZoneOffset.UTC))
                .endDateTime(teamEvent.getEndDateTime().toInstant(ZoneOffset.UTC))
                .build();
    }

}
