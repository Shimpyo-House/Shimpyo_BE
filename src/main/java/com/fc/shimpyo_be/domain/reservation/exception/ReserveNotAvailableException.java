package com.fc.shimpyo_be.domain.reservation.exception;

import com.fc.shimpyo_be.domain.reservation.dto.response.ValidateReservationResultResponseDto;
import com.fc.shimpyo_be.global.exception.ErrorCode;
import lombok.Getter;

@Getter
public class ReserveNotAvailableException extends RuntimeException {

    private final ErrorCode errorCode;
    private ValidateReservationResultResponseDto data;

    public ReserveNotAvailableException() {
        super(ErrorCode.UNAVAILABLE_ROOMS.getSimpleMessage());
        this.errorCode = ErrorCode.UNAVAILABLE_ROOMS;
    }

    public ReserveNotAvailableException(ValidateReservationResultResponseDto data) {
        super(ErrorCode.UNAVAILABLE_ROOMS.getSimpleMessage());
        this.errorCode = ErrorCode.UNAVAILABLE_ROOMS;
        this.data = data;
    }
}
