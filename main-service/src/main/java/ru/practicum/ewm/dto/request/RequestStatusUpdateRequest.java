package ru.practicum.ewm.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import ru.practicum.ewm.model.RequestStatus;

import java.util.List;

@Data
@AllArgsConstructor
public class RequestStatusUpdateRequest {
    private List<Long> requestIds;
    @NotNull
    private RequestStatus status;
}
