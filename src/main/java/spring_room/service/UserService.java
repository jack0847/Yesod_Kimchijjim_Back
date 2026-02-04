package spring_room.service;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import spring_room.domain.RoomMember;
import spring_room.domain.User;
import spring_room.dto.UserRe.UserRequest;
import spring_room.repository.RoomMemberRepository;

@Service
@RequiredArgsConstructor
public class UserService {

    private final RoomRepository roomRepository;
    private final RoomMemberRepository roomMemberRepository;

    public void joinRoom(UserRequest userRequest, User user) {

        Room room = roomRepository.findByCode(userRequest.getCode())
                .orElseThrow(() -> new IllegalArgumentException("찾는 방이 없습니다."));


        long currentCount = roomMemberRepository.countByMember(room);
        long nowCount = currentCount + 1;

        RoomMember roomMember = RoomMember.builder()
                .room(room)
                .user(user)
                .name(userRequest.getName())
                .roomUserId(nowCount)
                .build();

        roomMemberRepository.save(roomMember);
    }
}

