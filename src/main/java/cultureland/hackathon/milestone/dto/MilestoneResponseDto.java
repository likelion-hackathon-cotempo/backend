package cultureland.hackathon.milestone.dto;

import cultureland.hackathon.milestone.entity.Milestone;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.Instant;
import java.time.ZoneOffset;

@Getter
@Builder(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class MilestoneResponseDto {

    private final Long milestoneId;

    private final String title;

    private final Instant dueDateTime;

    private final boolean completed;

    public static MilestoneResponseDto from(Milestone milestone) {
        return MilestoneResponseDto.builder()
                .milestoneId(milestone.getMilestoneId())
                .title(milestone.getTitle())
                .dueDateTime(milestone.getDueDateTime().toInstant(ZoneOffset.UTC))
                .completed(milestone.isCompleted)
                .build();
    }

}
