package main.ui.addcomplex;

import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
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

        delete.setVisible(isEditing);

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

        if (!checkField()) {
            return;
        }

        String cName = name.getText().trim();
        String cAddr = address.getText().trim();

        if (dbHandler.isComplexExist(-1, cName)) {
            CustomAlert.showErrorMessage("Tên khu đã tồn tại", "Hãy nhập tên khác");
            return;
        }

        Complex c = new Complex(
                cName,
                cAddr);

        if (dbHandler.insertNewComplex(c)) {
            CustomAlert.showSimpleAlert("Thành công", "Khu " + cName + " đã được thêm");
            clearEntries();
        } else {
            CustomAlert.showErrorMessage("Không thêm được khu", "Hãy kiểm lại tra thông tin và thử lại");
        }
    }

    private void handleEdit() {
        if (!checkField()) {
            return;
        }

        String cName = name.getText().trim();
        String cAddr = address.getText().trim();

        if (dbHandler.isComplexExist(currentComplex.getId(), cName)) {
            CustomAlert.showErrorMessage("Tên khu đã tồn tại", "Hãy nhập tên khác");
            return;
        }

        currentComplex.setTen(cName);
        currentComplex.setDiaChi(cAddr);

        if (dbHandler.updateComplex(currentComplex)) {
            CustomAlert.showSimpleAlert("Thành công", "Chỉnh sửa thành công");
            currentComplex = null;
        } else {
            CustomAlert.showErrorMessage("Thất bại", "Đã có lỗi xảy ra. Vui lòng thử lại sau");
        }
    }

    @FXML
    private void handleDeleteComplex(ActionEvent event) {
        if (DatabaseHandler.getInstance().isComplexDeletable(currentComplex.getId())) {
            CustomAlert.showErrorMessage("Lỗi", "Không thể xóa khu nhà có phòng.");
            return;
        }

        Optional<ButtonType> answer
                = CustomAlert.confirmDelete(
                        "Xóa khách",
                        "Bạn có chắc muốn xóa" + currentComplex.getTen() + "?").showAndWait();
        if (answer.get() == ButtonType.OK) {
            Boolean result = dbHandler.deleteComplex(currentComplex);
            if (result) {
                CustomAlert.showSimpleAlert(
                        "Đã xóa",
                        "Đã xóa khu " + currentComplex.getTen());
            } else {
                CustomAlert.showSimpleAlert(
                        "Thất bại",
                        "Hãy xóa các thông tin liên quan đến khu này và thử lại");
            }
        }
    }

    @FXML
    private void handleCancel(ActionEvent event) {
        Stage stage = (Stage) root.getScene().getWindow();
        stage.close();
    }

    private boolean checkField() {
        if (name.getText().isBlank()) {
            CustomAlert.showErrorMessage("Tên khu trống", "Hãy nhập tên khu");
            return false;
        } else if (address.getText().isBlank()) {
            CustomAlert.showErrorMessage("Địa chỉ trống", "Hãy nhập địa chỉ");
            return false;
        }
        return true;
    }
}
