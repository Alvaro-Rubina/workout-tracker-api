package org.alvarub.workouttrackerproject.service;

import lombok.RequiredArgsConstructor;
import org.alvarub.workouttrackerproject.exception.ForbiddenOperationException;
import org.alvarub.workouttrackerproject.exception.NotFoundException;
import org.alvarub.workouttrackerproject.mapper.ComentarioMapper;
import org.alvarub.workouttrackerproject.persistence.dto.comentario.ComentarioContentRequestDTO;
import org.alvarub.workouttrackerproject.persistence.dto.comentario.ComentarioRequestDTO;
import org.alvarub.workouttrackerproject.persistence.dto.comentario.ComentarioResponseDTO;
import org.alvarub.workouttrackerproject.persistence.dto.comentario.ComentarioSimpleDTO;
import org.alvarub.workouttrackerproject.persistence.entity.Comentario;
import org.alvarub.workouttrackerproject.persistence.repository.ComentarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ComentarioService {

    private final ComentarioRepository comentarioRepository;
    private final ComentarioMapper comentarioMapper;
    private final UsuarioService usuarioService;
    private final RutinaService rutinaService;

    @Transactional
    public ComentarioResponseDTO save(ComentarioRequestDTO dto) {
        Comentario comentario = comentarioMapper.toEntity(dto);

        comentario.setUser(usuarioService.getUsuarioOrThrow(dto.getUserId(), true));
        comentario.setRoutine(rutinaService.getRutinaOrThrow(dto.getRoutineId()));

        // TODO: cambiar la excepcion por una más adecuada
        if (dto.getReplyToId() != null) {
            Comentario replyTo = getComentarioOrThrow(dto.getReplyToId());

            if (!replyTo.getRoutine().equals(comentario.getRoutine())) {
                throw new ForbiddenOperationException("No es posible responder un comentario de una rutina distinta");
            }
            comentario.setReplyTo(replyTo);
        }

        return comentarioMapper.toResponseDTO(comentarioRepository.save(comentario));
    }

    @Transactional(readOnly = true)
    public List<ComentarioResponseDTO> findAllByRoutineId(Long routineId) {
        return comentarioRepository.findAllByRoutineId(routineId).stream()
                .map(comentarioMapper::toResponseDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ComentarioResponseDTO> findAllByUserId(Long userId) {
        return comentarioRepository.findAllByUserId(userId).stream()
                .map(comentarioMapper::toResponseDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ComentarioSimpleDTO> findAllSimpleByUserId(Long userId) {
        return comentarioRepository.findAllByUserId(userId).stream()
                .map(comentarioMapper::toSimpleDTO)
                .toList();
    }

    @Transactional
    public ComentarioSimpleDTO updateComentario(Long id, ComentarioContentRequestDTO dto) {
        Comentario comentario = getComentarioOrThrow(id);

        comentario.setContent(dto.content());
        comentarioRepository.save(comentario);

        return comentarioMapper.toSimpleDTO(comentario);
    }

    @Transactional
    public void hardDelete(Long id) {
        Comentario comentario = getComentarioOrThrow(id);

        comentario.getReplies().forEach(replie -> {
            replie.setReplyTo(null);
        });

        comentarioRepository.delete(comentario);
    }

    // Métodos auxiliares
    public Comentario getComentarioOrThrow(Long id) {
        return comentarioRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Comentario con el ID " + id + " no encontrado"));
    }
}
