package main.ui.addroom;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.layout.StackPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import main.database.DatabaseHandler;
import main.model.Complex;
import main.ui.addcomplex.AddComplexController;
import main.ui.alert.CustomAlert;
import main.ui.listcustomer.ListCustomerController;
import main.util.Util;

public class AddRoomController implements Initializable {

    @FXML
    private StackPane root;

    @FXML
    private ComboBox<Complex> comboBox;

    @FXML
    private Button addComplex;

    @FXML
    private Button editComplex;

    @FXML
    private TextArea name;

    @FXML
    private TextArea desc;

    @FXML
    private Button save;

    @FXML
    private Button cancel;

    
    // extra elements
    ObservableList<Complex> list = FXCollections.observableArrayList();
    DatabaseHandler dbHandler;
    private boolean isEditing = false;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        dbHandler = DatabaseHandler.getInstance();
        loadData();
    }

    @FXML
    private void loadData() {
        list.clear();
        comboBox.getItems().clear();

        String query = "SELECT * FROM KHU";
        ResultSet rs = dbHandler.execQuery(query);

        try {
            while (rs.next()) {
                int id = rs.getInt("MAKHU");
                String ten = rs.getString("TENKHU");
                String diaChi = rs.getString("DIACHI");
                list.add(new Complex(id, ten, diaChi));
                
                System.out.println(id + ten + diaChi);
            }
        } catch (SQLException ex) {
            Logger.getLogger(AddRoomController.class.getName()).log(Level.SEVERE, null, ex);
        }
        comboBox.getItems().addAll(list);
    }

    @FXML
    private void handleSave(ActionEvent evt) {
        // TODO not done saving thís
        Complex selected = comboBox.getSelectionModel().getSelectedItem();
        System.out.println(selected.debugString());
    }
    
    @FXML
    private void handleCancel(ActionEvent evt) {
        getStage().close();
    }
    
    @FXML
    private void handleAddComplex(ActionEvent event) {
        System.out.println("load add complex window");
        Stage stage = (Stage) Util.loadWindow(getClass().getResource(
                "/main/ui/addcomplex/addComplex.fxml"),
                "Thêm khu nhà", getStage());
        stage.setOnHiding((e) -> {
            handleRefresh(new ActionEvent());
        });
    }
    
    @FXML
    private void handleEditComplex(ActionEvent event) {
        System.out.println("editing complex");

        Complex selectedForEdit = comboBox.getSelectionModel().getSelectedItem();

        if (selectedForEdit == null) {
            CustomAlert.showErrorMessage("Chưa chọn.", "Hãy chọn một khu nhà để chỉnh sửa");
            return;
        }
        
        System.out.println(selectedForEdit.debugString());
        
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(
                    "/main/ui/addcomplex/addComplex.fxml"));
            Parent parent = loader.load();

            AddComplexController con = loader.getController();
            con.loadEntries(selectedForEdit);
            
            Stage stage = new Stage(StageStyle.DECORATED);
            stage.initOwner(getStage());
            stage.initModality(Modality.WINDOW_MODAL);

            Scene scene = new Scene(parent);
            scene.getStylesheets().add(getClass().getResource(Util.STYLE_SHEET_LOCATION).toString());

            stage.setScene(scene);
            stage.setTitle("Chỉnh sửa khu nhà");
            stage.show();
            Util.setWindowIcon(stage);

            stage.setOnHiding((e) -> {
                handleRefresh(new ActionEvent());
            });
        } catch (IOException ex) {
            Logger.getLogger(AddRoomController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }



    @FXML
    private void handleRefresh(ActionEvent event) {
        loadData();
    }


    private void handleEditRoom() {
        Complex chosenComplex = comboBox.getSelectionModel().getSelectedItem();
        
        String rName = name.getText().trim();
        String rDescript = desc.getText().trim();

//        currentCustomer.setHoTen(customerName);
//        currentCustomer.setGioiTinh(customerSex);
//        currentCustomer.setNgaySinh(customerBDay);
//        currentCustomer.setSDT(customerSDT);
//        currentCustomer.setCMND(customerCMND);
        
//        if(dbHandler.updateCustomer(currentCustomer)) {
//            CustomAlert.showSimpleAlert("Đã thêm", "Update thành công");
//            currentCustomer = null;
//        } else {
//            CustomAlert.showErrorMessage("Chỉnh sửa thất bại", "Không thể thực hiện");
//        }
    }
    
    private Stage getStage() {
        return (Stage) root.getScene().getWindow();
    }
    
}
