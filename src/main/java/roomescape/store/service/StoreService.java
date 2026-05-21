package roomescape.store.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.store.entity.Store;
import roomescape.store.exception.StoreNotFoundException;
import roomescape.store.repository.StoreRepository;

@Service
public class StoreService {

    private final StoreRepository storeRepository;

    public StoreService(StoreRepository storeRepository) {
        this.storeRepository = storeRepository;
    }

    @Transactional(readOnly = true)
    public Store findById(Long id) {
        return storeRepository.findById(id)
                .orElseThrow(() -> new StoreNotFoundException(id));
    }
}
