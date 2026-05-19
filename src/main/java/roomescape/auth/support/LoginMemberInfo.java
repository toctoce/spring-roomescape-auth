package roomescape.auth.support;

import roomescape.member.entity.Member;

public record LoginMemberInfo(
        Long id,
        String name,
        String email
) {
    public static LoginMemberInfo from(Member member) {
        return new LoginMemberInfo(member.getId(), member.getName(), member.getEmail());
    }
}
