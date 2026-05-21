package roomescape.member.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.member.entity.Member;
import roomescape.member.entity.MemberRole;
import roomescape.member.exception.MemberDuplicatedException;
import roomescape.member.exception.MemberNotFoundException;
import roomescape.member.payload.ManagerCreateRequest;
import roomescape.member.payload.MemberCreateRequest;
import roomescape.member.repository.MemberRepository;
import roomescape.store.service.StoreService;

@Service
public class MemberService {

    private final MemberRepository memberRepository;
    private final StoreService storeService;

    public MemberService(MemberRepository memberRepository, StoreService storeService) {
        this.memberRepository = memberRepository;
        this.storeService = storeService;
    }

    @Transactional
    public Member save(MemberCreateRequest request) {
        validateDuplicatedEmail(request.email());
        Member member = Member.of(null, request.name(), request.email(), request.password(), MemberRole.USER, null);
        return memberRepository.save(member);
    }

    @Transactional
    public Member saveManager(ManagerCreateRequest request) {
        validateDuplicatedEmail(request.email());
        storeService.findById(request.storeId());
        Member member = Member.of(
                null,
                request.name(),
                request.email(),
                request.password(),
                MemberRole.MANAGER,
                request.storeId()
        );
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
