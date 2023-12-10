package com.fc.shimpyo_be.domain.reservation.unit.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fc.shimpyo_be.config.AbstractContainersSupport;
import com.fc.shimpyo_be.domain.reservation.dto.request.*;
import com.fc.shimpyo_be.domain.reservation.dto.response.ReservationInfoResponseDto;
import com.fc.shimpyo_be.domain.reservation.dto.response.SaveReservationResponseDto;
import com.fc.shimpyo_be.domain.reservation.dto.response.ValidationResultResponseDto;
import com.fc.shimpyo_be.domain.reservation.entity.PayMethod;
import com.fc.shimpyo_be.domain.reservation.facade.PreoccupyRoomsLockFacade;
import com.fc.shimpyo_be.domain.reservation.facade.ReservationLockFacade;
import com.fc.shimpyo_be.domain.reservation.service.ReservationService;
import com.fc.shimpyo_be.domain.reservationproduct.dto.request.ReservationProductRequestDto;
import com.fc.shimpyo_be.domain.reservationproduct.dto.response.ReservationProductResponseDto;
import com.fc.shimpyo_be.global.util.SecurityUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
public class ReservationRestControllerTest extends AbstractContainersSupport {

    private MockMvc mockMvc;

    @MockBean
    private ReservationService reservationService;

    @MockBean
    private PreoccupyRoomsLockFacade preoccupyRoomsLockFacade;

    @MockBean
    private ReservationLockFacade reservationLockFacade;

    @MockBean
    private SecurityUtil securityUtil;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp(@Autowired WebApplicationContext applicationContext) {
        this.mockMvc = MockMvcBuilders
            .webAppContextSetup(applicationContext)
            .apply(springSecurity())
            .alwaysDo(print())
            .build();
    }

    @WithMockUser(roles = "USER")
    @DisplayName("[api][POST][정상] 예약 주문 결제 API 성공 테스트")
    @Test
    void saveReservation_Api_test() throws Exception {
        //given
        String requestUrl = "/api/reservations";

        SaveReservationRequestDto requestDto
            = new SaveReservationRequestDto(
            List.of(
                new ReservationProductRequestDto(
                    1L, "2023-11-20", "2023-11-23",
                    "visitor1", "010-1111-1111", 150000),
                new ReservationProductRequestDto(
                    2L, "2023-11-18", "2023-11-20",
                    "visitor2", "010-2222-2222", 200000)
            ), PayMethod.CREDIT_CARD, 350000
        );

        SaveReservationResponseDto responseDto = new SaveReservationResponseDto(
            1L,
            List.of(
                new ReservationProductResponseDto("숙소1", 1L, "객실1", 2, 3,
                    "2023-11-20", "2023-11-23", "13:00", "12:00",
                    "visitor1", "010-1111-1111", 150000),
                new ReservationProductResponseDto("숙소2", 2L, "객실2", 2, 3,
                    "2023-11-18", "2023-11-20", "13:00", "12:00",
                    "visitor2", "010-2222-2222", 200000)
            ),
            requestDto.payMethod(),
            requestDto.totalPrice(),
            "2023-12-06 10:00:00"
        );

        given(securityUtil.getCurrentMemberId()).willReturn(1L);
        given(reservationLockFacade.saveReservation(anyLong(), any(SaveReservationRequestDto.class)))
            .willReturn(responseDto);

        //when & then
        mockMvc.perform(
                post(requestUrl)
                    .content(objectMapper.writeValueAsString(requestDto))
                    .contentType(MediaType.APPLICATION_JSON)
            )
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.data").isNotEmpty())
            .andExpect(jsonPath("$.data.reservationId").isNumber());
    }

    @WithMockUser(roles = "USER")
    @DisplayName("[api][GET][정상] 전체 주문 목록 조회 API 성공 테스트")
    @Test
    void getReservationInfoList_Api_test() throws Exception {
        //given
        String requestUrl = "/api/reservations";
        int size = 1;
        int page = 0;

        PageRequest pageRequest = PageRequest.of(page, size);
        List<ReservationInfoResponseDto> content
            = List.of(
            new ReservationInfoResponseDto(
                2L,
                3L,
                5L,
                "호텔2",
                "호텔 photoUrl",
                "호텔 주소",
                "호텔 상세 주소",
                1L,
                "객실1",
                "2023-11-23",
                "2023-11-26",
                "14:00",
                "12:00",
                220000,
                "CREDIT_CARD"
            )
        );

        given(securityUtil.getCurrentMemberId()).willReturn(1L);
        given(reservationService.getReservationInfoList(anyLong(), any(Pageable.class)))
            .willReturn(new PageImpl<>(content, pageRequest, 3));

        //when & then
        mockMvc.perform(
                get(requestUrl)
                    .param("size", String.valueOf(size))
                    .param("page", String.valueOf(page))
            )
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data.totalPages", is(3)))
            .andExpect(jsonPath("$.data.totalElements", is(3)));
    }

    @WithMockUser(roles = "USER")
    @DisplayName("[api][POST][정상] 예약 유효성 검증 및 예약 선점 API")
    @Test
    void checkAvailableAndPreoccupy_test() throws Exception {
        // given
        String requestUrl = "/api/reservations/preoccupy";

        PreoccupyRoomsRequestDto requestDto
            = new PreoccupyRoomsRequestDto(
            List.of(
                new PreoccupyRoomItemRequestDto(1L, "2023-12-23", "2023-12-25"),
                new PreoccupyRoomItemRequestDto(2L, "2023-11-11", "2023-11-14")
            )
        );

        ValidationResultResponseDto responseDto
            = new ValidationResultResponseDto(true, new ArrayList<>());

        given(securityUtil.getCurrentMemberId())
            .willReturn(1L);
        willDoNothing()
            .given(preoccupyRoomsLockFacade)
            .checkAvailableAndPreoccupy(1L, requestDto);

        // when & then
        mockMvc.perform(
                post(requestUrl)
                    .content(objectMapper.writeValueAsString(requestDto))
                    .contentType(MediaType.APPLICATION_JSON)
            )
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data").isEmpty());
    }

    @WithMockUser(roles = "USER")
    @DisplayName("[api][POST][에러] 예약 유효성 검증 및 예약 선점 API - 예약 리스트 최대 갯수 초과")
    @Test
    void checkAvailableAndPreoccupy_size_validation_error_test() throws Exception {
        // given
        String requestUrl = "/api/reservations/preoccupy";

        PreoccupyRoomsRequestDto requestDto
            = new PreoccupyRoomsRequestDto(
            List.of(
                new PreoccupyRoomItemRequestDto(1L, "2023-12-23", "2023-12-25"),
                new PreoccupyRoomItemRequestDto(2L, "2023-11-11", "2023-11-14"),
                new PreoccupyRoomItemRequestDto(3L, "2023-11-11", "2023-11-14"),
                new PreoccupyRoomItemRequestDto(4L, "2023-11-11", "2023-11-14")
            )
        );

        ValidationResultResponseDto responseDto
            = new ValidationResultResponseDto(true, new ArrayList<>());

        given(securityUtil.getCurrentMemberId())
            .willReturn(1L);
        willDoNothing()
            .given(preoccupyRoomsLockFacade)
            .checkAvailableAndPreoccupy(1L, requestDto);

        // when & then
        mockMvc.perform(
                post(requestUrl)
                    .content(objectMapper.writeValueAsString(requestDto))
                    .contentType(MediaType.APPLICATION_JSON)
            )
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.code", is(400)));
    }

    @WithMockUser(roles = "USER")
    @DisplayName("[api][POST][에러] 예약 유효성 검증 및 예약 선점 API - 스트링 날짜 유효성 에러")
    @Test
    void checkAvailableAndPreoccupy_localdate_validation_error_test() throws Exception {
        // given
        String requestUrl = "/api/reservations/preoccupy";

        PreoccupyRoomsRequestDto requestDto
            = new PreoccupyRoomsRequestDto(
            List.of(
                new PreoccupyRoomItemRequestDto(1L, "202-12-23", "2023-12-25"),
                new PreoccupyRoomItemRequestDto(2L, "2023-11-11", "2023-11-14"),
                new PreoccupyRoomItemRequestDto(3L, "2023-11-11", "2023-11-14")
            )
        );

        ValidationResultResponseDto responseDto
            = new ValidationResultResponseDto(true, new ArrayList<>());

        given(securityUtil.getCurrentMemberId())
            .willReturn(1L);
        willDoNothing()
            .given(preoccupyRoomsLockFacade)
            .checkAvailableAndPreoccupy(1L, requestDto);

        // when & then
        mockMvc.perform(
                post(requestUrl)
                    .content(objectMapper.writeValueAsString(requestDto))
                    .contentType(MediaType.APPLICATION_JSON)
            )
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.code", is(400)));
    }

    @WithMockUser(roles = "USER")
    @DisplayName("[api][POST][정상] 예약 객실 선점 취소 API")
    @Test
    void releaseRooms_test() throws Exception {
        // given
        String requestUrl = "/api/reservations/release";

        ReleaseRoomsRequestDto requestDto = new ReleaseRoomsRequestDto(
            List.of(
                new ReleaseRoomItemRequestDto(1L, "2023-12-23", "2023-12-25"),
                new ReleaseRoomItemRequestDto(2L, "2023-11-11", "2023-11-14"),
                new ReleaseRoomItemRequestDto(3L, "2023-11-15", "2023-11-16")
            )
        );

        given(securityUtil.getCurrentMemberId())
            .willReturn(1L);
        willDoNothing()
            .given(reservationService)
            .releaseRooms(1L, requestDto);

        // when & then
        mockMvc.perform(
                post(requestUrl)
                    .content(objectMapper.writeValueAsString(requestDto))
                    .contentType(MediaType.APPLICATION_JSON)
            )
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code", is(200)))
            .andExpect(jsonPath("$.data").isEmpty());
    }
}
