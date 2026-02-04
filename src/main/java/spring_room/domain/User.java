package spring_room.domain;

import jakarta.persistence.*;
import lombok.*;

import java.lang.reflect.Member;

@Builder
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String googleToken;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "roomMember_id")
    private RoomMember roomMember;
}
