package org.alvarub.workouttrackerproject.persistence.dto.categoria;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CategoriaRequestDTO implements Serializable {

    @NotBlank(message = "El campo name es obligatorio")
    private String name;

    @Builder.Default
    private Boolean active = true;

}