package com.radutodosan.appointments.services;

import com.radutodosan.appointments.dtos.AppointmentRequestDTO;
import com.radutodosan.appointments.entities.Appointment;
import com.radutodosan.appointments.enums.AppointmentStatus;
import com.radutodosan.appointments.repositories.AppointmentRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;

    public AppointmentService(AppointmentRepository repository) {
        this.appointmentRepository = repository;
    }

    public Appointment createAppointment(AppointmentRequestDTO appointmentRequest, String userUsername) {
        Appointment newAppointment = Appointment.builder()
                .title(appointmentRequest.getTitle())
                .description(appointmentRequest.getDescription())
                .mechanicUsername(appointmentRequest.getMechanicUsername())
                .userUsername(userUsername)
                .date(LocalDateTime.now())
                .status(AppointmentStatus.PENDING)
                .build();
        return appointmentRepository.save(newAppointment);
    }

    public List<Appointment> getAppointmentsForMechanic(String mechanicUsername) {
        return appointmentRepository.findByMechanicUsername(mechanicUsername);
    }

    public List<Appointment> getAppointmentsForUser(String userUsername) {
        return appointmentRepository.findByUserUsername(userUsername);
    }

    public Optional<Appointment> getById(Long id) {
        return appointmentRepository.findById(id);
    }

    public void delete(Long id) {
        appointmentRepository.deleteById(id);
    }
}
