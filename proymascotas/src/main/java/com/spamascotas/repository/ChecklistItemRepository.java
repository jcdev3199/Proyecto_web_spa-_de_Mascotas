package com.spamascotas.repository;

import com.spamascotas.model.ChecklistItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChecklistItemRepository extends JpaRepository<ChecklistItem, Integer> {
}