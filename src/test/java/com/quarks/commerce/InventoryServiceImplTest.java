package com.quarks.commerce;

import com.quarks.commerce.model.Item;
import com.quarks.commerce.model.Reservation;
import com.quarks.commerce.service.InventoryService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@SpringBootTest
@Transactional
public class InventoryServiceImplTest {
    @Autowired
    private InventoryService inventoryService;

    @Test
    void testCreateSupplyAndGetAvailability() {
        Item item = inventoryService.createSupply("TestItem", "desc", BigDecimal.valueOf(10), 100);
        int available = inventoryService.getItemAvailability(item.getId());
        Assertions.assertEquals(100, available);
    }

    @Test
    void testReserveAndCancel() {
        Item item = inventoryService.createSupply("TestItem2", "desc", BigDecimal.valueOf(20), 50);
        Reservation reservation = inventoryService.reserveItem(item.getId(), 10, 1L);
        Assertions.assertEquals("RESERVED", reservation.getStatus());
        int available = inventoryService.getItemAvailability(item.getId());
        Assertions.assertEquals(40, available);
        Reservation cancelled = inventoryService.cancelReservation(reservation.getId(), 1L);
        Assertions.assertEquals("CANCELLED", cancelled.getStatus());
        int availableAfterCancel = inventoryService.getItemAvailability(item.getId());
        Assertions.assertEquals(50, availableAfterCancel);
    }

    @Test
    void testReserveNotEnoughStock() {
        Item item = inventoryService.createSupply("TestItem3", "desc", BigDecimal.valueOf(30), 5);
        Assertions.assertThrows(RuntimeException.class, () -> {
            inventoryService.reserveItem(item.getId(), 10, 2L);
        });
    }
} 