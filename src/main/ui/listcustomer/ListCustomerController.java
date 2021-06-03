package main.ui.listcustomer;

import java.io.IOException;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import main.database.DatabaseHandler;
import main.model.Customer;
import main.ui.addcustomer.AddCustomerController;
import main.ui.alert.CustomAlert;
import main.util.MasterController;
import main.util.Util;

public class ListCustomerController implements Initializable {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private AnchorPane root;

    @FXML
    private ComboBox<String> comboBox;

    @FXML
    private Button btnAdd;

    @FXML
    private TableView<Customer> customerTable;

    @FXML
    private MenuItem editMenu;

    @FXML
    private MenuItem deleteMenu;

    @FXML
    private MenuItem refreshMenu;

    // extra elements
    public static ObservableList<Customer> listOfAllCustomers = FXCollections.observableArrayList();
    public static ObservableList<Customer> listOfCustomersWithNoRoom = FXCollections.observableArrayList();
    public static ObservableList<Customer> listOfCustomersWithRoom = FXCollections.observableArrayList();
    public static ObservableList<Customer> listOfOldCustomers = FXCollections.observableArrayList();

//    private boolean datachanged = false;
    DatabaseHandler handler;

    public static void main(String[] args) {
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        MasterController.getInstance().registerListCustomerController(this);
        handler = DatabaseHandler.getInstance();

        initCustomerTableColumns();

        comboBox.getItems().addAll(
                Util.FILTER_ALL,
                Util.FILTER_CUSTOMER_NO_ROOM,
                Util.FILTER_CUSTOMER_HAS_ROOM,
                Util.FILTER_CUSTOMER_MOVED);
        comboBox.getSelectionModel().selectFirst();

        loadData();
    }

    private Stage getStage() {
        return (Stage) root.getScene().getWindow();
    }

    @FXML
    private void handleComboBoxChange(ActionEvent event) {
        loadData();
    }

    private void loadData() {
        switch (comboBox.getSelectionModel().getSelectedItem()) {
            case Util.FILTER_ALL:
                loadAllCustomers();
                break;
            case Util.FILTER_CUSTOMER_NO_ROOM:
                loadCustomersWithNoRoom();
                break;
            case Util.FILTER_CUSTOMER_HAS_ROOM:
                loadCustomersWithRoom();
                break;
            case Util.FILTER_CUSTOMER_MOVED:
                loadOldCustomers();
                break;
        }
    }

    private void loadDataToTable(ResultSet rs, ObservableList list) {
        list.clear();

        try {
            while (rs.next()) {
                list.add(
                        new Customer(
                                rs.getInt("MAKH"),
                                rs.getString("HOTEN"),
                                rs.getBoolean("GIOITINH"),
                                Util.SQLDateToLocalDate(rs.getDate("NGAYSINH")),
                                rs.getString("CMND"),
                                rs.getString("SDT")));
            }

            rs.close();
        } catch (SQLException ex) {
            Logger.getLogger(ListCustomerController.class.getName()).log(Level.SEVERE, null, ex);
        }
        customerTable.setItems(list);
    }

    private void loadAllCustomers() {
        String query = "SELECT * FROM KHACH";

        loadDataToTable(handler.execQuery(query), listOfAllCustomers);
    }

    private void loadCustomersWithNoRoom() {
        loadDataToTable(handler.getCustomersWithNoRoom(), listOfCustomersWithNoRoom);
    }

    private void loadCustomersWithRoom() {
        loadDataToTable(handler.getCustomersWithRoom(), listOfCustomersWithRoom);
    }

    private void loadOldCustomers() {
        loadDataToTable(handler.getOldCustomers(), listOfCustomersWithRoom);
    }

    @FXML
    private void handleAddButton(ActionEvent event) {
        System.out.println("load add customer window");
        Stage stage = (Stage) Util.loadWindow(getClass().getResource(
                "/main/ui/addcustomer/addCustomer.fxml"),
                "Add New Customer", getStage());
        stage.setOnHiding((e) -> {
            handleRefresh(new ActionEvent());
        });
    }

    @FXML
    void handleEditButton(ActionEvent event) {
        Customer selectedForEdit = customerTable.getSelectionModel().getSelectedItem();

        if (selectedForEdit == null) {
            CustomAlert.showErrorMessage("Chưa chọn.", "Hãy chọn một khách để chỉnh sửa");
            return;
        }
        System.out.println(selectedForEdit.toString());

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/main/ui/addcustomer/addCustomer.fxml"));
            Parent parent = loader.load();

            AddCustomerController con = loader.getController();
            con.loadEntries(selectedForEdit);

            Stage stage = new Stage(StageStyle.DECORATED);
            stage.initOwner(getStage());
            stage.initModality(Modality.WINDOW_MODAL);

            Scene scene = new Scene(parent);
            scene.getStylesheets().add(getClass().getResource(Util.STYLE_SHEET_LOCATION).toString());

            stage.setScene(scene);
            stage.setTitle("Chỉnh sửa thông tin khách");
            stage.show();
            Util.setWindowIcon(stage);

            stage.setOnHiding((e) -> {
                handleRefresh(new ActionEvent());
            });
        } catch (IOException ex) {
            Logger.getLogger(ListCustomerController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @FXML
    private void handleRefresh(ActionEvent event) {
        loadData();
    }

    @FXML
    private void handleDelete(ActionEvent event) {
        Customer selectedForDeletion = customerTable.getSelectionModel().getSelectedItem();

        if (selectedForDeletion == null) {
            CustomAlert.showErrorMessage("Chưa chọn.", "Hãy chọn một khách để xóa");
            return;
        }

        //  check if customer has any related data before deleting
        if (DatabaseHandler.getInstance().isCustomerDeletable(selectedForDeletion.getId())) {
            CustomAlert.showErrorMessage("Lỗi", "Không thể xóa khách đã/đang ở trong hệ thống.");
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Xóa khách");
        alert.setContentText("Bạn có muốn xóa " + selectedForDeletion.getHoTen() + " ?");
        Optional<ButtonType> answer = alert.showAndWait();
        if (answer.get() == ButtonType.OK) {
            Boolean result = handler.deleteCustomer(selectedForDeletion);
            if (result) {
                CustomAlert.showSimpleAlert("Đã xóa ", selectedForDeletion.getHoTen() + " was deleted successfully.");
                listOfAllCustomers.remove(selectedForDeletion);
            } else {
                CustomAlert.showSimpleAlert("Thất bại", selectedForDeletion.getHoTen() + " không thể xóa được");
            }
        } else {
            CustomAlert.showSimpleAlert("Hủy", "Hủy xóa");
        }
    }

    public void initCustomerTableColumns() {
        TableColumn<Customer, Integer> idCol = new TableColumn<>("ID");
        TableColumn<Customer, String> hotenCol = new TableColumn<>("Họ tên");
        TableColumn<Customer, Boolean> gioitinhCol = new TableColumn<>("Giới tính");
        TableColumn<Customer, LocalDate> ngaysinhCol = new TableColumn<>("Ngày sinh");
        TableColumn<Customer, String> sdtCol = new TableColumn<>("SDT");
        TableColumn<Customer, String> cmndCol = new TableColumn<>("CMND");

        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        hotenCol.setCellValueFactory(new PropertyValueFactory<>("hoTen"));
        gioitinhCol.setCellValueFactory(new PropertyValueFactory<>("gioiTinh"));
        ngaysinhCol.setCellValueFactory(new PropertyValueFactory<>("ngaySinh"));
        sdtCol.setCellValueFactory(new PropertyValueFactory<>("SDT"));
        cmndCol.setCellValueFactory(new PropertyValueFactory<>("CMND"));

        // field formating
        gioitinhCol.setCellFactory(column -> {
            return new TableCell<Customer, Boolean>() {
                @Override
                protected void updateItem(Boolean item, boolean empty) {
                    super.updateItem(item, true);

                    if (item == null || empty) {
                        setText(null);
                        setStyle("");
                    } else {
                        setText(Util.bitToGender(item));
                    }
                }
            };
        });

        ngaysinhCol.setCellFactory(column -> {
            return new TableCell<Customer, LocalDate>() {
                @Override
                protected void updateItem(LocalDate item, boolean empty) {
                    super.updateItem(item, true);

                    if (item == null || empty) {
                        setText(null);
                        setStyle("");
                    } else {
                        setText(Util.DATE_TIME_FORMATTER.format(item));
                    }
                }
            };
        });

        customerTable.getColumns().addAll(idCol, hotenCol, gioitinhCol, ngaysinhCol, sdtCol, cmndCol);
        idCol.setVisible(false);

        hotenCol.setMinWidth(150);
    }

}
