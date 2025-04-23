package com.radutodosan.appointments.services;

import com.radutodosan.appointments.dtos.AppointmentRequestDTO;
import com.radutodosan.appointments.entities.Appointment;
import com.radutodosan.appointments.enums.AppointmentStatus;
import com.radutodosan.appointments.repositories.AppointmentRepository;
import org.springframework.stereotype.Service;

import java.nio.file.AccessDeniedException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;

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

    public Appointment cancelAppointmentAsClient(Long appointmentId, String userUsername) throws AccessDeniedException {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new NoSuchElementException("Appointment not found"));

        if (!appointment.getUserUsername().equals(userUsername)) {
            throw new AccessDeniedException("You are not allowed to cancel this appointment");
        }

        appointment.setStatus(AppointmentStatus.CANCELLED);
        return appointmentRepository.save(appointment);
    }


    public Appointment updateStatusAsMechanic(Long appointmentId, String mechanicUsername, AppointmentStatus newStatus) throws AccessDeniedException {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new NoSuchElementException("Appointment not found"));

        if (!appointment.getMechanicUsername().equals(mechanicUsername)) {
            throw new AccessDeniedException("You are not allowed to update this appointment");
        }

        appointment.setStatus(newStatus);
        return appointmentRepository.save(appointment);
    }

}
