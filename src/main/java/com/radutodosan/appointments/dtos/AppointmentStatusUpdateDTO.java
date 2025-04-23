package com.radutodosan.appointments.dtos;

import com.radutodosan.appointments.enums.AppointmentStatus;
import lombok.Data;

@Data
public class AppointmentStatusUpdateDTO {
    private AppointmentStatus status;
}
