package org.example.yesodkimchijjimback.repository;

import org.example.yesodkimchijjimback.domain.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoomRepository extends JpaRepository<Room, Long> {
    Optional<Room> findByRoomCode(String roomCode);

    void deleteByRoomCode(String roomCode);

    boolean existsByRoomCode(String roomCode);

}
