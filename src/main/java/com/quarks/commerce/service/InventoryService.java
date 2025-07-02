package com.quarks.commerce.service;

import com.quarks.commerce.model.Item;
import com.quarks.commerce.model.Reservation;

public interface InventoryService {
    Item createSupply(String name, String description, java.math.BigDecimal price, int quantity);
    Reservation reserveItem(Long itemId, int quantity, Long userId);
    Reservation cancelReservation(Long reservationId, Long userId);
    int getItemAvailability(Long itemId);
} 