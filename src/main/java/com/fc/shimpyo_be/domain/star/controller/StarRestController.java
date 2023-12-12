package com.fc.shimpyo_be.domain.star.controller;

import com.fc.shimpyo_be.domain.star.dto.request.StarRegisterRequestDto;
import com.fc.shimpyo_be.domain.star.dto.response.StarResponseDto;
import com.fc.shimpyo_be.domain.star.service.StarService;
import com.fc.shimpyo_be.global.common.ResponseDto;
import com.fc.shimpyo_be.global.util.SecurityUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/stars")
@RestController
public class StarRestController {

    private final StarService starService;
    private final SecurityUtil securityUtil;

    @PostMapping
    public ResponseEntity<ResponseDto<StarResponseDto>> register(@Valid @RequestBody StarRegisterRequestDto request) {
        log.debug("[api][POST] /api/stars");

        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(
                ResponseDto.res(
                    HttpStatus.CREATED,
                    starService.register(securityUtil.getCurrentMemberId(), request),
                    "별점 등록이 성공적으로 완료되었습니다."
                )
            );
    }
}
