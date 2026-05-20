package roomescape.auth.support;

import java.util.Optional;
import org.springframework.stereotype.Component;
import roomescape.auth.exception.LoginRequiredException;

@Component
public class AuthorizationExtractor {

    private static final String BEARER_PREFIX = "Bearer ";

    public Optional<String> extractBearerToken(String authorizationHeader) {
        if (authorizationHeader == null || authorizationHeader.isBlank()) {
            return Optional.empty();
        }

        if (!authorizationHeader.startsWith(BEARER_PREFIX)) {
            throw new LoginRequiredException();
        }

        String token = authorizationHeader.substring(BEARER_PREFIX.length());
        if (token.isBlank()) {
            throw new LoginRequiredException();
        }

        return Optional.of(token);
    }
}
