package app.converters;

import app.dto.HotelDTO;
import app.dto.RoomDTO;
import app.entities.Hotel;
import app.entities.Room;


public class EntityConverter {


    public Hotel fromHotel(HotelDTO hotelDTO) {
        if (hotelDTO == null) {
            return null;
        }
        Hotel h = new Hotel();
        h.setId(hotelDTO.id());
        h.setName(hotelDTO.name());
        h.setAddress(hotelDTO.address());
        if (hotelDTO.rooms() != null) {
            hotelDTO.rooms().stream()
                    .map(this::fromRoom)
                    .forEach(h::addRoom);
        }
        h.setRooms(h.getAllRooms().size());
        return h;
    }

    public Room fromRoom(RoomDTO roomDTO) {
        if (roomDTO == null) {
            return null;
        }
        Room r = new Room();
        r.setId(roomDTO.id());
        r.setRoomNumber(roomDTO.number());
        r.setPrice(roomDTO.price());
        if (roomDTO.hotelId() != null) {
            Hotel h = new Hotel();
            h.setId(roomDTO.hotelId());
            r.setHotel(h); // attach by id; service layer can load/verify
        }
        return r;
    }

}
