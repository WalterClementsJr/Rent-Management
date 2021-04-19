package main.util;

import java.io.IOException;
import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import main.ui.main.MainController;

public class Util {
    private static final SimpleDateFormat DATE_TIME_FORMAT = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss a");
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd-MM-yyyy");
    public static final String APP_ICON_LOCATION    = "main/resources/icons/icon.png";
    public static final String APP_NAME             = "Quản Lý Nhà Trọ";
    public static final String STYLE_SHEET_LOCATION = "/main/ui/bootstrap3.css";
    
    public static void setWindowIcon(Stage stage) {
        stage.getIcons().add(new Image(APP_ICON_LOCATION));
    }
    
    public static AnchorPane loadPane(URL loc) {
        try {
            FXMLLoader loader = new FXMLLoader(loc);
            
            loader.getController();
            return loader.load();
        } catch (IOException ex) {
            java.util.logging.Logger.getLogger(MainController.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        return null;
    }
    
    public static Object loadWindow(URL loc, String title, Stage parentStage) {
        Object controller = null;
        try {
            FXMLLoader loader = new FXMLLoader(loc);
            Parent parent = loader.load();
            controller = loader.getController();
            
            Stage stage;
            if (parentStage != null) {
                stage = parentStage;
            } else {
                stage = new Stage(StageStyle.DECORATED);
            }
            stage.setTitle(title);
            stage.setScene(new Scene(parent));
            stage.show();
            setWindowIcon(stage);
        } catch (IOException ex) {
            java.util.logging.Logger.getLogger(MainController.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        return controller;
    }
    
    public static String formatDateTimeString(Date date) {
        return DATE_TIME_FORMAT.format(date);
    }
    
    public static String formatDateTimeString(Long time) {
        return DATE_TIME_FORMAT.format(new Date(time));
    }

    public static String dateToString(java.util.Date date) {
        return DATE_FORMAT.format(date);
    }
    
    public static java.util.Date stringToDate(String string) throws ParseException {
        DATE_FORMAT.setLenient(false);
        return DATE_FORMAT.parse(string);
    }
    
    public static java.sql.Date utilDateToSQLDate(java.util.Date utilDate) {
        return new java.sql.Date(utilDate.getTime());
    }
    
    public static java.util.Date SQLDateToUtilDate(java.sql.Date sqlDate) {
        return new java.util.Date(sqlDate.getTime());
    }
    
    public static Boolean genderToBit(String gt) {
        if("Nam".equals(gt)) return false;
        else return true;
    }
    
    public static String bitToGender(Boolean b) {
        if(b == true) return "Nữ";
        else return "Nam";
    }
    
    
}
