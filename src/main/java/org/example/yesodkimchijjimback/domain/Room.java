package org.example.yesodkimchijjimback.domain;

import jakarta.persistence.*;
import lombok.*;
import org.checkerframework.checker.units.qual.C;
import org.example.yesodkimchijjimback.dto.room.RoomRequest;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Room {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String roomName;

    @Column(nullable = false, unique = true)
    private String roomCode;

    @Column(nullable = false)
    private int maxPeople;

    @Column(nullable = false)
    private int currentPeople;

    @Builder.Default
    @OneToMany(mappedBy = "room", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<RoomMember> members = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "room", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Opinion> opinions = new ArrayList<>();

    @ElementCollection
    @Builder.Default
    private List<String> allVote = new ArrayList<>();

    @Builder.Default
    private String status = "MODIFYING"; // 방 전체 상태

    public void addVote(String voteString){
        this.allVote.add(voteString);
    }

    public static Room fromEntity(RoomRequest roomRequest){
        String randomCode;
        Random random = new Random();

        randomCode = random.ints(48, 91)
                .filter(i -> (i <= 57 || i >= 65))
                .limit(6)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();


        return builder()
                .roomName(roomRequest.getRoomName())
                .roomCode(randomCode)
                .maxPeople(roomRequest.getMaxPeople())
                .status("WAITING")
                .build();
    }



    public void update(RoomRequest roomRequest){ //방 이름이나 정보 변경
        this.roomName = roomRequest.getRoomName();
        this.maxPeople = roomRequest.getMaxPeople();
    }
    public void updateStatus(String status) { //방 상태 변경
        this.status = status;
    }
}