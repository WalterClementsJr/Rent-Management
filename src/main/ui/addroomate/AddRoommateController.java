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
import main.ui.listcustomer.ListCustomerController;
import main.util.AutoCompleteTextField;
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
    private Button delete;
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

    // TODO fix bugs
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        handler = DatabaseHandler.getInstance();

        startDate.getEditor().setDisable(true);
        endDate.getEditor().setDisable(true);
        delete.setVisible(false);

        // populate customers with no rooms
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
                makh,
                startDate.getValue(),
                endDate.getValue())) {
            CustomAlert.showSimpleAlert(
                    "Thành công", "Đã thêm khách ở ghép");
        } else {
            CustomAlert.showErrorMessage(
                    "Đã có lỗi xảy ra", "Không thêm được thông tin");
        }
    }

    @FXML
    private void handleDelete(ActionEvent event) {
    }

    private void handleEdit() {
        if (endDate.getValue() == null) {
            CustomAlert.showErrorMessage("Chưa điền ngày trả phòng", "Hãy nhập/chọn ngày trả phòng");
            return;
        } else if (endDate.getValue().compareTo(hdEnd) > 0) {
            CustomAlert.showErrorMessage("Lỗi", "Ngày đi không hợp lệ. Hãy nhập lại.");
            return;
        } else if (endDate.getValue().compareTo(startDate.getValue()) <= 0) {
            CustomAlert.showErrorMessage("Lỗi", "Ngày đi phải lớn hơn ngày vào. Hãy nhập lại.");
            return;
        }

        if (handler.updateRoommateStayingPeriod(
                currenthdkID,
                startDate.getValue(),
                endDate.getValue())) {
            CustomAlert.showSimpleAlert("Thành công", "Đã sửa thông tin");
            getStage().close();
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
        } else if (endDate != null && endDate.getValue().compareTo(startDate.getValue()) <= 0) {
            CustomAlert.showErrorMessage("Lỗi", "Ngày vào phải bé hơn ngày đi. Hãy nhập lại.");
            return false;
        }

        return true;
    }

    public void loadData(int hd, int makh, LocalDate start, LocalDate end) {
        this.makh = makh;
        this.currentHdID = hd;
        this.hdStart = start;
        this.hdEnd = end;

        System.out.println(hdStart + " - " + hdEnd);

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
    }

    private void loadCustomer() {
        // TODO Empty
        list.addAll(ListCustomerController.listOfCustomersWithNoRoom);
        findCustomer = new AutoCompleteTextField(list);
    }

    public void loadEntries(int hdkID, LocalDate start, LocalDate end) {
        box.getChildren().remove(findCustomer);

        startDate.setValue(start);
        endDate.setValue(end);

        startDate.setDayCellFactory(param -> new DateCell() {
            @Override
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                setDisable(
                        date.isBefore(hdStart)
                        || date.isAfter(hdEnd));
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

        delete.setVisible(true);
        isEditing = true;
        currenthdkID = hdkID;
    }

    private Stage getStage() {
        return (Stage) root.getScene().getWindow();
    }

}
