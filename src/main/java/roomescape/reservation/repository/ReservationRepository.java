package roomescape.reservation.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import roomescape.reservation.entity.Reservation;

public interface ReservationRepository {

    Reservation save(Reservation reservation);

    Optional<Reservation> findById(Long id);

    Optional<Reservation> findByDateAndTimeIdAndThemeId(LocalDate date, Long timeId, Long themeId);

    Optional<Reservation> findByStoreIdAndDateAndTimeIdAndThemeId(Long storeId, LocalDate date, Long timeId, Long themeId);

    List<Reservation> findAll();

    List<Reservation> findByMemberId(Long memberId);

    List<Reservation> findByStoreId(Long storeId);

    Reservation update(Reservation reservation);

    void deleteById(Long id);

}
