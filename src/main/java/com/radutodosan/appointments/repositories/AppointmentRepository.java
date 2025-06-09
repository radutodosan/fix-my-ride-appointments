package com.radutodosan.appointments.repositories;

import com.radutodosan.appointments.entities.Appointment;
import com.radutodosan.appointments.enums.AppointmentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
    List<Appointment> findByClientUsername(String username);
    List<Appointment> findByMechanicUsername(String username);

    @Query("""
    SELECT CASE WHEN COUNT(a) = 0 THEN true ELSE false END
    FROM Appointment a
    WHERE a.mechanicUsername = :mechanicUsername
      AND a.appointmentDate = :date
      AND a.status IN (:blockingStatuses)
""")
    boolean isSlotAvailable(
            @Param("mechanicUsername") String mechanicUsername,
            @Param("date") String date,
            @Param("blockingStatuses") Collection<AppointmentStatus> blockingStatuses
    );

}
