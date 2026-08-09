package cultureland.hackathon.team.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class TeamJoinRequestDto {

    @NotBlank(message = "Invite code is required.")
    @Size(min = 6, max = 6, message = "Invite code must not exceed 6 characters.")
    private String inviteCode;

    @Size(max = 30, message = "Position must not exceed 30 characters.")
    private String position;

}
