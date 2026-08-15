package cultureland.hackathon.milestone.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class MilestoneBulkCreateRequestDto {

    // 추천 결과 전체 등록이므로 최소 1개, 최대 6개까지 허용
    @NotEmpty(message = "At least one milestone is required.")
    @Size(
            max = 6,
            message = "No more than 6 milestones can be created at once."
    )
    private List<
            @Valid MilestoneCreateRequestDto
            > milestones;
}
