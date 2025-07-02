package com.quarks.commerce.service.impl;

import com.quarks.commerce.model.Item;
import com.quarks.commerce.model.Inventory;
import com.quarks.commerce.model.Reservation;
import com.quarks.commerce.repository.ItemRepository;
import com.quarks.commerce.repository.InventoryRepository;
import com.quarks.commerce.repository.ReservationRepository;
import com.quarks.commerce.repository.UserRepository;
import com.quarks.commerce.service.InventoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

@Service
public class InventoryServiceImpl implements InventoryService {
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private InventoryRepository inventoryRepository;
    @Autowired
    private ReservationRepository reservationRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RedisTemplate<String, Integer> redisTemplate;

    private static final String INVENTORY_KEY_PREFIX = "inventory:";
    private static final String LOCK_KEY_PREFIX = "lock:inventory:";

    @Override
    @Transactional
    public Item createSupply(String name, String description, BigDecimal price, int quantity) {
        Item item = new Item();
        item.setName(name);
        item.setDescription(description);
        item.setPrice(price);
        item = itemRepository.save(item);
        Inventory inventory = new Inventory();
        inventory.setItem(item);
        inventory.setQuantity(quantity);
        inventoryRepository.save(inventory);
        return item;
    }

    @Override
    @Transactional
    public Reservation reserveItem(Long itemId, int quantity, Long userId) {
        if (!userRepository.existsById(userId)) throw new RuntimeException("User does not exist");
        String lockKey = LOCK_KEY_PREFIX + itemId;
        ValueOperations<String, Integer> ops = redisTemplate.opsForValue();
        boolean locked = redisTemplate.opsForValue().setIfAbsent(lockKey, 1, 5, TimeUnit.SECONDS);
        if (!locked) throw new RuntimeException("Inventory is busy, try again");
        try {
            Item item = itemRepository.findById(itemId).orElseThrow();
            Inventory inventory = inventoryRepository.findByItem(item).orElseThrow();
            if (inventory.getQuantity() < quantity) throw new RuntimeException("Not enough stock");
            inventory.setQuantity(inventory.getQuantity() - quantity);
            inventoryRepository.save(inventory);
            ops.set(INVENTORY_KEY_PREFIX + itemId, inventory.getQuantity(), 10, TimeUnit.MINUTES);
            Reservation reservation = new Reservation();
            reservation.setItem(item);
            reservation.setReservedQuantity(quantity);
            reservation.setStatus("RESERVED");
            reservation.setUserId(userId);
            reservation.setCreatedAt(LocalDateTime.now());
            return reservationRepository.save(reservation);
        } finally {
            redisTemplate.delete(lockKey);
        }
    }

    @Override
    @Transactional
    public Reservation cancelReservation(Long reservationId, Long userId) {
        if (!userRepository.existsById(userId)) throw new RuntimeException("User does not exist");
        Reservation reservation = reservationRepository.findById(reservationId).orElseThrow();
        Long itemId = reservation.getItem().getId();
        String lockKey = LOCK_KEY_PREFIX + itemId;
        ValueOperations<String, Integer> ops = redisTemplate.opsForValue();
        boolean locked = redisTemplate.opsForValue().setIfAbsent(lockKey, 1, 5, TimeUnit.SECONDS);
        if (!locked) throw new RuntimeException("Inventory is busy, try again");
        try {
            if (!reservation.getUserId().equals(userId)) throw new RuntimeException("Unauthorized");
            if (!reservation.getStatus().equals("RESERVED")) throw new RuntimeException("Already cancelled");
            reservation.setStatus("CANCELLED");
            reservationRepository.save(reservation);
            Inventory inventory = inventoryRepository.findByItem(reservation.getItem()).orElseThrow();
            inventory.setQuantity(inventory.getQuantity() + reservation.getReservedQuantity());
            inventoryRepository.save(inventory);
            ops.set(INVENTORY_KEY_PREFIX + itemId, inventory.getQuantity(), 10, TimeUnit.MINUTES);
            return reservation;
        } finally {
            redisTemplate.delete(lockKey);
        }
    }

    @Override
    public int getItemAvailability(Long itemId) {
        String redisKey = INVENTORY_KEY_PREFIX + itemId;
        ValueOperations<String, Integer> ops = redisTemplate.opsForValue();
        Integer cached = ops.get(redisKey);
        if (cached != null) {
            return cached;
        }
        Item item = itemRepository.findById(itemId).orElseThrow();
        Inventory inventory = inventoryRepository.findByItem(item).orElseThrow();
        int quantity = inventory.getQuantity();
        ops.set(redisKey, quantity, 10, TimeUnit.MINUTES);
        return quantity;
    }
} 