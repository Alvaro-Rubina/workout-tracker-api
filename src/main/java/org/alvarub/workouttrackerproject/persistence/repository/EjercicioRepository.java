package org.alvarub.workouttrackerproject.persistence.repository;

import org.alvarub.workouttrackerproject.persistence.entity.Ejercicio;
import org.alvarub.workouttrackerproject.persistence.entity.Equipamiento;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EjercicioRepository extends JpaRepository<Ejercicio, Long> {
    List<Ejercicio> findAllByEquipmentContains(Equipamiento equipamiento);
}