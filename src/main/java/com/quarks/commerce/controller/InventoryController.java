package com.quarks.commerce.controller;

import com.quarks.commerce.model.Item;
import com.quarks.commerce.model.Reservation;
import com.quarks.commerce.service.InventoryService;
import com.quarks.commerce.dto.CreateSupplyRequest;
import com.quarks.commerce.dto.ReserveItemRequest;
import com.quarks.commerce.dto.CancelReservationRequest;
import com.quarks.commerce.dto.ReservationResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Map;

@RestController
@RequestMapping("/api/inventory")
public class InventoryController {
    @Autowired
    private InventoryService inventoryService;

    @PostMapping("/supply")
    public ResponseEntity<Item> createSupply(@RequestBody CreateSupplyRequest req) {
        Item item = inventoryService.createSupply(req.getName(), req.getDescription(), req.getPrice(), req.getQuantity());
        return ResponseEntity.ok(item);
    }

    @PostMapping("/reserve")
    public ResponseEntity<ReservationResponse> reserveItem(@RequestBody ReserveItemRequest req) {
        Reservation reservation = inventoryService.reserveItem(req.getItemId(), req.getQuantity(), req.getUserId());
        return ResponseEntity.ok(mapToDto(reservation));
    }

    @PostMapping("/cancel")
    public ResponseEntity<ReservationResponse> cancelReservation(@RequestBody CancelReservationRequest req) {
        Reservation reservation = inventoryService.cancelReservation(req.getReservationId(), req.getUserId());
        return ResponseEntity.ok(mapToDto(reservation));
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Map<String, Object>> getItemAvailability(@PathVariable Long itemId) {
        int available = inventoryService.getItemAvailability(itemId);
        return ResponseEntity.ok(Map.of("itemId", itemId, "available", available));
    }

    private ReservationResponse mapToDto(Reservation reservation) {
        ReservationResponse dto = new ReservationResponse();
        dto.setId(reservation.getId());
        dto.setItemId(reservation.getItem().getId());
        dto.setReservedQuantity(reservation.getReservedQuantity());
        dto.setStatus(reservation.getStatus());
        dto.setUserId(reservation.getUserId());
        dto.setCreatedAt(reservation.getCreatedAt());
        return dto;
    }
} 