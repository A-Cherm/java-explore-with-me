package ru.practicum.ewm.admin.controller;

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
import org.springframework.web.util.UriUtils;
import ru.practicum.ewm.admin.service.AdminCommentService;
import ru.practicum.ewm.dto.comment.CommentDto;
import ru.practicum.ewm.dto.user.UserShortDto;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AdminCommentController.class)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class AdminCommentControllerTest {
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private final MockMvc mvc;
    private final ObjectMapper mapper;
    @MockBean
    private AdminCommentService commentService;

    @Test
    void testGetComments() throws Exception {
        LocalDateTime date = LocalDateTime.of(2000, 1, 1, 0, 0, 0);
        CommentDto commentDto1 = new CommentDto(1L, new UserShortDto(1L, "aaa"), "abc", date);
        CommentDto commentDto2 = new CommentDto(1L, new UserShortDto(2L, "bbb"), "qwe", date.plusDays(1));
        LocalDateTime startDate = date.minusMonths(1);
        LocalDateTime endDate = date.plusDays(1);
        String encodedStart = UriUtils.encode(formatter.format(startDate), StandardCharsets.UTF_8);
        String encodedEnd = UriUtils.encode(formatter.format(endDate), StandardCharsets.UTF_8);

        when(commentService.getComments(List.of(1L), encodedStart, encodedEnd, 1, 3))
                .thenReturn(List.of(commentDto1, commentDto2));

        String url = "/admin/comments?events=1&rangeStart=" + encodedStart
                + "&rangeEnd=" + encodedEnd + "&from=1&size=3";
        MvcResult result = mvc.perform(get(url)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        String json = result.getResponse().getContentAsString();
        List<CommentDto> comments = mapper.readValue(json, new TypeReference<>() {});

        assertThat(comments).hasSize(2).contains(commentDto1, commentDto2);

        verify(commentService, times(1))
                .getComments(List.of(1L), encodedStart, encodedEnd, 1, 3);
    }

    @Test
    void testDeleteComment() throws Exception {
        mvc.perform(delete("/admin/comments/1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        verify(commentService, times(1))
                .deleteComment(1L);
    }
}