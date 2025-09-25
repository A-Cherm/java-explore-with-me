package ru.practicum.ewm.dto.category;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

@Data
@AllArgsConstructor
@Schema(description = "Сущность категории")
public class CategoryDto {
    @Schema(accessMode = Schema.AccessMode.READ_ONLY, description = "Идентификатор категории", example = "1")
    private Long id;
    @NotBlank
    @Length(max = 50)
    @Schema(description = "Название категории", example = "Выставки", maxLength = 50)
    private String name;
}
