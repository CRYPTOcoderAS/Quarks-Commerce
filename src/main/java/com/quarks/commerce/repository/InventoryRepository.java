package com.quarks.commerce.repository;

import com.quarks.commerce.model.Inventory;
import com.quarks.commerce.model.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface InventoryRepository extends JpaRepository<Inventory, Long> {
    Optional<Inventory> findByItem(Item item);
} 