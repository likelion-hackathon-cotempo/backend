package cultureland.hackathon.event.dto;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Getter
@NoArgsConstructor
public class TeamEventUpdateRequestDto {

    @Size(max = 100, message = "Title must not exceed 100 characters.")
    @Pattern(regexp = ".*\\S.*", message = "Title must not be blank.")
    private String title;

    // UTC 기준 ISO-8601
    private Instant startDateTime;

    private Instant endDateTime;

}
