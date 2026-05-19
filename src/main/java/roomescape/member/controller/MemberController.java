package roomescape.member.controller;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import roomescape.auth.support.LoginMember;
import roomescape.auth.support.LoginMemberInfo;
import roomescape.member.entity.Member;
import roomescape.member.payload.MemberCreateRequest;
import roomescape.member.payload.MemberResponse;
import roomescape.member.service.MemberService;

@RestController
@RequestMapping("/members")
public class MemberController {

    private final MemberService memberService;

    public MemberController(MemberService memberService) {
        this.memberService = memberService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public MemberResponse postMember(@Valid @RequestBody MemberCreateRequest request) {
        Member member = memberService.save(request);
        return MemberResponse.from(member);
    }

    @GetMapping("/me")
    @ResponseStatus(HttpStatus.OK)
    public MemberResponse getMe(@LoginMember LoginMemberInfo loginMember) {
        return new MemberResponse(loginMember.id(), loginMember.name(), loginMember.email());
    }
}
