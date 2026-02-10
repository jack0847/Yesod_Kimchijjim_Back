package org.example.yesodkimchijjimback.dto.room;

import lombok.Builder;
import lombok.Data;
import org.example.yesodkimchijjimback.domain.Room;

@Data
@Builder
public class RoomResponse {
    private Long id;
    private String roomName;
    private String roomCode;
    private int maxPeople;

    public static RoomResponse fromResponse(Room room){
        return RoomResponse.builder()
                .id(room.getId())
                .roomName(room.getRoomName())
                .roomCode(room.getRoomCode())
                .maxPeople(room.getMaxPeople())
                .build();
    }
}