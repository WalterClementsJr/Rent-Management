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

        // non number text field
        name.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue,
                    String newValue) {
                String nameRegex = "[a-zA-Z????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????f?????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????? ,.'-]{0,50}";
                if (!newValue.matches(nameRegex)) {
                    name.setText(oldValue);
                    name.positionCaret(name.getLength());
                }
            }
        });

        // sdt, cmnd number-only text field
        sdt.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(
                    ObservableValue<? extends String> observable,
                    String oldValue,
                    String newValue) {
                if (!newValue.matches("\\d{0,11}")) {
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
                if (!newValue.matches("\\d{0,12}")) {
                    cmnd.setText(oldValue);
                    cmnd.positionCaret(cmnd.getLength());
                }
            }
        });
    }

    /**
     *
     * @param customer load th??ng tin c???a customer l??n c???a s??? edit
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
                    "CMND ???? t???n t???i",
                    "S??? CMND c???a %s ???? t???n t???i".formatted(customerName));
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
                    "Th??nh c??ng",
                    customerName + " ???? ???????c th??m");
            getStage().close();
        } else {
            CustomAlert.showErrorMessage(
                    "Th???t b???i",
                    "H??y ki???m l???i tra th??ng tin v?? th??? l???i");
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
                    "CMND ???? t???n t???i",
                    "S??? CMND c???a %s ???? t???n t???i.".formatted(currentCustomer.getHoTen()));
            return;
        }

        currentCustomer.setHoTen(customerName);
        currentCustomer.setGioiTinh(customerSex);
        currentCustomer.setNgaySinh(customerBDay);
        currentCustomer.setSDT(customerSDT);
        currentCustomer.setCMND(customerCMND);

        Optional<ButtonType> answer
                = CustomAlert.confirmDialog(
                        "Ch???nh s???a kh??ch",
                        "X??c nh???n ch???nh s???a?").showAndWait();
        if (answer.get() == ButtonType.OK) {
            if (dbHandler.updateCustomer(currentCustomer)) {
                CustomAlert.showSimpleAlert(
                        "Th??nh c??ng", "Ch???nh s???a th??nh c??ng");
                currentCustomer = null;
                getStage().close();
            } else {
                CustomAlert.showErrorMessage("Th???t b???i", "???? c?? l???i x???y ra");
            }
        }

    }

    @FXML
    private void handleCancel(ActionEvent event) {
        getStage().close();
    }

    private boolean checkEntries() {
        if (name.getText().isBlank()) {
            CustomAlert.showErrorMessage("T??n kh??ch tr???ng", "H??y nh???p t??n kh??ch");
            return false;
        } else if (sex.getSelectedToggle() == null) {
            CustomAlert.showErrorMessage(
                    "Ch??a ch???n gi???i t??nh",
                    "H??y nh???p ?????y ????? th??ng tin");
            return false;
        } else if (datePicker.getValue() == null) {
            CustomAlert.showErrorMessage(
                    "Ch??a nh???p ng??y sinh",
                    "H??y nh???p ?????y ????? th??ng tin");
            return false;
        } else if (sdt.getText().isBlank()) {
            CustomAlert.showErrorMessage(
                    "Ch??a nh???p s??? ??i???n tho???i",
                    "H??y nh???p ?????y ????? th??ng tin");
            return false;
        } else if (!(sdt.getText().matches("\\d{10}")
                || sdt.getText().matches("\\d{11}"))) {
            CustomAlert.showErrorMessage(
                    "S??? ??i???n tho???i sai c?? ph??p",
                    "S??? ??i???n tho???i ph???i c?? 10 s??? ho???c 11 s???");
            return false;
        } else if (cmnd.getText().isBlank()) {
            CustomAlert.showErrorMessage(
                    "Ch??a nh???p ch???ng minh nh??n d??n",
                    "H??y nh???p ?????y ????? th??ng tin");
            return false;
        } else if (!(cmnd.getText().matches("\\d{9}")
                || cmnd.getText().matches("\\d{12}"))) {
            CustomAlert.showErrorMessage(
                    "",
                    "Ch???ng minh nh??n d??n sai c?? ph??p");
            return false;
        }
        return true;
    }

    private Stage getStage() {
        return (Stage) rootAddCustomer.getScene().getWindow();
    }
}
