package roomescape.member.exception;

import roomescape.common.exception.DuplicatedException;

public class MemberDuplicatedException extends DuplicatedException {

    public MemberDuplicatedException(String email) {
        super("이미 가입된 이메일입니다. email = " + email);
    }
}
