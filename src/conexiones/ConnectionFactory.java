package conexiones;
import java.sql.PreparedStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JOptionPane;
import modelos.Huesped;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Date;
import java.text.SimpleDateFormat;
import modelos.Reservas;

/**
 *
 * @author Jesus
 */
public class ConnectionFactory {
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    
    /***
     * Captura la conexion a la base de datos
     * @param database nombre de la base de datos que queremos conectar
     * @param user nombre del usuario de myql
     * @param password contraseña de mysql
     * @return la conexion abierta para ser usada
     * @throws SQLException excepcion que captura cualquier error con respeto a la apertura de la misma.
     */
    public static Connection getConnection(String database, String user, String password) throws SQLException{
        String url = "jdbc:mysql://localhost:3306/"+database;
        Connection con = DriverManager.getConnection(
                url,
                user,
                password
        );
        return con;
    }
    
    /***
     * Crea nuevos usuarios(huepedes)
     * @param huesped recibe un objeto para usar sus propiedades
     * @param connection recibe la conexion
     * @return un booleano dependiendo si crea o no al usuario.
     */
    public boolean createHuesped(Huesped huesped,Connection connection){
        try (connection){
            PreparedStatement ps = connection.prepareStatement(
                    "insert into huespedes(dni,nombre,apellidos,fecha_nacimiento,nacionalidad,telefono,contrasenhia)"
                    + "values(?,?,?,?,?,?,?)"
            );
            ps.setLong(1, huesped.getDni());
            ps.setString(2, huesped.getNombre());
            ps.setString(3, huesped.getApellidos());
            ps.setDate(4, huesped.getFecha_nacimiento());
            ps.setString(5, huesped.getNacionalidad());
            ps.setString(6, huesped.getTelefono());
            ps.setString(7, huesped.getContrasenhia());
            ps.execute();
            System.out.println("Transaccion exitosa... :)");
            JOptionPane.showMessageDialog(
                    null,
                    "Creación de usuario exitosamente."
            );
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(
                    null,
                    "Ya existe un usuario con este registro."
            );
            return false;
        }
    }
    
    /***
     * Sirve para que el usuario inicie sesion
     * @param username recibe el nombre de usuario
     * @param password recibe la contrasenhia
     * @param connection recibe la conexion
     * @return una lista del usuario(dni) y contrasenhia.
     */
    public boolean loginUser(String username, String password, Connection connection){
        try(connection) {
            
            Statement st = connection.createStatement();
            st.execute("select dni,contrasenhia from huespedes");
            boolean inicioSesion = false;
            ResultSet resultSet = st.getResultSet();
            
            while (resultSet.next()) {        
                long dni = resultSet.getLong("dni");
                String contrasenhia = resultSet.getString("contrasenhia");
                if(dni == Long.parseLong(username) && contrasenhia.equals(password)){
                    inicioSesion = true;
                    String data = String.valueOf(dni)+","+contrasenhia;
                    try {
                        FileWriter file = new FileWriter("Datos-usuario.txt");
                        file.write(data);
                        file.close();
                        
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    
                    break;
                }
            }
            return inicioSesion;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    /***
     * Lista todos los usuarios de la base de datos
     * @param con recibe la conexion lista para ser usada
     * @return una lista de diccionarios, con los datos de todos los usuarios de la base de datos.
     */
    public ArrayList<Map<String,String>> listAllClient(Connection con){
        try (con){
                ArrayList<Map<String,String>> listado = new ArrayList<>();
                Statement st = con.createStatement();
                st.execute("select dni, nombre, apellidos, telefono from huespedes");
                ResultSet resultSet = st.getResultSet();
                while(resultSet.next()){
                    System.out.println(resultSet.getString("Nombre"));
                    Map<String,String> row = new HashMap<>();
                    row.put("DNI", String.valueOf(resultSet.getLong("dni")));
                    row.put("NOMBRE", resultSet.getString("nombre"));
                    row.put("APELLIDOS", resultSet.getString("apellidos"));
                    row.put("TELEFONO", resultSet.getString("telefono"));
                    listado.add(row);
                }
                return listado;
                
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
    
    public Map<String,Integer> loadFormas(Connection connection){
        Map<String,Integer> data = null;
        Statement st;
        try (connection) {
            st = connection.createStatement();
            st.execute("SELECT * FROM FORMA_PAGOS");
            ResultSet  resultSet = st.getResultSet();
            data = new HashMap<>();
            while (resultSet.next()) {                
                data.put(
                        resultSet.getString("forma_pago"),
                        resultSet.getInt("id")
                );
            }
        } catch (SQLException ex) {
            System.out.println("Error en al cargar las formas de pago.");
            throw new RuntimeException(ex);
        }
        return data;
    }

    public void registerReserva(Reservas reservas, Connection con) {
        try (con){
            String formatterDate = "";
            PreparedStatement prepareStatement = con.prepareStatement(
                    "INSERT INTO RESERVACIONES"
                            + "(fecha_entrada,fecha_salida,valor,id_forma_pago,id_cliente)"
                            + "values(?,?,?,?,?)"
            );
            formatterDate = this.dateFormat.format(reservas.getFecha_ingreso());
            prepareStatement.setDate(1, Date.valueOf(formatterDate));
            formatterDate = this.dateFormat.format(reservas.getFecha_salida());
            prepareStatement.setDate(2, Date.valueOf(formatterDate));
            prepareStatement.setLong(3, reservas.getValor());
            prepareStatement.setInt(4, reservas.getId_forma_pago());
            prepareStatement.setLong(5, reservas.getDni_cliente());
            prepareStatement.execute();
            System.out.println("Transaccion exitosa");
            JOptionPane.showMessageDialog(null, "Transacción exitosa.");
        } catch (SQLException ex) {
            System.out.println("Error en la transaccion.");
            throw new RuntimeException(ex);
        }
    }
    
}
