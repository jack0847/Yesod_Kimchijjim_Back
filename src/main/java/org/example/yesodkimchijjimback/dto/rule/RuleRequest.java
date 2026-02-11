package org.example.yesodkimchijjimback.dto.rule;
import lombok.Data;
import lombok.Getter;

import java.util.List;

@Data
@Getter
public class RuleRequest {

    private Long roomCode;
    private Long questionId; //수정함
    private Long userId;
    private List<String> opinion;//수정함
    private String category;
}