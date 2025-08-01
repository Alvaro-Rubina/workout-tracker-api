package org.alvarub.workouttrackerproject.persistence.repository;

import org.alvarub.workouttrackerproject.persistence.entity.Musculo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MusculoRepository extends JpaRepository<Musculo, Long> {
}