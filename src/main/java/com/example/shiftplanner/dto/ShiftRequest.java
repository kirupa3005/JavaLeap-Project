package com.example.shiftplanner.dto;

import jakarta.validation.constraints.NotBlank;

public class ShiftRequest {

    @NotBlank(message = "Shift name is required")
    private String shiftName;

    @NotBlank(message = "Start time is required (e.g. 09:00)")
    private String startTime;

    @NotBlank(message = "End time is required (e.g. 17:00)")
    private String endTime;

    public ShiftRequest() {
    }

    public ShiftRequest(String shiftName, String startTime, String endTime) {
        this.shiftName = shiftName;
        this.startTime = startTime;
        this.endTime = endTime;
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
}
