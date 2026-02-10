package org.example.yesodkimchijjimback.repository;

import org.example.yesodkimchijjimback.domain.Rule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RuleRepository extends JpaRepository<Rule, Long> {
    List<Rule> findByRoomCode(String roomCode);
}