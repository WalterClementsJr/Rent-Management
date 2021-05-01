package main.ui.listroom;

import java.math.BigDecimal;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import main.database.DatabaseHandler;
import main.model.Room;
import main.ui.listcustomer.ListCustomerController;


public class ListRoomController implements Initializable {
    @FXML
    private AnchorPane root;

    @FXML
    private Button add;

    @FXML
    private TableView<Room> tableView;

    @FXML
    private MenuItem refreshMenu;

    @FXML
    private MenuItem editMenu;

    @FXML
    private MenuItem deleteMenu;
    
    
    // Extra elements
    ObservableList<Room> list = FXCollections.observableArrayList();
    DatabaseHandler handler = null;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // !IMPORTANT
        handler = DatabaseHandler.getInstance();
        
        // setup UI elements
        // TODO
        initRoomTableColumns(tableView);
        loadData(1);
        
        
    }
    
    private Stage getStage() {
        return (Stage) root.getScene().getWindow();
    }
    
    private void loadData(int complexId) {
        list.clear();
        
        ResultSet rs = handler.getRoomsFromComplex(complexId);

        try {
            while (rs.next()) {
                int id = rs.getInt("MAPHONG");
                String ten = rs.getString("TENPHONG");
                int soNguoi = rs.getInt("SONGUOI");  
                String moTa = rs.getString("MOTA");
                BigDecimal giaGoc = rs.getBigDecimal("GIAGOC");
                BigDecimal tienCoc = rs.getBigDecimal("TIENCOC");
                int dt = rs.getInt("DIENTICH");
                int makhu = rs.getInt("MAKHU");
                
                list.add(new Room(id, ten, soNguoi, giaGoc, tienCoc, dt, moTa, makhu));
            }
        } catch (SQLException ex) {
            Logger.getLogger(ListCustomerController.class.getName()).log(Level.SEVERE, null, ex);
        }
        tableView.setItems(list);
    }
    
    @FXML
    private void handleEditButton(ActionEvent event) {
        System.out.println("Editing in list room");
    }
    
    @FXML
    private void handleDeleteButton(ActionEvent event) {
        System.out.println("delet in list room");
    }
    
    @FXML
    private void handleAddButton(ActionEvent event) {
        System.out.println("add in list room");
    }
    
    @FXML
    private void handleRefresh(ActionEvent event) {
        // TODO get current complex
        loadData(1);
    }
    
    // TODO remove this later
    @FXML
    private void doSomething(ActionEvent event) {
        System.out.println("Doing something");
    }
    
    public void initRoomTableColumns(TableView tableView) {
        TableColumn<Room, Integer> idCol =
                new TableColumn<Room, Integer>("Mã phòng");
        TableColumn<Room, String> tenPhongCol =
                new TableColumn<Room, String>("Tên phòng");
        TableColumn<Room, Short> soNguoiCol =
                new TableColumn<Room, Short>("Só người");
        TableColumn<Room, BigDecimal> giaGocCol =
                new TableColumn<Room, BigDecimal>("Giá gốc");
        TableColumn<Room, BigDecimal> tienCocCol =
                new TableColumn<Room, BigDecimal>("Tiền cọc");
        TableColumn<Room, Integer> dtCol =
                new TableColumn<Room, Integer>("Diện tích");
        TableColumn<Room, String> moTaCol =
                new TableColumn<Room, String>("Mô tả");
        TableColumn<Room, Integer> makhuCol =
                new TableColumn<Room, Integer>("Mã khu");



        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        tenPhongCol.setCellValueFactory(new PropertyValueFactory<>("tenPhong"));
        soNguoiCol.setCellValueFactory(new PropertyValueFactory<>("soNguoi"));
        giaGocCol.setCellValueFactory(new PropertyValueFactory<>("giaGoc"));
        tienCocCol.setCellValueFactory(new PropertyValueFactory<>("tienCoc"));
        dtCol.setCellValueFactory(new PropertyValueFactory<>("dienTich"));
        moTaCol.setCellValueFactory(new PropertyValueFactory<>("moTa"));
        makhuCol.setCellValueFactory(new PropertyValueFactory<>("maKhu"));
                
        tienCocCol.setCellFactory(column -> {
            return new TableCell<Room, BigDecimal>() {
                @Override
                protected void updateItem(BigDecimal item, boolean empty) {
                    super.updateItem(item, true);

                    if (item == null || empty) {
                        setText(null);
                        setStyle("");
                    } else {
                        DecimalFormat formatter = new DecimalFormat("###,###");
                        setText(formatter.format(item));
                    }
                }
            };
        });
        
        giaGocCol.setCellFactory(column -> {
            return new TableCell<Room, BigDecimal>() {
                @Override
                protected void updateItem(BigDecimal item, boolean empty) {
                    super.updateItem(item, true);

                    if (item == null || empty) {
                        setText(null);
                        setStyle("");
                    } else {
                        DecimalFormat formatter = new DecimalFormat("###,###");
                        setText(formatter.format(item));
                    }
                }
            };
        });
        
        tableView.getColumns().addAll(idCol, tenPhongCol, soNguoiCol, giaGocCol,
                tienCocCol, dtCol, moTaCol, makhuCol);
        idCol.setVisible(false);
        makhuCol.setVisible(false);
    }

}
