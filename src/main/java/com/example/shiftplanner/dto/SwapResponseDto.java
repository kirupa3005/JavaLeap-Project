package com.example.shiftplanner.dto;

import com.example.shiftplanner.entity.SwapRequest;
import com.example.shiftplanner.entity.SwapStatus;
import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDate;

public class SwapResponseDto {

    private Long id;
    private Long rosterId;
    private Long requestedById;
    private String requestedByName;
    private Long requestedToId;
    private String requestedToName;
    private String shiftName;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate shiftDate;

    private String reason;
    private SwapStatus status;

    public SwapResponseDto() {
    }

    public SwapResponseDto(Long id, Long rosterId, Long requestedById, String requestedByName,
                           Long requestedToId, String requestedToName, String shiftName,
                           LocalDate shiftDate, String reason, SwapStatus status) {
        this.id = id;
        this.rosterId = rosterId;
        this.requestedById = requestedById;
        this.requestedByName = requestedByName;
        this.requestedToId = requestedToId;
        this.requestedToName = requestedToName;
        this.shiftName = shiftName;
        this.shiftDate = shiftDate;
        this.reason = reason;
        this.status = status;
    }

    public static SwapResponseDto fromEntity(SwapRequest swap) {
        if (swap == null) return null;
        return new SwapResponseDto(
                swap.getId(),
                swap.getRoster() != null ? swap.getRoster().getId() : null,
                swap.getRequestedBy() != null ? swap.getRequestedBy().getId() : null,
                swap.getRequestedBy() != null ? swap.getRequestedBy().getName() : null,
                swap.getRequestedTo() != null ? swap.getRequestedTo().getId() : null,
                swap.getRequestedTo() != null ? swap.getRequestedTo().getName() : null,
                (swap.getRoster() != null && swap.getRoster().getShift() != null)
                        ? swap.getRoster().getShift().getShiftName() : null,
                swap.getRoster() != null ? swap.getRoster().getDate() : null,
                swap.getReason(),
                swap.getStatus()
        );
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getRosterId() {
        return rosterId;
    }

    public void setRosterId(Long rosterId) {
        this.rosterId = rosterId;
    }

    public Long getRequestedById() {
        return requestedById;
    }

    public void setRequestedById(Long requestedById) {
        this.requestedById = requestedById;
    }

    public String getRequestedByName() {
        return requestedByName;
    }

    public void setRequestedByName(String requestedByName) {
        this.requestedByName = requestedByName;
    }

    public Long getRequestedToId() {
        return requestedToId;
    }

    public void setRequestedToId(Long requestedToId) {
        this.requestedToId = requestedToId;
    }

    public String getRequestedToName() {
        return requestedToName;
    }

    public void setRequestedToName(String requestedToName) {
        this.requestedToName = requestedToName;
    }

    public String getShiftName() {
        return shiftName;
    }

    public void setShiftName(String shiftName) {
        this.shiftName = shiftName;
    }

    public LocalDate getShiftDate() {
        return shiftDate;
    }

    public void setShiftDate(LocalDate shiftDate) {
        this.shiftDate = shiftDate;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public SwapStatus getStatus() {
        return status;
    }

    public void setStatus(SwapStatus status) {
        this.status = status;
    }
}
