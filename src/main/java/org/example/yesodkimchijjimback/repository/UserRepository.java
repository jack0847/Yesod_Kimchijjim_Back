package org.example.yesodkimchijjimback.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.example.yesodkimchijjimback.domain.User;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByGoogleSub(String sub);

    Optional<User> findByEmail(String email);
}
