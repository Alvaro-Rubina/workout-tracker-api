package org.alvarub.workouttrackerproject.persistence.repository;

import org.alvarub.workouttrackerproject.persistence.entity.Categoria;
import org.alvarub.workouttrackerproject.persistence.entity.Rutina;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RutinaRepository extends JpaRepository<Rutina, Long> {
    List<Rutina> findAllByCategory(Categoria categoria);
    List<Rutina> findByUser_Auth0Id(String auth0Id);

}