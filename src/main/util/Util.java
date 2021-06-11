package main.util;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.MathContext;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import static java.time.temporal.ChronoUnit.DAYS;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import main.app.Main;
import main.app.settings.Setting;
import main.ui.listcontract.ListContractController;

public class Util {

    public static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("d-M-yyyy");
    public static final DateTimeFormatter SQL_DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-M-d");

    public static final String APP_ICON_LOCATION
            = "main/resources/icons/icon.png";
    public static final String APP_NAME
            = "Quản Lý Nhà Trọ";

    public static enum Themes {
        BOOTSTRAP(
                "Bootstrap3", "/main/app/bootstrap3.css"),
        WIN7(
                "Windows 7", "/main/app/win7.css"),
        FLAT(
                "FlatBee", "/main/app/flatbee.css"),
        MATERIAL(
                "Material FX", "/main/app/material.css"),
        DARK(
                "Dark (Beta)", "/main/app/dark.css"),
        GLISTENDARK(
                "Glisten Dark (Beta)", "/main/app/glistendark.css"),
        MODENATOUCH(
                "Modena (Beta)", "/main/app/modena.css"),
        MODENAWOB(
                "Modena - White On Black (Beta)", "/main/app/whiteOnBlack.css"),
        CASPIAN(
                "Caspian (Beta)", "/main/app/caspian.css"),
        CASPIANEM(
                "Caspian Embedded (Beta)", "/main/app/embedded.css");

        private final String name;
        private final String location;

        private Themes(String name, String location) {
            this.name = name;
            this.location = location;
        }

        public String getName() {
            return name;
        }

        public String getLocation() {
            return location;
        }

        @Override
        public String toString() {
            return name;
        }
    }

    public static final String FILTER_ALL = "Tất cả";
    public static final String FILTER_ACTIVE = "Đang ở";
    public static final String FILTER_OLD = "Đã hết hạn";
    public static final String FILTER_ROOM_EMPTY = "Trống";
    public static final String FILTER_ROOM_OCCUPIED = "Đã có người";
    public static final String FILTER_CUSTOMER_NO_ROOM = "Chưa có phòng";
    public static final String FILTER_CUSTOMER_HAS_ROOM = "Đã có phòng";
    public static final String FILTER_CUSTOMER_MOVED = "Đã chuyển đi";

    public static void main(String[] args) {
        checkLogin(null);
    }

    public static void setWindowIcon(Stage stage) {
        stage.getIcons().add(new Image(APP_ICON_LOCATION));
    }

    /**
     * load pane with its controller
     *
     * @param loc fxml file URL
     * @return AnchorPane
     */
    public static AnchorPane loadPane(URL loc) {
        try {
            FXMLLoader loader = new FXMLLoader(loc);
            loader.getController();
            return loader.load();
        } catch (IOException ex) {
            Logger.getLogger(Util.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    /**
     * show login
     *
     * @param parentStage
     */
    public static void checkLogin(Stage parentStage) {
        if (Setting.getInstance().checkPassword("")) {
            Setting.IS_VERIFIED = true;
        } else if (!Setting.IS_VERIFIED) {
            try {
                FXMLLoader loader = new FXMLLoader(
                        Main.class.getResource(
                                "/main/ui/login/login.fxml"));
                loader.getController();
                Parent login = loader.load();

                Stage stage = new Stage(StageStyle.DECORATED);
                stage.initOwner(parentStage);
                stage.initModality(Modality.WINDOW_MODAL);

                Scene scene = new Scene(login);
                scene.getStylesheets().add(Util.class.getResource(
                        Setting.getInstance().getSTYLE_SHEET()).toString());
                stage.setScene(scene);
                stage.setTitle("Đăng nhập");
                setWindowIcon(stage);
                stage.showAndWait();
            } catch (IOException | IllegalStateException ex) {
                Logger.getLogger(Util.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    /**
     *
     * @param loc đường dẫn đến file fxml cần load
     * @param title tên window
     * @param parentStage window cha
     * @return Object
     */
    public static Object loadWindow(URL loc, String title, Stage parentStage) {
        try {
            Parent parent = FXMLLoader.load(loc);

            Stage stage = new Stage();
            stage.initOwner(parentStage);
            stage.initModality(Modality.WINDOW_MODAL);
            stage.initStyle(StageStyle.DECORATED);

            Scene scene = new Scene(parent);
            scene.getStylesheets().add(Setting.getInstance().getSTYLE_SHEET());

            stage.setTitle(title);
            stage.setScene(scene);
            stage.show();

            setWindowIcon(stage);
            return stage;
        } catch (IOException ex) {
            Logger.getLogger(Util.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public static LocalDate stringToLocalDate(String string) {
        return LocalDate.parse(string, DATE_TIME_FORMATTER);
    }

    public static LocalDate SQLDateToLocalDate(java.sql.Date sqlDate) {
        return sqlDate.toLocalDate();
    }

    public static java.sql.Date LocalDateToSQLDate(LocalDate local) {
        return java.sql.Date.valueOf(local);
    }

    public static String chuanHoaTen(String ten) {
        ten = ten.toLowerCase();
        String[] tungTuTrongTen = ten.split("[\\s]+");
        ten = "";
        for (int i = 0; i < tungTuTrongTen.length; i++) {
            String phanChuThuong = tungTuTrongTen[i].substring(1);
            tungTuTrongTen[i] = tungTuTrongTen[i].substring(0, 1).toUpperCase();
            if (i == tungTuTrongTen.length - 1) {
                ten = ten + tungTuTrongTen[i] + phanChuThuong;
            } else {
                ten = ten + tungTuTrongTen[i] + phanChuThuong + " ";
            }
        }
        return ten;
    }

    public static Boolean genderToBit(String gt) {
        return !"Nam".equals(gt);
    }

    public static String bitToGender(Boolean b) {
        return b == true ? "Nữ" : "Nam";
    }

    public static void loadResultSetToList(ResultSet rs, ObservableList list) {
        list.clear();
        try {
            while (rs.next()) {
                ObservableList row = FXCollections.observableArrayList();
                for (int i = 1; i <= rs.getMetaData().getColumnCount(); i++) {
                    row.add(rs.getString(i).trim());
                }
                list.add(row);
            }
            rs.close();
        } catch (SQLException ex) {
            Logger.getLogger(ListContractController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Calculate rent base on how many dates between 2 LocalDates and round to
     * nearest 10000
     *
     * @param monthlyRent
     * @param start
     * @param end
     * @return
     */
    public static BigDecimal getRent(BigDecimal monthlyRent, LocalDate start, LocalDate end) {
        long duration = DAYS.between(start, end);
        BigDecimal rent
                = monthlyRent.multiply(BigDecimal.valueOf(12 * duration));
        rent = rent.divide(BigDecimal.valueOf(365), MathContext.DECIMAL64);
        return rent;
    }

    public static BigDecimal getRent(BigDecimal monthlyRent, int duration) {
        BigDecimal rent
                = monthlyRent.multiply(BigDecimal.valueOf(12 * duration));
        rent = rent.divide(BigDecimal.valueOf(365), MathContext.DECIMAL64);
        return rent;
    }
}
