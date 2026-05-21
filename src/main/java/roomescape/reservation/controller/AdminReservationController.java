package roomescape.reservation.controller;

import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import roomescape.auth.support.LoginMember;
import roomescape.member.entity.Member;
import roomescape.reservation.payload.ReservationResponse;
import roomescape.reservation.payload.ReservationUpdateRequest;
import roomescape.reservation.service.ReservationService;

@RestController
@RequestMapping("/admin/reservations")
public class AdminReservationController {

    private final ReservationService reservationService;

    public AdminReservationController(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<ReservationResponse> getAllReservations(@LoginMember Member member) {
        return reservationService.findByManager(member)
                .stream()
                .map(ReservationResponse::from)
                .toList();
    }

    @PatchMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ReservationResponse updateReservation(@LoginMember Member member,
                                                 @PathVariable Long id,
                                                 @RequestBody ReservationUpdateRequest request) {
        return ReservationResponse.from(reservationService.updateByIdAndManager(id, member, request));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteReservation(@LoginMember Member member, @PathVariable Long id) {
        reservationService.deleteByIdAndManager(id, member);
    }
}
