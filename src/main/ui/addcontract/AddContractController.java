package main.ui.addcontract;

import java.math.BigDecimal;
import java.net.URL;
import java.time.LocalDate;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;
import java.util.SortedSet;
import java.util.TreeSet;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import main.database.DatabaseHandler;
import main.model.Contract;
import main.model.Customer;
import main.model.Room;
import main.ui.alert.CustomAlert;
import main.ui.listcustomer.ListCustomerController;
import main.util.AutoCompleteTextField;

public class AddContractController implements Initializable {

    @FXML
    private AnchorPane root;

    @FXML
    private HBox box;

    @FXML
    private DatePicker startDate;

    @FXML
    private DatePicker endDate;

    @FXML
    private Label warning;

    @FXML
    private TextField deposit;

    @FXML
    private Button save;

    @FXML
    private Button cancel;
    

    // Extra elements
    @FXML
    private AutoCompleteTextField<Customer> findCustomer;
    private SortedSet<Customer> list =
            new TreeSet<>((Customer c1, Customer c2) -> c1.toString().compareTo(c2.toString()));
    private Room currentRoom;
    private Contract currentContract;
    private boolean isEditing = false;
    
    DatabaseHandler handler;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        handler = DatabaseHandler.getInstance();
        loadCustomer();

        findCustomer.getEntryMenu().setOnAction(e -> {
            ((MenuItem) e.getTarget()).addEventHandler(Event.ANY, event
                    -> {
                if (findCustomer.getLastSelectedObject() != null) {
                    findCustomer.setText(findCustomer.getLastSelectedObject().toString());
                }
            });
        });
        
        // date pickers date format
        startDate.setConverter(new StringConverter<LocalDate>() {
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
            {
                startDate.setPromptText("".toLowerCase());
            }
            @Override
            public String toString(LocalDate date) {
                if (date != null) { return dateFormatter.format(date);
                } else { return "";
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
        endDate.setConverter(new StringConverter<LocalDate>() {
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
            {
                endDate.setPromptText("".toLowerCase());
            }
            @Override
            public String toString(LocalDate date) {
                if (date != null) { return dateFormatter.format(date);
                } else { return "";
                }
            }
            @Override
            public LocalDate fromString(String string) {
                if (string != null && !string.isEmpty()) {
                    return LocalDate.parse(string, dateFormatter);
                } else { return null;
                }
            }
        });
        // deposit field format
        deposit.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue,
                    String newValue) {
                if (newValue.matches("\\d{0,15}")) {
                    String value = newValue;
                } else {
                    deposit.setText(oldValue);
                    deposit.positionCaret(deposit.getLength());
                }
            }
        });
        
        box.getChildren().add(findCustomer);
        startDate.setValue(LocalDate.now());
        
        currentRoom = new Room("Phong", 1, BigDecimal.ZERO, BigDecimal.TEN, 14, "Nothing", 0);
        currentContract = new Contract(0, 0, LocalDate.now(), LocalDate.of(2021, Month.MARCH, 10), BigDecimal.ONE);
        
        if (currentContract != null) {
            deposit.setText(currentContract.getTienCoc().stripTrailingZeros().toPlainString());
        } else {
            deposit.setText(currentRoom.getTienCoc().stripTrailingZeros().toPlainString());            
        }
        
    }    
    
    private void loadCustomer() {
        // TODO uncomment bc in testing listcustomercontroller.list doesn't have any data
//        list.addAll(ListCustomerController.listOfCustomerWithNoRoom);

        list.add(new Customer(1, "Võ Mạng Tường", true, LocalDate.now(), "1111", "111111"));
        list.add(new Customer(1, "Nguyen Thanh Tu", true, LocalDate.now(), "33333", "111111"));
        list.add(new Customer(1, "Tân Thiên", false, LocalDate.now(), "222", "111111"));
        list.add(new Customer(1, "Tơ Bùi", true, LocalDate.now(), "5555555", "111111"));    
        findCustomer =  new AutoCompleteTextField(list);
    }
    
    private boolean checkEntries() {
        if(findCustomer.getLastSelectedObject() == null) {
            CustomAlert.showErrorMessage("Chưa chọn khách", "Hãy nhập tên và sdt của khách vào thanh tìm kiếm và chọn một khách");
            return false;
        } else if (startDate.getValue() == null) {
            CustomAlert.showErrorMessage("Chưa điền ngày nhận phòng", "Hãy nhập/chọn ngày nhận phòng");
            return false;
        } else if (endDate.getValue() == null) {
            CustomAlert.showErrorMessage("Chưa điền ngày trả phòng", "Hãy nhập/chọn ngày trả phòng");
            return false;
        } else if (deposit.getText().isBlank()) {
            CustomAlert.showErrorMessage("Chưa nhập tiền cọc", "Hãy nhập tiền cọc");
            return false;
        }
        return true;
    }
    
    private void loadEntries(Contract c) {
        for(Customer customer : ListCustomerController.listOfAllCustomers) {
            if (c.getMaKH() == customer.getId()) {
                findCustomer.setText(c.toString());
                break;
            }
        }
        findCustomer.setEditable(false);
        startDate.setValue(c.getNgayNhan());
        startDate.setEditable(false);
        endDate.setValue(c.getNgayTra());
        
    }
    
    @FXML
    private void handleAdd(ActionEvent event) {
        if (isEditing) {
            handleEdit();
            return;
        }
        if (!checkEntries()) {
            return;
        }
        
        Contract con = new Contract(
                currentRoom.getId(),
                findCustomer.getLastSelectedObject().getId(),
                startDate.getValue(),
                endDate.getValue(),
                new BigDecimal(deposit.getText().trim()));
        
        if(handler.insertNewContract(con)) {
            CustomAlert.showSimpleAlert(
                    "Thành công", "Đã thêm hợp đồng");
        }
    }
    
    private void handleEdit() {
        if (!checkEntries()) {
            return;
        }
        currentContract.setNgayTra(endDate.getValue());
        currentContract.setTienCoc(new BigDecimal(deposit.getText().trim()));
        
        if (handler.updateContract(currentContract)) {
            CustomAlert.showSimpleAlert("Thành công", "Đã sửa hợp đồng");
            getStage().close();
            currentContract = null;
        }
    }
    
    private Stage getStage() {
        return (Stage) root.getScene().getWindow();
    }
    
    @FXML
    private void handleCancel(ActionEvent event) {
        getStage().close();
    }
    
    
}