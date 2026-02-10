package org.example.yesodkimchijjimback.controller;

import lombok.RequiredArgsConstructor;
import org.example.yesodkimchijjimback.dto.opinion.OpinionResponse;
import org.example.yesodkimchijjimback.service.OpinionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/rooms")
public class OpinionController {

    private final OpinionService opinionService;

//    @PostMapping
//    public ResponseEntity<OpinionResponse> createOpinion(){
//
//    }
//
//    @GetMapping
//    public ResponseEntity<OpinionResponse> getOpinion(){
//
//    }
//
//    @PutMapping
//    public ResponseEntity<OpinionResponse> updateOpinion(){
//
//    }
//
//    @DeleteMapping
//    public ResponseEntity<OpinionResponse> deleteOpinion(){
//
//    }
}
