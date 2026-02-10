package org.example.yesodkimchijjimback.dto.opinion;

import lombok.Data;

@Data
public class OpinionRequest {
    private Long id;
    private String content;
    private String roomCode;
}
