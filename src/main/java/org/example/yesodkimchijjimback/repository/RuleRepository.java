package org.example.yesodkimchijjimback.repository;

import org.example.yesodkimchijjimback.domain.Room;
import org.example.yesodkimchijjimback.domain.Rule;
import org.example.yesodkimchijjimback.dto.rule.RuleRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RuleRepository extends JpaRepository<Rule, Long> {

    List<Rule> findAllByRoom(Room room);

    Optional<Rule> findByRoomAndQuestionId(Room room, Long questionId);

    Optional<Rule> findByIdAndRoom(Long id, Room room);
}