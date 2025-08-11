package org.alvarub.workouttrackerproject.service;

import lombok.RequiredArgsConstructor;
import org.alvarub.workouttrackerproject.exception.NotFoundException;
import org.alvarub.workouttrackerproject.mapper.UsuarioMapper;
import org.alvarub.workouttrackerproject.persistence.dto.usuario.UsuarioRequestDTO;
import org.alvarub.workouttrackerproject.persistence.dto.usuario.UsuarioResponseDTO;
import org.alvarub.workouttrackerproject.persistence.dto.usuario.UsuarioStatsDTO;
import org.alvarub.workouttrackerproject.persistence.entity.Usuario;
import org.alvarub.workouttrackerproject.persistence.repository.UsuarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final UsuarioMapper usuarioMapper;
    private final RolService rolService;

    @Transactional
    public UsuarioResponseDTO save(UsuarioRequestDTO dto) {
        Usuario usuario = usuarioMapper.toEntity(dto);

        dto.getRoleIds().forEach(roleId -> {
            usuario.getRoles().add(rolService.getRolOrThrow(roleId, true));
        });

        return usuarioMapper.toSimpleDTO(usuarioRepository.save(usuario));
    }

    @Transactional(readOnly = true)
    public UsuarioResponseDTO findById(Long id) {
        Usuario usuario = getUsuarioOrThrow (id, false);
        return usuarioMapper.toSimpleDTO(usuario);
    }

    @Transactional(readOnly = true)
    public UsuarioStatsDTO findStatsById(Long id) {
        Usuario usuario = getUsuarioOrThrow(id, false);
        return usuarioMapper.toStatsDTO(usuario);
    }

    @Transactional(readOnly = true)
    public List<UsuarioResponseDTO> findAll() {
        return usuarioRepository.findAll().stream()
                .map(usuarioMapper::toSimpleDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<UsuarioStatsDTO> findAllStats() {
        return usuarioRepository.findAll().stream()
                .map(usuarioMapper::toStatsDTO)
                .toList();
    }

    @Transactional
    public UsuarioResponseDTO toggleActive(Long id) {
        Usuario usuario = getUsuarioOrThrow(id, false);

        // Al desactivar al usuario
        if (usuario.getActive()) {
            // Guardo el estado actual de cada rutina y la paso a privada
            usuario.getCreatedRoutines().forEach(rutina -> {
                rutina.setPreviousPublicState(rutina.getIsPublic());

                if (rutina.getIsPublic()) {
                    rutina.setIsPublic(false);
                }
            });
        }

        // Al activar al usuario
        else {
            // Vuelvo publicas solo las que lo eran antes de desactivar al usuario
            usuario.getCreatedRoutines().forEach(rutina -> {
                if (Boolean.TRUE.equals(rutina.getPreviousPublicState())) {
                    rutina.setIsPublic(true);
                }

                rutina.setPreviousPublicState(null);
            });
        }

        usuario.setActive(!usuario.getActive());

        return usuarioMapper.toSimpleDTO(usuario);
    }

    // Métodos auxiliares
    public Usuario getUsuarioOrThrow(Long id, boolean verifyActive) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Usuario con el ID " + id + " no encontrado"));

        if (verifyActive && !usuario.getActive()) {
            throw new NotFoundException("Usuario el ID " + id + " inactivo");
        }

        return usuario;
    }
}
