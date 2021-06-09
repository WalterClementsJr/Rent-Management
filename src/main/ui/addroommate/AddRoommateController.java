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
import main.database.DatabaseHandler;
import main.model.Customer;
import main.ui.alert.CustomAlert;
import main.ui.listcustomer.ListCustomerController;
import main.util.AutoCompleteTextField;
import main.util.MasterController;

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
    @FXML
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
                    "Thành công", "Đã thêm khách ở ghép");
            MasterController.getInstance().getListCustomerController()
                    .handleRefresh(new ActionEvent());
            getStage().close();
        } else {
            CustomAlert.showErrorMessage(
                    "Thất bại",
                    "Hãy kiểm lại tra thông tin và thử lại");
        }
    }

    /**
     * edit roommate staying period
     */
    private void handleEdit() {
        if (endDate.getValue() == null) {
            CustomAlert.showErrorMessage(
                    "Chưa điền ngày trả phòng",
                    "Hãy nhập/chọn ngày trả phòng");
            return;
        } else if (endDate.getValue().compareTo(hdEnd) > 0) {
            CustomAlert.showErrorMessage(
                    "",
                    "Ngày đi không được lớn hơn ngày trả hợp đồng. Hãy nhập lại.");
            return;
        } else if (endDate.getValue().compareTo(startDate.getValue()) <= 0) {
            CustomAlert.showErrorMessage("Lỗi", "Ngày đi phải lớn hơn ngày vào. Hãy nhập lại.");
            return;
        }

        Optional<ButtonType> answer
                = CustomAlert.confirmDialog(
                        "Chỉnh sửa khách ở ghép",
                        "Xác nhận chỉnh sửa?").showAndWait();
        if (answer.get() == ButtonType.OK) {
            if (handler.updateRoommateStayingPeriod(
                    currenthdkID,
                    startDate.getValue(),
                    endDate.getValue())) {
                CustomAlert.showSimpleAlert(
                        "Thành công", "Đã sửa thông tin");
                MasterController.getInstance().getListCustomerController()
                        .handleRefresh(new ActionEvent());
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

    private boolean checkEntriesForInsert() {
        if (findCustomer.getLastSelectedObject() == null) {
            CustomAlert.showErrorMessage("Chưa chọn khách", "Hãy tìm khách trên thanh tìm kiếm và chọn một");
            return false;
        } else if (currentHdID == 0) {
            CustomAlert.showErrorMessage("Có lỗi xảy ra", "Không tìm được hợp đồng");
        } else if (startDate.getValue() == null) {
            CustomAlert.showErrorMessage("Nhập thiếu", "Hãy nhập/chọn ngày vào");
            return false;
        } else if (endDate.getValue() != null && endDate.getValue().compareTo(startDate.getValue()) <= 0) {
            CustomAlert.showErrorMessage("Lỗi", "Ngày vào phải nhỏ hơn ngày đi. Hãy nhập lại.");
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
        findCustomer = new AutoCompleteTextField(list);
    }

    private Stage getStage() {
        return (Stage) root.getScene().getWindow();
    }
}
