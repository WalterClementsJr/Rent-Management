package main.ui.addroommate;

import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.SortedSet;
import java.util.TreeSet;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DateCell;
import javafx.scene.control.DatePicker;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import main.app.settings.Setting;
import main.database.DatabaseHandler;
import main.model.Customer;
import main.ui.alert.CustomAlert;
import main.ui.listcustomer.ListCustomerController;
import main.util.AutoCompleteTextField;
import main.util.MasterController;
import main.util.Util;

public class AddRoommateController implements Initializable {

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
    private Button cancel;

    // extra elements
    private AutoCompleteTextField<Customer> findCustomer;
    private SortedSet<Customer> list
            = new TreeSet<>((Customer c1, Customer c2) -> c1.toString().compareTo(c2.toString()));

    // inserting items
    private int currentHdID, makh;
    private LocalDate hdStart, hdEnd;

    // editing items
    private int currenthdkID;
    boolean isEditing = false;

    DatabaseHandler handler;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        handler = DatabaseHandler.getInstance();

        startDate.getEditor().setDisable(true);
        endDate.getEditor().setDisable(true);

        // load up customers with no rooms
        loadCustomer();
        findCustomer.getEntryMenu().setOnAction(e -> {
            ((MenuItem) e.getTarget()).addEventHandler(Event.ANY, event
                    -> {
                if (findCustomer.getLastSelectedObject() != null) {
                    findCustomer.setText(findCustomer.getLastSelectedObject().toString());
                }
            });
        });

        startDate.setValue(LocalDate.now());
        // date pickers format
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

        // add customer search
        box.getChildren().add(findCustomer);
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
        if (!checkEntriesForInsert()) {
            return;
        }

        if (handler.insertRoommate(
                currentHdID,
                findCustomer.getLastSelectedObject().getId(),
                startDate.getValue(),
                endDate.getValue())) {
            CustomAlert.showSimpleAlert(
                    "Th??nh c??ng", "???? th??m kh??ch ??? gh??p");
            MasterController.getInstance().getListCustomerController()
                    .handleRefresh(new ActionEvent());
            getStage().close();
        } else {
            CustomAlert.showErrorMessage(
                    "Th???t b???i",
                    "H??y ki???m l???i tra th??ng tin v?? th??? l???i");
        }
    }

    /**
     * edit roommate staying period
     */
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
        } else if (endDate.getValue().compareTo(hdEnd) > 0) {
            CustomAlert.showErrorMessage(
                    "",
                    "Ng??y ??i kh??ng ???????c l???n h??n ng??y tr??? h???p ?????ng. H??y nh???p l???i.");
            return;
        } else if (endDate.getValue().compareTo(startDate.getValue()) <= 0) {
            CustomAlert.showErrorMessage("L???i", "Ng??y ??i ph???i l???n h??n ng??y v??o. H??y nh???p l???i.");
            return;
        }

        Optional<ButtonType> answer
                = CustomAlert.confirmDialog(
                        "Ch???nh s???a kh??ch ??? gh??p",
                        "X??c nh???n ch???nh s???a?").showAndWait();
        if (answer.get() == ButtonType.OK) {
            if (handler.updateRoommateStayingPeriod(
                    currenthdkID,
                    startDate.getValue(),
                    endDate.getValue())) {
                CustomAlert.showSimpleAlert(
                        "Th??nh c??ng", "???? s???a th??ng tin");
                MasterController.getInstance().getListCustomerController()
                        .handleRefresh(new ActionEvent());
                getStage().close();
            } else {
                CustomAlert.showErrorMessage(
                        "Th???t b???i",
                        "H??y ki???m l???i tra th??ng tin v?? th??? l???i");
            }
        }

    }

    @FXML
    private void handleCancel(ActionEvent event) {
        getStage().close();
    }

    private boolean checkEntriesForInsert() {
        if (findCustomer.getLastSelectedObject() == null) {
            CustomAlert.showErrorMessage("Ch??a ch???n kh??ch", "H??y t??m kh??ch tr??n thanh t??m ki???m v?? ch???n m???t");
            return false;
        } else if (currentHdID == 0) {
            CustomAlert.showErrorMessage("C?? l???i x???y ra", "Kh??ng t??m ???????c h???p ?????ng");
        } else if (startDate.getValue() == null) {
            CustomAlert.showErrorMessage("Nh???p thi???u", "H??y nh???p/ch???n ng??y v??o");
            return false;
        } else if (endDate.getValue() != null && endDate.getValue().compareTo(startDate.getValue()) <= 0) {
            CustomAlert.showErrorMessage("L???i", "Ng??y v??o ph???i nh??? h??n ng??y ??i. H??y nh???p l???i.");
            return false;
        }
        return true;
    }

    /**
     * load data into roommate pane when insert roommate
     *
     * @param mahd
     * @param hdStart
     * @param hdEnd
     */
    public void loadDataForInsert(int mahd, LocalDate hdStart, LocalDate hdEnd) {
        this.currentHdID = mahd;
        this.hdStart = hdStart;
        this.hdEnd = hdEnd;

        // set date pickers' limit when roommate can choose to stay.
        startDate.setDayCellFactory(param -> new DateCell() {
            @Override
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                setDisable(date.isBefore(hdStart) || date.isAfter(hdEnd));
            }
        });

        endDate.setDayCellFactory(param -> new DateCell() {
            @Override
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                setDisable(
                        date.isBefore(startDate.getValue())
                        || date.isEqual(startDate.getValue())
                        || date.isAfter(hdEnd));
            }
        });

        if (LocalDate.now().isBefore(hdEnd)) {
            startDate.setValue(LocalDate.now());
        } else {
            startDate.setValue(hdStart);
        }
        endDate.setValue(hdEnd);
    }

    /**
     * load data items for updating roommate info
     *
     * @param hdkID
     * @param hdEnd
     * @param hdStart
     * @param rmStart
     * @param rmEnd
     */
    public void loadDataForUpdate(int hdkID, LocalDate hdStart, LocalDate hdEnd, LocalDate rmStart, LocalDate rmEnd) {
        box.getChildren().remove(findCustomer);

        currenthdkID = hdkID;
        this.hdStart = hdStart;
        this.hdEnd = hdEnd;
        startDate.setValue(rmStart);
        endDate.setValue(rmEnd);

        // disable dates before hdStart, after hdEnd
        startDate.setDayCellFactory(param -> new DateCell() {
            @Override
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                setDisable(
                        date.isBefore(hdStart)
                        || date.isAfter(hdEnd));
            }
        });

        // disable dates before startDate, startDate, after hdEnd
        endDate.setDayCellFactory(param -> new DateCell() {
            @Override
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                setDisable(
                        date.isBefore(startDate.getValue())
                        || date.isEqual(startDate.getValue())
                        || date.isAfter(hdEnd));
            }
        });

        isEditing = true;
    }

    private void loadCustomer() {
        list.addAll(ListCustomerController.listOfCustomersWithNoRoom);
        findCustomer = new AutoCompleteTextField<>(list);
    }

    private Stage getStage() {
        return (Stage) root.getScene().getWindow();
    }
}
