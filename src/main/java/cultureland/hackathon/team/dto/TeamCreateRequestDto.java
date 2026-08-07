package cultureland.hackathon.team.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class TeamCreateRequestDto {

    @NotBlank(message = "Team name is required.")
    @Size(max = 50, message = "Team name must not exceed 50 characters.")
    private String name;

    // 팀 생성자가 팀 내에서 가지는 직군 (예: Backend, UX Designer)
    @Size(max = 30, message = "Position must not exceed 30 characters.")
    private String position;

}
