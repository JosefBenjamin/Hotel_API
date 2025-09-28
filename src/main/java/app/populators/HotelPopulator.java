package app.populators;
import app.dao.IDAO;
import app.entities.Hotel;


import java.util.List;

public class HotelPopulator {


    public static List<Hotel> populateHotels(IDAO hotelDAO) {

        Hotel h1 = Hotel.builder()
                .hotelId(45)
                .name("Hilton")
                .address("Copenhagen Main St 5")
                .rooms(100)
                .build();

        Hotel h2 = Hotel.builder()
                .hotelId(971)
                .name("Four Seasons")
                .address("Somewhere in Dubai")
                .rooms(56)
                .build();

        Hotel h3 = Hotel.builder()
                .hotelId(1)
                .name("Trump Tower")
                .address("New York City baby")
                .rooms(250)
                .build();

        Hotel h4 = Hotel.builder()
                .hotelId(39)
                .name("White Lotus")
                .address("Sicily, at the beach")
                .rooms(22)
                .build();

        Hotel h5 = Hotel.builder()
                .hotelId(298)
                .name("Hafnia")
                .address("Somewhere in Torshavn")
                .rooms(30)
                .build();



        hotelDAO.createHotel(h1);
        hotelDAO.createHotel(h2);
        hotelDAO.createHotel(h3);
        hotelDAO.createHotel(h4);
        hotelDAO.createHotel(h5);

        return List.of(h1, h2, h3, h4, h5);
    }

}
