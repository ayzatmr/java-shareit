package ru.practicum.shareit.item.mapper;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;

class CommentMapperTest {

    private final CommentMapper commentMapper = new CommentMapperImpl();

    private final User booker = User.builder()
            .id(1L)
            .name("vlad")
            .email("vlad@email.com")
            .build();
    private final Comment comment = Comment.builder()
            .id(1L)
            .text("text")
            .author(booker)
            .created(LocalDateTime.now())
            .build();


    @Test
    void toDto() {
        CommentDto newCommentDto = commentMapper.toDto(comment);
        assertThat(newCommentDto.getId(), is(comment.getId()));
        assertThat(newCommentDto.getText(), is(comment.getText()));
        assertThat(newCommentDto.getAuthorName(), is(comment.getAuthor().getName()));
    }

    @Test
    void toDtoList() {
        List<CommentDto> newCommentDto = commentMapper.toDtoList(List.of(comment));
        assertThat(newCommentDto.get(0).getId(), is(comment.getId()));
        assertThat(newCommentDto.get(0).getText(), is(comment.getText()));
        assertThat(newCommentDto.get(0).getAuthorName(), is(comment.getAuthor().getName()));
    }

    @Test
    void toNullDto() {
        CommentDto newCommentDto = commentMapper.toDto(null);
        assertThat(newCommentDto, nullValue());
    }

    @Test
    void toNullDtoList() {
        List<CommentDto> commentDtos = commentMapper.toDtoList(null);
        assertThat(commentDtos, nullValue());
    }
}