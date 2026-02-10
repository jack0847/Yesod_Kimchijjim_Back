package org.example.yesodkimchijjimback.dto.rule;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor

public class MatchResponse {

    private String status;
    private List<String> resultRuleList;
}