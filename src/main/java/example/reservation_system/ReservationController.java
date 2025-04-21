package example.reservation_system;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
        return reservationService.findAllReservation();
    }

    @PostMapping("/reservations")
    public ResponseEntity<Reservation> createReservation(@RequestBody Reservation reservationToCreate) {
        log.info("createReservation new");
        return ResponseEntity.status(HttpStatus.CREATED).body(reservationService.createReservation(reservationToCreate));
    }

    @DeleteMapping("/reservations/{id}")
    public ResponseEntity<String > deleteReservationById(@PathVariable Long id) {
        log.info("deleteReservationById " + id);
        reservationService.deleteReservationById(id);
        return ResponseEntity.status(HttpStatus.OK).body("Your reservation has been deleted");
    }

    @PutMapping("/reservations/{id}")
    public ResponseEntity<Reservation> updateReservation(@PathVariable Long id, @RequestBody Reservation reservationToUpdate) {
        log.info("called updateReservation: " + id + reservationToUpdate.toString());
        return ResponseEntity.status(HttpStatus.OK).body(reservationService.update(id, reservationToUpdate));
    }

    @PostMapping("/{id}/approve")
    public ResponseEntity<Reservation> approveReservation(@PathVariable Long id) {
        log.info("approveReservation: " + id);
        Reservation reservation = reservationService.approveReservation(id);
        return ResponseEntity.ok(reservation);
    }
}
