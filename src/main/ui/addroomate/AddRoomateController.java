package main.ui.addroomate;

import java.math.BigDecimal;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;
import java.util.SortedSet;
import java.util.TreeSet;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.DateCell;
import javafx.scene.control.DatePicker;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import main.database.DatabaseHandler;
import main.model.Customer;
import main.ui.alert.CustomAlert;
import main.util.AutoCompleteTextField;

public class AddRoomateController implements Initializable {

    @FXML
    private AnchorPane root;
    @FXML
    private HBox box;
    @FXML
    private DatePicker startDate;
    @FXML
    private DatePicker endDate;
    @FXML
    private Button save;
    @FXML
    private Button delete;
    @FXML
    private Button cancel;

    // extra elements
    @FXML
    private AutoCompleteTextField<Customer> findCustomer;
    private SortedSet<Customer> list
            = new TreeSet<>((Customer c1, Customer c2) -> c1.toString().compareTo(c2.toString()));
    
    DatabaseHandler handler;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        handler = DatabaseHandler.getInstance();

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
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("d-M-yyyy");

            {
                startDate.setPromptText("".toLowerCase());
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
        endDate.setConverter(new StringConverter<LocalDate>() {
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("d-M-yyyy");

            {
                endDate.setPromptText("".toLowerCase());
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
        
        // disable dates after startdate
        endDate.setDayCellFactory(param -> new DateCell() {
            @Override
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                setDisable(empty || date.compareTo(startDate.getValue()) <= 0);

            }
        });
        
        // add customer search
        box.getChildren().add(findCustomer);
    }    

    @FXML
    private void handleSave(ActionEvent event) {
    }

    @FXML
    private void handleDelete(ActionEvent event) {
    }
    
    private void handleEdit() {
//        if (endDate.getValue() == null) {
//            CustomAlert.showErrorMessage("Chưa điền ngày trả phòng", "Hãy nhập/chọn ngày trả phòng");
//            return;
//        } else if (endDate.getValue().compareTo(startDate.getValue()) <= 0) {
//            CustomAlert.showErrorMessage("Lỗi", "Ngày nhận phòng phải lớn hơn ngày trả phòng. Hãy nhập lại.");
//            return;
//        } else if (deposit.getText().isBlank()) {
//            CustomAlert.showErrorMessage("Chưa nhập tiền cọc", "Hãy nhập tiền cọc");
//            return;
//        }
//
//        currentContract.setNgayTra(endDate.getValue());
//        currentContract.setTienCoc(new BigDecimal(deposit.getText().trim()));
//
//        if (handler.updateContract(currentContract)) {
//            CustomAlert.showSimpleAlert("Thành công", "Đã sửa hợp đồng");
//            currentContract = null;
//            getStage().close();
//        }
    }

    @FXML
    private void handleCancel(ActionEvent event) {
        getStage().close();
    }
    
    private boolean checkEntries() {
        if (findCustomer.getLastSelectedObject() == null) {
            CustomAlert.showErrorMessage("Chưa chọn khách", "Hãy tìm khách trên thanh tìm kiếm và chọn một");
            return false;
        } else if (startDate.getValue() == null) {
            CustomAlert.showErrorMessage("Nhập thiếu", "Hãy nhập/chọn ngày vào");
            return false;
        } else if (endDate != null && endDate.getValue().compareTo(startDate.getValue()) <= 0) {
            CustomAlert.showErrorMessage("Lỗi", "Ngày vào phải bé hơn ngày đi. Hãy nhập lại.");
            return false;
        }
        
        return true;
    }
    private Stage getStage() {
        return (Stage) root.getScene().getWindow();
    }
    
}
