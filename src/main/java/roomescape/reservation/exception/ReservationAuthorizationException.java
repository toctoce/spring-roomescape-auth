package roomescape.reservation.exception;

import roomescape.common.exception.AccessDeniedException;

public class ReservationAuthorizationException extends AccessDeniedException {

    public ReservationAuthorizationException() {
        super("접근 권한이 없습니다.");
    }
}
