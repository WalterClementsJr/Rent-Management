package main.ui.listroom;

import java.io.IOException;
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
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Tab;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import main.database.DatabaseHandler;
import main.model.Complex;
import main.model.Room;
import main.ui.addcomplex.AddComplexController;
import main.ui.addroom.AddRoomController;
import main.ui.alert.CustomAlert;
import main.util.Util;


public class ListRoomController implements Initializable {

    @FXML
    private AnchorPane root;

    @FXML
    private Button add;

    @FXML
    private Tab allTab;

    @FXML
    private TableView<Room> roomTable;

    @FXML
    private MenuItem refreshMenu;

    @FXML
    private MenuItem editMenu;

    @FXML
    private MenuItem deleteMenu;

    @FXML
    private Tab emptyTab;

    @FXML
    private Tab occupiedTab;

    @FXML
    private ComboBox<Complex> comboBox;

    @FXML
    private ComboBox<String> filter;
    
    @FXML
    private Button addComplex;

    @FXML
    private Button editComplex;

    // Extra elements
    public static ObservableList<Room> listOfAllRooms = FXCollections.observableArrayList();
    public static ObservableList<Room> listOfEmptyRooms = FXCollections.observableArrayList();
    public static ObservableList<Room> listOfAvailableRooms = FXCollections.observableArrayList();
    
    ObservableList<Complex> complexList = FXCollections.observableArrayList();
    
    DatabaseHandler handler;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // !IMPORTANT
        handler = DatabaseHandler.getInstance();
        
        // setup UI elements
        initRoomTableColumns(roomTable);
        loadComplexData();
        comboBox.getSelectionModel().selectFirst();
        
        filter.getItems().addAll("Tất cả", "Trống", "Đã có người");
        filter.getSelectionModel().selectFirst();

        loadData();
    }
    
    private Stage getStage() {
        return (Stage) root.getScene().getWindow();
    }
    
    private void loadData() {
        Complex chosenComplex = comboBox.getSelectionModel().getSelectedItem();
        
        switch (filter.getSelectionModel().getSelectedItem()) {
            case "Tất cả":
                loadAllRooms(chosenComplex.getId());
                break;
            case "Trống":
                loadEmptyRooms(chosenComplex.getId());
                break;
            case "Đã có người":
                loadOccupiedRooms(chosenComplex.getId());
                break;
        }
    }
    
    @FXML
    private void loadRoom(ActionEvent event) {
        loadData();
    }
    
    private void loadAllRooms(int complexId) {
        loadDataToTable(handler.getAllRoomsFromComplex(complexId), listOfAllRooms);
    }
    
    private void loadEmptyRooms(int complexId) {
        loadDataToTable(handler.getEmptyRoomsFromComplex(complexId), listOfEmptyRooms);
    }
    
    private void loadOccupiedRooms(int complexId) {
        loadDataToTable(handler.getOccuppiedRoomsFromComplex(complexId), listOfAvailableRooms);
    }
    
    private void loadDataToTable(ResultSet rs, ObservableList list) {
        list.clear();

        try {
            while (rs.next()) {
                list.add(new Room(
                        rs.getInt("MAPHONG"),
                        rs.getString("TENPHONG"),
                        rs.getInt("SONGUOI"),
                        rs.getBigDecimal("GIAGOC"),
                        rs.getBigDecimal("TIENCOC"),
                        rs.getInt("DIENTICH"),
                        rs.getString("MOTA"),
                        rs.getInt("MAKHU")));
            }
            rs.close();
        } catch (SQLException ex) {
            Logger.getLogger(ListRoomController.class.getName()).log(Level.SEVERE, null, ex);
        }
        roomTable.setItems(list);
    }
    
    private void loadComplexData() {
        complexList.clear();
        comboBox.getItems().clear();

        String query = "SELECT * FROM KHU";
        ResultSet rs = handler.execQuery(query);

        try {
            while (rs.next()) {
                int id = rs.getInt("MAKHU");
                String ten = rs.getString("TENKHU");
                String diaChi = rs.getString("DIACHI");
                complexList.add(new Complex(id, ten, diaChi));

                System.out.println(id + ten + diaChi);
            }
            rs.close();
        } catch (SQLException ex) {
            Logger.getLogger(ListRoomController.class.getName()).log(Level.SEVERE, null, ex);
        }
        comboBox.getItems().addAll(complexList);
    }
    
    @FXML
    private void handleRefresh(ActionEvent event) {
        loadAllRooms(comboBox.getSelectionModel().getSelectedItem().getId());
    }
    
//     TODO remove this later
    @FXML
    private void doSomething(Event event) {
        System.out.println("Doing something");
    }
    
    public void initRoomTableColumns(TableView tableView) {
        TableColumn<Room, Integer> idCol =
                new TableColumn<>("Mã phòng");
        TableColumn<Room, String> tenPhongCol =
                new TableColumn<>("Tên phòng");
        TableColumn<Room, Short> soNguoiCol =
                new TableColumn<>("Số người tối đa");
        TableColumn<Room, BigDecimal> giaGocCol =
                new TableColumn<>("Giá gốc");
        TableColumn<Room, BigDecimal> tienCocCol =
                new TableColumn<>("Tiền cọc");
        TableColumn<Room, Integer> dtCol =
                new TableColumn<>("Diện tích");
        TableColumn<Room, String> moTaCol =
                new TableColumn<>("Mô tả");
        TableColumn<Room, Integer> makhuCol =
                new TableColumn<>("Mã khu");

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
    
    @FXML
    private void handleAddComplex(ActionEvent event) {
        Stage stage = (Stage) Util.loadWindow(getClass().getResource(
                "/main/ui/addcomplex/addComplex.fxml"),
                "Thêm khu nhà", getStage());
        stage.setOnHiding((e) -> {
            handleRefresh(new ActionEvent());
        });
    }

    @FXML
    private void handleEditComplex(ActionEvent event) {
        Complex selectedForEdit = comboBox.getSelectionModel().getSelectedItem();

        if (selectedForEdit == null) {
            CustomAlert.showErrorMessage("Chưa chọn.", "Hãy chọn một khu nhà để chỉnh sửa");
            return;
        }

        System.out.println(selectedForEdit.debugString());

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(
                    "/main/ui/addcomplex/addComplex.fxml"));
            Parent parent = loader.load();

            AddComplexController con = loader.getController();
            con.loadEntries(selectedForEdit);

            Stage stage = new Stage(StageStyle.DECORATED);
            stage.initOwner(getStage());
            stage.initModality(Modality.WINDOW_MODAL);

            Scene scene = new Scene(parent);
            scene.getStylesheets().add(getClass().getResource(Util.STYLE_SHEET_LOCATION).toString());

            stage.setScene(scene);
            stage.setTitle("Chỉnh sửa khu nhà");
            stage.show();
            Util.setWindowIcon(stage);

            stage.setOnHiding((e) -> {
                handleRefresh(new ActionEvent());
            });
        } catch (IOException ex) {
            Logger.getLogger(ListRoomController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @FXML
    private void handleAddRoom(ActionEvent event) {
        Stage stage = (Stage) Util.loadWindow(
                getClass().getResource("/main/ui/addroom/addRoom.fxml"),
                "Thêm phòng", getStage());
    }
    
    @FXML
    private void handleEditRoom(ActionEvent event){
        Room selectedForEdit = roomTable.getSelectionModel().getSelectedItem();
        
        if (selectedForEdit == null) {
            CustomAlert.showErrorMessage("Chưa chọn.", "Hãy chọn một phòng để chỉnh sửa");
            return;
        }
        
        System.out.println(selectedForEdit.debugString());
        
        try {
            FXMLLoader loader = new FXMLLoader(getClass()
                    .getResource("/main/ui/addRoom/addRoom.fxml"));
            Parent parent = loader.load();

            AddRoomController con = loader.getController();
            con.loadEntries(selectedForEdit);
            
            Stage stage = new Stage(StageStyle.DECORATED);
            stage.initOwner(getStage());
            stage.initModality(Modality.WINDOW_MODAL);

            Scene scene = new Scene(parent);
            scene.getStylesheets().add(getClass()
                    .getResource(Util.STYLE_SHEET_LOCATION).toString());

            stage.setScene(scene);
            stage.setTitle("Sửa phòng");
            stage.show();
            Util.setWindowIcon(stage);

            stage.setOnHiding((e) -> {
                handleRefresh(new ActionEvent());
            });
        } catch (IOException ex) {
            Logger.getLogger(ListRoomController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    @FXML
    private void handleDeleteButton(ActionEvent event) {
        Room selected= roomTable.getSelectionModel().getSelectedItem();
        
        if (selected == null) {
            CustomAlert.showErrorMessage("Chưa chọn.", "Hãy chọn một phòng để chỉnh sửa");
            return;
        }
        // TODO check for active contracts before deleting a room
        if (handler.deleteRoom(selected)) {
            CustomAlert.showSimpleAlert("Xóa thành công", "Đã xóa phòng");
            
            loadRoom(new ActionEvent());
        } else {
            CustomAlert.showErrorMessage("Không thể xóa phòng", "Phòng đang có hợp đồng hoặc đã có lỗi");
        }
        
    }    
}
