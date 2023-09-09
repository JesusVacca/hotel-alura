
package hotel.alura.v2;
import conexiones.ConnectionFactory;
import java.sql.SQLException;
import java.sql.Connection;
import java.sql.Date;
import modelos.Huesped;
/**
 *
 * @author Jesus
 */
public class HotelAluraV2 {
    

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        ConnectionFactory connectionFactory = new ConnectionFactory();
        try {
            Connection con = ConnectionFactory.getConnection("hotel_alura", "root", "Jesus@1999");
            Huesped hp = new Huesped();
            hp.setDni(1008735273);
            hp.setNombre("Jesus");
            hp.setApellidos("Vacca Romero");
            hp.setFecha_nacimiento(Date.valueOf("1999-04-30"));
            hp.setContrasenhia("Jesus@1999");
            hp.setTelefono("3117984622");
            hp.setNacionalidad("Colombiano");
            connectionFactory.createHuesped(hp, con);
            System.out.println("Conexion exitosa");

            //connectionFactory.listAllClient(con);
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
}
