package roomescape.auth.payload;

public record MobileLoginResponse(
        String accessToken,
        String tokenType,
        long expiresIn
) {
}
