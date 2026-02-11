package org.example.yesodkimchijjimback.dto.UserRe;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserJoinRequest {

    private String roomCode;
    private String nickname;
}
