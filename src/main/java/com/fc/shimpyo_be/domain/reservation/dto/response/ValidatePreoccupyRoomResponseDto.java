package com.fc.shimpyo_be.domain.reservation.dto.response;

import lombok.Builder;

@Builder
public record ValidatePreoccupyRoomResponseDto(
    Long roomCode,
    String startDate,
    String endDate,
    Long roomId
) {
}
