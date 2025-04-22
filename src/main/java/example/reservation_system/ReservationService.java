package example.reservation_system;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class ReservationService {

    private final AtomicLong idCounter;
    private final ReservationRepo repository;

    public ReservationService(ReservationRepo reservationRepo) {
        this.repository = reservationRepo;
        idCounter = new AtomicLong();
    }

    public Reservation getById(Long id) {
        ReservationEntity reservationEntity = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Not found reservation by id = " + id));
        return toDomainReservation(reservationEntity);
    }

    public List<Reservation> findAllReservation() {
        List<ReservationEntity> allEntities = repository.findAll();
        return allEntities.stream().
                map(this::toDomainReservation).
                toList();
    }

    public Reservation createReservation(Reservation reservationToCreate) {
        if (reservationToCreate.id() != null || reservationToCreate.status() != null) {
            throw new IllegalArgumentException("Reservation id should be ");
        }
        var entityToSave = new ReservationEntity(
                null,
                reservationToCreate.UserId(),
                reservationToCreate.roomId(),
                reservationToCreate.startDate(),
                reservationToCreate.endDate(),
                ReservationStatus.PENDING);
        ReservationEntity savedReservation = repository.save(entityToSave);
        return toDomainReservation(savedReservation);
    }


    public Reservation update(Long id, Reservation reservationToUpdate) {
        var reservationEntity = repository.findById(id).
                orElseThrow(() -> new EntityNotFoundException("Not found reservation by id = " + id));
        if (reservationEntity.getStatus() != ReservationStatus.PENDING) {
            throw new IllegalStateException("Cannot modify reservation: status = " + reservationEntity.getStatus());
        }
        var reservationToSave = new ReservationEntity(
                reservationEntity.getId(),
                reservationToUpdate.UserId(),
                reservationToUpdate.roomId(),
                reservationToUpdate.startDate(),
                reservationToUpdate.endDate(),
                ReservationStatus.PENDING);
        ReservationEntity updatedReservation = repository.save(reservationToSave);
        return toDomainReservation(updatedReservation);
    }

    public void deleteReservationById(Long id) {
        if (!repository.existsById(id)) {
            throw new EntityNotFoundException("Not found reservation by id:" + id);
        }
        repository.deleteById(id);
    }


    public Reservation approveReservation(Long id) {
        ReservationEntity reservationEntity = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Not found reservation by od = " + id));
        if (reservationEntity.getStatus() != ReservationStatus.PENDING) {
            throw new IllegalStateException("Cannot approve reservation: status = " + reservationEntity.getStatus());
        }
        var isConflict = isReservationConflict(reservationEntity);
        if (isConflict) {
            throw new IllegalStateException("Cannot approve reservation: status = " + reservationEntity.getStatus());
        }
        reservationEntity.setStatus(ReservationStatus.APPROVED);
        repository.save(reservationEntity);
        return toDomainReservation(reservationEntity);
    }

    private boolean isReservationConflict(
            ReservationEntity reservation) {
        var allReservations = repository.findAll();
        for (ReservationEntity existingReservation : allReservations) {
            if (reservation.getId().equals(existingReservation.getId())) {
                continue;
            }
            if (!reservation.getRoomId().equals(existingReservation.getRoomId())) {
                continue;
            }
            if (!existingReservation.getStatus().equals(ReservationStatus.APPROVED)) {
                continue;
            }
            if (reservation.getStartDate().isBefore(existingReservation.getEndDate()) && existingReservation.getStartDate().isAfter(reservation.getEndDate())) {
                return true;
            }
        }
        return false;
    }

    private Reservation toDomainReservation(
            ReservationEntity reservation
    ) {
        return new Reservation(
                reservation.getId(),
                reservation.getUserId(),
                reservation.getRoomId(),
                reservation.getStartDate(),
                reservation.getEndDate(),
                reservation.getStatus()
        );
    }

    public List<Reservation> findAllReservationWhereIdMore5() {
        List<ReservationEntity> allEntities = repository.findReservationsWithIdGreaterThan(5L);
        return allEntities.stream().
                map(this::toDomainReservation).
                toList();
    }

    public List<Reservation> findAllReservationsWithStatusPending() {
        List<ReservationEntity> allEntities = repository.findAllReservationsWithStatusPending();
        return allEntities.stream().
                map(this::toDomainReservation).
                toList();
    }

    @Transactional
    public void cancelReservationById(Long id) {
        if (!repository.existsById(id)) {
            throw new EntityNotFoundException("Not found reservation by id = " + id);
        }
        repository.setStatus(id, ReservationStatus.CANCELLED);
    }
}