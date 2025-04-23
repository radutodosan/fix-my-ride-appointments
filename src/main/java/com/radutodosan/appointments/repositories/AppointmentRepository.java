package com.radutodosan.appointments.repositories;

import com.radutodosan.appointments.entities.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
    List<Appointment> findByUserUsername(String username);
    List<Appointment> findByMechanicUsername(String username);

    boolean existsByMechanicUsernameAndAppointmentDateAndAppointmentTime(String mechanicUsername, String date, String time);
}
