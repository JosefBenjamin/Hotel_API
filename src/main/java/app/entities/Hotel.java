package app.entities;


import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString(exclude = {"allRooms"})
@EqualsAndHashCode(exclude = {"allRooms"})
public class Hotel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private Integer hotelId;
    private String name;
    private String address;
    private int rooms;

    @OneToMany(mappedBy = "hotel", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private Set<Room> allRooms = new HashSet<>();


    //TODO: Helpers methods that add or remove a room from hashset

    public void addRoom(Room room) {
            allRooms.add(room);
            room.setHotel(this);
    }


    public void deleteRoom(Room room) {
        allRooms.remove(room);
        room.setHotel(null);
    }

}
