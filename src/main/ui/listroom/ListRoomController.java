package main.ui.listroom;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import main.app.settings.Setting;
import main.database.DatabaseHandler;
import main.model.Complex;
import main.model.Room;
import main.ui.addcomplex.AddComplexController;
import main.ui.addcontract.AddContractController;
import main.ui.addmaintenance.AddMaintenanceController;
import main.ui.addroom.AddRoomController;
import main.ui.alert.CustomAlert;
import main.util.MasterController;
import main.util.Util;

public class ListRoomController implements Initializable {

    @FXML
    private AnchorPane root;

    @FXML
    private Button add;

    @FXML
    private ComboBox<Complex> comboBox;

    @FXML
    private Tooltip complexTooltip;

    @FXML
    private Button addComplex;

    @FXML
    private Button editComplex;

    @FXML
    private ComboBox<String> filter;

    @FXML
    private TextField filterField;

    @FXML
    private TableView<Room> roomTable;

    @FXML
    private MenuItem refreshMenu;

    @FXML
    private MenuItem editMenu;

    @FXML
    private MenuItem deleteMenu;

    @FXML
    private MenuItem addContract;

    // Extra elements
    public static ObservableList<Room> listOfAllRooms = FXCollections.observableArrayList();
    public static ObservableList<Room> listOfEmptyRooms = FXCollections.observableArrayList();
    public static ObservableList<Room> listOfOccupiedRooms = FXCollections.observableArrayList();

    FilteredList<Room> allFilteredList;
    FilteredList<Room> emptyFilteredList;
    FilteredList<Room> occupiedFilteredList;

    public static ObservableList<Complex> complexList = FXCollections.observableArrayList();
    DatabaseHandler handler;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // !IMPORTANT
        MasterController.getInstance().registerListRoomController(this);
        handler = DatabaseHandler.getInstance();

        // setup UI elements
        roomTable.setPlaceholder(new Label("Kh??ng c?? th??ng tin"));
        initRoomTableColumns(roomTable);

        loadComplexData();
        comboBox.setItems(complexList);
        comboBox.getSelectionModel().selectFirst();

        try {
            complexTooltip.setText(
                    comboBox.getSelectionModel().getSelectedItem().getDescription());
        } catch (NullPointerException e) {
        }

        filter.getItems().addAll(Util.FILTER_ALL, Util.FILTER_ROOM_EMPTY, Util.FILTER_ROOM_OCCUPIED);
        filter.getSelectionModel().selectFirst();

        Util.loadIconToButton("/main/resources/icons/add.png", addComplex);
        Util.loadIconToButton("/main/resources/icons/edit24.png", editComplex);

        allFilteredList = new FilteredList<>(listOfAllRooms);
        emptyFilteredList = new FilteredList<>(listOfEmptyRooms);
        occupiedFilteredList = new FilteredList<>(listOfOccupiedRooms);

        setFilterFieldProperty(allFilteredList);
        setFilterFieldProperty(emptyFilteredList);
        setFilterFieldProperty(occupiedFilteredList);

        loadData();
        loadListToTable();
    }

    private Stage getStage() {
        return (Stage) root.getScene().getWindow();
    }

    @FXML
    private void complexChanged(ActionEvent event) {

        listOfAllRooms.clear();
        listOfEmptyRooms.clear();
        listOfOccupiedRooms.clear();

        try {
            complexTooltip.setText(comboBox.getSelectionModel().getSelectedItem().getDescription());
        } catch (NullPointerException ex) {
        }
        loadData();
    }

    @FXML
    private void filterChanged(ActionEvent event) {
        loadListToTable();
    }

    private void loadData() {
        try {
            Complex chosenComplex = comboBox.getSelectionModel().getSelectedItem();
            listOfAllRooms.clear();
            listOfEmptyRooms.clear();
            listOfOccupiedRooms.clear();

            loadAllRooms(chosenComplex.getId());
            loadEmptyRooms(chosenComplex.getId());
            loadOccupiedRooms(chosenComplex.getId());
        } catch (Exception e) {
        }
    }

    private void loadListToTable() {
        switch (filter.getSelectionModel().getSelectedItem()) {
            case Util.FILTER_ALL ->
                roomTable.setItems(allFilteredList);
            case Util.FILTER_ROOM_EMPTY ->
                roomTable.setItems(emptyFilteredList);
            case Util.FILTER_ROOM_OCCUPIED ->
                roomTable.setItems(occupiedFilteredList);
        }
    }

    private void loadAllRooms(int complexId) {
        loadResultSetToList(handler.getAllRoomsFromComplex(complexId), listOfAllRooms);
    }

    private void loadEmptyRooms(int complexId) {
        loadResultSetToList(handler.getEmptyRoomsFromComplex(complexId), listOfEmptyRooms);
    }

    private void loadOccupiedRooms(int complexId) {
        loadResultSetToList(handler.getOccuppiedRoomsFromComplex(complexId), listOfOccupiedRooms);
    }

    private void loadComplexData() {
        complexList.clear();

        ResultSet rs = handler.selectAllComplex();
        try {
            while (rs.next()) {
                int id = rs.getInt("MAKHU");
                String ten = rs.getString("TENKHU");
                String diaChi = rs.getString("DIACHI");
                complexList.add(new Complex(id, ten, diaChi));
            }
            rs.close();
        } catch (SQLException ex) {
            Logger.getLogger(ListRoomController.class.getName()).log(Level.SEVERE, null, ex);
        }
        comboBox.getSelectionModel().selectFirst();

        // reselect combobox from listcontract and listmaintenance
        try {
            MasterController.getInstance().getListContractController().comboBoxSelectFirst();
            MasterController.getInstance().getListMaintenanceController().comboBoxSelectFirst();
        } catch (NullPointerException e) {
        }
    }

    @FXML
    public void handleRefresh(ActionEvent event) {
        filterField.clear();

        loadComplexData();
        loadData();
        loadListToTable();
    }

    private void loadResultSetToList(ResultSet rs, ObservableList list) {
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
    }

    @FXML
    private void handleAddComplex(ActionEvent event) {
        Stage stage = (Stage) Util.loadWindow(getClass().getResource(
                "/main/ui/addcomplex/addComplex.fxml"),
                "Th??m khu nh??", getStage());
        stage.setOnHiding((e) -> {
            handleRefresh(new ActionEvent());
        });
    }

    @FXML
    private void handleEditComplex(ActionEvent event) {
        Complex selectedForEdit = comboBox.getSelectionModel().getSelectedItem();

        if (selectedForEdit == null) {
            CustomAlert.showErrorMessage("Ch??a ch???n.", "H??y ch???n m???t khu nh?? ????? ch???nh s???a");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(
                    "/main/ui/addcomplex/addComplex.fxml"));
            Parent parent = loader.load();

            AddComplexController con = loader.getController();
            con.loadEntries(selectedForEdit);

            Stage stage = new Stage(StageStyle.DECORATED);
            stage.initOwner(getStage());
            stage.initModality(Modality.WINDOW_MODAL);
            stage.setResizable(false);

            Scene scene = new Scene(parent);
            scene.getStylesheets().add(getClass().getResource(Setting.getInstance().getSTYLE_SHEET()).toString());

            stage.setScene(scene);
            stage.setTitle("Ch???nh s???a khu nh??");
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
                "Th??m ph??ng", getStage());
        stage.setOnHiding((e) -> {
            handleRefresh(new ActionEvent());
        });
    }

    @FXML
    private void handleEditRoom(ActionEvent event) {
        Room selectedForEdit = roomTable.getSelectionModel().getSelectedItem();

        if (selectedForEdit == null) {
            CustomAlert.showErrorMessage("Ch??a ch???n.", "H??y ch???n m???t ph??ng ????? ch???nh s???a");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass()
                    .getResource("/main/ui/addroom/addRoom.fxml"));
            Parent parent = loader.load();

            AddRoomController con = loader.getController();
            con.loadEntries(selectedForEdit);

            Stage stage = new Stage(StageStyle.DECORATED);
            stage.initOwner(getStage());
            stage.initModality(Modality.WINDOW_MODAL);
            stage.setResizable(false);

            Scene scene = new Scene(parent);
            scene.getStylesheets().add(getClass()
                    .getResource(Setting.getInstance().getSTYLE_SHEET()).toString());

            stage.setScene(scene);
            stage.setTitle("S???a ph??ng");
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
    private void handleAddContract(ActionEvent event) {
        Room selected = roomTable.getSelectionModel().getSelectedItem();

        if (selected == null) {
            CustomAlert.showErrorMessage("Ch??a ch???n.", "H??y ch???n m???t ph??ng ????? th??m h???p ?????ng");
            return;
        }

        // only add contract for empty room
        for (Room r : listOfEmptyRooms) {
            if (r.equals(selected)) {
                // load add contract pane
                try {
                    FXMLLoader loader = new FXMLLoader(getClass()
                            .getResource("/main/ui/addcontract/addContract.fxml"));
                    Parent parent = loader.load();

                    AddContractController con = loader.getController();
                    con.setCurrentRoom(selected);

                    Stage stage = new Stage(StageStyle.DECORATED);
                    stage.initOwner(getStage());
                    stage.initModality(Modality.WINDOW_MODAL);
                    stage.setResizable(false);

                    Scene scene = new Scene(parent);
                    scene.getStylesheets().add(getClass()
                            .getResource(Setting.getInstance().getSTYLE_SHEET()).toString());

                    stage.setScene(scene);
                    stage.setTitle("Th??m h???p ?????ng");
                    stage.show();
                    Util.setWindowIcon(stage);

                    stage.setOnHiding((e) -> {
                        MasterController.getInstance().ListContractControllerRefresh();
                        handleRefresh(new ActionEvent());
                    });
                    return;
                } catch (IOException ex) {
                    Logger.getLogger(ListRoomController.class.getName()).log(Level.SEVERE, null, ex);
                    return;
                }
            }
        }

        CustomAlert.showErrorMessage(
                "Kh??ng th??? th???c hi???n", "Ph??ng ??ang c?? h???p ?????ng");
    }

    @FXML
    private void handleDeleteButton(ActionEvent event) {
        Util.checkLogin(getStage());

        if (!Setting.IS_VERIFIED) {
            return;
        }
        Room selected = roomTable.getSelectionModel().getSelectedItem();

        if (selected == null) {
            CustomAlert.showErrorMessage("Ch??a ch???n.", "H??y ch???n m???t ph??ng ????? ch???nh s???a");
            return;
        }

        if (!DatabaseHandler.getInstance().isRoomDeletable(selected.getId())) {
            CustomAlert.showErrorMessage("L???i", "Kh??ng th??? x??a ph??ng do ???? c?? l???ch s??? ng?????i ???.");
            return;
        }

        Optional<ButtonType> answer
                = CustomAlert.confirmDialog(
                        "X??a ph??ng",
                        "B???n c?? ch???c mu???n x??a ph??ng?").showAndWait();
        if (answer.get() == ButtonType.OK) {
            if (handler.deleteRoom(selected)) {
                CustomAlert.showSimpleAlert("???? x??a " + selected.getTenPhong(), "");
                handleRefresh(new ActionEvent());
            } else {
                CustomAlert.showSimpleAlert("Th???t b???i", selected.getTenPhong() + " kh??ng th??? x??a ???????c");
            }
        } else {
            CustomAlert.showSimpleAlert("H???y", "H???y x??a");
        }
    }

    @FXML
    private void handleAddMaintenance(ActionEvent event) {
        Room selected = roomTable.getSelectionModel().getSelectedItem();

        if (selected == null) {
            CustomAlert.showErrorMessage("Ch??a ch???n.", "H??y ch???n m???t ph??ng");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass()
                    .getResource("/main/ui/addmaintenance/addMaintenance.fxml"));
            Parent parent = loader.load();

            AddMaintenanceController con = loader.getController();
            con.loadRoom(selected);

            Stage stage = new Stage(StageStyle.DECORATED);
            stage.initOwner(getStage());
            stage.initModality(Modality.WINDOW_MODAL);
            stage.setResizable(false);

            Scene scene = new Scene(parent);
            scene.getStylesheets().add(getClass()
                    .getResource(Setting.getInstance().getSTYLE_SHEET()).toString());

            stage.setScene(scene);
            stage.setTitle("Th??m th??ng tin b???o tr??");
            stage.show();
            Util.setWindowIcon(stage);

            stage.setOnHiding((e) -> {
                handleRefresh(new ActionEvent());
            });
        } catch (IOException ex) {
            Logger.getLogger(ListRoomController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void initRoomTableColumns(TableView tableView) {
        TableColumn<Room, Integer> idCol
                = new TableColumn<>("M?? ph??ng");
        TableColumn<Room, String> tenPhongCol
                = new TableColumn<>("T??n ph??ng");
        TableColumn<Room, Short> soNguoiCol
                = new TableColumn<>("S??? ng?????i t???i ??a");
        TableColumn<Room, BigDecimal> giaGocCol
                = new TableColumn<>("Gi?? g???c");
        TableColumn<Room, BigDecimal> tienCocCol
                = new TableColumn<>("Ti???n c???c");
        TableColumn<Room, Integer> dtCol
                = new TableColumn<>("Di???n t??ch");
        TableColumn<Room, String> moTaCol
                = new TableColumn<>("M?? t???");
        TableColumn<Room, Integer> makhuCol
                = new TableColumn<>("M?? khu");

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

        moTaCol.prefWidthProperty().bind(tableView.widthProperty().divide(1.5));
    }

    void setFilterFieldProperty(FilteredList<Room> f) {
        filterField.textProperty().addListener((observable, oldValue, newValue) -> {
            f.setPredicate(room -> {
                if (newValue == null || newValue.isBlank()) {
                    return true;
                }
                String lowerCaseFilter = newValue.toLowerCase();
                if (room.getTenPhong().toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                } else {
                    return room.getMoTa().contains(lowerCaseFilter);
                }
            });
        });
    }
}
