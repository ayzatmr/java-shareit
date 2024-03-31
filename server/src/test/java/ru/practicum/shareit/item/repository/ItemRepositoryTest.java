package ru.practicum.shareit.item.repository;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import javax.transaction.Transactional;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

@DataJpaTest
@Transactional
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ItemRepositoryTest {

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private UserRepository userRepository;

    private Item item;

    private User user;

    @BeforeEach
    void beforeEach() {
        user = userRepository.save(User.builder()
                .name("ayzat")
                .email("ayzat@email.com")
                .build());
        item = itemRepository.save(Item.builder()
                .name("item")
                .description("description")
                .available(true)
                .owner(user)
                .build());
    }

    @AfterAll
    void afterAll() {
        itemRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void findAllByOwnerIdOrderById() {
        List<Item> items = itemRepository.findAllByOwnerIdOrderById(user.getId(), null);

        assertThat(items.size(), is(1));
        assertThat(items.get(0).getId(), is(item.getId()));
    }

    @Test
    void findAllByNameOrDescription() {
        List<Item> items = itemRepository.findAllByNameOrDescription(item.getDescription(), null);
        assertThat(items.size(), is(1));
        assertThat(items.get(0).getId(), is(item.getId()));
    }
}