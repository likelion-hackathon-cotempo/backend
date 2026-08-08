package cultureland.hackathon.member.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class SignupRequestDto {
    @NotBlank(message = "Name is required.")
    @Size(max = 30, message = "Name must not exceed 30 characters.")
    private String name;

    @NotBlank(message = "Email is required.")
    @Email(message = "Please enter a valid email address.")
    @Size(max = 100, message = "Email must not exceed 100 characters.")
    private String email;

    @NotBlank(message = "Password is required.")
    private String password;

    @NotBlank(message = "Country is required.")
    @Pattern(
            regexp = "^[A-Z]{2}$",
            message = "Country must be a two-letter country code."
    )
    private String country;

    @NotBlank(message = "Timezone is required.")
    @Size(max = 50, message = "Timezone must not exceed 50 characters.")
    private String timezone;
}
