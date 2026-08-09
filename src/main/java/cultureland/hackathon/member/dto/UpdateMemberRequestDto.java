package cultureland.hackathon.member.dto;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UpdateMemberRequestDto {
    @Size(max = 30, message = "Name must not exceed 30 characters.")
    @Pattern(
            regexp = ".*\\S.*",
            message = "Name must not be blank."
    )
    private String name;

    @Pattern(
            regexp = "^[A-Z]{2}$",
            message = "Country must be a two-letter country code."
    )
    private String country;

    @Size(max = 50, message = "Timezone must not exceed 50 characters.")
    @Pattern(
            regexp = ".*\\S.*",
            message = "Timezone must not be blank."
    )
    private String timezone;
}
