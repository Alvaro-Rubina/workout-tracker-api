package org.alvarub.workouttrackerproject.persistence.repository;

import org.alvarub.workouttrackerproject.persistence.entity.Agenda;
import org.alvarub.workouttrackerproject.persistence.entity.Rutina;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AgendaRepository extends JpaRepository<Agenda, Long> {
    List<Agenda> findAllByRoutine(Rutina rutina);
    List<Agenda> findByUser_Auth0Id(String auth0Id);
    boolean existsByUser_IdAndRoutine_Id(Long userId, Long routineId);
}