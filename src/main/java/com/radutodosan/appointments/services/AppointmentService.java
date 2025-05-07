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

    public Appointment createAppointment(AppointmentRequestDTO appointmentRequest, String clientUsername) {

        // Check for slot conflict
        if (isSlotTaken(appointmentRequest.getMechanicUsername(), appointmentRequest.getAppointmentDate())) {
            throw new IllegalArgumentException("Selected date and time are already taken for this mechanic.");
        }

        Appointment newAppointment = Appointment.builder()
                .title(appointmentRequest.getTitle())
                .description(appointmentRequest.getDescription())
                .mechanicUsername(appointmentRequest.getMechanicUsername())
                .clientUsername(clientUsername)
                .date(LocalDateTime.now())
                .appointmentDate(appointmentRequest.getAppointmentDate())
                .carDetails(appointmentRequest.getCarDetails())
                .status(AppointmentStatus.PENDING)
                .build();
        return appointmentRepository.save(newAppointment);
    }

    public List<Appointment> getAppointmentsForMechanic(String mechanicUsername) {
        return appointmentRepository.findByMechanicUsername(mechanicUsername);
    }

    public List<Appointment> getAppointmentsForUser(String clientUsername) {
        return appointmentRepository.findByClientUsername(clientUsername);
    }

    public Appointment cancelAppointmentAsClient(Long appointmentId, String clientUsername) throws AccessDeniedException {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new NoSuchElementException("Appointment not found"));

        if (!appointment.getClientUsername() .equals(clientUsername)) {
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

    public boolean isSlotTaken(String mechanicUsername, String date) {
        return appointmentRepository.existsByMechanicUsernameAndAppointmentDate(
                mechanicUsername, date);
    }

}
