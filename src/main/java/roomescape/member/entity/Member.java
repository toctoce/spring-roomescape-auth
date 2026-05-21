package roomescape.member.entity;

import java.util.Objects;

public class Member {

    private final Long id;
    private final String name;
    private final String email;
    private final String password;
    private final MemberRole role;
    private final Long storeId;

    private Member(String name, String email, String password) {
        this(null, name, email, password, MemberRole.USER, null);
    }

    private Member(Long id, String name, String email, String password) {
        this(id, name, email, password, MemberRole.USER, null);
    }

    private Member(Long id, String name, String email, String password, MemberRole role, Long storeId) {
        validate(name, email, password);
        this.id = id;
        this.name = name;
        this.email = email;
        this.password = password;
        this.role = role;
        this.storeId = storeId;
    }

    private void validate(String name, String email, String password) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("회원 이름은 필수입니다.");
        }
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("회원 이메일은 필수입니다.");
        }
        if (password == null || password.isBlank()) {
            throw new IllegalArgumentException("회원 비밀번호는 필수입니다.");
        }
    }

    public static Member of(String name, String email, String password) {
        return new Member(name, email, password);
    }

    public static Member of(Long id, String name, String email, String password) {
        return new Member(id, name, email, password);
    }

    public static Member of(Long id, String name, String email, String password, MemberRole role, Long storeId) {
        return new Member(id, name, email, password, role, storeId);
    }

    public static Member toEntity(Member member, Long id) {
        return new Member(id, member.name, member.email, member.password, member.role, member.storeId);
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public MemberRole getRole() {
        return role;
    }

    public Long getStoreId() {
        return storeId;
    }

    public boolean isManager() {
        return role == MemberRole.MANAGER;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Member member = (Member) o;
        return Objects.equals(id, member.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
