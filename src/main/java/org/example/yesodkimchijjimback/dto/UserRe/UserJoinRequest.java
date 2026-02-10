package org.example.yesodkimchijjimback.dto.UserRe;

import lombok.Data;

@Data
public class UserJoinRequest {

    private Long userId;
    private String code;
    private String nickname;
}
