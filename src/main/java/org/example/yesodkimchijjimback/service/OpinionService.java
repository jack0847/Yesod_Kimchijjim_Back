package org.example.yesodkimchijjimback.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.example.yesodkimchijjimback.domain.Opinion;
import org.example.yesodkimchijjimback.domain.Room;
import org.example.yesodkimchijjimback.domain.RoomMember;
import org.example.yesodkimchijjimback.domain.User;
import org.example.yesodkimchijjimback.dto.opinion.OpinionRequest;
import org.example.yesodkimchijjimback.dto.opinion.OpinionResponse;
import org.example.yesodkimchijjimback.repository.OpinionRepository;
import org.example.yesodkimchijjimback.repository.RoomMemberRepository;
import org.example.yesodkimchijjimback.repository.RoomRepository;
import org.example.yesodkimchijjimback.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OpinionService {

    private final UserRepository userRepository;
    private final RoomRepository roomRepository;
    private final OpinionRepository opinionRepository;
    private final RoomMemberRepository roomMemberRepository;

    @Transactional
    public OpinionResponse createOpinion(Long userId, OpinionRequest opinionRequest){
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 유저입니다."));

        Room room = roomRepository.findByRoomCode(opinionRequest.getRoomCode())
                .orElseThrow(() -> new IllegalArgumentException("찾는 방이 없습니다."));

        RoomMember roomMember = roomMemberRepository.findByUserAndRoom(user, room).orElseThrow();

        Opinion opinion = opinionRepository.save(Opinion.builder()
                .content(opinionRequest.getContent())
                .room(room)
                .user(user)
                .nickname(roomMember.getNickname())
                .build());

        return OpinionResponse.fromResponse(opinion);
    }

//    @Transactional
//    public OpinionResponse getOpinion(Long userId, String roomCode){
//
//    }
//
//    @Transactional
//    public OpinionResponse getAllOpinion(){
//
//    }
//
//    @Transactional
//    public OpinionResponse updateOpinion(){
//
//    }
//
//    @Transactional
//    public OpinionResponse deleteOpinion(){
//
//    }
}
