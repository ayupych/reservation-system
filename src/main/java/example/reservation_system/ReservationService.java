package example.reservation_system;

import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
public class ReservationService {

    private final Map<Long, Reservation> reservationsMap = Map.of(
            1L,
            new Reservation(1L,
                    100L,
                    10L,
                    LocalDate.now(),
                    LocalDate.now().plusDays(5),
                    ReservationStatus.APPROVED),
            2L,
            new Reservation(2L,
                    200L,
                    20L,
                    LocalDate.now(),
                    LocalDate.now().plusDays(5),
                    ReservationStatus.APPROVED)
    );


    public Reservation getById(Long id) {
        if (!reservationsMap.containsKey(id)) {
            throw new NoSuchElementException("No found reservation by id: " + id);
        }
        return reservationsMap.get(id);
    }

    public List<Reservation> getAllReservations() {
        return reservationsMap.values().stream().toList();
    }
}
