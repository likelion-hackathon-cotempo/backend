package cultureland.hackathon.teamEvent.prompt;

public class MeetingRecommendationPrompt {

    private static final int MAX_RECOMMENDATIONS = 3;

    private final class MeetingRecommendationPrompt() {
    }

    public static String create(String candidatesJson) {
        return """
                    You are an assistant that selects suitable meeting times for a global team.
                    
                    The backend has already generated and ranked the available meeting candidates.
                    Select the most suitable candidates by considering each member's local date and time, personal schedule conflicts, and total conflict score.
                    
                    [Selection rules]
                    1. Select no more than %d candidates.
                    2. Select candidates only from the provided candidate list.
                    3. Never create, modify, infer, or suggest a new meeting time.
                    4. Return each selected candidate using its original candidateId.
                    5. Do not return the same candidateId more than once.
                    6. Prefer candidates with a lower totalConflictScore.
                    7. Consider every member's localStartDateTime and localEndDateTime.
                    8. Avoid candidates that are too early in the morning or too late at night for any team member.
                    9. Prefer candidates that start at or after 08:00 and end by 22:00 in each member's local timezone.
                    10. Consider a member less available when hasConflict is true.
                    11. Use conflictScore to understand how strongly the candidate conflicts with each member's personal schedule.
                    12. Arrange the selected candidates from most recommended to least recommended.
                    13. Write a short English description explaining why each candidate was selected.
                    14. The description must be based only on the provided candidate information.
                    15. Return only valid JSON.
                    16. Do not include Markdown, code fences, comments, or text outside the JSON.
                    
                    [Candidate data]
                    %s
                    
                    [Required JSON format]
                    {
                        "recommendations": [
                            {
                                "candidateId": 1,
                                "description": "Everyone is available and the meeting falls within comfortable local hours."
                            },
                            {
                                "candidateId": 2,
                                "description": "Most members are available, but one member has a low-priority personal schedule conflict."
                            }
                        ]
                    }
                    """.formatted(
                MAX_RECOMMENDATIONS,
                candidatesJson
        );
    }
}
