package main.util;

import java.io.IOException;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Callback;
import main.database.DatabaseHandler;
import main.ui.main.MainController;

public class Util {
    public static final SimpleDateFormat DATE_TIME_FORMAT = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss a");
    public static final SimpleDateFormat DISPLAY_DATE_FORMAT = new SimpleDateFormat("dd-MM-yyyy");
    public static final SimpleDateFormat DATABASE_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");    
    public static final String APP_ICON_LOCATION    = "main/resources/icons/icon.png";
    public static final String APP_NAME             = "Quản Lý Nhà Trọ";
    public static final String STYLE_SHEET_LOCATION = "src/main/app/bootstrap3.css";
    
    public static void setWindowIcon(Stage stage) {
        stage.getIcons().add(new Image(APP_ICON_LOCATION));
    }
    
    /**
     * 
     * @param loc đường dẫn đến file fxml cần load
     * @return AnchorPane
     */
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
    
    /**
     * 
     * @param loc đường dẫn đến file fxml cần load
     * @param title tên window
     * @param parentStage window cha
     * @return Object
     */
    public static Object loadWindow(URL loc, String title, Stage parentStage) {
        Object controller = null;
        try {
            Parent parent = FXMLLoader.load(loc);
   
            Stage stage = new Stage();
            stage.initOwner(parentStage);
            stage.initModality(Modality.WINDOW_MODAL);
            stage.initStyle(StageStyle.DECORATED);
            
            Scene scene = new Scene(parent);
            scene.getStylesheets().add(Util.STYLE_SHEET_LOCATION);
            
            stage.setTitle(title);
            stage.setScene(scene);
            stage.show();
            
            setWindowIcon(stage);
            return stage;
        } catch (IOException ex) {
            java.util.logging.Logger.getLogger(MainController.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        return null;
    }
    
    public static String formatDateTimeString(Date date) {
        return DATE_TIME_FORMAT.format(date);
    }
    
    public static String formatDateTimeString(Long time) {
        return DATE_TIME_FORMAT.format(new Date(time));
    }

    public static String dateToString(java.util.Date date) {
        return DISPLAY_DATE_FORMAT.format(date);
    }
    
    public static java.util.Date stringToDate(String string) throws ParseException {
        DISPLAY_DATE_FORMAT.setLenient(false);
        return DISPLAY_DATE_FORMAT.parse(string);
    }
    
    public static LocalDate SQLDateToLocalDate(java.sql.Date sqlDate) {
        return sqlDate.toLocalDate();
    }
    
    public static java.util.Date LocalDateToSQLDate(LocalDate local) {
        return java.sql.Date.valueOf(local);
    }
    
    public static Boolean genderToBit(String gt) {
        return !"Nam".equals(gt);
    }
    
    public static String bitToGender(Boolean b) {
        if(b == true) return "Nữ";
        else return "Nam";
    }
    
    /**
     * 
     * @param list
     * @param tableView
     * @param rs
     * @throws SQLException
     * load data lên tableView
     */
    public static void loadResultSetToTable(ObservableList list, ResultSet rs, TableView tableView) throws SQLException {
        list.clear();
        
        for (int i = 0; i < rs.getMetaData().getColumnCount(); i++) {
            final int j = i;
            TableColumn col = new TableColumn(rs.getMetaData().getColumnName(i + 1));
            col.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<ObservableList, String>, ObservableValue<String>>() {
                @Override
                public ObservableValue<String> call(TableColumn.CellDataFeatures<ObservableList, String> param) {
                    return new SimpleStringProperty(param.getValue().get(j).toString());
                }
            });

            tableView.getColumns().addAll(col);
            System.out.println("Column [" + i + "] ");
        }
        
        while (rs.next()) {
            //Iterate Row
            ObservableList<String> row = FXCollections.observableArrayList();
            for (int i = 1; i <= rs.getMetaData().getColumnCount(); i++) {
                //Iterate Column
                row.add(rs.getString(i));
            }
            System.out.println("Row [1] added " + row);
            list.add(row);

        }
        
        tableView.setItems(list);
        
    }
    
}
