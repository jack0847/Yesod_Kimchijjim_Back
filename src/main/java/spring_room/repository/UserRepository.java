package spring_room.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import spring_room.domain.User;

public interface UserRepository extends JpaRepository<User, Long> {
}
