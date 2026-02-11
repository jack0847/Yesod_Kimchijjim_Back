package org.example.yesodkimchijjimback.controller;

import lombok.RequiredArgsConstructor;
import org.example.yesodkimchijjimback.dto.opinion.OpinionRequest;
import org.example.yesodkimchijjimback.dto.opinion.OpinionResponse;
import org.example.yesodkimchijjimback.service.OpinionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/room")
public class OpinionController {

    private final OpinionService opinionService;

    @PostMapping("/opinions/create")
    public ResponseEntity<OpinionResponse> createOpinion(
            @SessionAttribute(name = GoogleAuthController.SESSION_USER_ID) Long userId,
            @RequestBody OpinionRequest opinionRequest){
        OpinionResponse opinionResponse = opinionService.createOpinion(userId, opinionRequest);

        return ResponseEntity.status(HttpStatus.CREATED).body(opinionResponse);
    }

    @GetMapping("/{roomCode}/opinions")
    public List<OpinionResponse> getAllOpinions(@PathVariable String roomCode) {
        return opinionService.getAllOpinions(roomCode);
    }
}
