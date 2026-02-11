package org.example.yesodkimchijjimback.dto.room;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class WaitingRoomResponse {
    private boolean isFull;
    private Long currentPeople;

    public static WaitingRoomResponse fromResponse(boolean isFull, Long currentPeople){
        return WaitingRoomResponse.builder()
                .isFull(isFull)
                .currentPeople(currentPeople)
                .build();
    }
}
