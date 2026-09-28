package com.example.shiftplanner.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "swap_requests")
public class SwapRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "roster_id", nullable = false)
    private Roster roster;

    @ManyToOne(optional = false)
    @JoinColumn(name = "requested_by_id", nullable = false)
    private Employee requestedBy;

    @ManyToOne(optional = false)
    @JoinColumn(name = "requested_to_id", nullable = false)
    private Employee requestedTo;

    private String reason;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SwapStatus status;

    public SwapRequest() {
    }

    public SwapRequest(Long id, Roster roster, Employee requestedBy, Employee requestedTo, String reason, SwapStatus status) {
        this.id = id;
        this.roster = roster;
        this.requestedBy = requestedBy;
        this.requestedTo = requestedTo;
        this.reason = reason;
        this.status = status;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Roster getRoster() {
        return roster;
    }

    public void setRoster(Roster roster) {
        this.roster = roster;
    }

    public Employee getRequestedBy() {
        return requestedBy;
    }

    public void setRequestedBy(Employee requestedBy) {
        this.requestedBy = requestedBy;
    }

    public Employee getRequestedTo() {
        return requestedTo;
    }

    public void setRequestedTo(Employee requestedTo) {
        this.requestedTo = requestedTo;
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
