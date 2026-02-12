package org.example.yesodkimchijjimback.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.example.yesodkimchijjimback.domain.Room;
import org.example.yesodkimchijjimback.domain.RoomMember;
import org.example.yesodkimchijjimback.domain.User;
import org.example.yesodkimchijjimback.dto.UserRe.UserJoinRequest;
import org.example.yesodkimchijjimback.dto.room.RoomRequest;
import org.example.yesodkimchijjimback.dto.room.RoomResponse;
import org.example.yesodkimchijjimback.dto.room.WaitingRoomResponse;
import org.example.yesodkimchijjimback.repository.RoomMemberRepository;
import org.example.yesodkimchijjimback.repository.RoomRepository;
import org.example.yesodkimchijjimback.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RoomService {

    private final RoomRepository roomRepository;
    private final RoomMemberRepository roomMemberRepository;
    private final UserRepository userRepository;

    @Transactional
    public RoomResponse createRoom(RoomRequest roomRequest, Long userId){
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 유저입니다."));

        Room room = roomRepository.save(Room.fromEntity(roomRequest));
        RoomMember host = RoomMember.builder()
                .room(room)
                .user(user)
                .nickname(roomRequest.getHostNickname())
                .isHost(true)
                .build();

        roomMemberRepository.save(host);

        return RoomResponse.fromResponse(room);
    }

    @Transactional
    public RoomResponse readRoom(String roomCode){
        Room room = roomRepository.findByRoomCode(roomCode).orElseThrow();
        return RoomResponse.fromResponse(room);
    }

    @Transactional
    public RoomResponse updateRoom(RoomRequest roomRequest, String roomCode, Long userId){
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 유저입니다."));

        Room room = roomRepository.findByRoomCode(roomCode)
                .orElseThrow(() -> new IllegalArgumentException("찾는 방이 없습니다."));

        RoomMember roomMember = roomMemberRepository.findByUserAndRoom(user, room).orElseThrow();

        if(!roomMember.isHost()){
            throw new IllegalStateException("방장만 방의 정보를 수정할 수 있습니다.");
        }

        room.update(roomRequest);
        return RoomResponse.fromResponse(room);
    }

    @Transactional
    public void deleteRoom(String roomCode, Long userId){
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 유저입니다."));

        Room room = roomRepository.findByRoomCode(roomCode)
                .orElseThrow(() -> new IllegalArgumentException("찾는 방이 없습니다."));

        RoomMember roomMember = roomMemberRepository.findByUserAndRoom(user, room).orElseThrow();

        if(!roomMember.isHost()){
            throw new IllegalStateException("방장만 방을 삭제할 수 있습니다.");
        }

        roomRepository.delete(room);
    }

    @Transactional
    public void joinRoom(UserJoinRequest userJoinRequest, Long userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 유저입니다."));

        Room room = roomRepository.findByRoomCode(userJoinRequest.getRoomCode())
                .orElseThrow(() -> new IllegalArgumentException("찾는 방이 없습니다."));

        if (roomMemberRepository.existsByRoomAndUser(room, user)) {
            throw new IllegalStateException("이미 참여 중인 방입니다.");
        }

        Long currentPeople = roomMemberRepository.countByRoom(room);
        boolean isHost;

        isHost = (currentPeople == 0);

        if(currentPeople >= room.getMaxPeople()){
            throw new IllegalStateException("방이 가득 찼습니다.");
        }

        RoomMember roomMember = RoomMember.builder()
                .room(room)
                .user(user)
                .nickname(userJoinRequest.getNickname())
                .isHost(isHost)
                .build();

        roomMemberRepository.save(roomMember);
    }

    @Transactional
    public void checkRoomCode(String roomCode) {
        Room room = roomRepository.findByRoomCode(roomCode)
                .orElseThrow(() ->  new IllegalArgumentException("방을 찾을 수 없습니다."));
        Long currentPeople = roomMemberRepository.countByRoom(room);
        if(currentPeople >= room.getMaxPeople()){
            throw new IllegalStateException("방이 가득 찼습니다.");
        }
    }

    public WaitingRoomResponse checkMember(String roomCode) {
        Room room = roomRepository.findByRoomCode(roomCode)
                .orElseThrow(() ->  new IllegalArgumentException("방을 찾을 수 없습니다."));
        Long currentPeople = roomMemberRepository.countByRoom(room);
        boolean isFull = (currentPeople >= room.getMaxPeople());

        System.out.println(">>> [WaitingRoom Check] Room: " + roomCode +
                " | Current: " + currentPeople +
                " | Max: " + room.getMaxPeople() +
                " | isFull: " + isFull);

        return WaitingRoomResponse.fromResponse(isFull, currentPeople, room.getMaxPeople());
    }

    @Transactional
    public void leaveRoom(String roomCode, Long userId){
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 유저입니다."));

        Room room = roomRepository.findByRoomCode(roomCode)
                .orElseThrow(() -> new IllegalArgumentException("찾는 방이 없습니다."));

        RoomMember roomMember = roomMemberRepository.findByUserAndRoom(user, room).orElseThrow();

        if(roomMember.isHost()) throw new IllegalStateException("방장은 방을 나갈 수 없습니다.");

        roomMemberRepository.delete(roomMember);
    }

    @Transactional
    public String getUserNicknameInRoom(String roomCode, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 유저입니다."));

        Room room = roomRepository.findByRoomCode(roomCode)
                .orElseThrow(() -> new IllegalArgumentException("찾는 방이 없습니다."));

        return roomMemberRepository.findByUserAndRoom(user, room).orElseThrow().getNickname();
    }
}
