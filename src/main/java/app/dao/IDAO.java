package app.dao;

import app.entities.Hotel;
import app.entities.Room;

import java.util.List;

public interface IDAO {

    List<Hotel> getAllHotels();

    Hotel getHotelById(int id);

    Hotel createHotel(Hotel hotel);

    Hotel updateHotel(Hotel hotel);

    boolean deleteHotel(Hotel hotel);

    boolean addRoom(Hotel hotel, Room room);

    boolean removeRoom(Hotel hotel, Room room);

    Room getRoomById(int id);

    List<Room> getRoomsForHotel(Hotel hotel);

}
