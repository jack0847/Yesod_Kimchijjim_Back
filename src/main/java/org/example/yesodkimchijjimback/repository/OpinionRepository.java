package org.example.yesodkimchijjimback.repository;

import org.example.yesodkimchijjimback.domain.Opinion;
import org.example.yesodkimchijjimback.dto.opinion.OpinionResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OpinionRepository extends JpaRepository<Opinion, Long> {
    List<Opinion> findAllByRoomRoomCodeOrderByCreatedDateDesc(String roomCode);
}
