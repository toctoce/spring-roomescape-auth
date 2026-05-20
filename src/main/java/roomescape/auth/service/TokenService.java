package roomescape.auth.service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Service;
import roomescape.auth.exception.LoginRequiredException;
import roomescape.auth.payload.MobileLoginResponse;

@Service
public class TokenService {

    private static final String TOKEN_TYPE = "Bearer";
    private static final Duration TOKEN_EXPIRATION = Duration.ofHours(1);

    private final Map<String, TokenAuthentication> tokens = new ConcurrentHashMap<>();

    public MobileLoginResponse issue(Long memberId) {
        String accessToken = UUID.randomUUID().toString();
        LocalDateTime expiresAt = LocalDateTime.now().plus(TOKEN_EXPIRATION);
        tokens.put(accessToken, new TokenAuthentication(memberId, expiresAt));
        return new MobileLoginResponse(accessToken, TOKEN_TYPE, TOKEN_EXPIRATION.toSeconds());
    }

    public Optional<Long> findMemberId(String accessToken) {
        if (accessToken == null || accessToken.isBlank()) {
            return Optional.empty();
        }

        TokenAuthentication authentication = tokens.get(accessToken);
        if (authentication == null) {
            throw new LoginRequiredException();
        }

        if (authentication.isExpired()) {
            tokens.remove(accessToken);
            throw new LoginRequiredException();
        }

        return Optional.of(authentication.memberId());
    }

    private record TokenAuthentication(
            Long memberId,
            LocalDateTime expiresAt
    ) {

        private boolean isExpired() {
            return expiresAt.isBefore(LocalDateTime.now());
        }
    }
}
