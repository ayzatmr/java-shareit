package ru.practicum.shareit.request.repository;

import lombok.SneakyThrows;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import javax.transaction.Transactional;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

@DataJpaTest
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Transactional
class ItemRequestStorageTest {

    @Autowired
    private ItemRequestRepository itemRequestRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private UserRepository userRepository;

    private User user;
    private User user2;
    private ItemRequest itemRequest;

    @BeforeAll
    void beforeAll() {
        user = userRepository.save(
                User.builder()
                        .name("user")
                        .email("tany@email.com")
                        .build());
        user2 = userRepository.save(
                User.builder()
                        .name("user2")
                        .email("dan@email.com")
                        .build());
    }

    @BeforeEach
    public void beforeEach() {
        itemRequest = itemRequestRepository.save(
                ItemRequest.builder()
                        .user(user2)
                        .description("description")
                        .build());
        itemRepository.save(
                Item.builder()
                        .name("item")
                        .description("description")
                        .available(true)
                        .owner(user)
                        .request(itemRequest)
                        .build());
    }

    @AfterAll
    void afterAll() {
        itemRepository.deleteAll();
        itemRequestRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    public void getUserItemRequests() {
        List<ItemRequest> requests = itemRequestRepository.getUserItemRequests(user2.getId(), null);

        assertThat(requests.size(), is(1));
        assertThat(requests.get(0).getId(), is(itemRequest.getId()));
        assertThat(requests.get(0).getCreated(), is(notNullValue()));
    }

    @Test
    @SneakyThrows
    public void findAvailableRequests() {
        Page<ItemRequest> requests = itemRequestRepository.findAvailableRequests(user.getId(), null);

        assertThat(requests.getContent().size(), is(1));
        assertThat(requests.getContent(), is(List.of(itemRequest)));
    }
}