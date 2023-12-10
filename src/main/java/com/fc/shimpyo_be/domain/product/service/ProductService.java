package com.fc.shimpyo_be.domain.product.service;

import com.fc.shimpyo_be.domain.product.dto.request.SearchKeywordRequest;
import com.fc.shimpyo_be.domain.product.dto.response.ProductDetailsResponse;
import com.fc.shimpyo_be.domain.product.dto.response.PaginatedProductResponse;
import com.fc.shimpyo_be.domain.product.entity.Product;
import com.fc.shimpyo_be.domain.product.exception.ProductNotFoundException;
import com.fc.shimpyo_be.domain.product.repository.ProductRepository;
import com.fc.shimpyo_be.domain.product.util.ProductMapper;
import com.fc.shimpyo_be.domain.room.dto.response.RoomResponse;
import com.fc.shimpyo_be.global.util.DateTimeUtil;
import java.time.LocalDate;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    private final RedisTemplate<String, Object> restTemplate;

    public PaginatedProductResponse getProducts(final SearchKeywordRequest searchKeywordRequest,
        final Pageable pageable) {

        Page<Product> products = Optional.of(productRepository.findAll(searchKeywordRequest, pageable)).orElseThrow();

        return PaginatedProductResponse.builder()
            .productResponses(products.getContent().stream().map(ProductMapper::toProductResponse).toList())
            .pageCount(products.getTotalPages())
            .build();
    }

    public ProductDetailsResponse getProductDetails(final Long productId, final String startDate,
        final String endDate) {
        Product product = productRepository.findById(productId)
            .orElseThrow(ProductNotFoundException::new);
        ProductDetailsResponse productDetailsResponse = ProductMapper.toProductDetailsResponse(
            product);
        productDetailsResponse.rooms().stream().filter(
                roomResponse -> !isAvailableForReservation(roomResponse.getRoomId(), startDate,
                    endDate))
            .forEach(RoomResponse::setReserved);
        return productDetailsResponse;
    }

    public boolean isAvailableForReservation(final Long roomId, final String startDate,
        final String endDate) {
        ValueOperations<String, Object> values = restTemplate.opsForValue();

        LocalDate startLocalDate = DateTimeUtil.toLocalDate(startDate);
        LocalDate endLocalDate = DateTimeUtil.toLocalDate(endDate);

        while (startLocalDate.isBefore(endLocalDate)) {

            String accommodationDate = DateTimeUtil.toString(startLocalDate);
            if (values.get("roomId:" + String.valueOf(roomId) + ":" + accommodationDate) != null) {
                return false;
            }
            startLocalDate = startLocalDate.plusDays(1);
        }

        return true;
    }


}
