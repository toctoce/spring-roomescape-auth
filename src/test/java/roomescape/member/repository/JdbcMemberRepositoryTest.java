package roomescape.member.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import roomescape.member.entity.Member;
import roomescape.member.exception.MemberDuplicatedException;

@Sql(statements = "DELETE FROM members")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
class JdbcMemberRepositoryTest {

    @Autowired
    private MemberRepository memberRepository;

    @Test
    void 회원을_저장한다() {
        Member member = Member.of("사용자", "member@example.com", "password");

        Member savedMember = memberRepository.save(member);

        assertThat(savedMember.getId()).isPositive();
        assertThat(savedMember.getName()).isEqualTo("사용자");
        assertThat(savedMember.getEmail()).isEqualTo("member@example.com");
        assertThat(savedMember.getPassword()).isEqualTo("password");
    }

    @Test
    void 이메일로_회원을_조회한다() {
        Member member = memberRepository.save(Member.of("사용자", "member@example.com", "password"));

        Member foundMember = memberRepository.findByEmail("member@example.com")
                .orElseThrow();

        assertThat(foundMember).isEqualTo(member);
        assertThat(foundMember.getName()).isEqualTo("사용자");
        assertThat(foundMember.getEmail()).isEqualTo("member@example.com");
    }

    @Test
    void 가입된_이메일인지_확인한다() {
        memberRepository.save(Member.of("사용자", "member@example.com", "password"));

        boolean exists = memberRepository.existsByEmail("member@example.com");

        assertThat(exists).isTrue();
    }

    @Test
    void 이미_가입된_이메일로_저장하면_에러를_던진다() {
        memberRepository.save(Member.of("사용자", "member@example.com", "password"));

        assertThatThrownBy(() -> memberRepository.save(Member.of("다른사용자", "member@example.com", "password")))
                .isInstanceOf(MemberDuplicatedException.class);
    }
}
