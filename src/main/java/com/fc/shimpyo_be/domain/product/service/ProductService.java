package com.fc.shimpyo_be.domain.product.service;

import com.fc.shimpyo_be.domain.product.dto.request.SearchKeywordRequest;
import com.fc.shimpyo_be.domain.product.dto.response.ProductDetailsResponse;
import com.fc.shimpyo_be.domain.product.dto.response.ProductResponse;
import com.fc.shimpyo_be.domain.product.entity.Product;
import com.fc.shimpyo_be.domain.product.exception.ProductNotFoundException;
import com.fc.shimpyo_be.domain.product.repository.ProductRepository;
import com.fc.shimpyo_be.domain.product.repository.model.ProductSpecification;
import com.fc.shimpyo_be.domain.product.util.ProductMapper;
import com.fc.shimpyo_be.global.util.DateTimeUtil;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    private final RedisTemplate<String, Object> restTemplate;

    public List<ProductResponse> getProducts(final SearchKeywordRequest searchKeywordRequest,
        final Pageable pageable) {

        Specification<Product> spec = (root, query, criteriaBuilder) -> null;

        if (searchKeywordRequest.productName() != null) {
            spec = spec.and(
                ProductSpecification.likeProductName(searchKeywordRequest.productName()));
        }
        if (searchKeywordRequest.category() != null) {
            if (searchKeywordRequest.category().contains(",")) {
                String[] categories = searchKeywordRequest.category().split(",");
                for (int i = 0; i < categories.length; i++) {
                    spec = spec.or(ProductSpecification.equalCategory(categories[i]));
                }
            } else {
                spec = spec.and(
                    ProductSpecification.equalCategory(searchKeywordRequest.category()));
            }
        }
        if (searchKeywordRequest.address() != null) {
            spec = spec.and(ProductSpecification.likeAddress(searchKeywordRequest.address()));
        }

        return Optional.of(productRepository.findAll(spec, pageable)).orElseThrow().getContent()
            .stream().map(ProductMapper::toProductResponse).toList();
    }

    public ProductDetailsResponse getProductDetails(final Long productId, final String startDate,
        final String endDate) {
        Product product = productRepository.findById(productId)
            .orElseThrow(ProductNotFoundException::new);
        ProductDetailsResponse productDetailsResponse = ProductMapper.toProductDetailsResponse(
            product);
        productDetailsResponse.rooms().stream().forEach(
            roomResponse -> roomResponse.setRemaining(
                isAvailableForReservationUsingRoomCode(product, roomResponse.getRoomCode(),
                    startDate,
                    endDate)));
        return productDetailsResponse;
    }

    public long isAvailableForReservationUsingRoomCode(final Product product, final Long roomCode,
        final String startDate,
        final String endDate) {

        AtomicLong remaining = new AtomicLong(0);

        product.getRooms().stream()
            .filter(room -> room.getCode() == roomCode)
            .forEach(room -> {
                if (isAvailableForReservationUsingRoomId(room.getId(), startDate, endDate)) {
                    remaining.getAndIncrement();
                }
            });

        return remaining.get();
    }

    public boolean isAvailableForReservationUsingRoomId(final Long roomId, final String startDate,
        final String endDate) {
        ValueOperations<String, Object> values = restTemplate.opsForValue();

        LocalDate startLocalDate = DateTimeUtil.toLocalDate(startDate);
        LocalDate endLocalDate = DateTimeUtil.toLocalDate(endDate);

        while (startLocalDate.isBefore(endLocalDate)) {

            String accommodationDate = DateTimeUtil.toString(startLocalDate);
            if (values.get("roomId:" + roomId + ":" + accommodationDate) != null) {
                return false;
            }
            startLocalDate = startLocalDate.plusDays(1);
        }

        return true;
    }


}
