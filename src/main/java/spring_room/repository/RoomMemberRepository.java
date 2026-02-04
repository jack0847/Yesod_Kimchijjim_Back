package spring_room.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import spring_room.domain.RoomMember;

public interface RoomMemberRepository extends JpaRepository<RoomMember, Long> {
    long countByMember(Room room);

}
