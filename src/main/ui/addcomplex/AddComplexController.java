package main.ui.addcomplex;

import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import main.database.DatabaseHandler;
import main.model.Complex;
import main.ui.alert.CustomAlert;

public class AddComplexController implements Initializable {
    @FXML
    private StackPane root;
    
    @FXML
    private TextField name;

    @FXML
    private TextArea address;

    @FXML
    private Button save;

    @FXML
    private Button delete;

    @FXML
    private Button cancel;

    // extra elements
    DatabaseHandler dbHandler;
    private boolean isEditing = false;
    Complex currentComplex = null;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        dbHandler = DatabaseHandler.getInstance();
        showDeleteButton(isEditing);
    }
    
    public void showDeleteButton(boolean show) {
        delete.setVisible(show);
    }

    public void loadEntries(Complex c) {
        name.setText(c.getTen());
        address.setText(c.getDiaChi());

        isEditing = true;
        currentComplex = c;
    }
    
    public void clearEntries() {
        name.setText("");
        address.setText("");
    }
    
    @FXML
    private void handleSave(ActionEvent event) {
        if (isEditing) {
            handleEdit();
            return;
        }

        String cName = name.getText().trim();
        String cAddr = address.getText().trim();
        
        // TODO complex name check
//        if (dbHandler.isComplexNameExist(cName)) {
//            CustomAlert.showErrorMessage("Tên khu đã tồn tại", "Hãy nhập tên khác");
//            return;
//        }
        
        Complex c = new Complex(
                cName,
                cAddr);

        if (dbHandler.insertNewComplex(c)) {
            CustomAlert.showSimpleAlert("Khu ", cName + " đã được thêm");
            clearEntries();
        } else {
            CustomAlert.showErrorMessage("Không thêm được khu", "Hãy kiểm lại tra thông tin và thử lại");
        }
    }

    @FXML
    private void handleEdit() {
        String cName = name.getText().trim();
        String cAddr = address.getText().trim();
        
        currentComplex.setTen(cName);
        currentComplex.setDiaChi(cAddr);
        
        if(dbHandler.updateComplex(currentComplex)) {
            CustomAlert.showSimpleAlert("Chỉnh sửa hành công", "");
            currentComplex = null;
        } else {
            CustomAlert.showErrorMessage("Chỉnh sửa thất bại", "Không thể thực hiện");
        }
    }
    
    @FXML
    private void handleDeleteComplex(ActionEvent event) {
        // TODO make a check before deleting a complex
//        if (DatabaseHandler.getInstance().isRenting(selectedForDeletion)) {
//            CustomAlert.showErrorMessage("Không thể xóa", "Không thể xóa khu nhà có phòng đang cho thuê.");
//            return;
//        }
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Xóa khu");
        alert.setContentText("Bạn có muốn xóa khu " + currentComplex.getTen() + " ?");
        Optional<ButtonType> answer = alert.showAndWait();
        if (answer.get() == ButtonType.OK) {
            Boolean result = dbHandler.deleteComplex(currentComplex);
            if (result) {
                CustomAlert.showSimpleAlert("Đã xóa", "Đã xóa khu " + currentComplex.getTen());
            } else {
                CustomAlert.showSimpleAlert("Thất bại", "Không thể xóa khu " + currentComplex.getTen());
            }
        } else {
            CustomAlert.showSimpleAlert("Hủy", "Hủy xóa");
        }
    }
    
    @FXML
    private void handleCancel(ActionEvent event) {
        Stage stage = (Stage) root.getScene().getWindow();
        stage.close();
    }
}
