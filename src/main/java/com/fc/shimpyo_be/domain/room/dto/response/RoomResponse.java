package com.fc.shimpyo_be.domain.room.dto.response;

import java.util.Objects;
import lombok.Builder;
import lombok.Getter;

@Getter
public class RoomResponse {

    private final long roomCode;
    private final String roomName;
    private final Long price;
    private final String description;
    private final Long standard;
    private final Long capacity;
    private final String checkIn;
    private final String checkOut;
    private final RoomOptionResponse roomOptionResponse;
    private Long remaining;

    @Builder
    public RoomResponse(long roomCode, String roomName, Long price, String description,
        Long standard,
        Long capacity, String checkIn, String checkOut,
        RoomOptionResponse roomOptionResponse) {
        this.roomCode = roomCode;
        this.roomName = roomName;
        this.price = price;
        this.description = description;
        this.standard = standard;
        this.capacity = capacity;
        this.checkIn = checkIn;
        this.checkOut = checkOut;
        this.roomOptionResponse = roomOptionResponse;
        this.remaining = 0L;
    }
    public void setRemaining(long remaining) {
        this.remaining = remaining;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        RoomResponse other = (RoomResponse) obj;
        return Objects.equals(roomCode, other.roomCode);
    }

}
