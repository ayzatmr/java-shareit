package ru.practicum.shareit.request.integration;

import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.NewItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import javax.transaction.Transactional;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Transactional
class ItemRequestServiceIntegrationTest {

    @Autowired
    private ItemRequestService itemRequestService;

    @Autowired
    private UserRepository userRepository;

    private User user;

    private NewItemRequestDto newItemRequestDto;

    @BeforeAll
    public void beforeAll() {
        user = userRepository.save(User.builder()
                .name("name")
                .email("email@email.com")
                .build());
        newItemRequestDto = new NewItemRequestDto("description");
    }

    @Test
    void addNewItemRequest() {
        ItemRequestDto itemRequestDto = itemRequestService.create(user.getId(), newItemRequestDto);

        assertNotNull(itemRequestDto.getId());
        assertThat(itemRequestDto.getDescription(), is(newItemRequestDto.getDescription()));
        assertNotNull(itemRequestDto.getCreated());
    }

    @Test
    void getUserItemRequests() {
        ItemRequestDto itemRequestDto = itemRequestService.create(user.getId(), newItemRequestDto);
        List<ItemRequestDto> requests = itemRequestService.getUserItemRequests(user.getId());
        assertThat(requests, is(List.of(itemRequestDto)));
    }

    @Test
    @SneakyThrows
    void getAvailableItemRequests() {
        User user2 = userRepository.save(User.builder()
                .name("user2")
                .email("user2@email.com")
                .build());
        ItemRequestDto itemRequestDto = itemRequestService.create(user2.getId(), newItemRequestDto);

        List<ItemRequestDto> availableItemRequests = itemRequestService
                .getAvailableItemRequests(user.getId(), 0, 50);

        assertThat(availableItemRequests, notNullValue());
        assertThat(availableItemRequests, contains(itemRequestDto));
    }

    @Test
    void getItemRequestById() {
        ItemRequestDto savedRequest = itemRequestService.create(user.getId(), newItemRequestDto);
        ItemRequestDto request = itemRequestService.get(user.getId(), savedRequest.getId());
        assertThat(request, is(savedRequest));
    }
}