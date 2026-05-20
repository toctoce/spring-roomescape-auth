package roomescape.auth.controller;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import roomescape.auth.payload.LoginRequest;
import roomescape.auth.payload.MobileLoginResponse;
import roomescape.auth.service.AuthService;
import roomescape.auth.service.TokenService;
import roomescape.member.entity.Member;

@RestController
@RequestMapping("/mobile")
public class MobileAuthController {

    private final AuthService authService;
    private final TokenService tokenService;

    public MobileAuthController(AuthService authService, TokenService tokenService) {
        this.authService = authService;
        this.tokenService = tokenService;
    }

    @PostMapping("/login")
    @ResponseStatus(HttpStatus.OK)
    public MobileLoginResponse login(@Valid @RequestBody LoginRequest request) {
        Member member = authService.login(request);
        return tokenService.issue(member.getId());
    }
}
