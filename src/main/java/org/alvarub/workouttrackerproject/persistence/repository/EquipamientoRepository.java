package org.alvarub.workouttrackerproject.persistence.repository;

import org.alvarub.workouttrackerproject.persistence.entity.Equipamiento;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EquipamientoRepository extends JpaRepository<Equipamiento, Long> {
}