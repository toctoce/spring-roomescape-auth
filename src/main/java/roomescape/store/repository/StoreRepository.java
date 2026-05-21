package roomescape.store.repository;

import java.util.Optional;
import roomescape.store.entity.Store;

public interface StoreRepository {

    Store save(Store store);

    Optional<Store> findById(Long id);
}
