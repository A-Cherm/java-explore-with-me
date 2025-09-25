package ru.practicum.ewm.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
@Schema(description = "Результат изменения id заявок")
public class RequestStatusUpdateResult {
    @Schema(description = "Список подтверждённых заявок")
    private List<RequestDto> confirmedRequests;
    @Schema(description = "Список отменённых заявок")
    private List<RequestDto> rejectedRequests;
}
