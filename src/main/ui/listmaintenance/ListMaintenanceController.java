package main.ui.listmaintenance;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.sql.ResultSet;
import java.text.DecimalFormat;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Callback;
import main.database.DatabaseHandler;
import main.model.Complex;
import main.model.Maintenance;
import main.ui.addmaintenance.AddMaintenanceController;
import main.ui.alert.CustomAlert;
import main.util.Util;

public class ListMaintenanceController implements Initializable {

    @FXML
    private AnchorPane root;
    @FXML
    private ComboBox<Complex> comboBox;
    @FXML
    private TableView tableView;
    @FXML
    private MenuItem refreshMenu;
    @FXML
    private MenuItem editMenu;
    @FXML
    private MenuItem deleteMenu;
    @FXML
    private Button help;

    // extra elements
    // TODO remove this line in production
    ObservableList<Complex> complexList = FXCollections.observableArrayList();
//    ObservableList<Complex> complexList = ListRoomController.complexList;

    ObservableList<Complex> listOfAllMaintenance = FXCollections.observableArrayList();

    DatabaseHandler handler;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        handler = DatabaseHandler.getInstance();
        initTableColumns(tableView);

        // TODO uncomment these lines when running in production
//        comboBox.getItems().addAll(complexList);
//        comboBox.getSelectionModel().selectFirst();
        loadComplexData();
        comboBox.getSelectionModel().selectFirst();

        loadData();
    }

    @FXML
    private void complexChanged(ActionEvent event) {
        loadData();
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
            }
            rs.close();
        } catch (SQLException ex) {
            Logger.getLogger(ListMaintenanceController.class.getName()).log(Level.SEVERE, null, ex);
        }

        comboBox.getItems().addAll(complexList);
    }

    private void loadData() {
        Complex chosenComplex = comboBox.getValue();

        try {
            listOfAllMaintenance.clear();
            loadAllMaintenance(chosenComplex.getId());
            loadListToTable();
        } catch (NullPointerException ex) {
            return;
        }
    }

    private void loadAllMaintenance(int complexId) {
        listOfAllMaintenance.clear();
        loadResultSetToList(handler.getMaintenanceFromComplex(complexId), listOfAllMaintenance);
    }

    private void loadResultSetToList(ResultSet rs, ObservableList list) {
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
            Logger.getLogger(ListMaintenanceController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void loadListToTable() {
        tableView.setItems(listOfAllMaintenance);
    }

    @FXML
    private void handleRefresh(ActionEvent event) {
        // TODO remove this line in production
        loadComplexData();

        comboBox.getSelectionModel().selectFirst();
        loadData();
    }

    @FXML
    private void handleEditButton(ActionEvent event) {
        ObservableList list = tableView.getSelectionModel().getSelectedItems();
        ObservableList row;

        // get row data
        try {
            row = (ObservableList) tableView.getSelectionModel().getSelectedItems().get(0);
            if (row == null) {
                CustomAlert.showErrorMessage("Chưa chọn.", "Hãy chọn thông tin bảo trì để xóa");
                return;
            }
            Maintenance m = new Maintenance(
                    Integer.parseInt(row.get(0).toString()),
                    Integer.parseInt(row.get(1).toString()),
                    new BigDecimal(row.get(3).toString()),
                    LocalDate.parse(row.get(4).toString(), Util.SQL_DATE_TIME_FORMATTER),
                    row.get(5).toString());
            try {
                // load edit pane
                FXMLLoader loader = new FXMLLoader(getClass()
                        .getResource("/main/ui/addmaintenance/addMaintenance.fxml"));
                Parent parent = loader.load();

                AddMaintenanceController controller = loader.getController();
                controller.loadEntries(m);

                Stage stage = new Stage(StageStyle.DECORATED);
                stage.initOwner(getStage());
                stage.initModality(Modality.WINDOW_MODAL);

                Scene scene = new Scene(parent);
                scene.getStylesheets().add(getClass()
                        .getResource(Util.STYLE_SHEET_LOCATION).toString());

                stage.setScene(scene);
                stage.setTitle("Chỉnh sửa bảo trì");
                stage.show();
                Util.setWindowIcon(stage);

                // refresh on close
                stage.setOnHiding((e) -> {
                    handleRefresh(new ActionEvent());
                });
            } catch (IOException ex) {
                Logger.getLogger(ListMaintenanceController.class.getName()).log(Level.SEVERE, null, ex);
            }

        } catch (IndexOutOfBoundsException ex) {
            CustomAlert.showErrorMessage("Chưa chọn.", "Hãy chọn thông tin bảo trì để chỉnh sửa");
        }
    }

    @FXML
    private void handleDelete(ActionEvent event) {
        ObservableList row;

        try {
            row = (ObservableList) tableView.getSelectionModel().getSelectedItems().get(0);
            if (row == null) {
                CustomAlert.showErrorMessage("Chưa chọn.", "Hãy chọn thông tin bảo trì để xóa");
                return;
            }

            Optional<ButtonType> answer
                    = CustomAlert.confirmDelete(
                            "Xóa bảo trì",
                            "Bạn có chắc muốn xóa thông tin bảo trì?").showAndWait();

            if (answer.get() == ButtonType.OK) {
                if (handler.deleteMaintenance(Integer.parseInt(row.get(0).toString()))) {
                    CustomAlert.showSimpleAlert(
                            "Xóa thành công", "Đã xóa thông tin bảo trì");
                    handleRefresh(new ActionEvent());
                } else {
                    CustomAlert.showErrorMessage(
                            "Không thể xóa", "Đã có lỗi xảy ra");
                }
            }
        } catch (IndexOutOfBoundsException ex) {
            CustomAlert.showErrorMessage("Chưa chọn.", "Hãy chọn thông tin bảo trì để chỉnh sửa");
        }
    }

    @FXML
    private void handleHelp(ActionEvent event) {
        // TODO help
    }

    public void initTableColumns(TableView tableView) {
        TableColumn idCol
                = new TableColumn<>("Mã bao tri");
        TableColumn maphongCol
                = new TableColumn<>("Mã phòng");
        TableColumn tenPhongCol
                = new TableColumn<>("Tên phòng");
        TableColumn chiphiCol
                = new TableColumn<>("Chi phí");
        TableColumn ngayCol
                = new TableColumn<>("Ngày");
        TableColumn moTaCol
                = new TableColumn<>("Mô tả");

        tableView.getColumns().addAll(
                idCol, maphongCol,
                tenPhongCol, chiphiCol,
                ngayCol, moTaCol);

        tenPhongCol.setMinWidth(200);
        chiphiCol.setMinWidth(150);
        ngayCol.setMinWidth(150);
        moTaCol.setMinWidth(300);

        idCol.setVisible(false);
        maphongCol.setVisible(false);

        for (int i = 0; i < tableView.getColumns().size(); i++) {
            final int t = i;
            TableColumn col = (TableColumn) tableView.getColumns().get(i);
            col.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<ObservableList, String>, ObservableValue<String>>() {
                @Override
                public ObservableValue<String> call(TableColumn.CellDataFeatures<ObservableList, String> param) {
                    return new SimpleStringProperty(param.getValue().get(t).toString());
                }
            });
        }
        // formating
        ngayCol.setCellFactory(column -> {
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
        chiphiCol.setCellFactory(column -> {
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

    private Stage getStage() {
        return (Stage) root.getScene().getWindow();
    }
}
