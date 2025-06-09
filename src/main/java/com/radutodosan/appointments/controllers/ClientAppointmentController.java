package com.radutodosan.appointments.controllers;

import com.radutodosan.appointments.dtos.ApiResponseDTO;
import com.radutodosan.appointments.dtos.AppointmentRequestDTO;
import com.radutodosan.appointments.entities.Appointment;
import com.radutodosan.appointments.services.AppointmentService;
import com.radutodosan.appointments.services.AuthValidator;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.file.AccessDeniedException;
import java.util.List;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/client")
public class ClientAppointmentController {

    private final AppointmentService appointmentService;
    private final AuthValidator authValidator;

    public ClientAppointmentController(AppointmentService service, AuthValidator authValidator) {
        this.appointmentService = service;
        this.authValidator = authValidator;
    }

    @PostMapping("/create")
    public ResponseEntity<?> create(@RequestHeader("Authorization") String token,
                                    @RequestBody AppointmentRequestDTO appointmentRequest) {

        // Authenticate user
        String clientUsername = authValidator.getAuthenticatedUsername(token);
        if (clientUsername == null) {
            ApiResponseDTO<?> error = new ApiResponseDTO<>(false, "Not authenticated as user", null);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
        }

        // Verify if mechanic exists
        boolean mechanicExists = authValidator.doesMechanicExist(appointmentRequest.getMechanicUsername());
        if (!mechanicExists) {
            ApiResponseDTO<?> error = new ApiResponseDTO<>(false, "Mechanic does not exist", null);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }

        // Check for mechanic free time slot and create appointment
        try {
            Appointment createdAppointment = appointmentService.createAppointment(appointmentRequest, clientUsername);
            ApiResponseDTO<Appointment> response = new ApiResponseDTO<>(true, "Appointment created successfully", createdAppointment);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponseDTO<>(false, e.getMessage(), null));
        }
    }


    @GetMapping("/view-appointments")
    public ResponseEntity<?> getByUser(@RequestHeader("Authorization") String token) {

        String clientUsername = authValidator.getAuthenticatedUsername(token);
        if (clientUsername == null) {
            ApiResponseDTO<?> error = new ApiResponseDTO<>(false, "Not authenticated as user", null);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
        }

        List<Appointment> appointments = appointmentService.getAppointmentsForUser(clientUsername);
        ApiResponseDTO<List<Appointment>> response = new ApiResponseDTO<>(true, "Appointments for user retrieved", appointments);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/cancel/{appointmentId}")
    public ResponseEntity<?> cancelAppointmentAsUser(@RequestHeader("Authorization") String token,
                                                     @PathVariable Long appointmentId) {

        String clientUsername = authValidator.getAuthenticatedUsername(token);
        if (clientUsername == null) {
            ApiResponseDTO<?> error = new ApiResponseDTO<>(false, "Not authenticated as user", null);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
        }

        try {
            Appointment cancelled = appointmentService.cancelAppointmentAsClient(appointmentId, clientUsername);
            ApiResponseDTO<Appointment> response = new ApiResponseDTO<>(
                    true,
                    "Appointment cancelled successfully",
                    cancelled
            );

            return ResponseEntity.ok(response);

        } catch (NoSuchElementException e) {
            ApiResponseDTO<?> error = new ApiResponseDTO<>(false, e.getMessage(), null);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);

        } catch (AccessDeniedException e) {
            ApiResponseDTO<?> error = new ApiResponseDTO<>(false, e.getMessage(), null);
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);

        } catch (Exception e) {
            ApiResponseDTO<?> error = new ApiResponseDTO<>(false, "Failed to cancel appointment", null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }


}
