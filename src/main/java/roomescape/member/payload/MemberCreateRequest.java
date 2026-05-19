package roomescape.member.payload;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record MemberCreateRequest(
        @NotBlank
        @Size(max = 10)
        String name,
        @NotBlank
        @Email
        String email,
        @NotBlank
        String password
) {
}
