package cultureland.hackathon.milestone.prompt;

import java.time.Instant;

public final class MilestoneRecommendationPrompt {

    private static final int MAX_RECOMMENDATIONS = 6;

    // 정적 메서드만 제공하므로 객체 생성을 막는다.
    private MilestoneRecommendationPrompt() {
    }

    public static String create(
            String projectType,
            Instant startDateTime,
            Instant dueDateTime
    ) {
        return """
                You are an assistant that recommends project milestones.

                Create a practical milestone plan based on the project information below.

                [Project information]
                - Project type: %s
                - Planning start date and time: %s
                - Project due date and time: %s
                - Timezone: UTC

                [Requirements]
                1. Recommend no more than %d milestones.
                2. Adjust the number and scope of milestones to the project type and available period.
                3. Include only essential and meaningful stages of the project.
                4. Arrange the milestones in a realistic execution order.
                5. Every milestone dueDateTime must be after the planning start date and time.
                6. Every milestone dueDateTime must be on or before the final project due date and time.
                7. Sort the milestones by dueDateTime in ascending order.
                8. Each milestone must contain:
                   - a clear and actionable title
                   - a recommended due date and time
                   - a short explanation of what should be completed
                9. The final milestone should represent an appropriate closing stage for the project,
                   such as completion, submission, presentation, release, or final review.
                10. Each recommendation must be independently understandable and usable as a milestone.
                11. Do not include duplicate titles or milestones with substantially overlapping purposes.
                12. Return every dueDateTime as a UTC ISO-8601 value ending in Z.
                13. Return all text fields in English.
                14. Return only valid JSON.
                15. Do not include Markdown, code fences, comments, or explanations outside the JSON.

                [Required JSON format]
                {
                  "recommendations": [
                    {
                      "title": "Complete initial research",
                      "dueDateTime": "2026-08-15T09:00:00Z",
                      "description": "Complete the necessary research and summarize the key findings."
                    }
                  ]
                }
                """.formatted(
                projectType,
                startDateTime,
                dueDateTime,
                MAX_RECOMMENDATIONS
        );
    }
}
