package roomescape.reservation.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.reservation.entity.Reservation;
import roomescape.reservation.exception.PastReservationNotAllowedException;
import roomescape.reservation.exception.ReservationAccessDeniedException;
import roomescape.reservation.exception.ReservationDuplicatedException;
import roomescape.reservation.exception.ReservationNotFoundException;
import roomescape.reservation.payload.ReservationRequest;
import roomescape.reservation.payload.ReservationUpdateRequest;
import roomescape.reservation.repository.ReservationRepository;
import roomescape.reservationtime.entity.ReservationTime;
import roomescape.reservationtime.exception.ReservationTimeNotFoundException;
import roomescape.reservationtime.repository.ReservationTimeRepository;
import roomescape.theme.entity.Theme;
import roomescape.theme.exception.ThemeNotFoundException;
import roomescape.theme.repository.ThemeRepository;

@Service
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final ReservationTimeRepository reservationTimeRepository;
    private final ThemeRepository themeRepository;

    public ReservationService(ReservationRepository reservationRepository,
                              ReservationTimeRepository reservationTimeRepository,
                              ThemeRepository themeRepository) {
        this.reservationRepository = reservationRepository;
        this.reservationTimeRepository = reservationTimeRepository;
        this.themeRepository = themeRepository;
    }

    @Transactional
    public Reservation save(Long memberId, ReservationRequest request) {
        Reservation reservation = createReservation(
                null,
                memberId,
                request.timeId(),
                request.themeId(),
                request.date());
        return reservationRepository.save(reservation);
    }

    private boolean isPastDateTime(LocalDateTime localDateTime) {
        LocalDateTime now = LocalDateTime.now();
        return localDateTime.isBefore(now);
    }

    @Transactional(readOnly = true)
    public List<Reservation> findAll() {
        return reservationRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<Reservation> findByMemberId(Long memberId) {
        return reservationRepository.findByMemberId(memberId);
    }

    @Transactional
    public void deleteById(Long id) {
        reservationRepository.deleteById(id);
    }

    @Transactional
    public void cancelByIdAndMemberId(Long id, Long memberId) {
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new ReservationNotFoundException(id));
        validateOwner(reservation, memberId);
        reservationRepository.deleteById(id);
    }

    @Transactional
    public Reservation updateByIdAndMemberId(Long id, Long memberId, ReservationUpdateRequest request) {
        if (request.isEmpty()) {
            throw new IllegalArgumentException("변경할 예약 정보가 없습니다.");
        }
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new ReservationNotFoundException(id));
        validateOwner(reservation, memberId);

        LocalDate date = request.date() == null ? reservation.getDate() : request.date();
        Long timeId = request.timeId() == null ? reservation.getTime().getId() : request.timeId();
        Long themeId = request.themeId() == null ? reservation.getTheme().getId() : request.themeId();

        Reservation updatedReservation = createReservation(
                id,
                memberId,
                timeId,
                themeId,
                date);

        return reservationRepository.update(updatedReservation);
    }

    private void validateOwner(Reservation reservation, Long memberId) {
        if (!reservation.getMemberId().equals(memberId)) {
            throw new ReservationAccessDeniedException();
        }
    }

    private Reservation createReservation(Long id, Long memberId, Long timeId, Long themeId, LocalDate date) {
        ReservationTime reservationTime = reservationTimeRepository.findById(timeId)
                .orElseThrow(() -> new ReservationTimeNotFoundException(timeId));
        Theme theme = themeRepository.findById(themeId)
                .orElseThrow(() -> new ThemeNotFoundException(themeId));

        LocalDateTime localDateTime = LocalDateTime.of(date, reservationTime.getStartAt());
        if (isPastDateTime(localDateTime)) {
            throw new PastReservationNotAllowedException();
        }

        reservationRepository.findByDateAndTimeIdAndThemeId(date, timeId, themeId)
                .ifPresent(reservation -> {
                    throw new ReservationDuplicatedException(date, timeId, themeId);
                });

        return Reservation.of(
                id,
                memberId,
                date,
                reservationTime,
                theme);
    }
}
