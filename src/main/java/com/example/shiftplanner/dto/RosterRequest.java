package com.example.shiftplanner.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public class RosterRequest {

    @NotNull(message = "Employee ID is required")
    private Long employeeId;

    @NotNull(message = "Shift ID is required")
    private Long shiftId;

    @NotNull(message = "Date is required (YYYY-MM-DD)")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate date;

    public RosterRequest() {
    }

    public RosterRequest(Long employeeId, Long shiftId, LocalDate date) {
        this.employeeId = employeeId;
        this.shiftId = shiftId;
        this.date = date;
    }

    public Long getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(Long employeeId) {
        this.employeeId = employeeId;
    }

    public Long getShiftId() {
        return shiftId;
    }

    public void setShiftId(Long shiftId) {
        this.shiftId = shiftId;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }
}
