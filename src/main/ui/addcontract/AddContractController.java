package main.ui.addcontract;

import java.math.BigDecimal;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
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
import javafx.scene.control.ButtonType;
import javafx.scene.control.DateCell;
import javafx.scene.control.DatePicker;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import main.app.settings.Setting;
import main.database.DatabaseHandler;
import main.model.Contract;
import main.model.Customer;
import main.model.Room;
import main.ui.alert.CustomAlert;
import main.ui.listcustomer.ListCustomerController;
import main.util.AutoCompleteTextField;
import main.util.MasterController;
import main.util.Util;

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
    private TextField deposit;

    @FXML
    private Button save;

    @FXML
    private Button cancel;

    // Extra elements
    @FXML
    private AutoCompleteTextField<Customer> findCustomer;
    private SortedSet<Customer> list
            = new TreeSet<>((Customer c1, Customer c2) -> c1.toString().compareTo(c2.toString()));
    private Room currentRoom;
    private Contract currentContract;
    private boolean isEditing = false;

    DatabaseHandler handler;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
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

        // disable dates after startdate and today
        endDate.setDayCellFactory(param -> new DateCell() {
            @Override
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                setDisable(empty || date.compareTo(startDate.getValue()) <= 0);
                setDisable(empty || date.compareTo(LocalDate.now()) <= 0);
            }
        });
        // deposit field format
        deposit.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue,
                    String newValue) {
                if (!newValue.matches("\\d{0,10}")) {
                    deposit.setText(oldValue);
                    deposit.positionCaret(deposit.getLength());
                }
            }
        });

        box.getChildren().add(findCustomer);
        startDate.setValue(LocalDate.now());
    }

    private void loadCustomer() {
        list.addAll(ListCustomerController.listOfCustomersWithNoRoom);
        findCustomer = new AutoCompleteTextField<>(list);
    }

    private boolean checkEntries() {
        if (findCustomer.getLastSelectedObject() == null) {
            CustomAlert.showErrorMessage(
                    "Ch??a ch???n kh??ch",
                    "H??y t??m kh??ch tr??n thanh t??m ki???m v?? ch???n m???t");
            return false;
        } else if (startDate.getValue() == null) {
            CustomAlert.showErrorMessage(
                    "Ch??a ??i???n ng??y nh???n ph??ng",
                    "H??y nh???p/ch???n ng??y nh???n ph??ng");
            return false;
        } else if (endDate.getValue() == null) {
            CustomAlert.showErrorMessage(
                    "Ch??a ??i???n ng??y tr??? ph??ng",
                    "H??y nh???p/ch???n ng??y tr??? ph??ng");
            return false;
        } else if (endDate.getValue().compareTo(startDate.getValue()) <= 0) {
            CustomAlert.showErrorMessage(
                    "L???i",
                    "Ng??y nh???n ph??ng ph???i b?? h??n ng??y tr??? ph??ng. H??y nh???p l???i.");
            return false;
        } else if (deposit.getText().isBlank()) {
            CustomAlert.showErrorMessage(
                    "Ch??a nh???p ti???n c???c",
                    "H??y nh???p ti???n c???c");
            return false;
        }
        return true;
    }

    public void loadEntries(Contract c) {
        box.getChildren().remove(findCustomer);

        startDate.setValue(c.getNgayNhan());
        startDate.setDisable(true);
        endDate.setValue(c.getNgayTra());
        endDate.setDayCellFactory(param -> new DateCell() {
            @Override
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                setDisable(empty || date.compareTo(c.getNgayTra()) < 0);
//                setDisable(empty || date.compareTo(startDate.getValue()) < 0);
            }
        });

        deposit.setText(c.getTienCoc().stripTrailingZeros().toPlainString());

        isEditing = true;
        currentContract = c;
    }

    public void setCurrentRoom(Room r) {
        currentRoom = r;
        deposit.setText(currentRoom.getTienCoc().stripTrailingZeros().toPlainString());
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

        if (handler.isContractOverlap(-1, 6, con.getNgayNhan(), con.getNgayTra())) {
            CustomAlert.showErrorMessage(
                    "L???i",
                    "Kh??ng th??m ???????c h???p ?????ng do tr??ng th???i h???n c???a m???t h???p ?????ng kh??c");
            return;
        }
        if (handler.insertNewContract(con)) {
            CustomAlert.showSimpleAlert(
                    "Th??nh c??ng", "???? th??m h???p ?????ng");

            MasterController.getInstance().getListCustomerController()
                    .handleRefresh(new ActionEvent());
            getStage().close();
        } else {
            CustomAlert.showErrorMessage(
                    "L???i", "Kh??ng th??m ???????c th??ng tin");
        }
    }

    private void handleEdit() {
        Util.checkLogin(getStage());

        if (!Setting.IS_VERIFIED) {
            return;
        }
        if (endDate.getValue() == null) {
            CustomAlert.showErrorMessage(
                    "Ch??a ??i???n ng??y tr??? ph??ng",
                    "H??y nh???p/ch???n ng??y tr??? ph??ng");
            return;
        } else if (endDate.getValue().compareTo(startDate.getValue()) <= 0) {
            CustomAlert.showErrorMessage(
                    "Ng??y nh???n ph??ng ph???i l???n h??n ng??y tr??? ph??ng",
                    "Xin nh???p l???i v?? th??? l???i sau.");
            return;
        } else if (deposit.getText().isBlank()) {
            CustomAlert.showErrorMessage(
                    "Ch??a nh???p ti???n c???c",
                    "H??y nh???p ti???n c???c");
            return;
        }

        currentContract.setNgayTra(endDate.getValue());
        currentContract.setTienCoc(new BigDecimal(deposit.getText().trim()));

        if (handler.isContractOverlap(
                currentContract.getId(),
                currentContract.getMaPhong(),
                startDate.getValue(),
                endDate.getValue())) {
            CustomAlert.showErrorMessage(
                    "L???i",
                    "Kh??ng s???a ???????c h???p ?????ng do tr??ng th???i h???n c???a m???t h???p ?????ng kh??c");
            return;
        }
        Optional<ButtonType> answer
                = CustomAlert.confirmDialog(
                        "Ch???nh s???a h???p ?????ng",
                        "X??c nh???n ch???nh s???a?").showAndWait();
        if (answer.get() == ButtonType.OK) {
            if (handler.updateContract(currentContract)) {
                CustomAlert.showSimpleAlert("Th??nh c??ng", "???? s???a h???p ?????ng");
                MasterController.getInstance().getListCustomerController()
                        .handleRefresh(new ActionEvent());
                currentContract = null;
                getStage().close();
            } else {
                CustomAlert.showErrorMessage("Th???t b???i", "???? c?? l???i x???y ra");
            }
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
