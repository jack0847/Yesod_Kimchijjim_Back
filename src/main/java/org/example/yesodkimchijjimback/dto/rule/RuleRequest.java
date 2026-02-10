package org.example.yesodkimchijjimback.dto.rule;
import lombok.Data;

import java.util.List;

@Data
public class RuleRequest {

    private List<String> rules;
}