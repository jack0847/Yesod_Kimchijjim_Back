package spring_room.dto.RoomMemberRe;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RoomMemberResponse {

    private Long memberId;
    private String name;
    private boolean isHost;
    private String roomName;
    private Long roomId;
}
