package org.alvarub.workouttrackerproject.service;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.alvarub.workouttrackerproject.exception.NotFoundException;
import org.alvarub.workouttrackerproject.mapper.RolMapper;
import org.alvarub.workouttrackerproject.persistence.dto.rol.RolRequestDTO;
import org.alvarub.workouttrackerproject.persistence.dto.rol.RolResponseDTO;
import org.alvarub.workouttrackerproject.persistence.entity.Rol;
import org.alvarub.workouttrackerproject.persistence.repository.RolRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.alvarub.workouttrackerproject.utils.Constants.ADMIN_ROL_NAME;
import static org.alvarub.workouttrackerproject.utils.Constants.USER_ROL_NAME;

@Service
@RequiredArgsConstructor
public class RolService {

    private final RolRepository rolRepository;
    private final RolMapper rolMapper;

    // Método para guardar los roles predefinidos
    @PostConstruct
    public void initDefaultRoles() {
        if (!rolRepository.existsByNameIgnoreCase(ADMIN_ROL_NAME)) {
            Rol rol = Rol.builder()
                    .name(ADMIN_ROL_NAME)
                    .description("Rol con permisos especiales para administradores del sistema")
                    .active(true)
                    .build();
            rolRepository.save(rol);
        }

        if (!rolRepository.existsByNameIgnoreCase(USER_ROL_NAME)) {
            Rol rol = Rol.builder()
                    .name(USER_ROL_NAME)
                    .description("Rol con permisos limitados para usuarios del sistema")
                    .active(true)
                    .build();
            rolRepository.save(rol);
        }
    }

    @Transactional
    public RolResponseDTO save(RolRequestDTO dto) {
        Rol rol = rolMapper.toEntity(dto);
        return rolMapper.toResponseDTO(rol);
    }

    @Transactional(readOnly = true)
    public RolResponseDTO findById(Long id) {
        Rol rol = getRolOrThrow(id, false);
        return rolMapper.toResponseDTO(rol);
    }

    @Transactional(readOnly = true)
    public List<RolResponseDTO> findAll() {
        return rolRepository.findAll().stream()
                .map(rolMapper::toResponseDTO)
                .toList();
    }

    // Métodos auxiliares
    public Rol getRolOrThrow(Long id, boolean verifyActive) {
        Rol rol = rolRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Rol con el id " + id + " no encontrado"));

        if (verifyActive && !rol.getActive()) {
            throw new NotFoundException("Rol el ID " + id + " inactivo");
        }

        return rol;
    }
}
