package com.example.shiftplanner.dto;

import com.example.shiftplanner.entity.Roster;
import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDate;

public class RosterResponse {

    private Long id;
    private Long employeeId;
    private String employeeName;
    private String employeeEmail;
    private Long shiftId;
    private String shiftName;
    private String startTime;
    private String endTime;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate date;

    public RosterResponse() {
    }

    public RosterResponse(Long id, Long employeeId, String employeeName, String employeeEmail,
                          Long shiftId, String shiftName, String startTime, String endTime, LocalDate date) {
        this.id = id;
        this.employeeId = employeeId;
        this.employeeName = employeeName;
        this.employeeEmail = employeeEmail;
        this.shiftId = shiftId;
        this.shiftName = shiftName;
        this.startTime = startTime;
        this.endTime = endTime;
        this.date = date;
    }

    public static RosterResponse fromEntity(Roster roster) {
        if (roster == null) return null;
        return new RosterResponse(
                roster.getId(),
                roster.getEmployee() != null ? roster.getEmployee().getId() : null,
                roster.getEmployee() != null ? roster.getEmployee().getName() : null,
                roster.getEmployee() != null ? roster.getEmployee().getEmail() : null,
                roster.getShift() != null ? roster.getShift().getId() : null,
                roster.getShift() != null ? roster.getShift().getShiftName() : null,
                roster.getShift() != null ? roster.getShift().getStartTime() : null,
                roster.getShift() != null ? roster.getShift().getEndTime() : null,
                roster.getDate()
        );
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(Long employeeId) {
        this.employeeId = employeeId;
    }

    public String getEmployeeName() {
        return employeeName;
    }

    public void setEmployeeName(String employeeName) {
        this.employeeName = employeeName;
    }

    public String getEmployeeEmail() {
        return employeeEmail;
    }

    public void setEmail(String employeeEmail) {
        this.employeeEmail = employeeEmail;
    }

    public Long getShiftId() {
        return shiftId;
    }

    public void setShiftId(Long shiftId) {
        this.shiftId = shiftId;
    }

    public String getShiftName() {
        return shiftName;
    }

    public void setShiftName(String shiftName) {
        this.shiftName = shiftName;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }
}
