package org.example.yesodkimchijjimback.domain;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

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

    @Column(nullable = false, length = 30)
    private String name;

    @Column(nullable = false, unique = true, length = 50)
    private String email;

    @Column(unique = true)
    private String googleSub;

    @OneToMany (mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<RoomMember> roomMembers = new ArrayList<>();

    @OneToMany (mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Opinion> opinions = new ArrayList<>();

    public void updateName(String name) {
        this.name = name;
    }

    public void connectGoogle(String googleSub) {
        this.googleSub = googleSub;
    }
}
