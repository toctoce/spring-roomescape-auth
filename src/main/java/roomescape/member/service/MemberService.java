package roomescape.member.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.member.entity.Member;
import roomescape.member.exception.MemberDuplicatedException;
import roomescape.member.exception.MemberNotFoundException;
import roomescape.member.payload.MemberCreateRequest;
import roomescape.member.repository.MemberRepository;

@Service
public class MemberService {

    private final MemberRepository memberRepository;

    public MemberService(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    @Transactional
    public Member save(MemberCreateRequest request) {
        validateDuplicatedEmail(request.email());
        Member member = Member.of(request.name(), request.email(), request.password());
        return memberRepository.save(member);
    }

    private void validateDuplicatedEmail(String email) {
        if (memberRepository.existsByEmail(email)) {
            throw new MemberDuplicatedException(email);
        }
    }

    @Transactional(readOnly = true)
    public Member findById(Long id) {
        return memberRepository.findById(id)
                .orElseThrow(() -> new MemberNotFoundException(id));
    }
}
