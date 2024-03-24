package ru.practicum.shareit.request.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.NewItemRequestDto;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.Mockito.*;
import static ru.practicum.shareit.request.service.ItemRequestServiceImpl.REQUEST_SORTING;

@ExtendWith(MockitoExtension.class)
class ItemRequestServiceTest {

    @Mock
    private ItemRequestRepository itemRequestRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ItemRequestMapper itemRequestMapper;
    @InjectMocks
    private ItemRequestServiceImpl itemRequestService;
    private ItemRequest itemRequest;
    private NewItemRequestDto newItemRequestDto;
    private User user;

    @BeforeEach
    public void beforeEach() {
        user = User.builder()
                .id(1L)
                .name("user")
                .email("manuel@email.com")
                .build();
        newItemRequestDto = NewItemRequestDto.builder()
                .description("description")
                .build();
        itemRequest = ItemRequest.builder()
                .id(1L)
                .user(user)
                .description("description")
                .build();
    }

    @Test
    public void addNewItemRequest() {

        when(userRepository.findById(user.getId()))
                .thenReturn(Optional.of(user));
        when(itemRequestRepository.save(any()))
                .thenReturn(itemRequest);
        when(itemRequestMapper.toModel(newItemRequestDto))
                .thenReturn(itemRequest);

        itemRequestService.create(user.getId(), newItemRequestDto);

        verify(userRepository, times(1)).findById(user.getId());
        verify(itemRequestRepository, times(1)).save(any());
    }

    @Test
    public void getUserItemRequests() {
        when(userRepository.findById(user.getId()))
                .thenReturn(Optional.of(user));
        when(itemRequestRepository.getUserItemRequests(user.getId(), REQUEST_SORTING))
                .thenReturn(Collections.emptyList());


        List<ItemRequestDto> requests = itemRequestService.getUserItemRequests(user.getId());

        assertThat(requests, is(notNullValue()));
        assertThat(requests, is(Collections.emptyList()));

        verify(userRepository, times(1)).findById(user.getId());
        verify(itemRequestRepository, times(1)).getUserItemRequests(user.getId(), REQUEST_SORTING);
    }

    @Test
    public void getAvailableItemRequests() {
        when(userRepository.findById(user.getId()))
                .thenReturn(Optional.of(user));
        when(itemRequestRepository.findAvailableRequests(anyLong(), any(Pageable.class)))
                .thenReturn(Page.empty());

        itemRequestService.getAvailableItemRequests(user.getId(), 0, 50);

        verify(itemRequestRepository, times(1)).findAvailableRequests(anyLong(), any(Pageable.class));
    }

    @Test
    public void getItemRequestById() {
        when(userRepository.findById(user.getId()))
                .thenReturn(Optional.of(user));
        when(itemRequestRepository.findById(itemRequest.getId()))
                .thenReturn(Optional.of(itemRequest));

        itemRequestService.get(user.getId(), itemRequest.getId());

        verify(userRepository, times(1)).findById(user.getId());
        verify(itemRequestRepository, times(1)).findById(itemRequest.getId());
    }
}