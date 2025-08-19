package ru.practicum.ewm.user.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import ru.practicum.ewm.dto.request.RequestDto;
import ru.practicum.ewm.model.RequestStatus;
import ru.practicum.ewm.user.service.UserRequestService;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserRequestsController.class)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class UserRequestsControllerTest {
    private final MockMvc mvc;
    private final ObjectMapper mapper;
    @MockBean
    private UserRequestService requestService;

    @Test
    void testGetRequests() throws Exception {
        LocalDateTime date = LocalDateTime.of(LocalDateTime.now().getYear(), 1, 1, 0, 0, 0);
        RequestDto requestDto = new RequestDto(1L, 2L, 3L, date, RequestStatus.PENDING);

        when(requestService.getRequests(2L))
                .thenReturn(List.of(requestDto));

        MvcResult result = mvc.perform(get("/users/2/requests")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        String json = result.getResponse().getContentAsString();
        List<RequestDto> requests = mapper.readValue(json, new TypeReference<>() {});

        assertThat(requests).isNotNull().hasSize(1);
        assertThat(requests.getFirst()).isEqualTo(requestDto);

        verify(requestService, times(1))
                .getRequests(2L);
    }

    @Test
    void testCreateRequest() throws Exception {
        LocalDateTime date = LocalDateTime.of(LocalDateTime.now().getYear(), 1, 1, 0, 0, 0);
        RequestDto requestDto = new RequestDto(1L, 2L, 3L, date, RequestStatus.PENDING);

        when(requestService.createRequest(2L, 3L))
                .thenReturn(requestDto);

        MvcResult result = mvc.perform(post("/users/2/requests?eventId=3")
                        .content(mapper.writeValueAsString(requestDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andReturn();
        String json = result.getResponse().getContentAsString();
        RequestDto createdRequest = mapper.readValue(json, RequestDto.class);

        assertThat(createdRequest).isEqualTo(requestDto);

        verify(requestService, times(1))
                .createRequest(2L, 3L);
    }

    @Test
    void testCancelRequest() throws Exception {
        LocalDateTime date = LocalDateTime.of(LocalDateTime.now().getYear(), 1, 1, 0, 0, 0);
        RequestDto requestDto = new RequestDto(1L, 2L, 3L, date, RequestStatus.PENDING);

        when(requestService.cancelRequest(2L, 1L))
                .thenReturn(requestDto);

        MvcResult result = mvc.perform(patch("/users/2/requests/1/cancel")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        String json = result.getResponse().getContentAsString();
        RequestDto canceledRequest = mapper.readValue(json, RequestDto.class);

        assertThat(canceledRequest).isEqualTo(requestDto);

        verify(requestService, times(1))
                .cancelRequest(2L, 1L);
    }
}