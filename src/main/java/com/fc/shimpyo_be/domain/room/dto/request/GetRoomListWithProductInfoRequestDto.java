package com.fc.shimpyo_be.domain.room.dto.request;

import jakarta.validation.constraints.Size;

import java.util.List;

public record GetRoomListWithProductInfoRequestDto(
    @Size(min = 1, max = 3, message = "최소 1개, 최대 3개의 객실 식별자 정보가 필요합니다.")
    List<Long> roomIds
) {
}
