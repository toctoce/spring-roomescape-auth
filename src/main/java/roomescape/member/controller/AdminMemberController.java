package roomescape.member.controller;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import roomescape.member.entity.Member;
import roomescape.member.payload.ManagerCreateRequest;
import roomescape.member.payload.MemberResponse;
import roomescape.member.service.MemberService;

@RestController
@RequestMapping("/admin/members")
public class AdminMemberController {

    private final MemberService memberService;

    public AdminMemberController(MemberService memberService) {
        this.memberService = memberService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public MemberResponse postManager(@Valid @RequestBody ManagerCreateRequest request) {
        Member member = memberService.saveManager(request);
        return MemberResponse.from(member);
    }
}
