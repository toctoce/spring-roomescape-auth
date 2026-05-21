package roomescape.store.entity;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;

class StoreTest {

    @Test
    void 매장_이름이_비어있으면_검증에_실패한다() {
        assertThatThrownBy(() -> Store.of(""))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
