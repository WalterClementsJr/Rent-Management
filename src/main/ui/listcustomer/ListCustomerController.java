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
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
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
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Callback;
import main.database.DatabaseHandler;
import main.model.Customer;
import main.ui.addcustomer.AddCustomerController;
import main.ui.alert.CustomAlert;
import main.ui.main.MainController;
import main.util.Util;

public class ListCustomerController implements Initializable {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private AnchorPane root;

    @FXML
    private TableView<Customer> tableView;

    @FXML
    private MenuItem edit;

    @FXML
    private MenuItem delete;

    @FXML
    private Button btnEdit;

    @FXML
    private Button btnAdd;

    @FXML
    private Button btnDelete;

    // extra elements
    ObservableList<Customer> list = FXCollections.observableArrayList();

    public static void main(String[] args) {
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        initCol();
        try {
            loadData();
        } catch (SQLException ex) {
            Logger.getLogger(ListCustomerController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private Stage getStage() {
        return (Stage) root.getScene().getWindow();
    }
    
    private void initCol() {
        TableColumn<Customer, Integer> idCol = new TableColumn<Customer, Integer>("ID");
        TableColumn<Customer, String> hotenCol = new TableColumn<Customer, String>("Họ tên");
        TableColumn<Customer, Boolean> gioitinhCol = new TableColumn<Customer, Boolean>("Giới tính");
        TableColumn<Customer, LocalDate> ngaysinhCol = new TableColumn<Customer, LocalDate>("Ngày sinh");
        TableColumn<Customer, String> sdtCol = new TableColumn<Customer, String>("SDT");
        TableColumn<Customer, String> cmndCol = new TableColumn<Customer, String>("CMND");
        

        
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        hotenCol.setCellValueFactory(new PropertyValueFactory<>("hoTen"));
        gioitinhCol.setCellValueFactory(new PropertyValueFactory<>("gioiTinh"));
        ngaysinhCol.setCellValueFactory(new PropertyValueFactory<>("ngaySinh"));
        sdtCol.setCellValueFactory(new PropertyValueFactory<>("SDT"));
        cmndCol.setCellValueFactory(new PropertyValueFactory<>("CMND"));

//        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
//        hotenCol.setCellValueFactory(new PropertyValueFactory<>("hoTen"));
//        gioitinhCol.setCellValueFactory(cellData -> cellData.getValue().gioiTinhProperty());
//        ngaysinhCol.setCellValueFactory(new PropertyValueFactory<>("ngaySinh"));
//        sdtCol.setCellValueFactory(new PropertyValueFactory<>("SDT"));
//        cmndCol.setCellValueFactory(new PropertyValueFactory<>("CMND"));
//
//        gioitinhCol.setCellFactory(column -> {
//            return new TableCell<Customer, Boolean>() {
//                @Override
//                protected void updateItem(LocalDate item, boolean empty) {
//                    super.updateItem(item, empty);
//
//                    if (item == null || empty) {
//                        setText(null);
//                        setStyle("");
//                    } else {
//                        // Format date.
//                        setText(myDateFormatter.format(item));
//
//                        // Style all dates in March with a different color.
//                        if (item.getMonth() == Month.MARCH) {
//                            setTextFill(Color.CHOCOLATE);
//                            setStyle("-fx-background-color: yellow");
//                        } else {
//                            setTextFill(Color.BLACK);
//                            setStyle("");
//                        }
//                    }
//                }
//            };
//        });
        
        tableView.getColumns().addAll(idCol, hotenCol, gioitinhCol ,ngaysinhCol, sdtCol, cmndCol);
        System.out.println("added columns");
    }

    private void loadData() throws SQLException {
        list.clear();

        DatabaseHandler handler = DatabaseHandler.getInstance();
        String query = "SELECT * FROM KHACH";
        ResultSet rs = handler.execQuery(query);

//        Util.loadCustomerResultSetToTable(list, rs, tableView);

        try {
            while (rs.next()) {
                int id = rs.getInt("MAKH");
                String hoten = rs.getString("HOTEN");
                
                boolean gioiTinh = rs.getBoolean("GIOITINH");
                LocalDate ngaySinh = Util.SQLDateToLocalDate(rs.getDate("NGAYSINH"));   
                String cmnd = rs.getString("CMND");
                String sdt = rs.getString("SDT");
                
                System.out.println(id + hoten + gioiTinh);

                list.add(new Customer(id, hoten, gioiTinh, ngaySinh, sdt, cmnd));

            }
        } catch (SQLException ex) {
            Logger.getLogger(ListCustomerController.class.getName()).log(Level.SEVERE, null, ex);
        }

        tableView.setItems(list);
    }

    @FXML
    private void handleAddButton(ActionEvent event) {
        System.out.println("load add customer window");
        Stage stage = (Stage) Util.loadWindow(getClass().getResource(
                "/main/ui/addcustomer/addCustomer.fxml"),
                "Add New Customer", getStage());
    }

    @FXML
    void handleEditButton(ActionEvent event) {
        System.out.println("editing customer");

         // TODO: add row selected value
        Customer selectedForEdit = tableView.getSelectionModel().getSelectedItem();
        if (selectedForEdit == null) {
            CustomAlert.showErrorMessage("Chưa chọn.", "Hãy chọn một khách để chỉnh sửa");
            return;
        }
        System.out.println("selected: " + selectedForEdit.getHoTen());
        
        
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
            stage.setTitle("Edit Customer");
            stage.show();
            Util.setWindowIcon(stage);

//            stage.setOnHiding((e) -> {
//                handleRefresh(new ActionEvent());
//            });
        } catch (IOException ex) {
            Logger.getLogger(ListCustomerController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    @FXML
    private void handleDelete(ActionEvent event) {
        //Fetch the selected row
        Customer selectedForDeletion = tableView.getSelectionModel().getSelectedItem();
        if (selectedForDeletion == null) {
            CustomAlert.showErrorMessage("No book selected", "Please select a book for deletion.");
            return;
        }
        // TODO make a rent check and uncomment thís line
//        if (DatabaseHandler.getInstance().isRenting(selectedForDeletion)) {
//            CustomAlert.showErrorMessage("Không thể xóa", "Không thể xóa khách đang thuê trọ.");
//            return;
//        }
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Xóa khách");
        alert.setContentText("Bạn có chắc " + selectedForDeletion.getHoTen() + " ?");
        Optional<ButtonType> answer = alert.showAndWait();
        if (answer.get() == ButtonType.OK) {
            Boolean result = DatabaseHandler.getInstance().deleteCustomer(selectedForDeletion);
            if (result) {
                CustomAlert.showSimpleAlert("Đã xóa ", selectedForDeletion.getHoTen() + " was deleted successfully.");
                list.remove(selectedForDeletion);
            } else {
                CustomAlert.showSimpleAlert("Thất bại", selectedForDeletion.getHoTen() + " could not be deleted");
            }
        } else {
            CustomAlert.showSimpleAlert("Hủy", "Hủy xóa");
        }
    }


}
