package org.alvarub.workouttrackerproject.persistence.repository;

import org.alvarub.workouttrackerproject.persistence.entity.Sesion;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SesionRepository extends JpaRepository<Sesion, Long> {
}