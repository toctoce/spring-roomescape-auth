package roomescape.member.repository;

import java.util.Optional;
import javax.sql.DataSource;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import roomescape.member.entity.Member;
import roomescape.member.exception.MemberDuplicatedException;

@Repository
public class JdbcMemberRepository implements MemberRepository {

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    private final SimpleJdbcInsert jdbcInsert;

    public JdbcMemberRepository(NamedParameterJdbcTemplate namedParameterJdbcTemplate, DataSource source) {
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
        this.jdbcInsert = new SimpleJdbcInsert(source)
                .withTableName("members")
                .usingGeneratedKeyColumns("id");
    }

    public static final RowMapper<Member> MEMBER_ROW_MAPPER = (rs, rowNum) ->
            Member.of(
                    rs.getLong("id"),
                    rs.getString("name"),
                    rs.getString("email"),
                    rs.getString("password")
            );

    @Override
    public Member save(Member member) {
        SqlParameterSource params = new MapSqlParameterSource()
                .addValue("name", member.getName())
                .addValue("email", member.getEmail())
                .addValue("password", member.getPassword());
        try {
            Long id = jdbcInsert.executeAndReturnKey(params).longValue();
            return Member.toEntity(member, id);
        } catch (DataIntegrityViolationException e) {
            throw new MemberDuplicatedException(member.getEmail());
        }
    }

    @Override
    public Optional<Member> findById(Long id) {
        String sql = "SELECT id, name, email, password FROM members WHERE id = :id";
        SqlParameterSource params = new MapSqlParameterSource()
                .addValue("id", id);
        try {
            Member member = namedParameterJdbcTemplate.queryForObject(sql, params, MEMBER_ROW_MAPPER);
            return Optional.ofNullable(member);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public Optional<Member> findByEmail(String email) {
        String sql = "SELECT id, name, email, password FROM members WHERE email = :email";
        SqlParameterSource params = new MapSqlParameterSource()
                .addValue("email", email);
        try {
            Member member = namedParameterJdbcTemplate.queryForObject(sql, params, MEMBER_ROW_MAPPER);
            return Optional.ofNullable(member);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public boolean existsByEmail(String email) {
        String sql = "SELECT COUNT(*) FROM members WHERE email = :email";
        SqlParameterSource params = new MapSqlParameterSource()
                .addValue("email", email);
        Integer count = namedParameterJdbcTemplate.queryForObject(sql, params, Integer.class);
        return count != null && count > 0;
    }
}
