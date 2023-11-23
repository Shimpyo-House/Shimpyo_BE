package com.fc.shimpyo_be.domain.product.util;

import com.fc.shimpyo_be.domain.product.dto.response.ProductDetailsResponse;
import com.fc.shimpyo_be.domain.product.dto.response.ProductResponse;
import com.fc.shimpyo_be.domain.product.entity.Product;
import com.fc.shimpyo_be.domain.room.dto.response.RoomResponse;
import com.fc.shimpyo_be.domain.room.entity.Room;
import com.fc.shimpyo_be.domain.room.util.RoomMapper;
import java.util.stream.Collectors;

public class ProductMapper {

    public static ProductResponse toProductResponse(Product product) {
        return ProductResponse.builder().productId(product.getId()).productName(product.getName())
            .address(product.getAddress()).category(product.getCategory().toString())
            .image(ImageUrlParser.pareseThumbnail(product.getPhotoUrl())).starAvg(product.getStarAvg())
            .price(product.getRooms().isEmpty()
                ?0:Long.valueOf(product.getRooms().stream().map(Room::getPrice).min((o1, o2) -> o1-o2).orElseThrow()))
            .favorites(false)
            .build();
    }

    public static ProductDetailsResponse toProductDetailsResponse(Product product) {
        return ProductDetailsResponse.builder()
            .productId(product.getId())
            .category(product.getCategory().toString())
            .address(product.getAddress())
            .productName(product.getName())
            .description(product.getDescription())
            .starAvg(product.getStarAvg())
            .favorites(false)
            .images(ImageUrlParser.parse(product.getPhotoUrl()))
            .rooms(product.getRooms().stream().map(RoomMapper::from).toList())
            .build();
    }


}