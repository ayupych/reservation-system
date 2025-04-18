package example.reservation_system;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api")
public class ReservationController {
    private ReservationService reservationService;

    @Autowired
    public ReservationController(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    @GetMapping("/reservations/{id}")
    public Reservation getReservationById(@PathVariable Long id) {
        log.info("getReservationById " + id);
        return reservationService.getById(id);
    }

    @GetMapping("/reservations")
    public List<Reservation> getAllReservations() {
        log.info("getAllReservations");
        return reservationService.getAllReservations();
    }

}
