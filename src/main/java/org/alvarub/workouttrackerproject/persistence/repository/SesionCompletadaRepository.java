package org.alvarub.workouttrackerproject.persistence.repository;

import org.alvarub.workouttrackerproject.persistence.entity.SesionCompletada;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SesionCompletadaRepository extends JpaRepository<SesionCompletada, Long> {
    List<SesionCompletada> findByUser_Id(Long id);

}
