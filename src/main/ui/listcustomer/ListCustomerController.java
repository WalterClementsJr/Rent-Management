package main.ui.listcustomer;

import java.io.IOException;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Callback;
import main.database.DatabaseHandler;
import main.model.Customer;
import main.ui.addcustomer.AddCustomerController;
import main.ui.alert.CustomAlert;
import main.ui.main.MainController;
import main.util.Util;

public class ListCustomerController implements Initializable {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private AnchorPane root;

    @FXML
    private TableView<Customer> tableView;

    @FXML
    private MenuItem edit;

    @FXML
    private MenuItem delete;

    @FXML
    private Button btnEdit;

    @FXML
    private Button btnAdd;

    @FXML
    private Button btnDelete;

    // extra elements
    ObservableList<Customer> list = FXCollections.observableArrayList();

    public static void main(String[] args) {
        DatabaseHandler handler = DatabaseHandler.getInstance();
        String query = "SELECT * FROM KHACH";
        
        ResultSet rs = handler.execQuery(query);

        try {
            while (rs.next()) {
                System.out.println(rs.getString(1));
            }
        } catch (SQLException ex) {
            Logger.getLogger(ListCustomerController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        try {
            loadData();
        } catch (SQLException ex) {
            Logger.getLogger(ListCustomerController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private Stage getStage() {
        return (Stage) root.getScene().getWindow();
    }

    private void loadData() throws SQLException {
        list.clear();

        DatabaseHandler handler = DatabaseHandler.getInstance();
        String query = "SELECT * FROM KHACH";
        ResultSet rs = handler.execQuery(query);

        Util.loadResultSetToTable(list, rs, tableView);
    }

    @FXML
    private void handleAddButton(ActionEvent event) {
        System.out.println("load add customer window");
        Stage stage = (Stage) Util.loadWindow(getClass().getResource(
                "/main/ui/addcustomer/addCustomer.fxml"),
                "Add New Customer", getStage());
    }

    @FXML
    void handleEditButton(ActionEvent event) {
        System.out.println("editing customer");

//        Customer selectedForEdit = tableView.getSelectionModel().getSelectedItem();
//        if (selectedForEdit == null) {
//            AlertMaker.showErrorMessage("No book selected", "Please select a book for edit.");
//            return;
//        }
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/main/ui/addcustomer/addCustomer.fxml"));
            Parent parent = loader.load();

            loader.getController();

            Stage stage = new Stage(StageStyle.DECORATED);
            stage.initOwner(getStage());
            stage.initModality(Modality.WINDOW_MODAL);

            Scene scene = new Scene(parent);
            scene.getStylesheets().add(Util.STYLE_SHEET_LOCATION);

            stage.setScene(scene);
            stage.setTitle("Edit Customer");
            stage.show();
            Util.setWindowIcon(stage);

//            stage.setOnHiding((e) -> {
//                handleRefresh(new ActionEvent());
//            });
        } catch (IOException ex) {
            Logger.getLogger(ListCustomerController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
