package roomescape.reservation.payload;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public record ReservationRequest(
        @NotNull
        LocalDate date,
        @NotNull
        Long timeId,
        @NotNull
        Long themeId,
        Long storeId
) {
    public ReservationRequest(LocalDate date, Long timeId, Long themeId) {
        this(date, timeId, themeId, null);
    }
}
