package ru.practicum.shareit.request;

import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.util.List;

public interface ItemRequestService {
    ItemRequestDto create(ItemRequestDto itemRequestDto, Long userId);

    List<ItemRequestDto> getByRequester(Long requesterId);

    List<ItemRequestDto> getAll(Long userId, int from, int size);

    ItemRequestDto getById(Long requestId, Long userId);
}