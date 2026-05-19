package roomescape.auth.exception;

public class AuthenticationException extends RuntimeException {

    public AuthenticationException() {
        super("이메일 또는 비밀번호가 올바르지 않습니다.");
    }

    private AuthenticationException(String message) {
        super(message);
    }

    public static AuthenticationException loginRequired() {
        return new AuthenticationException("로그인이 필요합니다.");
    }
}
