/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main.ui.addcustomer;

import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
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
        
        sex = new ToggleGroup();
        btnMale.setToggleGroup(sex);
        btnFemale.setToggleGroup(sex);
        
        // set datePicker format
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
        sdt.setText(customer.getSDT());
        cmnd.setText(customer.getCMND());
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
    
    private boolean checkEntries() {
        if (name.getText().isBlank()
                || sex.getSelectedToggle()==null
                || datePicker.getValue()==null
                || sdt.getText().isBlank()
                || cmnd.getText().isBlank()) {
            CustomAlert.showErrorMessage("Hãy điền đủ các trường", "");
            return false;
        } else {
            return true;
        }
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
        
        if (dbHandler.isCMNDExist(customerCMND)) {
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
}
    

