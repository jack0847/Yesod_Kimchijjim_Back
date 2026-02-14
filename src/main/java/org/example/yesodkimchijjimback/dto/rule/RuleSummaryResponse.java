package org.example.yesodkimchijjimback.dto.rule;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RuleSummaryResponse {
    private Long id;
    private String rule;
    private Long questionId;
}