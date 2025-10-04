package org.alvarub.workouttrackerproject.service;

import lombok.RequiredArgsConstructor;
import org.alvarub.workouttrackerproject.exception.BusinessException;
import org.alvarub.workouttrackerproject.exception.ForbiddenOperationException;
import org.alvarub.workouttrackerproject.exception.NotFoundException;
import org.alvarub.workouttrackerproject.mapper.ComentarioMapper;
import org.alvarub.workouttrackerproject.persistence.dto.comentario.ComentarioContentRequestDTO;
import org.alvarub.workouttrackerproject.persistence.dto.comentario.ComentarioRequestDTO;
import org.alvarub.workouttrackerproject.persistence.dto.comentario.ComentarioResponseDTO;
import org.alvarub.workouttrackerproject.persistence.dto.comentario.ComentarioSimpleDTO;
import org.alvarub.workouttrackerproject.persistence.entity.Comentario;
import org.alvarub.workouttrackerproject.persistence.entity.Rutina;
import org.alvarub.workouttrackerproject.persistence.entity.Usuario;
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
    public ComentarioResponseDTO save(ComentarioRequestDTO dto, String auth0UserId) {
        Comentario comentario = comentarioMapper.toEntity(dto);

        Usuario usuario = usuarioService.getUsuarioByAuth0IdOrThrow(auth0UserId, true);
        Rutina rutina = rutinaService.getRutinaOrThrow(dto.getRoutineId());

        if (!Boolean.TRUE.equals(rutina.getIsPublic())) {
            if (!auth0UserId.equals(rutina.getUser().getAuth0Id())) {
                throw new ForbiddenOperationException("Usuario sin permiso para comentar en una rutina privada que no le pertenece");
            }
        }

        comentario.setUser(usuario);
        comentario.setRoutine(rutina);

        if (dto.getReplyToId() != null) {
            Comentario replyTo = getComentarioOrThrow(dto.getReplyToId());

            if (!replyTo.getRoutine().equals(comentario.getRoutine())) {
                throw new BusinessException("No es posible responder a un comentario de una rutina distinta");
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

    @Transactional(readOnly = true)
    public List<ComentarioResponseDTO> findAllByUser(String auth0UserId) {
        return comentarioRepository.findByUser_Auth0Id(auth0UserId).stream()
                .map(comentarioMapper::toResponseDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ComentarioSimpleDTO> findAllSimpleByUser(String auth0UserId) {
        return comentarioRepository.findByUser_Auth0Id(auth0UserId).stream()
                .map(comentarioMapper::toSimpleDTO)
                .toList();
    }

    @Transactional
    public ComentarioSimpleDTO updateComentario(Long id, String auth0UserId, ComentarioContentRequestDTO dto) {
        Comentario comentario = getComentarioOrThrow(id);

        if (!comentario.getUser().getAuth0Id().equals(auth0UserId)) {
            throw new ForbiddenOperationException("Usuario sin permiso para modificar el comentario de otro usuario");
        }

        comentario.setContent(dto.content());

        return comentarioMapper.toSimpleDTO(comentario);
    }

    @Transactional
    public ComentarioSimpleDTO toggleLikeOnComment(Long id, String auth0UserId) {
        Comentario comentario = getComentarioOrThrow(id);
        Usuario usuario = usuarioService.getUsuarioByAuth0IdOrThrow(auth0UserId, true);

        if (usuario.getLikedComments().contains(comentario)) {
            usuario.getLikedComments().remove(comentario);
            comentario.setLikes(comentario.getLikes() - 1);
        } else {
            usuario.getLikedComments().add(comentario);
            comentario.setLikes(comentario.getLikes() + 1);
        }

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
