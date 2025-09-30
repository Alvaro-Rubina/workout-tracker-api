package org.alvarub.workouttrackerproject.service;

import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import org.alvarub.workouttrackerproject.exception.NotFoundException;
import org.alvarub.workouttrackerproject.mapper.CategoriaMapper;
import org.alvarub.workouttrackerproject.persistence.dto.categoria.CategoriaRequestDTO;
import org.alvarub.workouttrackerproject.persistence.dto.categoria.CategoriaResponseDTO;
import org.alvarub.workouttrackerproject.persistence.entity.Categoria;
import org.alvarub.workouttrackerproject.persistence.repository.CategoriaRepository;
import org.alvarub.workouttrackerproject.persistence.repository.RutinaRepository;
import org.alvarub.workouttrackerproject.persistence.repository.SesionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.alvarub.workouttrackerproject.utils.Constants.DEFAULT_CATEGORY_NAME;

@Service
@AllArgsConstructor
public class CategoriaService {

    private final CategoriaRepository categoriaRepository;
    private final CategoriaMapper categoriaMapper;
    private final SesionRepository sesionRepository;
    private final RutinaRepository rutinaRepository;

    // Método para crear la categoría por defecto si es que no existe
    @PostConstruct
    public void initDefaultCategoria() {
        if (!categoriaRepository.existsByName(DEFAULT_CATEGORY_NAME)) {
            Categoria categoria = Categoria.builder()
                    .name(DEFAULT_CATEGORY_NAME)
                    .active(true)
                    .build();
            categoriaRepository.save(categoria);
        }
    }

    @Transactional
    public CategoriaResponseDTO save(CategoriaRequestDTO dto) {
        Categoria categoria = categoriaMapper.toEntity(dto);
        return categoriaMapper.toResponseDTO(categoriaRepository.save(categoria));
    }

    @Transactional(readOnly = true)
    public CategoriaResponseDTO findById(Long id, boolean verifyActive) {
        return categoriaMapper.toResponseDTO(getCategoriaOrThrow(id, verifyActive));
    }

    @Transactional(readOnly = true)
    public List<CategoriaResponseDTO> findAll() {
        return categoriaRepository.findAll().stream()
                .map(categoriaMapper::toResponseDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<CategoriaResponseDTO> findAllActive() {
        return categoriaRepository.findAll().stream()
                .filter(Categoria::getActive)
                .map(categoriaMapper::toResponseDTO)
                .toList();
    }

    @Transactional
    public CategoriaResponseDTO toggleActive(Long id) {
        Categoria categoria = getCategoriaOrThrow(id, false);

        if (categoria.getName().equalsIgnoreCase(DEFAULT_CATEGORY_NAME)) {
            throw new IllegalStateException("No es posible actualizar el estado de la categoría por defecto");
        }

        categoria.setActive(!categoria.getActive());
        return categoriaMapper.toResponseDTO(categoriaRepository.save(categoria));
    }

    @Transactional
    public CategoriaResponseDTO softDelete(Long id) {
        Categoria categoria = getCategoriaOrThrow(id, false);

        if (categoria.getName().equalsIgnoreCase(DEFAULT_CATEGORY_NAME)) {
            throw new IllegalStateException("No es posible desactivar la categoría ya que es la categoría por defecto");
        }

        if (!categoria.getActive()) {
            return categoriaMapper.toResponseDTO(categoria);
        }

        categoria.setActive(false);
        return categoriaMapper.toResponseDTO(categoriaRepository.save(categoria));
    }

    @Transactional
    public void hardDelete(Long id) {
        Categoria categoriaAEliminar = getCategoriaOrThrow(id, false);

        if (categoriaAEliminar.getName().equalsIgnoreCase(DEFAULT_CATEGORY_NAME)) {
            throw new IllegalStateException("No es posible eliminar la categoría por defecto");
        }

        Categoria categoriaDefault = getDefaultCategoriaOrThrow();

        sesionRepository.findAllByCategory(categoriaAEliminar).forEach(categoria -> {
            categoria.setCategory(categoriaDefault);
        });

        rutinaRepository.findAllByCategory(categoriaAEliminar).forEach(rutina -> {
            rutina.setCategory(categoriaDefault);
        });

        categoriaRepository.delete(categoriaAEliminar);
    }

    // Métodos auxiliares
    public Categoria getCategoriaOrThrow(Long id, boolean verifyActive) {
        Categoria categoria = categoriaRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Categoria con el ID " + id + " no encontrada"));

        if (verifyActive && !categoria.getActive()) {
            throw new NotFoundException("Categoria con el ID " + id + " inactiva");
        }

        return categoria;
    }

    private Categoria getDefaultCategoriaOrThrow() {
        return categoriaRepository.findByName(DEFAULT_CATEGORY_NAME)
                .orElseThrow(() -> new NotFoundException("Categoría por defecto no encontrada"));
    }
}
