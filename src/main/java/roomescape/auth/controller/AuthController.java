package roomescape.auth.controller;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import roomescape.auth.payload.LoginRequest;
import roomescape.auth.service.AuthService;
import roomescape.member.entity.Member;
import roomescape.member.payload.MemberResponse;

@RestController
public class AuthController {

    public static final String LOGIN_MEMBER_ID = "loginMemberId";

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    @ResponseStatus(HttpStatus.OK)
    public MemberResponse login(@Valid @RequestBody LoginRequest request, HttpSession session) {
        Member member = authService.login(request);
        session.setAttribute(LOGIN_MEMBER_ID, member.getId());
        return MemberResponse.from(member);
    }

    @PostMapping("/logout")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void logout(HttpSession session) {
        session.invalidate();
    }
}
