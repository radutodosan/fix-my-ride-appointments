package com.radutodosan.appointments.controllers;

import com.radutodosan.appointments.dtos.ApiResponseDTO;
import com.radutodosan.appointments.dtos.AppointmentStatusUpdateDTO;
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
@RequestMapping("/appointments/mechanic")
public class MechanicAppointmentController {
    private final AppointmentService appointmentService;
    private final AuthValidator authValidator;

    public MechanicAppointmentController(AppointmentService service, AuthValidator authValidator) {
        this.appointmentService = service;
        this.authValidator = authValidator;
    }


    @GetMapping("/view-appointments")
    public ResponseEntity<?> getByMechanic(@RequestHeader("Authorization") String token) {

        String mechanicUsername = authValidator.getAuthenticatedMechanic(token);
        if (mechanicUsername == null) {
            ApiResponseDTO<?> error = new ApiResponseDTO<>(false, "Not authenticated as mechanic", null);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
        }

        List<Appointment> appointments = appointmentService.getAppointmentsForMechanic(mechanicUsername);
        ApiResponseDTO<List<Appointment>> response = new ApiResponseDTO<>(true, "Appointments for mechanic retrieved", appointments);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/update-status/{appointmentId}")
    public ResponseEntity<?> updateStatusAsMechanic(@RequestHeader("Authorization") String token,
                                                    @PathVariable Long appointmentId,
                                                    @RequestBody AppointmentStatusUpdateDTO statusUpdateDTO) {
        String mechanicUsername = authValidator.getAuthenticatedMechanic(token);
        if (mechanicUsername == null) {
            ApiResponseDTO<?> error = new ApiResponseDTO<>(false, "Not authenticated as mechanic", null);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
        }

        try {
            Appointment updated = appointmentService.updateStatusAsMechanic(appointmentId, mechanicUsername, statusUpdateDTO.getStatus());
            ApiResponseDTO<Appointment> response = new ApiResponseDTO<>(
                    true,
                    "Appointment status updated successfully",
                    updated
            );
            return ResponseEntity.ok(response);
        } catch (NoSuchElementException e) {
            ApiResponseDTO<?> error = new ApiResponseDTO<>(false, e.getMessage(), null);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        } catch (AccessDeniedException e) {
            ApiResponseDTO<?> error = new ApiResponseDTO<>(false, e.getMessage(), null);
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
        } catch (Exception e) {
            ApiResponseDTO<?> error = new ApiResponseDTO<>(false, "Failed to update appointment status", null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

}
