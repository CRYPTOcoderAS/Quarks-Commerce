package com.quarks.commerce.repository;

import com.quarks.commerce.model.Reservation;
import com.quarks.commerce.model.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    List<Reservation> findByItem(Item item);
} 