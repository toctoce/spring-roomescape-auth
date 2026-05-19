package roomescape.auth.support;

import static roomescape.auth.controller.AuthController.LOGIN_MEMBER_ID;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import roomescape.auth.exception.LoginRequiredException;
import roomescape.member.entity.Member;
import roomescape.member.service.MemberService;

@Component
public class LoginMemberArgumentResolver implements HandlerMethodArgumentResolver {

    private final MemberService memberService;

    public LoginMemberArgumentResolver(MemberService memberService) {
        this.memberService = memberService;
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

        HttpSession session = request.getSession(false);
        if (session == null) {
            throw new LoginRequiredException();
        }

        Object memberId = session.getAttribute(LOGIN_MEMBER_ID);
        if (!(memberId instanceof Long id)) {
            throw new LoginRequiredException();
        }

        return memberService.findById(id);
    }
}
