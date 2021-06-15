package main.ui.addmaintenance;

import java.math.BigDecimal;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import main.app.settings.Setting;
import main.database.DatabaseHandler;
import main.model.Maintenance;
import main.model.Room;
import main.ui.alert.CustomAlert;
import main.util.Util;

public class AddMaintenanceController implements Initializable {

    @FXML
    private AnchorPane root;
    @FXML
    private TextArea desc;
    @FXML
    private TextField price;
    @FXML
    private DatePicker date;
    @FXML
    private Button save;
    @FXML
    private Button cancel;

    // extra
    private boolean isEditing = false;
    Maintenance currentMaintenance = null;
    Room currentRoom = null;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // date format
        date.setValue(LocalDate.now());
        date.setConverter(new StringConverter<LocalDate>() {
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("d-M-yyyy");

            {
                date.setPromptText("".toLowerCase());
            }

            @Override
            public String toString(LocalDate date) {
                if (date != null) {
                    return dateFormatter.format(date);
                } else {
                    return "";
                }
            }

            @Override
            public LocalDate fromString(String string) {
                if (string != null && !string.isEmpty()) {
                    return LocalDate.parse(string, dateFormatter);
                } else {
                    return null;
                }
            }
        });
        price.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue,
                    String newValue) {
                if (newValue.matches("\\d{0,15}")) {
                    String value = newValue;
                } else {
                    price.setText(oldValue);
                    price.positionCaret(price.getLength());
                }
            }
        });
    }

    @FXML
    private void handleAdd(ActionEvent event) {
        Util.checkLogin(getStage());

        if (!Setting.IS_VERIFIED) {
            return;
        }
        if (isEditing) {
            handleEdit();
            return;
        } else if (!checkEntries()) {
            return;
        } else if (currentRoom == null) {
            CustomAlert.showSimpleAlert("Lỗi", "Chưa chọn phòng");
            return;
        }

        Maintenance mtn = new Maintenance(
                currentRoom.getId(),
                new BigDecimal(price.getText().trim()),
                date.getValue(),
                desc.getText().trim());
        if (DatabaseHandler.getInstance().insertNewMaintenance(mtn)) {
            CustomAlert.showSimpleAlert(
                    "Thành công",
                    "Đã thêm thông tin bảo trì vào phòng");
            getStage().close();
        } else {
            CustomAlert.showErrorMessage(
                    "Thất bại",
                    "Hãy kiểm lại tra thông tin và thử lại");
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
        if (currentMaintenance == null) {
            CustomAlert.showSimpleAlert(
                    "Lỗi", "Chưa chọn thông tin bảo trì cần chỉnh sửa");
            return;
        }
        currentMaintenance.setMoTa(desc.getText().trim());
        currentMaintenance.setChiPhi(new BigDecimal(price.getText().trim()));
        currentMaintenance.setNgay(date.getValue());

        Optional<ButtonType> answer
                = CustomAlert.confirmDialog(
                        "Chỉnh sửa thông tin bảo trì",
                        "Xác nhận chỉnh sửa?").showAndWait();
        if (answer.get() == ButtonType.OK) {
            if (DatabaseHandler.getInstance().updateMaintenance(currentMaintenance)) {
                CustomAlert.showSimpleAlert(
                        "Thành công", "Đã sửa thông tin bảo trì");
                currentMaintenance = null;
                getStage().close();
            } else {
                CustomAlert.showErrorMessage(
                        "Thất bại",
                        "Hãy kiểm lại tra thông tin và thử lại");
            }
        }

    }

    @FXML
    private void handleCancel(ActionEvent event) {
        getStage().close();
    }

    private Stage getStage() {
        return (Stage) root.getScene().getWindow();
    }

    public void loadRoom(Room room) {
        currentRoom = room;
    }

    public void loadEntries(Maintenance m) {
        desc.setText(m.getMoTa());
        price.setText(m.getChiPhi().stripTrailingZeros().toPlainString());
        date.setValue(m.getNgay());

        isEditing = true;
        currentMaintenance = m;
    }

    private boolean checkEntries() {
        if (desc.getText().isBlank()) {
            CustomAlert.showErrorMessage("Mô tả trống", "Hãy nhập sơ lược thông tin sửa chữa/bảo trì");
            return false;
        } else if (desc.getText().length() > 100) {
            CustomAlert.showErrorMessage("Mô tả lớn hơn 100 ký tự", "Hãy nhập sơ lược thông tin sửa chữa/bảo trì (ngắn hơn 100 ký tự)");
            return false;
        } else if (price.getText().isBlank()) {
            CustomAlert.showErrorMessage("Chưa nhập chi phí", "Hãy nhập đầy đủ thông tin");
            return false;
        } else if (date.getValue() == null) {
            CustomAlert.showErrorMessage("Chưa nhập ngày", "Hãy nhập đầy đủ thông tin");
            return false;
        }
        return true;
    }
}
