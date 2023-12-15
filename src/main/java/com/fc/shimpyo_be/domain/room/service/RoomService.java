package com.fc.shimpyo_be.domain.room.service;

import com.fc.shimpyo_be.domain.product.entity.Product;
import com.fc.shimpyo_be.domain.product.exception.ProductNotFoundException;
import com.fc.shimpyo_be.domain.room.exception.RoomNotFoundException;
import com.fc.shimpyo_be.domain.room.dto.response.RoomWithProductResponseDto;
import com.fc.shimpyo_be.domain.room.entity.Room;
import com.fc.shimpyo_be.domain.room.repository.RoomRepository;
import com.fc.shimpyo_be.domain.room.util.RoomMapper;
import com.fc.shimpyo_be.global.exception.InvalidParameterException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class RoomService {

    private final RoomRepository roomRepository;

    public List<RoomWithProductResponseDto> getRoomsWithProductInfo(List<Long> roomIds) {

        return roomRepository.findAllInIdsWithProductAndPrice(roomIds)
            .stream()
            .map(RoomMapper::toRoomWithProductResponse)
            .toList();
    }

    public List<Long> getRoomIdsByCode(Long roomCode) {

        return roomRepository.findIdsByCode(roomCode);
    }

    public Room getRoomById(final Long roomId) {
        if(roomId == null) {
            throw new InvalidParameterException();
        }
        return roomRepository.findById(roomId).orElseThrow(RoomNotFoundException::new);

    }
}
