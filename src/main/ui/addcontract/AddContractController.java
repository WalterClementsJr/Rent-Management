package main.ui.addcontract;

import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;
import java.util.SortedSet;
import java.util.TreeSet;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import main.model.Customer;
import main.ui.alert.CustomAlert;
import main.util.AutoCompleteTextField;

public class AddContractController implements Initializable {

    @FXML
    private AnchorPane root;
    @FXML
    private HBox box;    
    @FXML
    private AutoCompleteTextField<Customer> findCustomer;
    @FXML
    private DatePicker startDate;
    @FXML
    private DatePicker endDate;
    @FXML
    private Label tip;
    @FXML
    private TextField deposit;

    // Extra elements
//    public static ObservableList<Customer> list = FXCollections.observableArrayList();
    SortedSet<Customer> list = new TreeSet<>((Customer c1, Customer c2) -> c1.toString().compareTo(c2.toString()));
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        loadCustomer();

        findCustomer.getEntryMenu().setOnAction(e -> {
            ((MenuItem) e.getTarget()).addEventHandler(Event.ANY, event
                    -> {
                if (findCustomer.getLastSelectedObject() != null) {
                    findCustomer.setText(findCustomer.getLastSelectedObject().toString());
                    System.out.println(findCustomer.getLastSelectedObject().debugString());
                }
            });
        });
        box.getChildren().add(findCustomer);
        
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
    
    
}
