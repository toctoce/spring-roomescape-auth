package roomescape.member.exception;

import roomescape.common.exception.NotFoundException;

public class MemberNotFoundException extends NotFoundException {

    public MemberNotFoundException(Long id) {
        super("존재하지 않는 회원입니다. id=" + id);
    }
}
