package org.example.yesodkimchijjimback.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.yesodkimchijjimback.dto.UserRe.UserJoinRequest;
import org.example.yesodkimchijjimback.dto.room.RoomRequest;
import org.example.yesodkimchijjimback.dto.room.RoomResponse;
import org.example.yesodkimchijjimback.service.RoomService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/rooms")
public class RoomController {

    private final RoomService roomService;

    @PostMapping
    public ResponseEntity<RoomResponse> create(
            @Valid @RequestBody RoomRequest roomRequest,
            @SessionAttribute(name = GoogleAuthController.SESSION_USER_ID, required = false)Long userId){
        RoomResponse roomResponse = roomService.createRoom(roomRequest, userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(roomResponse);
    }

    @GetMapping("/{roomCode}")
    public ResponseEntity<RoomResponse> read(@PathVariable String roomCode){
        RoomResponse roomResponse = roomService.readRoom(roomCode);
        return ResponseEntity.ok(roomResponse);
    }

    @PutMapping("/{roomCode}")
    public ResponseEntity<RoomResponse> update(
            @PathVariable String roomCode,
            @RequestBody RoomRequest roomRequest,
            @SessionAttribute(name = GoogleAuthController.SESSION_USER_ID, required = false)Long userId){
        RoomResponse roomResponse = roomService.updateRoom(roomRequest, roomCode, userId);
        return ResponseEntity.ok(roomResponse);
    }

    @DeleteMapping("/{roomCode}")
    public ResponseEntity<RoomResponse> delete(
            @PathVariable String roomCode,
            @SessionAttribute(name = GoogleAuthController.SESSION_USER_ID, required = false)Long userId){
        roomService.deleteRoom(roomCode, userId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/check/{roomCode}")
    public ResponseEntity<Void> checkRoomCode(@PathVariable String roomCode){
        if(roomService.checkRoomCode(roomCode)){
            return ResponseEntity.ok().build();
        }else{
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/join")
    public ResponseEntity<Void> joinRoom(@RequestBody UserJoinRequest userJoinRequest
            , @SessionAttribute(name = GoogleAuthController.SESSION_USER_ID, required = false) Long userId){
        roomService.joinRoom(userJoinRequest, userId);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @DeleteMapping("/{roomCode}/leave")
    public ResponseEntity<Void> leaveRoom(@PathVariable String roomCode,
            @SessionAttribute(name = GoogleAuthController.SESSION_USER_ID, required = false) Long userId){
        roomService.leaveRoom(roomCode, userId);
        return ResponseEntity.ok().build();
    }
}
