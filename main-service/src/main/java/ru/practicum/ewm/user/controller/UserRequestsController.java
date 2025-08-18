package ru.practicum.ewm.user.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.dto.RequestDto;
import ru.practicum.ewm.user.service.UserRequestService;

import java.util.List;

@RestController
@RequestMapping("/users/{userId}/requests")
@Slf4j
@RequiredArgsConstructor
public class UserRequestsController {
    private final UserRequestService requestService;

    @GetMapping
    public List<RequestDto> getRequests(@PathVariable Long userId) {
        List<RequestDto> requests = requestService.getRequests(userId);

        log.info("Возвращается список заявок пользователя {}: {}", userId, requests);
        return requests;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public RequestDto createRequest(@PathVariable Long userId,
                                    @RequestParam Long eventId) {
        RequestDto request = requestService.createRequest(userId, eventId);

        log.info("Создана заявка {}", request);
        return request;
    }

    @PatchMapping("/{requestId}/cancel")
    public RequestDto cancelRequest(@PathVariable Long userId,
                                    @PathVariable Long requestId) {
        RequestDto request = requestService.cancelRequest(userId, requestId);

        log.info("Удалена заявка {}", request);
        return request;
    }
}
