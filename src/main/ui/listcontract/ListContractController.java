package main.ui.listcontract;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.AnchorPane;
import javafx.util.Callback;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableCell;
import javafx.scene.control.Tooltip;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import main.database.DatabaseHandler;
import main.model.Complex;
import main.model.Contract;
import main.ui.addcontract.AddContractController;
import main.ui.addroommate.AddRoommateController;
import main.ui.alert.CustomAlert;
import main.ui.listroom.ListRoomController;
import main.util.MasterController;
import main.util.Util;

public class ListContractController implements Initializable {

    @FXML
    private AnchorPane root;

    @FXML
    private ComboBox<Complex> comboBox;

    @FXML
    private Tooltip complexTooltip;

    @FXML
    private ComboBox<String> filter;

    @FXML
    private TableView contractTable;

    @FXML
    private MenuItem refreshMenu;

    @FXML
    private MenuItem editMenu;

    @FXML
    private MenuItem deleteMenu;

    @FXML
    private MenuItem returnMenu;

    @FXML
    private TableView roommateTable;

    @FXML
    private MenuItem refreshMenu1;

    @FXML
    private MenuItem editMenu1;

    @FXML
    private MenuItem deleteMenu1;

    @FXML
    private MenuItem returnMenu1;

    // extra elements
    ObservableList<Complex> complexList = ListRoomController.complexList;
    // TODO uncomment/remove these lines in Main
//    ObservableList<Complex> complexList = FXCollections.observableArrayList();

    public static ObservableList listOfAllContracts = FXCollections.observableArrayList();
    public static ObservableList listOfActiveContracts = FXCollections.observableArrayList();
    public static ObservableList listOfOldContracts = FXCollections.observableArrayList();

    public static ObservableList listOfAllRoommates = FXCollections.observableArrayList();
    public static ObservableList listOfActiveRoommates = FXCollections.observableArrayList();
    public static ObservableList listOfOldRoommates = FXCollections.observableArrayList();

    DatabaseHandler handler;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        MasterController.getInstance().registerListContractController(this);
        handler = DatabaseHandler.getInstance();

        // TODO remove this complex loading in production
        loadComplexData();
        comboBox.getSelectionModel().selectFirst();

        initContractTableColumns();
        initRoommateTableColumns();

        filter.getItems().addAll(Util.FILTER_ALL, Util.FILTER_ACTIVE, Util.FILTER_OLD);
        filter.getSelectionModel().selectFirst();

        loadContractData();
        loadRoommatesData();
        loadContractsToTable();
        loadRoommatesToTable();
    }

    @FXML
    private void complexChanged(ActionEvent event) {

        listOfAllContracts.clear();
        listOfActiveContracts.clear();
        listOfOldContracts.clear();

        listOfAllRoommates.clear();
        listOfActiveRoommates.clear();
        listOfOldRoommates.clear();

        complexTooltip.setText(comboBox.getSelectionModel().getSelectedItem().getDescription());

        loadContractData();
        loadRoommatesData();
    }

    @FXML
    private void filterChanged(ActionEvent event) {
        loadContractsToTable();
        loadRoommatesToTable();
    }

    private void loadContractsToTable() {
        switch (filter.getSelectionModel().getSelectedItem()) {
            case Util.FILTER_ALL:
                contractTable.setItems(listOfAllContracts);
                break;
            case Util.FILTER_ACTIVE:
                contractTable.setItems(listOfActiveContracts);
                break;
            case Util.FILTER_OLD:
                contractTable.setItems(listOfOldContracts);
                break;
        }
    }

    private void loadRoommatesToTable() {
        switch (filter.getSelectionModel().getSelectedItem()) {
            case Util.FILTER_ALL:
                roommateTable.setItems(listOfAllRoommates);
                break;
            case Util.FILTER_ACTIVE:
                roommateTable.setItems(listOfActiveRoommates);
                break;
            case Util.FILTER_OLD:
                roommateTable.setItems(listOfOldRoommates);
                break;
        }
    }

    private void loadContractData() {
        listOfAllContracts.clear();
        listOfActiveContracts.clear();
        listOfOldContracts.clear();

        int id = comboBox.getSelectionModel().getSelectedItem().getId();

        loadAllContracts(id);
        loadActiveContracts(id);
        loadOldContracts(id);

        loadContractsToTable();
    }

    private void loadRoommatesData() {
        listOfAllRoommates.clear();
        listOfActiveRoommates.clear();
        listOfOldRoommates.clear();

        int id = comboBox.getSelectionModel().getSelectedItem().getId();

        loadAllRoommates(id);
        loadActiveRoommates(id);
        loadOldRoommates(id);

        loadRoommatesToTable();
    }

    public void loadAllContracts(int id) {
        loadResultSetToList(handler.getAllContractsWithInfo(id), listOfAllContracts);
    }

    public void loadActiveContracts(int id) {
        loadResultSetToList(handler.getActiveContractsWithInfo(id), listOfActiveContracts);
    }

    public void loadOldContracts(int id) {
        loadResultSetToList(handler.getOldContractsWithInfo(id), listOfOldContracts);
    }

    public void loadAllRoommates(int id) {
        loadResultSetToList(handler.getAllRoommatesWithInfo(id), listOfAllRoommates);
    }

    public void loadActiveRoommates(int id) {
        loadResultSetToList(handler.getActiveRoommatesWithInfo(id), listOfActiveRoommates);
    }

    public void loadOldRoommates(int id) {
        loadResultSetToList(handler.getOldRoommatesWithInfo(id), listOfOldRoommates);
    }

    @FXML
    void handleDeleteButton(ActionEvent event) {
        ObservableList row;
        try {
            row = (ObservableList) contractTable.getSelectionModel().getSelectedItems().get(0);
            if (row == null) {
                CustomAlert.showErrorMessage("Chưa chọn.", "Hãy chọn một hợp đồng để xóa");
                return;
            }
            Optional<ButtonType> answer
                    = CustomAlert.confirmDelete(
                            "Xóa hợp đồng",
                            "Bạn có chắc muốn xóa hợp đồng này?").showAndWait();
            if (answer.get() == ButtonType.OK) {
                if (handler.deleteContract(Integer.parseInt(row.get(0).toString()))) {
                    CustomAlert.showSimpleAlert(
                            "Xóa thành công", "Đã xóa hợp đồng");
                    handleRefresh(new ActionEvent());
                } else {
                    CustomAlert.showErrorMessage(
                            "Không thể xóa", "Đã có lỗi xảy ra");
                }
            }
        } catch (IndexOutOfBoundsException ex) {
            CustomAlert.showErrorMessage("Chưa chọn", "Hãy chọn một hợp đồng để xóa");
        }
    }

    @FXML
    void handleEditContract(ActionEvent event) {
        ObservableList row;
        try {
            row = (ObservableList) contractTable.getSelectionModel().getSelectedItems().get(0);
            if (row == null) {
                CustomAlert.showErrorMessage("Chưa chọn.", "Hãy chọn một hợp đồng để chỉnh sửa");
                return;
            }
            Contract con = new Contract(
                    Integer.parseInt(row.get(0).toString()),
                    Integer.parseInt(row.get(2).toString()),
                    Integer.parseInt(row.get(4).toString()),
                    LocalDate.parse(row.get(6).toString(), Util.SQL_DATE_TIME_FORMATTER),
                    LocalDate.parse(row.get(7).toString(), Util.SQL_DATE_TIME_FORMATTER),
                    new BigDecimal(row.get(8).toString()));
            try {
                FXMLLoader loader = new FXMLLoader(getClass()
                        .getResource("/main/ui/addcontract/addContract.fxml"));
                Parent parent = loader.load();

                AddContractController controller = loader.getController();
                controller.loadEntries(con);

                Stage stage = new Stage(StageStyle.DECORATED);
                stage.initOwner(getStage());
                stage.initModality(Modality.WINDOW_MODAL);

                Scene scene = new Scene(parent);
                scene.getStylesheets().add(getClass()
                        .getResource(Util.STYLE_SHEET_LOCATION).toString());

                stage.setScene(scene);
                stage.setTitle("Chỉnh sửa hợp đồng");
                stage.show();
                Util.setWindowIcon(stage);

                stage.setOnHiding((e) -> {
                    handleRefresh(new ActionEvent());
                });
            } catch (IOException ex) {
                Logger.getLogger(ListContractController.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (IndexOutOfBoundsException ex) {
            CustomAlert.showErrorMessage("Chưa chọn.", "Hãy chọn một hợp đồng để chỉnh sửa");
        }
    }

    @FXML
    public void handleRefresh(ActionEvent event) {
        loadContractData();
        loadRoommatesData();
    }

    @FXML
    public void handleRefreshRoommates(ActionEvent event) {
        loadRoommatesData();
    }

    @FXML
    public void handleReturn(ActionEvent e) {
        ObservableList selectedRow;
        try {
            selectedRow = (ObservableList) contractTable.getSelectionModel().getSelectedItems().get(0);
            if (selectedRow == null) {
                CustomAlert.showErrorMessage(
                        "Chưa chọn.",
                        "Hãy chọn một hợp đồng để kết thúc hợp đồng");
                return;
            }
            int selectedConId = Integer.parseInt((String) selectedRow.get(0));
            List activeConRow;
            int activeConId;

            for (int i = 0; i < listOfActiveContracts.size(); i++) {
                activeConRow = (List) listOfActiveContracts.get(i);
                activeConId = Integer.parseInt((String) activeConRow.get(0));
                if (activeConId == selectedConId) {
                    if (handler.isContractEndable(selectedConId)) {
                        Optional<ButtonType> answer
                                = CustomAlert.confirmDelete(
                                        "Kết thúc hợp đồng",
                                        "Bạn có chắc muốn kết thúc hợp đồng này?")
                                        .showAndWait();
                        if (answer.get() == ButtonType.OK) {
                            if (handler.endContract(selectedConId)) {
                                CustomAlert.showSimpleAlert(
                                        "Thành công",
                                        "Đã kết thúc hợp đồng vào ngày " + LocalDate.now().format(Util.DATE_TIME_FORMATTER));
                                handleRefresh(new ActionEvent());
                            } else {
                                CustomAlert.showErrorMessage(
                                        "Không thể thực hiện", "Đã có lỗi xảy ra");
                            }
                        }
                        handler.endContract(selectedConId);
                    } else {
                        CustomAlert.showErrorMessage(
                                "Không thể thực hiện",
                                "Hợp đồng chưa thanh toán đủ.\n"
                                + "Hãy thanh toán đầy đủ cho hợp đồng này và thử lại sau");
                    }
                    return;
                }
            }
            CustomAlert.showErrorMessage(
                    "Không thể thực hiện",
                    "Hợp đồng đã chọn chưa hết hạn");
        } catch (IndexOutOfBoundsException ex) {
            CustomAlert.showErrorMessage("Chưa chọn", "Hãy chọn một hợp đồng để xóa");
        }
    }

    @FXML
    public void handleAddRoommate(ActionEvent evt) {
        // TODO add roommate here
        ObservableList selectedRow;
        try {
            selectedRow = (ObservableList) contractTable.getSelectionModel().getSelectedItems().get(0);
            if (selectedRow == null) {
                CustomAlert.showErrorMessage(
                        "Chưa chọn.",
                        "Hãy chọn một hợp đồng thêm khách ở ghép");
                return;
            }
            int mahd = Integer.parseInt((String) selectedRow.get(0));
            LocalDate start = LocalDate.parse(
                    selectedRow.get(7).toString(),
                    Util.SQL_DATE_TIME_FORMATTER);
            LocalDate end = LocalDate.parse(
                    selectedRow.get(8).toString(),
                    Util.SQL_DATE_TIME_FORMATTER);

            try {
                FXMLLoader loader = new FXMLLoader(getClass()
                        .getResource("/main/ui/addroommate/addRoommate.fxml"));
                Parent parent = loader.load();

                AddRoommateController con = loader.getController();
                con.loadDataForInsert(mahd, start, end);

                Stage stage = new Stage(StageStyle.DECORATED);
                stage.initOwner(getStage());
                stage.initModality(Modality.WINDOW_MODAL);

                Scene scene = new Scene(parent);
                scene.getStylesheets().add(getClass()
                        .getResource(Util.STYLE_SHEET_LOCATION).toString());

                stage.setScene(scene);
                stage.setTitle("Thêm khách ở ghép");
                stage.show();
                Util.setWindowIcon(stage);

                stage.setOnHiding((e) -> {
                    handleRefresh(new ActionEvent());
                });
            } catch (IOException ex) {
                Logger.getLogger(ListContractController.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (IndexOutOfBoundsException ex) {
            CustomAlert.showErrorMessage("Chưa chọn", "\nHãy chọn một hợp đồng để thêm");
        }
    }

    @FXML
    public void handleEditRoommate(ActionEvent e) {
        // TODO edit (extends/end) roommates staying period
        ObservableList selectedRow;
        try {
            selectedRow = (ObservableList) roommateTable.getSelectionModel().getSelectedItems().get(0);
            if (selectedRow == null) {
                CustomAlert.showErrorMessage(
                        "Chưa chọn.",
                        "Hãy chọn thông tin khách ở ghép để chỉnh sửa");
                return;
            }
            int mahdk = Integer.parseInt((String) selectedRow.get(0));
            LocalDate hdStart = LocalDate.parse(
                    selectedRow.get(10).toString(),
                    Util.SQL_DATE_TIME_FORMATTER);
            LocalDate hdEnd = LocalDate.parse(
                    selectedRow.get(11).toString(),
                    Util.SQL_DATE_TIME_FORMATTER);
            LocalDate rmStart = LocalDate.parse(
                    selectedRow.get(12).toString(),
                    Util.SQL_DATE_TIME_FORMATTER);
            LocalDate rmEnd = LocalDate.parse(
                    selectedRow.get(13).toString(),
                    Util.SQL_DATE_TIME_FORMATTER);

            try {
                FXMLLoader loader = new FXMLLoader(getClass()
                        .getResource("/main/ui/addroommate/addRoommate.fxml"));
                Parent parent = loader.load();

                AddRoommateController con = loader.getController();
                con.loadDataForUpdate(mahdk, hdStart, hdEnd, rmStart, rmEnd);

                Stage stage = new Stage(StageStyle.DECORATED);
                stage.initOwner(getStage());
                stage.initModality(Modality.WINDOW_MODAL);

                Scene scene = new Scene(parent);
                scene.getStylesheets().add(getClass()
                        .getResource(Util.STYLE_SHEET_LOCATION).toString());

                stage.setScene(scene);
                stage.setTitle("Chỉnh sửa thời hạn ở ghép");
                stage.show();
                Util.setWindowIcon(stage);

                stage.setOnHiding((evt) -> {
                    handleRefresh(new ActionEvent());
                });
            } catch (IOException ex) {
                ex.printStackTrace(System.out);
                Logger.getLogger(ListContractController.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (IndexOutOfBoundsException ex) {
            ex.printStackTrace(System.out);
            CustomAlert.showErrorMessage(
                    "Chưa chọn",
                    "\nHãy chọn thông tin khách ở ghép để chỉnh sửa");
        }
    }

    @FXML
    public void handleRoommateReturn(ActionEvent e) {
        // TODO end ACTIVE roommates staying period

    }

    @FXML
    public void handleDeleteRoommate(ActionEvent e) {
        // TODO delete roommate
        ObservableList selectedRow;
        try {
            selectedRow = (ObservableList) roommateTable.getSelectionModel().getSelectedItems().get(0);
            if (selectedRow == null) {
                CustomAlert.showErrorMessage(
                        "Chưa chọn.",
                        "Hãy chọn thông tin khách ở ghép để xóa");
                return;
            }
            Optional<ButtonType> answer
                    = CustomAlert.confirmDelete(
                            "Xóa thông tin khách ở ghép",
                            "Bạn có chắc muốn xóa?").showAndWait();
            if (answer.get() == ButtonType.OK) {
                if (handler.deleteRoommate(Integer.parseInt((String) selectedRow.get(0)))) {
                    CustomAlert.showSimpleAlert(
                            "Xóa thành công", "Đã xóa khách ở ghép");
                    roommateTable.getItems().remove(selectedRow);
                    handleRefresh(new ActionEvent());
                } else {
                    CustomAlert.showErrorMessage(
                            "Không thể xóa", "Đã có lỗi xảy ra");
                }
            }
        } catch (IndexOutOfBoundsException ex) {
            CustomAlert.showErrorMessage(
                    "Chưa chọn",
                    "Hãy chọn thông tin khách ở ghép để xóa");
        }
    }

    public void loadResultSetToList(ResultSet rs, ObservableList list) {
        list.clear();
        try {
            while (rs.next()) {
                ObservableList row = FXCollections.observableArrayList();
                for (int i = 1; i <= rs.getMetaData().getColumnCount(); i++) {
                    row.add(rs.getString(i).trim());
                }
                list.add(row);
            }
            rs.close();
        } catch (SQLException ex) {
            Logger.getLogger(ListContractController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void initContractTableColumns() {
        TableColumn mahdongCol
                = new TableColumn<>("Mã hợp đồng");
        TableColumn maKhuCol
                = new TableColumn<>("Mã khu");
        TableColumn tenKhuCol
                = new TableColumn<>("Tên khu");
        TableColumn maphongCol
                = new TableColumn<>("Mã phòng");
        TableColumn tenPhongCol
                = new TableColumn<>("Tên phòng");
        TableColumn makhCol
                = new TableColumn<>("Mã khách");
        TableColumn tenkhachCol
                = new TableColumn<>("Tên khách");
        TableColumn ngayNhanCol
                = new TableColumn<>("Ngày nhận");
        TableColumn ngayTraCol
                = new TableColumn<>("Ngày trả");
        TableColumn tienCocCol
                = new TableColumn<>("Tiền cọc");
        TableColumn giaGocCol
                = new TableColumn<>("Giá gốc");
        TableColumn ngayttgannhatCol
                = new TableColumn<>("ngayttgannhat");
        TableColumn songayCol
                = new TableColumn<>("Số ngày");

        contractTable.getColumns().addAll(
                mahdongCol, maKhuCol, tenKhuCol, maphongCol, tenPhongCol, makhCol,
                tenkhachCol, ngayNhanCol, ngayTraCol, tienCocCol, giaGocCol, ngayttgannhatCol, songayCol);

        tenPhongCol.setMinWidth(150);
        tenkhachCol.setMinWidth(150);

        mahdongCol.setVisible(false);
        maKhuCol.setVisible(false);
        tenKhuCol.setVisible(false);
        maphongCol.setVisible(false);
        makhCol.setVisible(false);
        giaGocCol.setVisible(false);
        ngayttgannhatCol.setVisible(false);
        songayCol.setVisible(false);

        for (int i = 0; i < contractTable.getColumns().size(); i++) {
            final int t = i;
            TableColumn col = (TableColumn) contractTable.getColumns().get(i);
            col.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<ObservableList, String>, ObservableValue<String>>() {
                @Override
                public ObservableValue<String> call(TableColumn.CellDataFeatures<ObservableList, String> param) {
                    return new SimpleStringProperty(param.getValue().get(t).toString());
                }
            });
        }

        ngayNhanCol.setCellFactory(column -> {
            return new TableCell<String, String>() {
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, true);

                    if (item == null || empty) {
                        setText(null);
                        setStyle("");
                    } else {
                        LocalDate d = LocalDate.parse(item, Util.SQL_DATE_TIME_FORMATTER);
                        setText(Util.DATE_TIME_FORMATTER.format(d));
                        d = null;
                    }
                }
            };
        });
        ngayTraCol.setCellFactory(column -> {
            return new TableCell<String, String>() {
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, true);

                    if (item == null || empty) {
                        setText(null);
                        setStyle("");
                    } else {
                        LocalDate d = LocalDate.parse(item, Util.SQL_DATE_TIME_FORMATTER);
                        setText(Util.DATE_TIME_FORMATTER.format(d));
                        d = null;
                    }
                }
            };
        });

        tienCocCol.setCellFactory(column -> {
            return new TableCell<String, String>() {
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, true);

                    if (item == null || empty) {
                        setText(null);
                        setStyle("");
                    } else {
                        DecimalFormat formatter = new DecimalFormat("###,###");
                        BigDecimal d = new BigDecimal(item);
                        setText(formatter.format(d));
                        d = null;
                        formatter = null;
                    }
                }
            };
        });
    }

    public void initRoommateTableColumns() {
        TableColumn idhdk
                = new TableColumn<>("Mã hdk");
        TableColumn mahdongCol
                = new TableColumn<>("Mã hợp đồng");
        TableColumn maKhuCol
                = new TableColumn<>("Mã khu");
        TableColumn tenKhuCol
                = new TableColumn<>("Tên khu");
        TableColumn maphongCol
                = new TableColumn<>("Mã phòng");
        TableColumn tenPhongCol
                = new TableColumn<>("Tên phòng");
        TableColumn makhCol
                = new TableColumn<>("Mã khách");
        TableColumn tenkhCol
                = new TableColumn<>("Chủ hợp đồng");
        TableColumn maRoommateCol
                = new TableColumn<>("Mã kh ghép");
        TableColumn tenRoommateCol
                = new TableColumn<>("Tên khách");
        TableColumn ngayNhanCol
                = new TableColumn<>("Ngày nhận");
        TableColumn ngayTraCol
                = new TableColumn<>("Ngày trả");
        TableColumn ngayVaoCol
                = new TableColumn<>("Ngày vào");
        TableColumn ngayDiCol
                = new TableColumn<>("Ngày đi");

        roommateTable.getColumns().addAll(
                idhdk, mahdongCol, maKhuCol, tenKhuCol, maphongCol, tenPhongCol,
                makhCol, tenkhCol, maRoommateCol, tenRoommateCol,
                ngayNhanCol, ngayTraCol, ngayVaoCol, ngayDiCol);

        tenPhongCol.setMinWidth(150);
        tenkhCol.setMinWidth(150);
        tenRoommateCol.setMinWidth(150);

        idhdk.setVisible(false);
        mahdongCol.setVisible(false);
        maKhuCol.setVisible(false);
        tenKhuCol.setVisible(false);
        maphongCol.setVisible(false);
        makhCol.setVisible(false);
        maRoommateCol.setVisible(false);
        ngayNhanCol.setVisible(false);
        ngayTraCol.setVisible(false);

        for (int i = 0; i < roommateTable.getColumns().size(); i++) {
            final int t = i;
            TableColumn col = (TableColumn) roommateTable.getColumns().get(i);
            col.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<ObservableList, String>, ObservableValue<String>>() {
                @Override
                public ObservableValue<String> call(TableColumn.CellDataFeatures<ObservableList, String> param) {
                    return new SimpleStringProperty(param.getValue().get(t).toString());
                }
            });
        }

        ngayNhanCol.setCellFactory(column -> {
            return new TableCell<String, String>() {
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, true);

                    if (item == null || empty) {
                        setText(null);
                        setStyle("");
                    } else {
                        LocalDate d = LocalDate.parse(item, Util.SQL_DATE_TIME_FORMATTER);
                        setText(Util.DATE_TIME_FORMATTER.format(d));
                        d = null;
                    }
                }
            };
        });
        ngayTraCol.setCellFactory(column -> {
            return new TableCell<String, String>() {
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, true);

                    if (item == null || empty) {
                        setText(null);
                        setStyle("");
                    } else {
                        LocalDate d = LocalDate.parse(item, Util.SQL_DATE_TIME_FORMATTER);
                        setText(Util.DATE_TIME_FORMATTER.format(d));
                        d = null;
                    }
                }
            };
        });
    }

    private Stage getStage() {
        return (Stage) root.getScene().getWindow();
    }

    // TODO remove this in production
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
            }
            rs.close();
        } catch (SQLException ex) {
            Logger.getLogger(ListContractController.class.getName()).log(Level.SEVERE, null, ex);
        }

        comboBox.getItems().addAll(complexList);
    }
}
