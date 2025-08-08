package org.alvarub.workouttrackerproject.service;

import lombok.AllArgsConstructor;
import org.alvarub.workouttrackerproject.exception.NotFoundException;
import org.alvarub.workouttrackerproject.mapper.CategoriaMapper;
import org.alvarub.workouttrackerproject.persistence.dto.categoria.CategoriaRequestDTO;
import org.alvarub.workouttrackerproject.persistence.dto.categoria.CategoriaResponseDTO;
import org.alvarub.workouttrackerproject.persistence.entity.Categoria;
import org.alvarub.workouttrackerproject.persistence.repository.CategoriaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@AllArgsConstructor
public class CategoriaService {

    private final CategoriaRepository categoriaRepository;
    private final CategoriaMapper categoriaMapper;

    @Transactional
    public CategoriaResponseDTO save(CategoriaRequestDTO dto) {
        Categoria categoria = categoriaMapper.toEntity(dto);
        return categoriaMapper.toResponseDTO(categoriaRepository.save(categoria));
    }

    @Transactional(readOnly = true)
    public CategoriaResponseDTO findById(Long id) {
        return categoriaMapper.toResponseDTO(getCategoriaOrThrow(id, false));
    }

    @Transactional(readOnly = true)
    public List<CategoriaResponseDTO> findAll() {
        return categoriaRepository.findAll().stream()
                .map(categoriaMapper::toResponseDTO)
                .toList();
    }

    @Transactional
    public CategoriaResponseDTO toggleActive(Long id) {
        Categoria categoria = getCategoriaOrThrow(id, false);
        categoria.setActive(!categoria.getActive());
        return categoriaMapper.toResponseDTO(categoriaRepository.save(categoria));
    }

    @Transactional
    public CategoriaResponseDTO softDelete(Long id) {
        Categoria categoria = getCategoriaOrThrow(id, false);

        if (!categoria.getActive()) {
            return categoriaMapper.toResponseDTO(categoria);
        }

        categoria.setActive(false);
        return categoriaMapper.toResponseDTO(categoriaRepository.save(categoria));
    }

    @Transactional
    public void hardDelete(Long id) {
        Categoria categoria = getCategoriaOrThrow(id, false);
        categoriaRepository.delete(categoria);
    }

    // Métodos auxiliares
    public Categoria getCategoriaOrThrow(Long id, boolean verifyActive) {
        Categoria categoria = categoriaRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Categoria con el ID " + id + " no encontrada"));

        if (verifyActive && !categoria.getActive()) {
            throw new NotFoundException("Categoria con el ID " + " inactiva");
        }

        return categoria;
    }
}
