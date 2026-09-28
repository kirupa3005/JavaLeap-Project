package com.example.shiftplanner.dto;

import jakarta.validation.constraints.NotNull;

public class SwapRequestDto {

    @NotNull(message = "Roster ID is required")
    private Long rosterId;

    @NotNull(message = "Target employee ID (requestedToId) is required")
    private Long requestedToId;

    private String reason;

    public SwapRequestDto() {
    }

    public SwapRequestDto(Long rosterId, Long requestedToId, String reason) {
        this.rosterId = rosterId;
        this.requestedToId = requestedToId;
        this.reason = reason;
    }

    public Long getRosterId() {
        return rosterId;
    }

    public void setRosterId(Long rosterId) {
        this.rosterId = rosterId;
    }

    public Long getRequestedToId() {
        return requestedToId;
    }

    public void setRequestedToId(Long requestedToId) {
        this.requestedToId = requestedToId;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}
