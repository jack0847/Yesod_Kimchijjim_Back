package org.example.yesodkimchijjimback.dto.rule;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.yesodkimchijjimback.domain.Rule;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UpdateRuleResponse {

    private String status;
    private boolean amIHost;
    private List<Rule> data;
    private String category;
}
