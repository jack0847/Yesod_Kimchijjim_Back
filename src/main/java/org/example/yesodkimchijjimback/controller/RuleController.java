package org.example.yesodkimchijjimback.controller;

import lombok.RequiredArgsConstructor;
import org.example.yesodkimchijjimback.dto.rule.MatchResponse;
import org.example.yesodkimchijjimback.dto.rule.RuleRequest;
import org.example.yesodkimchijjimback.dto.rule.UpdateRuleResponse;
import org.example.yesodkimchijjimback.service.RuleService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/room/{roomCode}")
public class RuleController {

    private final RuleService ruleService;

    @PostMapping("/test/result") //답변 제출 할때
    public MatchResponse submitRule(@PathVariable String roomCode, @RequestBody RuleRequest ruleRequest) {
        Long userId = ruleRequest.getUserId(); // DTO에 userId가 있다고 가정
        return ruleService.submitRule(roomCode, ruleRequest, userId);
    }

    @GetMapping("/test/result")// 다 답변했는지 확인
    public MatchResponse redirect(@PathVariable String roomCode, @RequestParam Long userId) {
        return ruleService.redirect(roomCode, userId);
    }

    @PostMapping("/match") //match일때 전원 합의 확인
    public String startNextMatch(@PathVariable String roomCode, @RequestParam Long userId) {
        return ruleService.startNext(roomCode, userId);
    }
    @PostMapping("/rule/confirm/mismatch") //mismatch이고 방장이 적은 규칙 전원 합의 확인
    public String startNextMismatch(@PathVariable String roomCode, @RequestParam Long userId) {
        return ruleService.startNext(roomCode, userId);
    }

    @PostMapping("/rule/confirm") //방장이 강제 확정
    public String confirmRule(@PathVariable String roomCode, @RequestBody RuleRequest ruleRequest, @RequestParam Long userId) {
        ruleService.confirmRule(roomCode, ruleRequest, userId);
        return "Rule confirmed";
    }

    @GetMapping("/test/summary")
    public UpdateRuleResponse getRuleSummary(@PathVariable String roomCode, @RequestParam Long userId, @ModelAttribute RuleRequest request) {
        return ruleService.getRuleSummary(roomCode, userId, request);
    }

    @PostMapping("/test/summary")// 룰 방장만 추가하기
    public String addRule(@PathVariable String roomCode, @RequestBody RuleRequest request) {
        ruleService.addRule(roomCode, request.getUserId(), request);
        return "Rule Added";
    }

    @PutMapping("/test/summary") //룰 방장만 수정하기
    public String updateRule(@PathVariable String roomCode, @RequestBody RuleRequest request) {
        ruleService.updateRule(roomCode, request.getUserId(), request);
        return "Rule Updated";
    }

    @PostMapping("/test/summary/complete") //방장이 넘어갈때 전원 넘어가기
    public String completeRoom(@PathVariable String roomCode, @RequestParam Long userId) {
        ruleService.statusComplete(roomCode, userId);
        return "Room Completed";
    }


}