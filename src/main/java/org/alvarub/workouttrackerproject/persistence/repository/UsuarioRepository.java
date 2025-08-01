package org.alvarub.workouttrackerproject.persistence.repository;

import org.alvarub.workouttrackerproject.persistence.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
  }