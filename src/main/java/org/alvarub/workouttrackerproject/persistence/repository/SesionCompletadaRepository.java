package org.alvarub.workouttrackerproject.persistence.repository;

import org.alvarub.workouttrackerproject.persistence.entity.SesionCompletada;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SesionCompletadaRepository extends JpaRepository<SesionCompletada, Long> {
    List<SesionCompletada> findByUser_Id(Long id);

}
