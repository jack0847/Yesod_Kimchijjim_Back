
package org.example.yesodkimchijjimback.controller;


import lombok.RequiredArgsConstructor;
import org.example.yesodkimchijjimback.dto.rule.MatchResponse;
import org.example.yesodkimchijjimback.dto.rule.RuleRequest;
import org.example.yesodkimchijjimback.dto.rule.UpdateRuleResponse;
import org.example.yesodkimchijjimback.service.RuleService;
import org.springframework.web.bind.annotation.*;


@RestController
@RequiredArgsConstructor
@RequestMapping("/room")
public class RuleController {

    private final RuleService ruleService;

    // 답변 제출
    @PostMapping("/test/result")
    public MatchResponse submitRule(@RequestBody RuleRequest ruleRequest) {
        return ruleService.submitRule(ruleRequest.getRoomCode(), ruleRequest, ruleRequest.getUserId());
    }

    // 폴링 (GET 방식 - 파라미터 유지)
    @GetMapping("/{roomCode}/test/result")
    public MatchResponse redirect(@PathVariable String roomCode, @RequestParam Long userId) {
        return ruleService.redirect(roomCode, userId);
    }

    // MATCH 상황 전원 합의 확인
    @PostMapping("/match")
    public String startNextMatch(@RequestBody RuleRequest ruleRequest) {
        return ruleService.startNext(ruleRequest.getRoomCode(), ruleRequest.getUserId());
    }

    // MISMATCH 상황 방장 규칙 합의 확인
    @PostMapping("/rule/confirm/mismatch")
    public String startNextMismatch(@RequestBody RuleRequest ruleRequest) {
        return ruleService.startNext(ruleRequest.getRoomCode(), ruleRequest.getUserId());
    }

    // 방장이 강제 확정
    @PostMapping("/rule/confirm")
    public String confirmRule(@RequestBody RuleRequest ruleRequest) {
        ruleService.confirmRule(ruleRequest.getRoomCode(), ruleRequest, ruleRequest.getUserId());
        return "Rule confirmed";
    }

    // 전체 룰 가져오기 (GET 방식 - 파라미터 유지)
    @GetMapping("/{roomCode}/test/summary")
    public UpdateRuleResponse getRuleSummary(@PathVariable String roomCode, @RequestParam Long userId, @ModelAttribute RuleRequest request) {
        return ruleService.getRuleSummary(roomCode, userId, request);
    }

    // 룰 추가
    @PostMapping("/test/summary")
    public String addRule(@RequestBody RuleRequest request) {
        ruleService.addRule(request.getRoomCode(), request.getUserId(), request);
        return "Rule Added";
    }

    // 룰 수정
    @PutMapping("/test/summary/{ruleId}")
    public String updateRule(@PathVariable Long ruleId, @RequestBody RuleRequest request) {
        ruleService.updateRule(request.getRoomCode(), request.getUserId(), ruleId, request);
        return "Rule Updated";
    }

    // 최종 완료
    @PostMapping("/test/summary/complete")
    public String completeRoom(@RequestBody RuleRequest request) {
        ruleService.statusComplete(request.getRoomCode(), request.getUserId());
        return "Room Completed";
    }
}
