package com.quarks.commerce.dto;

public class CancelReservationRequest {
    private Long reservationId;
    private Long userId;

    public Long getReservationId() { return reservationId; }
    public void setReservationId(Long reservationId) { this.reservationId = reservationId; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
} 