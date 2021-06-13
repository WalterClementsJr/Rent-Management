package main.database;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.sql.CallableStatement;
import java.sql.Types;
import java.util.logging.Level;
import java.util.logging.Logger;
import main.model.Complex;
import main.model.Contract;
import main.model.Customer;
import main.model.Invoice;
import main.model.Maintenance;
import main.model.Room;
import main.ui.alert.CustomAlert;
import main.util.Util;

public final class DatabaseHandler {

    public static void main(String[] args) {
        DatabaseHandler.getInstance();
        System.out.println(dbHandler.isCMNDExist(1, "000000000000"));
        System.out.println(dbHandler.isCMNDExist(-1, "000000000001"));

    }

    private static DatabaseHandler dbHandler = null;

    private static final String DB_URL
            = "jdbc:sqlserver://localhost\\MSSQLSERVER:1433;databaseName=NhaTro";

    private Connection conn = null;
    private CallableStatement cstmt = null;
    private PreparedStatement stmt = null;

    public static DatabaseHandler getInstance() {
        if (dbHandler == null) {
            dbHandler = new DatabaseHandler();
        }
        return dbHandler;
    }

    public DatabaseHandler() {
        createConnection();
    }

    public void createConnection() {
        try {
            conn = DriverManager.getConnection(DB_URL, "dummy", "dummy");
            System.out.println("Connected to database");
        } catch (SQLException e) {
            CustomAlert.showErrorMessage("Không load được database", "");
            System.exit(0);
        }
    }

    /**
     * thực hiện các query trả về data
     *
     * @param query
     * @return ResultSet
     */
    public ResultSet execQuery(String query) {
        ResultSet result;
        try {
            stmt = conn.prepareStatement(query);
            result = stmt.executeQuery();
        } catch (SQLException ex) {
            Logger.getLogger(DatabaseHandler.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
        return result;
    }

    /**
     * thực hiện các query update data hoặc query không trả về giá trị
     *
     * @param query
     * @return true nếu thành công
     */
    public boolean execUpdate(String query) {
        try {
            stmt = conn.prepareStatement(query);
            stmt.executeUpdate();
            return true;
        } catch (SQLException ex) {
            Logger.getLogger(DatabaseHandler.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }

    /**
     *
     * @param id -1 là kiểm tra lúc thêm, không thì kiểm tra nếu id trùng nếu
     * trùng thì là chính nó
     * @param cmnd
     * @return
     */
    public boolean isCMNDExist(int id, String cmnd) {
        try {
            stmt = conn.prepareStatement(
                    "SELECT MAKH FROM KHACH WHERE CMND=?");
            stmt.setString(1, cmnd);
            ResultSet rs = stmt.executeQuery();
            int makh;

            if (!rs.next()) {
                System.out.println("doesnt have anything");
//                if (id == -1) {
//                    return true;
//                }
                return false;
            } else {
                if (id == -1) {
                    return true;
                }
                do {
                    makh = rs.getInt("MAKH");
                    if (id == makh) {
                        return false;
                    }
                } while (rs.next());
                rs.close();
                return false;
            }
        } catch (SQLException ex) {
            Logger.getLogger(DatabaseHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
        return true;
    }

    public boolean isRoomNameExist(int roomId, int complexId, String rName) {
        try {
            stmt = conn.prepareStatement(
                    "SELECT MAPHONG FROM PHONG WHERE TENPHONG=? AND MAKHU=?");
            stmt.setNString(1, rName);
            stmt.setInt(2, complexId);
            ResultSet rs = stmt.executeQuery();
            int maphong;

            if (rs.next()) {
                if (roomId == -1) {
                    return true;
                }

                do {
                    maphong = rs.getInt("MAPHONG");
                    if (roomId == maphong) {
                        return false;
                    }
                } while (rs.next());
                rs.close();
                return true;
            } else {
                return false;
            }
        } catch (SQLException ex) {
            Logger.getLogger(DatabaseHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
        return true;
    }

    public boolean isComplexExist(int complexId, String complexName) {
        try {
            stmt = conn.prepareStatement(
                    "SELECT MAKHU FROM KHU WHERE TENKHU=?");
            stmt.setNString(1, complexName);
            ResultSet rs = stmt.executeQuery();
            int makhu;

            if (rs.next()) {
                if (complexId == -1) {
                    return true;
                }

                do {
                    makhu = rs.getInt("MAKHU");
                    if (complexId == makhu) {
                        return false;
                    }
                } while (rs.next());
                rs.close();
                return true;
            } else {
                return false;
            }
        } catch (SQLException ex) {
            Logger.getLogger(DatabaseHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
        return true;
    }

    public boolean isComplexDeletable(int makhu) {
        try {
            stmt = conn.prepareStatement(
                    "select dbo.iskhudeletable(?) as candelete");
            stmt.setInt(1, makhu);

            ResultSet rs = stmt.executeQuery();
            if (!rs.next()) {
                return false;
            } else {
                boolean isDeletable = rs.getBoolean("candelete");
                rs.close();
                return isDeletable;
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

    public boolean isCustomerDeletable(int makh) {
        try {
            stmt = conn.prepareStatement(
                    "select dbo.iskhachdeletable(?) as candelete");
            stmt.setInt(1, makh);

            ResultSet rs = stmt.executeQuery();
            if (!rs.next()) {
                return false;
            } else {
                boolean isDeletable = rs.getBoolean("candelete");
                rs.close();
                return isDeletable;
            }
        } catch (SQLException ex) {
            Logger.getLogger(DatabaseHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    public ResultSet getCustomersInRoom(int roomId) {
        try {
            stmt = conn.prepareStatement(
                    "select * from GetKhachTrongPhong(?)");
            stmt.setInt(1, roomId);

            return stmt.executeQuery();
        } catch (SQLException ex) {
            Logger.getLogger(DatabaseHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public int getNumberOfCustomersInRoom(int roomId) {
        try {
            cstmt = conn.prepareCall(
                    "{ ? = call dbo.getsonguoitrongphong(?) }");
            cstmt.registerOutParameter(1, Types.INTEGER);
            cstmt.setInt(2, roomId);

            cstmt.execute();
            return cstmt.getInt(1);
        } catch (SQLException ex) {
            Logger.getLogger(DatabaseHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
        return -1;
    }

    public ResultSet getCustomersWithRoom() {
        try {
            stmt = conn.prepareStatement(
                    "select * from ViewKhachCoPhong");
            return stmt.executeQuery();
        } catch (SQLException ex) {
            Logger.getLogger(DatabaseHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public ResultSet getOldCustomers() {
        try {
            stmt = conn.prepareStatement(
                    "select * from ViewKhachCu");
            return stmt.executeQuery();
        } catch (SQLException ex) {
            Logger.getLogger(DatabaseHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public ResultSet getCustomersWithNoRoom() {
        try {
            stmt = conn.prepareStatement(
                    "select * from ViewKhachKhongCoPhong");
            return stmt.executeQuery();
        } catch (SQLException ex) {
            Logger.getLogger(DatabaseHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

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
                    "UPDATE KHU SET TENKHU=?, DIACHI=? WHERE MAKHU=?");
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

    public boolean insertNewRoom(Room room) {
        try {
            stmt = conn.prepareStatement(
                    "INSERT INTO PHONG VALUES(?,?,?,?,?,?,?)");
            stmt.setNString(
                    1, room.getTenPhong());
            stmt.setShort(
                    2, (short) room.getSoNguoi());
            stmt.setInt(
                    3, room.getMaKhu());
            stmt.setBigDecimal(
                    4, room.getGiaGoc());
            stmt.setBigDecimal(
                    5, room.getTienCoc());
            stmt.setInt(
                    6, room.getDienTich());
            stmt.setNString(
                    7, room.getMoTa());

            return stmt.executeUpdate() > 0;
        } catch (SQLException ex) {
            Logger.getLogger(DatabaseHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    public boolean updateRoom(Room room) {
        try {
            stmt = conn.prepareStatement(
                    "UPDATE PHONG SET TENPHONG=?, SONGUOI=?, MAKHU=?, GIAGOC=?, TIENCOC=?, DIENTICH=?, MOTA=? WHERE MAPHONG=?");
            stmt.setNString(
                    1, room.getTenPhong());
            stmt.setShort(
                    2, (short) room.getSoNguoi());
            stmt.setInt(
                    3, room.getMaKhu());
            stmt.setBigDecimal(
                    4, room.getGiaGoc());
            stmt.setBigDecimal(
                    5, room.getTienCoc());
            stmt.setInt(
                    6, room.getDienTich());
            stmt.setNString(
                    7, room.getMoTa());
            stmt.setInt(
                    8, room.getId());
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
                    "DELETE FROM PHONG WHERE MAPHONG=?");

            stmt.setInt(1, room.getId());

            return stmt.executeUpdate() > 0;
        } catch (SQLException ex) {
            Logger.getLogger(DatabaseHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    public boolean isRoomFull(int roomId) {
        try {
            cstmt = conn.prepareCall(
                    "{ ? = call dbo.isphongfull(?) }");
            cstmt.registerOutParameter(1, Types.BOOLEAN);
            cstmt.setInt(2, roomId);

            cstmt.execute();
            return cstmt.getBoolean(1);
        } catch (SQLException ex) {
            Logger.getLogger(DatabaseHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    public boolean isRoomDeletable(int maphong) {
        try {
            stmt = conn.prepareStatement(
                    "select dbo.IsPhongDeletable(?) as candelete");
            stmt.setInt(1, maphong);

            ResultSet rs = stmt.executeQuery();
            if (!rs.next()) {
                return false;
            } else {
                boolean isDeletable = rs.getBoolean("candelete");
                rs.close();
                return isDeletable;
            }
        } catch (SQLException ex) {
            Logger.getLogger(DatabaseHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    public ResultSet getAllRoomsFromComplex(int complexId) {
        try {
            stmt = conn.prepareStatement(
                    "select * from getphongthuockhu(?)");
            stmt.setInt(1, complexId);

            return stmt.executeQuery();
        } catch (SQLException ex) {
            Logger.getLogger(DatabaseHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public ResultSet getEmptyRoomsFromComplex(int complexId) {
        try {
            stmt = conn.prepareStatement(
                    "SELECT * FROM GETPHONGTRONGTHUOCKHU(?)");
            stmt.setInt(1, complexId);

            return stmt.executeQuery();
        } catch (SQLException ex) {
            Logger.getLogger(DatabaseHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public ResultSet getOccuppiedRoomsFromComplex(int complexId) {
        try {
            stmt = conn.prepareStatement(
                    "SELECT * FROM GETPHONGCONGUOITHUOCKHU(?)");
            stmt.setInt(1, complexId);

            return stmt.executeQuery();
        } catch (SQLException ex) {
            Logger.getLogger(DatabaseHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public ResultSet getAllContractsWithInfo(int makhu) {
        try {
            stmt = conn.prepareStatement(
                    "SELECT * FROM dbo.viewhopdongvaextrainfo where MAKHU=?");
            stmt.setInt(1, makhu);
            return stmt.executeQuery();
        } catch (SQLException ex) {
            Logger.getLogger(DatabaseHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public ResultSet getActiveContractsWithInfo(int makhu) {
        try {
            stmt = conn.prepareStatement(
                    "SELECT * FROM dbo.viewhopdongvaextrainfo where ngaytra>getdate() and makhu=?");
            stmt.setInt(1, makhu);

            return stmt.executeQuery();
        } catch (SQLException ex) {
            Logger.getLogger(DatabaseHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public ResultSet getOldContractsWithInfo(int makhu) {
        try {
            stmt = conn.prepareStatement(
                    "SELECT * FROM dbo.viewhopdongvaextrainfo where ngaytra<=getdate() and makhu=?");
            stmt.setInt(1, makhu);

            return stmt.executeQuery();
        } catch (SQLException ex) {
            Logger.getLogger(DatabaseHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public ResultSet getAllRoommatesWithInfo(int complexId) {
        try {
            stmt = conn.prepareStatement(
                    "SELECT * FROM viewkhachoghepvaextrainfo where makhu=?");
            stmt.setInt(1, complexId);

            return stmt.executeQuery();
        } catch (SQLException ex) {
            Logger.getLogger(DatabaseHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public ResultSet getActiveRoommatesWithInfo(int complexId) {
        try {
            stmt = conn.prepareStatement(
                    "SELECT * FROM viewkhachoghepvaextrainfo where makhu=? and ngaydi>getdate()");
            stmt.setInt(1, complexId);

            return stmt.executeQuery();
        } catch (SQLException ex) {
            Logger.getLogger(DatabaseHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public ResultSet getOldRoommatesWithInfo(int complexId) {
        try {
            stmt = conn.prepareStatement(
                    "SELECT * FROM viewkhachoghepvaextrainfo where makhu=? and ngaydi<=getdate()");
            stmt.setInt(1, complexId);

            return stmt.executeQuery();
        } catch (SQLException ex) {
            Logger.getLogger(DatabaseHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public boolean insertRoommate(int mahdong, int makh, LocalDate start, LocalDate end) {
        try {
            stmt = conn.prepareStatement(
                    "INSERT INTO HOPDONG_KHACH VALUES(?,?,?,?)");
            stmt.setInt(
                    1, mahdong);
            stmt.setInt(
                    2, makh);
            stmt.setDate(
                    3, Util.LocalDateToSQLDate(start));
            stmt.setDate(
                    4, Util.LocalDateToSQLDate(end));
            return stmt.executeUpdate() > 0;
        } catch (SQLException ex) {
            Logger.getLogger(DatabaseHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    public boolean updateRoommateStayingPeriod(int hdkID, LocalDate start, LocalDate end) {
        try {
            stmt = conn.prepareStatement(
                    "UPDATE HOPDONG_KHACH SET ngaynhan=?, ngaytra=? WHERE id=?");
            stmt.setDate(
                    1, Util.LocalDateToSQLDate(start));
            stmt.setDate(
                    2, Util.LocalDateToSQLDate(end));
            stmt.setInt(
                    3, hdkID);
            return (stmt.executeUpdate() > 0);
        } catch (SQLException ex) {
            Logger.getLogger(DatabaseHandler.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    public boolean endRoommateStayingPeriod(int hdkID) {
        try {
            stmt = conn.prepareStatement(
                    "UPDATE HOPDONG_KHACH SET ngaytra=getdate() WHERE id=?");
            stmt.setInt(1, hdkID);

            return (stmt.executeUpdate() > 0);
        } catch (SQLException ex) {
            Logger.getLogger(DatabaseHandler.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    public boolean deleteRoommate(int hdk) {
        try {
            stmt = conn.prepareStatement(
                    "DELETE FROM HOPDONG_KHACH WHERE id=?");
            stmt.setInt(1, hdk);

            return stmt.executeUpdate() > 0;
        } catch (SQLException ex) {
            Logger.getLogger(DatabaseHandler.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    public ResultSet getInDebtContractsWithInvoiceInfo() {
        try {
            stmt = conn.prepareStatement(
                    "select * from ViewHoadonvaextrainfo where songay>0");
            return stmt.executeQuery();
        } catch (SQLException ex) {
            Logger.getLogger(DatabaseHandler.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public ResultSet getPaidContractsWithInvoiceInfo() {
        try {
            stmt = conn.prepareStatement(
                    "select * from ViewHoadonvaextrainfo where songay<=0");
            return stmt.executeQuery();
        } catch (SQLException ex) {
            Logger.getLogger(DatabaseHandler.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public boolean deleteInvoice(int invoiceId) {
        try {
            stmt = conn.prepareStatement(
                    "DELETE FROM HOADON WHERE MAHDON=?");
            stmt.setInt(1, invoiceId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException ex) {
            Logger.getLogger(DatabaseHandler.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    public boolean insertNewContract(Contract c) {
        try {
            stmt = conn.prepareStatement(
                    "INSERT INTO HOPDONG VALUES(?,?,?,?,?)");
            stmt.setInt(
                    1, c.getMaPhong());
            stmt.setInt(
                    2, c.getMaKH());
            stmt.setDate(
                    3, Util.LocalDateToSQLDate(c.getNgayNhan()));
            stmt.setDate(
                    4, Util.LocalDateToSQLDate(c.getNgayTra()));
            stmt.setBigDecimal(
                    5, c.getTienCoc());
            return (stmt.executeUpdate() > 0);
        } catch (SQLException ex) {
            Logger.getLogger(DatabaseHandler.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    public boolean updateContract(Contract c) {
        try {
            stmt = conn.prepareStatement(
                    "UPDATE hopdong SET ngaytra=?, TIENCOC=? WHERE MAHDONG=?");
            stmt.setDate(
                    1, Util.LocalDateToSQLDate(c.getNgayTra()));
            stmt.setBigDecimal(
                    2, c.getTienCoc());
            stmt.setInt(
                    3, c.getId());
            return (stmt.executeUpdate() > 0);
        } catch (SQLException ex) {
            Logger.getLogger(DatabaseHandler.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    public boolean deleteContract(int contractId) {
        try {
            stmt = conn.prepareStatement(
                    "DELETE FROM HOPDONG WHERE MAHDONG=?");
            stmt.setInt(1, contractId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException ex) {
            Logger.getLogger(DatabaseHandler.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    public boolean isContractEndable(int mahdong) {
        try {
            stmt = conn.prepareStatement(
                    "select dbo.ishopdongendable(?) as canend");
            stmt.setInt(1, mahdong);

            ResultSet rs = stmt.executeQuery();
            if (!rs.next()) {
                return false;
            } else {
                boolean isEndable = rs.getBoolean("canend");
                rs.close();
                return isEndable;
            }
        } catch (SQLException ex) {
            Logger.getLogger(DatabaseHandler.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    public boolean endContract(int mahd, LocalDate ngaytra) {
        try {
            stmt = conn.prepareStatement(
                    "UPDATE hopdong SET ngaytra=? WHERE MAHDONG=?");
            stmt.setDate(
                    1, Util.LocalDateToSQLDate(ngaytra));
            stmt.setInt(
                    2, mahd);
            return (stmt.executeUpdate() > 0);
        } catch (SQLException ex) {
            Logger.getLogger(DatabaseHandler.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    public boolean endContractToday(int mahdong) {
        try {
            stmt = conn.prepareStatement(
                    "UPDATE hopdong SET ngaytra=getdate() WHERE MAHDONG=?");
            stmt.setInt(
                    1, mahdong);
            return (stmt.executeUpdate() > 0);
        } catch (SQLException ex) {
            Logger.getLogger(DatabaseHandler.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    public boolean insertNewInvoice(Invoice invoice) {
        try {
            stmt = conn.prepareStatement(
                    "INSERT INTO HOADON VALUES(?,?,?, getdate())");
            stmt.setInt(
                    1, invoice.getMahdong());
            stmt.setBigDecimal(
                    2, invoice.getSoTien());
            stmt.setDate(
                    3, Util.LocalDateToSQLDate(invoice.getNgayTT()));
            return (stmt.executeUpdate() > 0);
        } catch (SQLException ex) {
            Logger.getLogger(DatabaseHandler.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    public boolean insertNewMaintenance(Maintenance m) {
        try {
            stmt = conn.prepareStatement(
                    "INSERT INTO BAOTRI VALUES(?,?,?,?)");
            stmt.setInt(
                    1, m.getMaPhong());
            stmt.setBigDecimal(
                    2, m.getChiPhi());
            stmt.setDate(
                    3, Util.LocalDateToSQLDate(m.getNgay()));
            stmt.setNString(
                    4, m.getMoTa());
            return (stmt.executeUpdate() > 0);
        } catch (SQLException ex) {
            Logger.getLogger(DatabaseHandler.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    public boolean updateMaintenance(Maintenance m) {
        try {
            stmt = conn.prepareStatement(
                    "UPDATE BAOTRI SET CHIPHI=?, NGAY=?, MOTA=? WHERE MABAOTRI=?");

            stmt.setBigDecimal(
                    1, m.getChiPhi());
            stmt.setDate(
                    2, Util.LocalDateToSQLDate(m.getNgay()));
            stmt.setNString(
                    3, m.getMoTa());
            stmt.setInt(
                    4, m.getId());
            return (stmt.executeUpdate() > 0);
        } catch (SQLException ex) {
            Logger.getLogger(DatabaseHandler.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    public boolean deleteMaintenance(int maintenanceId) {
        try {
            stmt = conn.prepareStatement(
                    "DELETE FROM BAOTRI WHERE MABAOTRI=?");
            stmt.setInt(1, maintenanceId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException ex) {
            Logger.getLogger(DatabaseHandler.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    public ResultSet getMaintenanceFromComplex(int complexId) {
        try {
            stmt = conn.prepareStatement(
                    "select * from dbo.getbaotrithuockhu(?)");
            stmt.setInt(1, complexId);

            return stmt.executeQuery();
        } catch (SQLException ex) {
            Logger.getLogger(DatabaseHandler.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    /**
     * Methods for looking up statistic
     */
    public int getNumberOfCustomersInComplex(int complexId) {
        try {
            cstmt = conn.prepareCall(
                    "{ ? = call dbo.getsonguoitrongkhu(?) }");
            cstmt.registerOutParameter(1, Types.INTEGER);
            cstmt.setInt(2, complexId);

            cstmt.execute();
            return cstmt.getInt(1);
        } catch (SQLException ex) {
            Logger.getLogger(DatabaseHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
        return -1;
    }

    public BigDecimal getTotalRevenueOfComplexInMonth(int complexId, LocalDate date) {
        try {
            cstmt = conn.prepareCall(
                    "{ ? = call dbo.gettongtientrongthangcuakhu(?,?) }");
            cstmt.registerOutParameter(1, Types.DECIMAL);
            cstmt.setInt(2, complexId);
            cstmt.setDate(3, Util.LocalDateToSQLDate(date));

            cstmt.execute();
            return cstmt.getBigDecimal(1);
        } catch (SQLException ex) {
            Logger.getLogger(DatabaseHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
        return BigDecimal.valueOf(-1);
    }

    public BigDecimal getTotalRevenueOfComplexInYear(int complexId, LocalDate date) {
        try {
            cstmt = conn.prepareCall(
                    "{ ? = call dbo.gettongtientrongnamcuakhu(?,?) }");
            cstmt.registerOutParameter(1, Types.DECIMAL);
            cstmt.setInt(2, complexId);
            cstmt.setDate(3, Util.LocalDateToSQLDate(date));

            cstmt.execute();
            return cstmt.getBigDecimal(1);
        } catch (SQLException ex) {
            Logger.getLogger(DatabaseHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
        return BigDecimal.valueOf(-1);
    }

    public BigDecimal getTotalRevenueOfComplex(int complexId) {
        try {
            cstmt = conn.prepareCall(
                    "{ ? = call dbo.gettongtiencuakhu(?) }");
            cstmt.registerOutParameter(1, Types.DECIMAL);
            cstmt.setInt(2, complexId);

            cstmt.execute();
            return cstmt.getBigDecimal(1);
        } catch (SQLException ex) {
            Logger.getLogger(DatabaseHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
        return BigDecimal.valueOf(-1);
    }
}
