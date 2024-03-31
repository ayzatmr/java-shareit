package ru.practicum.shareit.item.repository;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

@DataJpaTest
@Transactional
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class CommentRepositoryTest {

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private UserRepository userRepository;

    private Item item;

    private Comment comment;

    @BeforeEach
    void beforeEach() {
        User user = userRepository.save(User.builder()
                .name("user")
                .email("john@email.com")
                .build());
        item = itemRepository.save(Item.builder()
                .name("item")
                .description("description")
                .available(true)
                .owner(user)
                .build());
        comment = commentRepository.save(Comment.builder()
                .text("comment")
                .created(LocalDateTime.now())
                .author(user)
                .item(item)
                .build());
    }

    @AfterAll
    void afterAll() {
        itemRepository.deleteAll();
        commentRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void findAllByItemId() {
        List<Comment> comments = commentRepository.findAllByItemId(item.getId());
        assertThat(comments.size(), is(1));
        assertThat(comments, is(List.of(comment)));
    }

    @Test
    void findAllByItemIdIn() {
        List<Comment> comments = commentRepository.findAllByItemIdIn(List.of(item.getId()));

        assertThat(comments, notNullValue());
        assertThat(comments.size(), is(1));
        assertThat(comments, is(List.of(comment)));
    }
}