package main.ui.addcomplex;

import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import main.app.settings.Setting;
import main.database.DatabaseHandler;
import main.model.Complex;
import main.ui.alert.CustomAlert;
import main.util.Util;

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

        // < 50 characters
        name.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue,
                    String newValue) {
                if (!newValue.matches(".{0,50}")) {
                    name.setText(oldValue);
                    name.positionCaret(name.getLength());
                }
            }
        });

        address.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue,
                    String newValue) {
                if (!newValue.matches(".{0,200}")) {
                    address.setText(oldValue);
                    address.positionCaret(address.getLength());
                }
            }
        });

        delete.setVisible(false);
    }

    public void loadEntries(Complex c) {
        name.setText(c.getTen());
        address.setText(c.getDiaChi());

        isEditing = true;
        delete.setVisible(true);
        currentComplex = c;
    }

    @FXML
    private void handleSave(ActionEvent event) {
        Util.checkLogin(getStage());

        if (!Setting.IS_VERIFIED) {
            return;
        }
        if (isEditing) {
            handleEdit();
            return;
        }

        if (!checkEntries()) {
            return;
        }

        String cName = name.getText().trim();
        String cAddr = !address.getText().isBlank() ? address.getText().trim() : "";

        if (dbHandler.isComplexExist(-1, cName)) {
            CustomAlert.showErrorMessage("T??n khu ???? t???n t???i", "H??y nh???p t??n kh??c");
            return;
        }

        Complex c = new Complex(
                cName,
                cAddr);

        if (dbHandler.insertNewComplex(c)) {
            CustomAlert.showSimpleAlert("Th??nh c??ng", "Khu " + cName + " ???? ???????c th??m");
            getStage().close();
        } else {
            CustomAlert.showErrorMessage("Kh??ng th??m ???????c khu", "H??y ki???m l???i tra th??ng tin v?? th??? l???i");
        }
    }

    private void handleEdit() {
        Util.checkLogin(getStage());

        if (!Setting.IS_VERIFIED) {
            return;
        }
        if (!checkEntries()) {
            return;
        }

        String cName = name.getText().trim();
        String cAddr = !address.getText().isBlank() ? address.getText().trim() : "";

        if (dbHandler.isComplexExist(currentComplex.getId(), cName)) {
            CustomAlert.showErrorMessage("T??n khu ???? t???n t???i", "H??y nh???p t??n kh??c");
            return;
        }

        currentComplex.setTen(cName);
        currentComplex.setDiaChi(cAddr);

        Optional<ButtonType> answer
                = CustomAlert.confirmDialog(
                        "Ch???nh s???a khu",
                        "X??c nh???n ch???nh s???a?"
                ).showAndWait();
        if (answer.get() == ButtonType.OK) {
            if (dbHandler.updateComplex(currentComplex)) {
                CustomAlert.showSimpleAlert(
                        "Th??nh c??ng", "Ch???nh s???a th??nh c??ng");
                currentComplex = null;
                getStage().close();
            } else {
                CustomAlert.showErrorMessage(
                        "Th???t b???i",
                        "???? c?? l???i x???y ra. Vui l??ng th??? l???i sau");
            }
        }
    }

    @FXML
    private void handleDeleteComplex(ActionEvent event) {
        if (!DatabaseHandler.getInstance().isComplexDeletable(currentComplex.getId())) {
            CustomAlert.showErrorMessage("L???i", "Kh??ng th??? x??a khu nh?? c?? ph??ng.");
            return;
        }

        Optional<ButtonType> answer
                = CustomAlert.confirmDialog(
                        "X??c nh???n x??a",
                        "B???n c?? ch???c mu???n x??a " + currentComplex.getTen() + "?").showAndWait();
        if (answer.get() == ButtonType.OK) {
            if (dbHandler.deleteComplex(currentComplex)) {
                CustomAlert.showSimpleAlert(
                        "Th??nh c??ng",
                        "???? x??a khu " + currentComplex.getTen());
                getStage().close();
            } else {
                CustomAlert.showSimpleAlert(
                        "Th???t b???i",
                        "H??y x??a c??c th??ng tin li??n quan ?????n khu n??y v?? th??? l???i");
            }
        }
    }

    @FXML
    private void handleCancel(ActionEvent event) {
        Stage stage = (Stage) root.getScene().getWindow();
        stage.close();
    }

    private boolean checkEntries() {
        if (name.getText().isBlank()) {
            CustomAlert.showErrorMessage("T??n khu tr???ng", "H??y nh???p t??n khu");
            return false;
        } else if (address.getText().length() > 200) {
            CustomAlert.showErrorMessage("?????a ch??? l???n h??n 200 k?? t???", "H??y nh???p l???i ?????a ch???");
            return false;
        }
        return true;
    }

    private Stage getStage() {
        return (Stage) root.getScene().getWindow();
    }
}
