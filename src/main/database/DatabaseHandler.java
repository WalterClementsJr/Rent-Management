package main.database;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JOptionPane;
import main.model.Customer;
import main.model.Room;
import main.model.RoomType;
import main.util.Util;

/**
 *
 * @author walker
 */

public final class DatabaseHandler {

    private static DatabaseHandler handler = new DatabaseHandler();

    private static final String DB_URL =
            "jdbc:sqlserver://localhost\\MSSQLSERVER:1433;user=%s;password=%s";
    private Connection conn = null;
    private PreparedStatement stmt = null;

    public DatabaseHandler() {
        createConnection();
    }
    
    private void createConnection() {
        try {
            conn = DriverManager.getConnection(DB_URL.formatted("dummy", "dummy"));
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Can't load database", "Database Error", JOptionPane.ERROR_MESSAGE);
            System.exit(0);
        }
    }

    public ResultSet executeQuery(String query) throws SQLException {
        ResultSet result;
        try {
            stmt = conn.prepareStatement(query);
            result = stmt.executeQuery(query);
        } catch (SQLException ex) {
            System.out.println("Exception at execQuery:dataHandler" + ex.getLocalizedMessage());
            return null;
        } finally {
            stmt.close();
        }
        return result;
    }

    public boolean executeAction(String query) throws SQLException {
        try {
            stmt = conn.prepareStatement(query);
            stmt.execute(query);
            return true;
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Error: " + ex.getMessage(), "An Error Occured", JOptionPane.ERROR_MESSAGE);
            System.out.println("Exception at execQuery:dataHandler" + ex.getLocalizedMessage());
            return false;
        } finally {
            stmt.close();
        }
    }

    public boolean insertNewCustomer(Customer customer) {
        try {
            stmt = conn.prepareStatement(
                    "INSERT INTO MEMBER(HOTEN,GIOITINH,NGAYSINH,SDT,CMND) VALUES(?,?,?,?,?)");
            stmt.setNString(1, customer.getHoTen());
            stmt.setBoolean(2, Util.genderToBit(customer.getGioiTinh()));
            stmt.setDate(3, Util.utilDateToSQLDate(customer.getNgaySinh()));
            stmt.setString(4, customer.getSDT());
            stmt.setString(4, customer.getCMND());
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException ex) { 
            Logger.getLogger(DatabaseHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    public boolean deleteCustomer(Customer customer) {
        try {
            stmt = conn.prepareStatement(
                    "DELETE FROM CUSTOMER WHERE ID=?");
            stmt.setInt(1, customer.getId());

            return stmt.executeUpdate() > 0;
        } catch (SQLException ex) { 
            Logger.getLogger(DatabaseHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }
    
    public boolean insertNewRoom(Room room, RoomType type) {
        try {
            stmt = conn.prepareStatement(
                    "INSERT INTO ROOM(TRONG,MALOAI) VALUES(?,?)");
            stmt.setBoolean(1, room.getStatus());
            stmt.setInt(2, type.getId());
            return stmt.executeUpdate() > 0;
        } catch (SQLException ex) {
            Logger.getLogger(DatabaseHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }
        
    public boolean deleteRoom(Room room) {
        try {
            stmt = conn.prepareStatement(
                    "DELETE FROM ROOM WHERE ID=?");
            stmt.setInt(1, room.getId());

            return stmt.executeUpdate() > 0;
        } catch (SQLException ex) {
            Logger.getLogger(DatabaseHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }
}
