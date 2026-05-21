package roomescape.store.exception;

import roomescape.common.exception.NotFoundException;

public class StoreNotFoundException extends NotFoundException {

    public StoreNotFoundException(Long id) {
        super("존재하지 않는 매장입니다. id=" + id);
    }
}
