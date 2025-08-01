package org.alvarub.workouttrackerproject.persistence.repository;

import org.alvarub.workouttrackerproject.persistence.entity.Rutina;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RutinaRepository extends JpaRepository<Rutina, Long> {
}