package com.fc.shimpyo_be.domain.star.service;

import com.fc.shimpyo_be.domain.member.entity.Member;
import com.fc.shimpyo_be.domain.member.repository.MemberRepository;
import com.fc.shimpyo_be.domain.product.entity.Product;
import com.fc.shimpyo_be.domain.product.repository.ProductRepository;
import com.fc.shimpyo_be.domain.star.entity.Star;
import com.fc.shimpyo_be.domain.star.repository.StarRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

@Slf4j
@RequiredArgsConstructor
@Service
public class StarMockDataInsertService {

    private final StarRepository starRepository;
    private final MemberRepository memberRepository;
    private final ProductRepository productRepository;

    @Transactional
    public void bulkInsert(Pageable pageable) {
        Page<Product> productPage = productRepository.findAll(pageable);
        List<Member> members = memberRepository.findAll();
        int memberSize = members.size();

        List<Product> products = productPage.getContent();

        for (Product product : products) {
            float score = (float) (Math.round(getRandomScore() * 10) / 10.0);
            starRepository.save(
              Star.builder()
                  .product(product)
                  .member(members.get(getRandomInt(memberSize)))
                  .score(score)
                  .build()
            );

            product.updateStarAvg(score);
        }
    }

    private double getRandomScore() {
        Random r = new Random();
        return 0 + r.nextFloat() * 5;
    }

    private Integer getRandomInt(int size) {
        if (0 >= size) {
            throw new IllegalArgumentException("Invalid range [" + 0 + ", " + size + "]");
        }
        return ThreadLocalRandom.current().nextInt(0, size);
    }
}
