package org.alvarub.workouttrackerproject.persistence.repository;

import org.alvarub.workouttrackerproject.persistence.entity.Rutina;
import org.alvarub.workouttrackerproject.persistence.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

  Optional<Usuario> findByAuth0Id(String auth0Id);

  Optional<Usuario> findByEmail(String email);

  boolean existsByEmail(String email);

  Optional<Usuario> findByCreatedRoutinesContains(Rutina rutina);

  List<Usuario> findAllByLikedRoutinesContains(Rutina rutina);

  List<Usuario> findAllBySavedRoutinesContains(Rutina rutina);
}