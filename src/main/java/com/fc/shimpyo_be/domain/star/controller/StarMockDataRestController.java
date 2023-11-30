package com.fc.shimpyo_be.domain.star.controller;

import com.fc.shimpyo_be.domain.star.service.StarMockDataInsertService;
import com.fc.shimpyo_be.global.common.ResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/stars/mockdata")
@RestController
public class StarMockDataRestController {

    private final StarMockDataInsertService starMockDataInsertService;

    @PostMapping
    public ResponseEntity<ResponseDto<Void>> batchInsert(
        @PageableDefault(size = 50, page = 0) Pageable pageable
        ) {
        starMockDataInsertService.bulkInsert(pageable);

        return ResponseEntity
            .status(HttpStatus.OK)
            .body(ResponseDto.res(HttpStatus.OK, "별점 Mock Data 등록이 완료되었습니다."));
    }
}
