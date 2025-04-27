package com.radutodosan.appointments.entities;

import com.radutodosan.appointments.enums.AppointmentStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Appointment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String userUsername;

    @Column(nullable = false)
    private String mechanicUsername;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private LocalDateTime date; // creation date

    @Column(nullable = false)
    private String carDetails;

    @Column(nullable = false)
    private String appointmentDate; // format: "YYYY-MM-DD"
    @Column(nullable = false)
    private String appointmentTime; // format: "HH:mm"

    @Column(nullable = false)
    private AppointmentStatus status; //  PENDING, CONFIRMED, CANCELLED

}
