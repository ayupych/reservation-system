package example.reservation_system;

import java.time.LocalDate;
public record Reservation(
        Long id,
        Long UserId,
        Long roomId,
        LocalDate startDate,
        LocalDate endDate,
        ReservationStatus status) {
    }

