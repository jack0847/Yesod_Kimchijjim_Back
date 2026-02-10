package org.example.yesodkimchijjimback.service;


import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.example.yesodkimchijjimback.domain.RoomMember;
import org.example.yesodkimchijjimback.domain.User;
import org.example.yesodkimchijjimback.dto.RoomMemberRe.RoomMemberResponse;
import org.example.yesodkimchijjimback.dto.UserRe.UserResponse;
import org.example.yesodkimchijjimback.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.example.yesodkimchijjimback.repository.RoomMemberRepository;

@Service
@RequiredArgsConstructor
public class UserService {

    private final RoomMemberRepository roomMemberRepository;
    private final UserRepository userRepository;

    @Transactional
    public UserResponse getMyUserProfile(Long userId){ // 본인 프로필 정보 가져오기
        User user = userRepository.findById(userId).orElseThrow();
        return UserResponse.fromResponse(user);
    }

    @Transactional
    public RoomMemberResponse getMyRoom(Long userId) { // 방 정보 가져오기
        User user = userRepository.findById(userId).orElseThrow();
        RoomMember roomMember = roomMemberRepository.findByUser(user).orElse(null);

        if (roomMember == null) {return null;}

        return RoomMemberResponse.fromResponse(roomMember);
    }

    @Transactional
    public void deleteUser(Long userId){ // 회원탈퇴
        User user = userRepository.findById(userId).orElseThrow();
        userRepository.deleteById(userId);
    }
}

