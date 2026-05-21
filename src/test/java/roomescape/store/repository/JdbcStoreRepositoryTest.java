package roomescape.store.repository;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import roomescape.store.entity.Store;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
class JdbcStoreRepositoryTest {

    @Autowired
    private StoreRepository storeRepository;

    @Test
    void 매장을_저장하는_테스트() {
        Store store = storeRepository.save(Store.of("잠실점"));

        assertThat(store.getId()).isPositive();
        assertThat(store.getName()).isEqualTo("잠실점");
    }

    @Test
    void 매장을_조회하는_테스트() {
        Store store = storeRepository.save(Store.of("강남점"));

        assertThat(storeRepository.findById(store.getId()))
                .contains(store);
    }

    @Test
    void 존재하지_않는_매장을_조회하면_Empty를_반환한다() {
        assertThat(storeRepository.findById(999L)).isEmpty();
    }
}
