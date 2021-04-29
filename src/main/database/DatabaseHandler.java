package main.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JOptionPane;
import main.model.Complex;
import main.model.Customer;
import main.model.Room;
import main.ui.alert.CustomAlert;
import main.util.Util;

public final class DatabaseHandler {

    private static DatabaseHandler dbHandler = null;

    private static final String DB_URL =
            "jdbc:sqlserver://localhost\\MSSQLSERVER:1433;databaseName=NhaTro";

    private static Connection conn = null;
    private static PreparedStatement stmt = null;
    
    public static DatabaseHandler getInstance() {
        if (dbHandler == null) {
            dbHandler = new DatabaseHandler();
        }
        return dbHandler;
    }
    
    public DatabaseHandler() {
        createConnection();
    }

    public static Connection getConn() {
        return conn;
    }

    public static PreparedStatement getStmt() {
        return stmt;
    }
    
    public void createConnection() {
        try {
            conn = DriverManager.getConnection(DB_URL,"sa","firetrucks");
            System.out.println("da ket noi");
        } catch (Exception e) {
            CustomAlert.showErrorMessage("Không load được database", "");
            e.printStackTrace();
            System.exit(0);
        }
    }

    /**
     *
     * @param query
     * @return
     * @throws SQLException thực hiện các query trả về ResultSet
     */
    public ResultSet execQuery(String query) {
        ResultSet result;
        try {
            stmt = conn.prepareStatement(query);
            result = stmt.executeQuery();
        } catch (SQLException ex) {
            ex.printStackTrace();
            return null;
        }
        return result;
    }

    /**
     *
     * @param query
     * @return
     * @throws SQLException thưc hiện các query update data hoặc query không trả
     * về giá trị gì
     */
    public boolean execUpdate(String query) {
        try {
            stmt = conn.prepareStatement(query);
            stmt.executeUpdate();
            return true;
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Error: " + ex.getMessage(), "An Error Occured", JOptionPane.ERROR_MESSAGE);
            System.out.println("Exception at execQuery:dataHandler" + ex.getLocalizedMessage());
            return false;
        }
    }

    public boolean isCMNDExist(String cmnd) {
        String check = "SELECT COUNT(*) FROM KHACH WHERE CMND=?";
        try {
            stmt = conn.prepareStatement(check);
            stmt.setString(1, cmnd);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                int count = rs.getInt(1);
                System.out.println("" + count);
                return (count > 0);
            }
        } catch (SQLException ex) {
            Logger.getLogger(DatabaseHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    public boolean insertNewCustomer(Customer customer) {
        try {
            stmt = conn.prepareStatement(
                    "INSERT INTO KHACH(HOTEN,GIOITINH,NGAYSINH,SDT,CMND) VALUES(?,?,?,?,?)");
            stmt.setNString(1, customer.getHoTen());
            stmt.setBoolean(2, customer.getGioiTinh());
            stmt.setDate(3, java.sql.Date.valueOf(customer.getNgaySinh()));
            stmt.setString(4, customer.getSDT());
            stmt.setString(5, customer.getCMND());

            return stmt.executeUpdate() > 0;
        } catch (SQLException ex) {
            Logger.getLogger(DatabaseHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    /**
     *
     * @param customer (with id)
     * @return true if update success, false if fails
     *
     */
    public boolean updateCustomer(Customer customer) {
        try {
            stmt = conn.prepareStatement(
                    "UPDATE KHACH SET HOTEN=?, GIOITINH=?, NGAYSINH=?, SDT=?, CMND=? WHERE MAKH=?");
            stmt.setNString(1, customer.getHoTen());
            stmt.setBoolean(2, customer.getGioiTinh());
            stmt.setDate(3, Util.LocalDateToSQLDate(customer.getNgaySinh()));
            stmt.setString(4, customer.getSDT());
            stmt.setString(5, customer.getCMND());
            stmt.setInt(6, customer.getId());
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException ex) {
            Logger.getLogger(DatabaseHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    public boolean deleteCustomer(Customer customer) {
        try {
            stmt = conn.prepareStatement(
                    "DELETE FROM KHACH WHERE MAKH=?");
            stmt.setInt(1, customer.getId());
            return stmt.executeUpdate() > 0;
        } catch (SQLException ex) {
            Logger.getLogger(DatabaseHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

//    public ResultSet customerWithRoom(Customer customer) {
//        try {
//            stmt = conn.prepareStatement(
//                    "SELECT MAKH, MAPHONG FROM HDONG WHERE MAKH=? AND NGAYTRA>GETDATE()");
//            stmt.setInt(1, customer.getId());
//
//            return stmt.executeUpdate() > 0;
//        } catch (SQLException ex) { 
//            Logger.getLogger(DatabaseHandler.class.getName()).log(Level.SEVERE, null, ex);
//        }
//        return null;
//    }
    
    public boolean insertNewComplex(Complex c) {
        try {
            stmt = conn.prepareStatement(
                    "INSERT INTO KHU VALUES(?,?)");

            stmt.setNString(1, c.getTen());
            stmt.setNString(2, c.getDiaChi());

            return stmt.executeUpdate() > 0;
        } catch (SQLException ex) {
            Logger.getLogger(DatabaseHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }
    
    public boolean updateComplex(Complex c) {
        try {
            stmt = conn.prepareStatement(
                    "UPDATE KHU SET TEN=?, DIACHI=? WHERE MAKHU=?");
            stmt.setNString(1, c.getTen());
            stmt.setNString(2, c.getDiaChi());
            stmt.setInt(3, c.getId());
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException ex) {
            Logger.getLogger(DatabaseHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }
    
    public boolean deleteComplex(Complex c) {
        try {
            stmt = conn.prepareStatement(
                    "DELETE FROM KHU WHERE MAKHU=?");
            stmt.setInt(1, c.getId());
            return stmt.executeUpdate() > 0;
        } catch (SQLException ex) {
            Logger.getLogger(DatabaseHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    public boolean insertNewRoom(Room room, int type) {
        try {
            stmt = conn.prepareStatement(
                    "INSERT INTO ROOM(TRONG,MALOAI) VALUES(?,?)");

            

            return stmt.executeUpdate() > 0;
        } catch (SQLException ex) {
            Logger.getLogger(DatabaseHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    public boolean updateRoom(Room room, int type) {
        try {
            stmt = conn.prepareStatement(
                "UPDATE PHONG SET =?, =?, =? WHERE =?");
            

            int res = stmt.executeUpdate();
            return (res > 0);
        } catch (SQLException ex) {
            Logger.getLogger(DatabaseHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    public boolean deleteRoom(Room room) {
        try {
            stmt = conn.prepareStatement(
                    "DELETE FROM PHONG WHERE ID=?");
            
            stmt.setInt(1, room.getId());

            return stmt.executeUpdate() > 0;
        } catch (SQLException ex) {
            Logger.getLogger(DatabaseHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

//    public ResultSet getAvailableRooms() {
//        try {
//            stmt = conn.prepareStatement(
//                    "SELECT PHONG WHERE");
//            stmt.setInt(1, customer.getId());
//
//            return stmt.executeUpdate() > 0;
//        } catch (SQLException ex) {
//            Logger.getLogger(DatabaseHandler.class.getName()).log(Level.SEVERE, null, ex);
//        }
//        return null;
//    }
}
