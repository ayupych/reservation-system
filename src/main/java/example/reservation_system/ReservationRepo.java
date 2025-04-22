package example.reservation_system;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReservationRepo extends JpaRepository<ReservationEntity, Long> {

    @Query("SELECT r FROM ReservationEntity r WHERE r.id > :id")
    List<ReservationEntity> findReservationsWithIdGreaterThan(@Param("id") Long id);

    @Query("SELECT r FROM ReservationEntity r WHERE r.status = PENDING")
    List<ReservationEntity> findAllReservationsWithStatusPending();

    @Modifying
    @Query("""
            update ReservationEntity r set r.status= :status
            where r.id =:id
            """)
    void setStatus(@Param("id") Long id,
                   @Param("status") ReservationStatus reservationStatus);
}
