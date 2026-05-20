package roomescape.auth.support;

import static roomescape.auth.controller.AuthController.LOGIN_MEMBER_ID;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import java.util.Optional;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import roomescape.auth.exception.LoginRequiredException;
import roomescape.auth.service.TokenService;
import roomescape.member.entity.Member;
import roomescape.member.service.MemberService;

@Component
public class LoginMemberArgumentResolver implements HandlerMethodArgumentResolver {

    private static final String AUTHORIZATION = "Authorization";

    private final MemberService memberService;
    private final TokenService tokenService;
    private final AuthorizationExtractor authorizationExtractor;

    public LoginMemberArgumentResolver(MemberService memberService,
                                       TokenService tokenService,
                                       AuthorizationExtractor authorizationExtractor) {
        this.memberService = memberService;
        this.tokenService = tokenService;
        this.authorizationExtractor = authorizationExtractor;
    }

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        boolean hasAnnotation = parameter.hasParameterAnnotation(LoginMember.class);
        boolean isMemberType = Member.class.isAssignableFrom(parameter.getParameterType());

        return hasAnnotation && isMemberType;
    }

    @Override
    public Object resolveArgument(MethodParameter parameter,
                                  ModelAndViewContainer mavContainer,
                                  NativeWebRequest webRequest,
                                  WebDataBinderFactory binderFactory) {
        HttpServletRequest request = webRequest.getNativeRequest(HttpServletRequest.class);
        if (request == null) {
            throw new LoginRequiredException();
        }

        Long memberId = findSessionMemberId(request)
                .or(() -> findTokenMemberId(request))
                .orElseThrow(LoginRequiredException::new);

        return memberService.findById(memberId);
    }

    private Optional<Long> findSessionMemberId(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            return Optional.empty();
        }

        Object memberId = session.getAttribute(LOGIN_MEMBER_ID);
        if (memberId instanceof Long id) {
            return Optional.of(id);
        }

        return Optional.empty();
    }

    private Optional<Long> findTokenMemberId(HttpServletRequest request) {
        return authorizationExtractor.extractBearerToken(request.getHeader(AUTHORIZATION))
                .flatMap(tokenService::findMemberId);
    }
}
