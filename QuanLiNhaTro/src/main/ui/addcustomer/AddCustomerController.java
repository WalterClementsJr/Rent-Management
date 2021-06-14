package main.ui.addcustomer;

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
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import main.app.settings.Setting;
import main.database.DatabaseHandler;
import main.model.Customer;
import main.ui.alert.CustomAlert;
import main.util.Util;

public class AddCustomerController implements Initializable {

    @FXML
    private StackPane rootAddCustomer;

    @FXML
    private TextField name;

    @FXML
    private RadioButton btnMale;

    @FXML
    private RadioButton btnFemale;

    @FXML
    private TextField sdt;

    @FXML
    private TextField cmnd;

    @FXML
    private DatePicker datePicker;

    @FXML
    private Button save;

    @FXML
    private Button cancel;

    // extra elements
    private DatabaseHandler dbHandler;
    private ToggleGroup sex;
    private boolean isEditing = false;
    Customer currentCustomer = null;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        dbHandler = DatabaseHandler.getInstance();

        // setup UI elements
        sex = new ToggleGroup();
        btnMale.setToggleGroup(sex);
        btnFemale.setToggleGroup(sex);

        // datePicker format
        datePicker.setConverter(new StringConverter<LocalDate>() {
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("d-M-yyyy");

            {
                datePicker.setPromptText("".toLowerCase());
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

        // sdt, cmnd number-only textArea
        sdt.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(
                    ObservableValue<? extends String> observable,
                    String oldValue,
                    String newValue) {
                if (newValue.matches("\\d{0,11}")) {
                    String value = newValue;
                } else {
                    sdt.setText(oldValue);
                    sdt.positionCaret(sdt.getLength());
                }
            }
        });
        cmnd.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable,
                    String oldValue,
                    String newValue) {
                if (newValue.matches("\\d{0,12}")) {
                    String value = newValue;
                } else {
                    cmnd.setText(oldValue);
                    cmnd.positionCaret(cmnd.getLength());
                }
            }
        });
    }

    /**
     *
     * @param customer load thông tin của customer lên cửa sổ edit
     */
    public void loadEntries(Customer customer) {
        name.setText(customer.getHoTen());

        if (customer.getGioiTinh()) {
            sex.selectToggle(btnFemale);
        } else {
            sex.selectToggle(btnMale);
        }

        datePicker.setValue(customer.getNgaySinh());
        sdt.setText(customer.getSDT().trim());
        cmnd.setText(customer.getCMND().trim());
        isEditing = true;
        currentCustomer = customer;
    }

    @FXML
    private void handleSave(ActionEvent event) {
        Util.checkLogin(getStage());

        if (!Setting.IS_VERIFIED) {
            return;
        } else if (isEditing) {
            handleEdit();
            return;
        } else if (!checkEntries()) {
            return;
        }

        String customerName = Util.chuanHoaTen(name.getText().trim());
        boolean customerSex = sex.getSelectedToggle().equals(btnFemale);
        LocalDate customerBDay = datePicker.getValue();
        String customerCMND = cmnd.getText().trim();
        String customerSDT = sdt.getText().trim();

        if (dbHandler.isCMNDExist(-1, customerCMND)) {
            CustomAlert.showErrorMessage(
                    "CMND đã tồn tại",
                    "Số CMND của %s đã tồn tại".formatted(customerName));
            return;
        }
        Customer customer = new Customer(
                customerName,
                customerSex,
                customerBDay,
                customerSDT,
                customerCMND);

        if (dbHandler.insertNewCustomer(customer)) {
            CustomAlert.showSimpleAlert(
                    "Thành công",
                    customerName + " đã được thêm");
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
        } else if (!checkEntries()) {
            return;
        }

        String customerName = Util.chuanHoaTen(name.getText().trim());
        boolean customerSex = sex.getSelectedToggle().equals(btnFemale);
        LocalDate customerBDay = datePicker.getValue();
        String customerCMND = cmnd.getText().trim();
        String customerSDT = sdt.getText().trim();

        if (dbHandler.isCMNDExist(currentCustomer.getId(), customerCMND)) {
            CustomAlert.showSimpleAlert(
                    "CMND đã tồn tại",
                    "Số CMND của %s đã tồn tại.".formatted(currentCustomer.getHoTen()));
            return;
        }

        currentCustomer.setHoTen(customerName);
        currentCustomer.setGioiTinh(customerSex);
        currentCustomer.setNgaySinh(customerBDay);
        currentCustomer.setSDT(customerSDT);
        currentCustomer.setCMND(customerCMND);

        Optional<ButtonType> answer
                = CustomAlert.confirmDialog(
                        "Chỉnh sửa khách",
                        "Xác nhận chỉnh sửa?").showAndWait();
        if (answer.get() == ButtonType.OK) {
            if (dbHandler.updateCustomer(currentCustomer)) {
                CustomAlert.showSimpleAlert(
                        "Thành công", "Chỉnh sửa thành công");
                currentCustomer = null;
                getStage().close();
            } else {
                CustomAlert.showErrorMessage("Thất bại", "Đã có lỗi xảy ra");
            }
        }

    }

    @FXML
    private void handleCancel(ActionEvent event) {
        getStage().close();
    }

    private boolean checkEntries() {
        if (name.getText().isBlank()) {
            CustomAlert.showErrorMessage("Tên khách trống", "Hãy nhập tên khách");
            return false;
        } else if (sex.getSelectedToggle() == null) {
            CustomAlert.showErrorMessage(
                    "Chưa chọn giới tính",
                    "Hãy nhập đầy đủ thông tin");
            return false;
        } else if (datePicker.getValue() == null) {
            CustomAlert.showErrorMessage(
                    "Chưa nhập ngày sinh",
                    "Hãy nhập đầy đủ thông tin");
            return false;
        } else if (sdt.getText().isBlank()) {
            CustomAlert.showErrorMessage(
                    "Chưa nhập số điện thoại",
                    "Hãy nhập đầy đủ thông tin");
            return false;
        } else if (!(sdt.getText().matches("\\d{10}")
                || sdt.getText().matches("\\d{11}"))) {
            CustomAlert.showErrorMessage(
                    "Số điện thoại sai cú pháp",
                    "Số điện thoại phải có 10 số hoặc 11 số");
            return false;
        } else if (cmnd.getText().isBlank()) {
            CustomAlert.showErrorMessage(
                    "Chưa nhập chứng minh nhân dân",
                    "Hãy nhập đầy đủ thông tin");
            return false;
        } else if (!(cmnd.getText().matches("\\d{9}")
                || cmnd.getText().matches("\\d{12}"))) {
            CustomAlert.showErrorMessage(
                    "",
                    "Chứng minh nhân dân sai cú pháp");
            return false;
        }
        return true;
    }

    private Stage getStage() {
        return (Stage) rootAddCustomer.getScene().getWindow();
    }
}
