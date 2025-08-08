package org.alvarub.workouttrackerproject.persistence.repository;

import org.alvarub.workouttrackerproject.persistence.entity.Rutina;
import org.alvarub.workouttrackerproject.persistence.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

  Optional<Usuario> findByCreatedRoutinesContains(Rutina rutina);

  List<Usuario> findAllByLikedRoutinesContains(Rutina rutina);

  List<Usuario> findAllBySavedRoutinesContains(Rutina rutina);
}