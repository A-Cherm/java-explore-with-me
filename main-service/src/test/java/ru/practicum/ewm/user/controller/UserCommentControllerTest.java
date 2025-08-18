package ru.practicum.ewm.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import ru.practicum.ewm.dto.comment.CommentDto;
import ru.practicum.ewm.dto.comment.NewCommentDto;
import ru.practicum.ewm.dto.comment.UpdateCommentDto;
import ru.practicum.ewm.dto.user.UserShortDto;
import ru.practicum.ewm.user.service.UserCommentService;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserCommentController.class)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class UserCommentControllerTest {
    private final MockMvc mvc;
    private final ObjectMapper mapper;
    @MockBean
    private UserCommentService commentService;

    @Test
    void testCreateComment() throws Exception {
        LocalDateTime date = LocalDateTime.of(2000, 1, 1, 0, 0, 0);
        NewCommentDto newCommentDto = new NewCommentDto("abc");
        CommentDto commentDto = new CommentDto(1L, new UserShortDto(1L, "aaa"), "abc", date);

        when(commentService.createComment(1L, 2L, newCommentDto))
                .thenReturn(commentDto);

        MvcResult result = mvc.perform(post("/users/1/events/2/comments")
                        .content(mapper.writeValueAsString(newCommentDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andReturn();
        String json = result.getResponse().getContentAsString();
        CommentDto createdComment = mapper.readValue(json, CommentDto.class);

        assertThat(createdComment).isEqualTo(commentDto);

        verify(commentService, times(1))
                .createComment(1L, 2L, newCommentDto);
    }

    @Test
    void testUpdateComment() throws Exception {
        LocalDateTime date = LocalDateTime.of(2000, 1, 1, 0, 0, 0);
        UpdateCommentDto updateCommentDto = new UpdateCommentDto("abc");
        CommentDto commentDto = new CommentDto(1L, new UserShortDto(1L, "aaa"), "abc", date);

        when(commentService.updateComment(1L, 1L, updateCommentDto))
                .thenReturn(commentDto);

        MvcResult result = mvc.perform(patch("/users/1/comments/1")
                        .content(mapper.writeValueAsString(updateCommentDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        String json = result.getResponse().getContentAsString();
        CommentDto createdComment = mapper.readValue(json, CommentDto.class);

        assertThat(createdComment).isEqualTo(commentDto);

        verify(commentService, times(1))
                .updateComment(1L, 1L, updateCommentDto);
    }

    @Test
    void testDeleteComment() throws Exception {
        mvc.perform(delete("/users/1/comments/1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent())
                .andReturn();

        verify(commentService, times(1))
                .deleteComment(1L, 1L);
    }
}