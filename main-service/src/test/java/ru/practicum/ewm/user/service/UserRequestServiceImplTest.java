package ru.practicum.ewm.user.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import ru.practicum.ewm.admin.service.*;
import ru.practicum.ewm.client.StatsClient;
import ru.practicum.ewm.config.QuerydslConfig;
import ru.practicum.ewm.dto.*;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.guest.service.GuestCategoryServiceImpl;
import ru.practicum.ewm.guest.service.GuestEventServiceImpl;
import ru.practicum.ewm.model.Request;
import ru.practicum.ewm.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DataJpaTest
@Import({UserRequestServiceImpl.class, UserServiceImpl.class, UserEventServiceImpl.class,
        GuestCategoryServiceImpl.class, AdminCategoryServiceImpl.class, AdminEventServiceImpl.class,
        GuestEventServiceImpl.class, QuerydslConfig.class})
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class UserRequestServiceImplTest {
    private final UserRequestService requestService;
    private final UserService userService;
    private final UserEventService userEventService;
    private final AdminEventService adminEventService;
    private final AdminCategoryService categoryService;
    @MockBean
    private StatsClient statsClient;

    private UserDto userDto1;
    private UserDto userDto2;
    private EventFullDto eventDto;

    @BeforeEach
    void setUp() {
        LocalDateTime date = LocalDateTime.of(LocalDateTime.now().getYear(), 1, 1, 0, 0, 0).plusYears(1);
        Location location = new Location((float) 10.0, (float) 20.0);
        userDto1 = userService.createUser(new NewUserDto("a", "a@mail"));
        userDto2 = userService.createUser(new NewUserDto("b", "b@mail"));
        CategoryDto categoryDto = categoryService.createCategory(new CategoryDto(null, "abc"));
        eventDto = userEventService.createEvent(userDto1.getId(), new NewEventDto("a".repeat(20), categoryDto.getId(), "b".repeat(20), date,
                location, true, 5, false, "c".repeat(5)));
        UpdateEventDto updateEventDto = new UpdateEventDto();
        updateEventDto.setStateAction(EventStateAction.PUBLISH_EVENT);
        adminEventService.updateEvent(eventDto.getId(), updateEventDto);
    }

    @Test
    void testGetRequests() {
        RequestDto createdRequest = requestService.createRequest(userDto2.getId(), eventDto.getId());

        List<RequestDto> requests = requestService.getRequests(userDto2.getId());

        assertThat(requests).isNotNull().hasSize(1);
        assertThat(requests.getFirst()).isEqualTo(createdRequest);
    }

    @Test
    void testCreateRequest() {
        RequestDto createdRequest = requestService.createRequest(userDto2.getId(), eventDto.getId());

        assertThat(createdRequest)
                .hasFieldOrPropertyWithValue("requester", userDto2.getId())
                .hasFieldOrPropertyWithValue("event", eventDto.getId())
                .hasFieldOrProperty("created");
    }

    @Test
    void testCancelRequest() {
        RequestDto createdRequest = requestService.createRequest(userDto2.getId(), eventDto.getId());

        RequestDto cancelledRequest = requestService.cancelRequest(userDto2.getId(), createdRequest.getId());

        assertThat(cancelledRequest)
                .usingRecursiveComparison()
                .ignoringFields("status")
                .isEqualTo(createdRequest);
    }

    @Test
    void testValidateRequest() {
        RequestDto createdRequest = requestService.createRequest(userDto2.getId(), eventDto.getId());
        User user = new User(userDto2.getId(), userDto2.getName(), userDto2.getEmail());
        Request request = requestService.validateRequest(createdRequest.getId());

        assertThat(request)
                .hasFieldOrProperty("requester")
                .hasFieldOrProperty("event")
                .hasFieldOrProperty("created");
        assertThat(request.getRequester().getName()).isEqualTo(user.getName());
        assertThrows(NotFoundException.class,
                () -> requestService.validateRequest(createdRequest.getId() + 1));
    }
}