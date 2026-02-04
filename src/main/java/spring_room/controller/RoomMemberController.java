package spring_room.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import spring_room.service.RoomMemberService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class RoomMemberController {

    private final RoomMemberService userService;



}
