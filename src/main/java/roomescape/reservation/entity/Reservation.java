package roomescape.reservation.entity;

import java.time.LocalDate;
import java.util.Objects;
import roomescape.reservationtime.entity.ReservationTime;
import roomescape.theme.entity.Theme;

public class Reservation {

    private final Long id;
    private final Long memberId;
    private final LocalDate date;
    private final ReservationTime time;
    private final Theme theme;

    private Reservation(Long memberId, LocalDate date, ReservationTime time, Theme theme) {
        this(null, memberId, date, time, theme);
    }

    private Reservation(Long id, Long memberId, LocalDate date, ReservationTime time, Theme theme) {
        validate(memberId, date, time, theme);
        this.id = id;
        this.memberId = memberId;
        this.date = date;
        this.time = time;
        this.theme = theme;
    }

    private void validate(Long memberId, LocalDate date, ReservationTime time, Theme theme) {
        if (memberId == null) {
            throw new IllegalArgumentException("예약자 회원 id는 필수입니다.");
        }
        if (date == null) {
            throw new IllegalArgumentException("예약 날짜는 필수입니다.");
        }
        if (time == null) {
            throw new IllegalArgumentException("예약 시간은 필수입니다.");
        }
        if (theme == null) {
            throw new IllegalArgumentException("예약 테마는 필수입니다.");
        }
    }

    public static Reservation of(Long memberId, LocalDate date, ReservationTime time, Theme theme) {
        return new Reservation(memberId, date, time, theme);
    }

    public static Reservation of(Long id, Long memberId, LocalDate date, ReservationTime time, Theme theme) {
        return new Reservation(id, memberId, date, time, theme);
    }

    public static Reservation toEntity(Reservation reservation, Long id) {
        return new Reservation(id, reservation.memberId, reservation.date, reservation.time, reservation.theme);
    }

    public Long getId() {
        return id;
    }

    public Long getMemberId() {
        return memberId;
    }

    public LocalDate getDate() {
        return date;
    }

    public ReservationTime getTime() {
        return time;
    }

    public Theme getTheme() {
        return theme;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Reservation that = (Reservation) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
