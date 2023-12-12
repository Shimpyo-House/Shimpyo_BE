package com.fc.shimpyo_be.domain.reservation.controller;

import com.fc.shimpyo_be.domain.reservation.dto.request.PreoccupyRoomsRequestDto;
import com.fc.shimpyo_be.domain.reservation.dto.request.ReleaseRoomsRequestDto;
import com.fc.shimpyo_be.domain.reservation.dto.request.SaveReservationRequestDto;
import com.fc.shimpyo_be.domain.reservation.dto.response.SaveReservationResponseDto;
import com.fc.shimpyo_be.domain.reservation.dto.response.ValidatePreoccupyResultResponseDto;
import com.fc.shimpyo_be.domain.reservation.facade.PreoccupyRoomsLockFacade;
import com.fc.shimpyo_be.domain.reservation.facade.ReservationLockFacade;
import com.fc.shimpyo_be.domain.reservation.service.ReservationService;
import com.fc.shimpyo_be.global.common.ResponseDto;
import com.fc.shimpyo_be.global.util.SecurityUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/reservations")
@RestController
public class ReservationRestController {

    private final ReservationService reservationService;
    private final PreoccupyRoomsLockFacade preoccupyRoomsLockFacade;
    private final ReservationLockFacade reservationLockFacade;
    private final SecurityUtil securityUtil;

    @PostMapping
    public ResponseEntity<ResponseDto<SaveReservationResponseDto>> saveReservation(
        @Valid @RequestBody SaveReservationRequestDto request
    ) {
        log.debug("[api][POST] /api/reservations");

        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(
                ResponseDto.res(
                    HttpStatus.CREATED,
                    reservationLockFacade.saveReservation(securityUtil.getCurrentMemberId(), request),
                    "예약 결제가 정상적으로 완료되었습니다."
                )
            );
    }

    @GetMapping
    public ResponseEntity<ResponseDto<Page<?>>> getReservationList(
        @PageableDefault(size = 10, page = 0, sort = "id", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        log.debug("[api][GET] /api/reservations");

        return ResponseEntity
            .status(HttpStatus.OK)
            .body(
                ResponseDto.res(
                    HttpStatus.OK,
                    reservationService.getReservationInfoList(securityUtil.getCurrentMemberId(), pageable),
                    "전체 주문 목록 조회 성공"
                )
            );
    }

    @PostMapping("/preoccupy")
    public ResponseEntity<ResponseDto<ValidatePreoccupyResultResponseDto>> checkAvailableAndPreoccupy(
        @Valid @RequestBody PreoccupyRoomsRequestDto request
    ) {
        log.debug("[api][POST] /api/reservations/preoccupy");

        return ResponseEntity
            .status(HttpStatus.OK)
            .body(
                ResponseDto.res(
                    HttpStatus.OK,
                    preoccupyRoomsLockFacade.checkAvailableAndPreoccupy(securityUtil.getCurrentMemberId(), request),
                    "예약 가능 유효성 검사와 객실 선점이 정상적으로 완료되었습니다."
                )
            );
    }

    @PostMapping("/release")
    public ResponseEntity<ResponseDto<Void>> releaseRooms(
        @Valid @RequestBody ReleaseRoomsRequestDto request
    ) {
        log.debug("[api][POST] /api/reservations/release");

        reservationService.releaseRooms(securityUtil.getCurrentMemberId(), request);

        return ResponseEntity
            .status(HttpStatus.OK)
            .body(
                ResponseDto.res(HttpStatus.OK, "객실 예약 선점이 정상적으로 취소 처리 되었습니다.")
            );
    }
}
