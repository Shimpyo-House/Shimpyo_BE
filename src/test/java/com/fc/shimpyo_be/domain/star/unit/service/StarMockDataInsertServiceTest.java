package com.fc.shimpyo_be.domain.star.unit.service;

import com.fc.shimpyo_be.config.AbstractContainersSupport;
import com.fc.shimpyo_be.domain.star.entity.Star;
import com.fc.shimpyo_be.domain.star.repository.StarRepository;
import com.fc.shimpyo_be.domain.star.service.StarMockDataInsertService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.jdbc.Sql;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class StarMockDataInsertServiceTest extends AbstractContainersSupport {

    @Autowired
    private StarMockDataInsertService starMockDataInsertService;

    @Autowired
    private StarRepository starRepository;

    @Sql("classpath:testdata/star-mockdata-setup.sql")
    @DisplayName("별점 mockdata를 저장하고 상품 별점 평균 정보를 업데이트 합니다.")
    @Test
    void bulkInsert() {
        // given
        PageRequest pageRequest = PageRequest.of(0, 2);

        // when
        starMockDataInsertService.bulkInsert(pageRequest);

        // then
        List<Star> stars = starRepository.findAll();
        assertThat(stars).hasSize(2);
    }
}
