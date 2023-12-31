package com.fc.shimpyo_be.domain.room.dto.response;

import lombok.Builder;

@Builder
public record RoomWithProductResponseDto(
    Long productId,
    String productName,
    String productThumbnail,
    String productAddress,
    String productDetailAddress,
    Long roomId,
    String roomName,
    Integer standard,
    Integer capacity,
    String checkIn,
    String checkOut,
    Long price
) {
}
