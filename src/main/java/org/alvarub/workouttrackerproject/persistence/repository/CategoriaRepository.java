package org.alvarub.workouttrackerproject.persistence.repository;

import org.alvarub.workouttrackerproject.persistence.entity.Categoria;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoriaRepository extends JpaRepository<Categoria, Long> {
}