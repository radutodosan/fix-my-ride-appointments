package com.radutodosan.appointments.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AppointmentRequestDTO {
    private String mechanicUsername;
    private String title;
    private String description;
}
