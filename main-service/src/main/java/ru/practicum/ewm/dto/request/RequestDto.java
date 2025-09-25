package ru.practicum.ewm.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.ewm.model.RequestStatus;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Сущность заявки на участие в событии")
public class RequestDto {
    @Schema(description = "Идентификатор заявки", example = "1")
    private Long id;
    @Schema(description = "Идентификатор автора завки", example = "2")
    private Long requester;
    @Schema(description = "Идентификатор события", example = "3")
    private Long event;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss:SSS")
    @Schema(accessMode = Schema.AccessMode.READ_ONLY, description = "Дата создания заявки",
            example = "2000-01-01T00:00:00:000", type = "string")
    private LocalDateTime created;
    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    private RequestStatus status;
}
