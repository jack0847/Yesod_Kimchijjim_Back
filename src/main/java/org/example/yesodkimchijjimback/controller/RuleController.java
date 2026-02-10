package org.example.yesodkimchijjimback.controller;

import lombok.RequiredArgsConstructor;
import org.example.yesodkimchijjimback.dto.rule.MatchResponse;
import org.example.yesodkimchijjimback.dto.rule.RuleRequest;
import org.example.yesodkimchijjimback.service.RuleService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/rooms")
public class RuleController {

    private final RuleService ruleService;

    @PostMapping("/{roomCode}/rules") // 룰 만들기
    public MatchResponse submitRule(@PathVariable String roomCode, @RequestBody RuleRequest ruleRequest){
        return ruleService.submitRule(roomCode, ruleRequest);
    }

    @GetMapping("/{roomCode}/redirect") // 완료했는지 응답보내기(자동 리다이렉션)
    public MatchResponse redirect(@PathVariable String roomCode){
        return ruleService.redirect(roomCode);
    }
}