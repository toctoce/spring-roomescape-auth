package roomescape.store.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import roomescape.store.entity.Store;
import roomescape.store.exception.StoreNotFoundException;
import roomescape.store.repository.StoreRepository;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
class StoreServiceTest {

    @Autowired
    private StoreService storeService;

    @Autowired
    private StoreRepository storeRepository;

    @Test
    void 매장을_조회한다() {
        Store store = storeRepository.save(Store.of("잠실점"));

        Store foundStore = storeService.findById(store.getId());

        assertThat(foundStore).isEqualTo(store);
    }

    @Test
    void 존재하지_않는_매장을_조회하면_예외를_던진다() {
        assertThatThrownBy(() -> storeService.findById(999L))
                .isInstanceOf(StoreNotFoundException.class);
    }
}
