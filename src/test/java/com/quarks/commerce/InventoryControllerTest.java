package com.quarks.commerce;

import com.quarks.commerce.dto.CreateSupplyRequest;
import com.quarks.commerce.dto.ReserveItemRequest;
import com.quarks.commerce.dto.CancelReservationRequest;
import com.quarks.commerce.model.Item;
import com.quarks.commerce.model.Reservation;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.transaction.annotation.Transactional;
import com.quarks.commerce.dto.CreateUserRequest;
import com.quarks.commerce.model.User;
import com.quarks.commerce.dto.ReservationResponse;

import java.math.BigDecimal;
import java.util.Map;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Transactional
public class InventoryControllerTest {
    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void testCreateSupplyAndGetAvailability() {
        String uniqueName = "RestItem" + System.currentTimeMillis();
        CreateSupplyRequest req = new CreateSupplyRequest();
        req.setName(uniqueName);
        req.setDescription("desc");
        req.setPrice(BigDecimal.valueOf(99));
        req.setQuantity(77);
        ResponseEntity<Item> response = restTemplate.postForEntity("/api/inventory/supply", req, Item.class);
        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Item item = response.getBody();
        Assertions.assertNotNull(item);
        ResponseEntity<Map> availResp = restTemplate.getForEntity("/api/inventory/" + item.getId(), Map.class);
        Assertions.assertEquals(HttpStatus.OK, availResp.getStatusCode());
        Assertions.assertEquals(77, ((Number)availResp.getBody().get("available")).intValue());
    }

    @Test
    void testReserveAndCancel() {
        // Create user
        CreateUserRequest userReq = new CreateUserRequest();
        userReq.setUsername("testuser" + System.currentTimeMillis());
        userReq.setEmail("testuser" + System.currentTimeMillis() + "@example.com");
        userReq.setPassword("password123");
        ResponseEntity<User> userResp = restTemplate.postForEntity("/api/users", userReq, User.class);
        Long userId = userResp.getBody().getId();

        String uniqueName = "RestItem2" + System.currentTimeMillis();
        CreateSupplyRequest req = new CreateSupplyRequest();
        req.setName(uniqueName);
        req.setDescription("desc");
        req.setPrice(BigDecimal.valueOf(50));
        req.setQuantity(30);
        Item item = restTemplate.postForObject("/api/inventory/supply", req, Item.class);
        ReserveItemRequest reserveReq = new ReserveItemRequest();
        reserveReq.setItemId(item.getId());
        reserveReq.setQuantity(5);
        reserveReq.setUserId(userId);
        ResponseEntity<ReservationResponse> reserveResp = restTemplate.postForEntity("/api/inventory/reserve", reserveReq, ReservationResponse.class);
        Assertions.assertEquals(HttpStatus.OK, reserveResp.getStatusCode());
        ReservationResponse reservation = reserveResp.getBody();
        Assertions.assertNotNull(reservation);
        Assertions.assertEquals("RESERVED", reservation.getStatus());
        CancelReservationRequest cancelReq = new CancelReservationRequest();
        cancelReq.setReservationId(reservation.getId());
        cancelReq.setUserId(userId);
        ResponseEntity<ReservationResponse> cancelResp = restTemplate.postForEntity("/api/inventory/cancel", cancelReq, ReservationResponse.class);
        Assertions.assertEquals(HttpStatus.OK, cancelResp.getStatusCode());
        Assertions.assertEquals("CANCELLED", cancelResp.getBody().getStatus());
    }

    @Test
    void testReserveNotEnoughStock() {
        // Create user
        CreateUserRequest userReq = new CreateUserRequest();
        userReq.setUsername("testuser2" + System.currentTimeMillis());
        userReq.setEmail("testuser2" + System.currentTimeMillis() + "@example.com");
        userReq.setPassword("password123");
        ResponseEntity<User> userResp = restTemplate.postForEntity("/api/users", userReq, User.class);
        Long userId = userResp.getBody().getId();

        String uniqueName = "RestItem3" + System.currentTimeMillis();
        CreateSupplyRequest req = new CreateSupplyRequest();
        req.setName(uniqueName);
        req.setDescription("desc");
        req.setPrice(BigDecimal.valueOf(10));
        req.setQuantity(2);
        Item item = restTemplate.postForObject("/api/inventory/supply", req, Item.class);
        ReserveItemRequest reserveReq = new ReserveItemRequest();
        reserveReq.setItemId(item.getId());
        reserveReq.setQuantity(5);
        reserveReq.setUserId(userId);
        ResponseEntity<String> resp = restTemplate.postForEntity("/api/inventory/reserve", reserveReq, String.class);
        Assertions.assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, resp.getStatusCode());
    }
} 