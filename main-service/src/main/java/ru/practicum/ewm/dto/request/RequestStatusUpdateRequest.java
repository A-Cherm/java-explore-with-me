package ru.practicum.ewm.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import ru.practicum.ewm.model.RequestStatus;

import java.util.List;

@Data
@AllArgsConstructor
@Schema(description = "Запрос на изменение статуса указанных заявок")
public class RequestStatusUpdateRequest {
    @Schema(description = "Список id заявок")
    private List<Long> requestIds;
    @NotNull
    @Schema(description = "Новый статус заявок")
    private RequestStatus status;
}
