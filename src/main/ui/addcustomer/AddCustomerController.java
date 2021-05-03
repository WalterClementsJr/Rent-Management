package main.ui.addcustomer;

import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import main.database.DatabaseHandler;
import main.model.Customer;
import main.ui.alert.CustomAlert;


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
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
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
        
        // sdt, cmnd number only text area
        sdt.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue,
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
            public void changed(ObservableValue<? extends String> observable, String oldValue,
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
     * @param customer 
     * load thông tin của customer lên cửa sổ edit
     */
    public void loadEntries(Customer customer) {
        name.setText(customer.getHoTen());
        
        if(customer.getGioiTinh()) {
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
    public void clearEntries() {
        name.clear();
        sex.selectToggle(null);
        datePicker.setValue(null);
        sdt.clear();
        cmnd.clear();
    }
    
    @FXML
    private void handleSave(ActionEvent event) {
        if (isEditing) {
            handleEdit();
            return;
        }

        String customerName = name.getText().trim();
        boolean customerSex = sex.getSelectedToggle().equals(btnFemale);
        LocalDate customerBDay = datePicker.getValue();
        String customerCMND = sdt.getText().trim();
        String customerSDT = cmnd.getText().trim();
        
        if (dbHandler.isCMNDExist(-1, customerCMND)) {
            CustomAlert.showErrorMessage("CMND đã tồn tại", "Số CMND của %s đã tồn tại.".formatted(customerName));
            return;
        }
        Customer customer = new Customer(
                customerName,
                customerSex,
                customerBDay,
                customerCMND,
                customerSDT);

        if (dbHandler.insertNewCustomer(customer)) {
            CustomAlert.showSimpleAlert("Khách ", customerName + " đã được thêm");
            clearEntries();
        } else {
            CustomAlert.showErrorMessage("Không thêm được khách", "Hãy kiểm lại tra thông tin và thử lại");
        }
    }

    private void handleEdit() {
        String customerName = name.getText().trim();
        boolean customerSex = sex.getSelectedToggle().equals(btnFemale);
        LocalDate customerBDay = datePicker.getValue();
        String customerCMND = cmnd.getText().trim();
        String customerSDT = sdt.getText().trim();
        
        if (dbHandler.isCMNDExist(currentCustomer.getId(), customerCMND)) {
            CustomAlert.showSimpleAlert("CMND đã tồn tại", "");
        }
        
        currentCustomer.setHoTen(customerName);
        currentCustomer.setGioiTinh(customerSex);
        currentCustomer.setNgaySinh(customerBDay);
        currentCustomer.setSDT(customerSDT);
        currentCustomer.setCMND(customerCMND);
        
        if(dbHandler.updateCustomer(currentCustomer)) {
            CustomAlert.showSimpleAlert("Đã thêm", "Update thành công");
            currentCustomer = null;
        } else {
            CustomAlert.showErrorMessage("Chỉnh sửa thất bại", "Không thể thực hiện");
        }
    }
    
    @FXML
    private void handleCancel(ActionEvent event) {
        Stage stage = (Stage) rootAddCustomer.getScene().getWindow();
        stage.close();
    }
    
    private boolean checkEntries() {
        if (name.getText().isBlank()) {
            CustomAlert.showErrorMessage("Tên khách trống", "Hãy nhập tên");
            return false;
        } else if (sex.getSelectedToggle() == null) {
            CustomAlert.showErrorMessage("Chưa chọn giới tính", "");
            return false;
        } else if (datePicker.getValue() == null) {
            CustomAlert.showErrorMessage("Chưa nhập ngày sinh", "");
            return false;
        } else if (sdt.getText().isBlank()) {
            CustomAlert.showErrorMessage("Chưa nhập số điện thoại", "");
            return false;
        } else if (cmnd.getText().isBlank()) {
            CustomAlert.showErrorMessage("Chưa nhập chứng minh nhân dân", "");
            return false;
        }
        return true;
    }
}
    

