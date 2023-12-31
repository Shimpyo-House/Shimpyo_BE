package com.fc.shimpyo_be.domain.room.service;

import com.fc.shimpyo_be.domain.room.exception.RoomNotFoundException;
import com.fc.shimpyo_be.domain.room.dto.response.RoomWithProductResponseDto;
import com.fc.shimpyo_be.domain.room.entity.Room;
import com.fc.shimpyo_be.domain.room.repository.RoomRepository;
import com.fc.shimpyo_be.domain.room.util.RoomMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class RoomService {

    private final RoomRepository roomRepository;

    @Transactional(readOnly = true)
    public List<RoomWithProductResponseDto> getRoomsWithProductInfo(List<Long> roomIds) {

        return roomRepository.findAllInIdsWithProductAndPrice(roomIds)
            .stream()
            .map(RoomMapper::toRoomWithProductResponse)
            .toList();
    }

    @Transactional(readOnly = true)
    public List<Long> getRoomIdsByCode(Long roomCode) {

        return roomRepository.findIdsByCode(roomCode);
    }

    @Transactional(readOnly = true)
    public Room getRoomById(Long id) {
        return roomRepository.findById(id)
            .orElseThrow(RoomNotFoundException::new);
    }
}
