package roomescape.auth.exception;

public class LoginRequiredException extends AuthenticationException {

    public LoginRequiredException() {
        super("로그인이 필요합니다.");
    }
}
