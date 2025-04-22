package com.radutodosan.appointments.controllers;

import com.radutodosan.appointments.dtos.ApiResponseDTO;
import com.radutodosan.appointments.dtos.AppointmentRequestDTO;
import com.radutodosan.appointments.entities.Appointment;
import com.radutodosan.appointments.services.AppointmentService;
import com.radutodosan.appointments.services.AuthValidator;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/appointments")
public class AppointmentController {
    private final AppointmentService appointmentService;
    private final AuthValidator authValidator;

    public AppointmentController(AppointmentService service, AuthValidator authValidator) {
        this.appointmentService = service;
        this.authValidator = authValidator;
    }

    @PostMapping("/create")
    public ResponseEntity<?> create(@RequestHeader("Authorization") String token,
                                    @RequestBody AppointmentRequestDTO appointmentRequest) {

        // Authenticate user
        String userUsername = authValidator.getAuthenticatedUsername(token);
        if (userUsername == null) {
            ApiResponseDTO<?> error = new ApiResponseDTO<>(false, "Not authenticated as user", null);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
        }

        // Verify if mechanic exists
        boolean mechanicExists = authValidator.doesMechanicExist(appointmentRequest.getMechanicUsername());
        if (!mechanicExists) {
            ApiResponseDTO<?> error = new ApiResponseDTO<>(false, "Mechanic does not exist", null);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }

        // Create appointment
        Appointment createdAppointment = appointmentService.createAppointment(appointmentRequest, userUsername);
        ApiResponseDTO<Appointment> response = new ApiResponseDTO<>(true, "Appointment created successfully", createdAppointment);
        return ResponseEntity.ok(response);
    }


    @GetMapping("/user")
    public ResponseEntity<?> getByUser(@RequestHeader("Authorization") String token) {

        String userUsername = authValidator.getAuthenticatedUsername(token);
        if (userUsername == null) {
            ApiResponseDTO<?> error = new ApiResponseDTO<>(false, "Not authenticated as user", null);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
        }

        List<Appointment> appointments = appointmentService.getAppointmentsForUser(userUsername);
        ApiResponseDTO<List<Appointment>> response = new ApiResponseDTO<>(true, "Appointments for user retrieved", appointments);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/mechanic")
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
}
