package roomescape.store.repository;

import java.util.Optional;
import javax.sql.DataSource;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import roomescape.store.entity.Store;

@Repository
public class JdbcStoreRepository implements StoreRepository {

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    private final SimpleJdbcInsert jdbcInsert;

    public JdbcStoreRepository(NamedParameterJdbcTemplate namedParameterJdbcTemplate, DataSource source) {
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
        this.jdbcInsert = new SimpleJdbcInsert(source)
                .withTableName("stores")
                .usingGeneratedKeyColumns("id");
    }

    public static final RowMapper<Store> STORE_ROW_MAPPER = (rs, rowNum) ->
            Store.of(
                    rs.getLong("id"),
                    rs.getString("name")
            );

    @Override
    public Store save(Store store) {
        SqlParameterSource params = new MapSqlParameterSource()
                .addValue("name", store.getName());
        Long id = jdbcInsert.executeAndReturnKey(params).longValue();
        return Store.toEntity(store, id);
    }

    @Override
    public Optional<Store> findById(Long id) {
        String sql = "SELECT id, name FROM stores WHERE id = :id";
        SqlParameterSource params = new MapSqlParameterSource()
                .addValue("id", id);
        try {
            Store store = namedParameterJdbcTemplate.queryForObject(sql, params, STORE_ROW_MAPPER);
            return Optional.ofNullable(store);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }
}
