
import app.config.ApplicationConfig;
import app.config.HibernateConfig;
import app.dao.HotelDAO;
import app.dao.IDAO;
import app.populators.HotelPopulator;
import app.populators.RoomPopulator;
import io.javalin.Javalin;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import org.junit.jupiter.api.*;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS) // allow @BeforeAll non-static and instance fields
public class RoomResourceTest {

    //TODO: Dependencies
    private static Javalin app; // only non-null if we start the server here
    private static final String BASE_URL = "http://localhost:7070/api/v1";
    private static EntityManagerFactory emf = HibernateConfig.getEntityManagerFactoryForTest();
    private static IDAO dao = HotelDAO.getInstance(emf);
    private HotelPopulator hotelPopulator = new HotelPopulator();
    private RoomPopulator roomPopulator = new RoomPopulator();


    private boolean isPortOpen(String host, int port) {
        try (Socket s = new Socket()) {
            s.connect(new InetSocketAddress(host, port), 300);
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    @BeforeAll
    void init()  {
        RestAssured.baseURI = BASE_URL;
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails(); // auto-log failures
        HibernateConfig.setTest(true); // Use Testcontainers DB + create-drop

        // If port 7070 isn't listening, start Javalin (same as your Main)
        if (!isPortOpen("localhost", 7070)) {
            app = ApplicationConfig.startServer(7070);
        }
        Assertions.assertTrue(isPortOpen("localhost", 7070),
                "Server is not listening on http://localhost:7070");
    }

    @BeforeEach
    void setUp(){
        hotelPopulator

    }

    @AfterEach
    void tearDown() {
            // Delete all data from database
            try (EntityManager em = emf.createEntityManager()) {
                em.getTransaction().begin();
                em.createQuery("DELETE FROM Hotel").executeUpdate();
                em.createQuery("DELETE FROM Room").executeUpdate();
                em.createNativeQuery("ALTER SEQUENCE hotel_id_seq RESTART WITH 1").executeUpdate();
                em.createNativeQuery("ALTER SEQUENCE room_id_seq RESTART WITH 1").executeUpdate();
                em.getTransaction().commit();
            }
            catch (Exception e) {
                e.printStackTrace();
            }

    }

    @AfterAll
    void closeDown() {
        if (app != null) {
            ApplicationConfig.stopServer(app);
        }
    }


}