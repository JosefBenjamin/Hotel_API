package app.converters;

import app.dto.HotelDTO;
import app.dto.RoomDTO;
import app.entities.Hotel;
import app.entities.Room;
import java.util.stream.Collectors;

public class DTOConverter {

    public HotelDTO fromHotel(Hotel hotel) {
       return new HotelDTO(
                hotel.getId(),
                hotel.getName(),
                hotel.getAddress(),
                hotel.getAllRooms().stream()
                        .map((room) -> this.fromRoom(room))
                        .collect(Collectors.toSet())
        );
    }

    public RoomDTO fromRoom(Room room) {
        return new RoomDTO(
                room.getId(),
                room.getHotel().getId(),
                room.getRoomNumber(),
                room.getPrice());
    }


}
