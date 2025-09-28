package app.populators;

import app.dao.IDAO;
import app.entities.Hotel;
import app.entities.Room;

import java.util.ArrayList;
import java.util.List;

public class RoomPopulator {

    /**
     * Create and persist a couple of rooms for each provided hotel using the DAO's addRoom method.
     * Returns the list of all created Room objects.
     *
     * Expected usage from Main:
     *   List<Hotel> hotels = HotelPopulator.populateCourses(dao);
     *   List<Room> rooms = RoomPopulator.populate(dao, hotels);
     */
    public static List<Room> populateRooms(IDAO dao, List<Hotel> hotels) {
        List<Room> created = new ArrayList<>();
        if (hotels == null || hotels.isEmpty()) return created;

        int businessRoomIdSeed = 1000; // simple unique business id seed for roomId

        for (int i = 0; i < hotels.size(); i++) {
            Hotel h = hotels.get(i);

            // Make two rooms per hotel with easy-to-read numbers like 101/102, 201/202, ...
            int floor = i + 1; // 1..N
            int roomNumber1 = floor * 100 + 1;
            int roomNumber2 = floor * 100 + 2;
            int roomNumber3 = floor * 100 + 2;


            // Price can scale a bit per hotel just to vary sample data
            double basePrice = 100.0 + (i * 20.0);

            Room r1 = new Room(
                    null,                      // id (generated)
                    businessRoomIdSeed++,      // roomId (business id)
                    null,                      // hotel (set via helper in addRoom)
                    roomNumber1,
                    basePrice
            );

            Room r2 = new Room(
                    null,
                    businessRoomIdSeed++,
                    null,
                    roomNumber2,
                    basePrice + 20.0
            );


            Room r3 = new Room(
                    null,
                    businessRoomIdSeed++,
                    null,
                    roomNumber3,
                    basePrice + 20.0
            );

            // Persist and attach via DAO (HotelDAO#addRoom handles both sides of the relation)
            dao.addRoom(h, r1);
            dao.addRoom(h, r2);
            dao.addRoom(h, r3);


            created.add(r1);
            created.add(r2);
            created.add(r3);

        }

        return created;
    }
}
