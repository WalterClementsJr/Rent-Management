package main.ui.listmaintenance;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URL;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
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
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Callback;
import main.app.settings.Setting;
import main.database.DatabaseHandler;
import main.model.Complex;
import main.model.Maintenance;
import main.ui.addmaintenance.AddMaintenanceController;
import main.ui.alert.CustomAlert;
import main.ui.listroom.ListRoomController;
import main.util.MasterController;
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
    ObservableList<Complex> complexList = ListRoomController.complexList;
    ObservableList listOfAllMaintenance = FXCollections.observableArrayList();

    FilteredList allFilteredList;

    DatabaseHandler handler;
    Setting setting;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        MasterController.getInstance().registerListMaintenanceController(this);

        handler = DatabaseHandler.getInstance();
        setting = Setting.getInstance();
        initTableColumns(tableView);

        comboBox.setItems(complexList);
        comboBox.getSelectionModel().selectFirst();

        loadData();
    }

    @FXML
    private void complexChanged(ActionEvent event) {
        loadData();
    }

    private void loadData() {
        Complex chosenComplex = comboBox.getValue();

        try {
            listOfAllMaintenance.clear();
            loadAllMaintenance(chosenComplex.getId());
            loadListToTable();
        } catch (NullPointerException ex) {
        }
    }

    private void loadAllMaintenance(int complexId) {
        listOfAllMaintenance.clear();
        Util.loadResultSetToList(handler.getMaintenanceFromComplex(complexId), listOfAllMaintenance);
    }

    private void loadListToTable() {
        tableView.setItems(listOfAllMaintenance);
    }

    @FXML
    public void handleRefresh(ActionEvent event) {
        comboBox.getSelectionModel().selectFirst();
        loadData();
    }

    @FXML
    private void handleEditButton(ActionEvent event) {
        ObservableList row;
        try {
            row = (ObservableList) tableView.getSelectionModel().getSelectedItems().get(0);
            if (row == null) {
                CustomAlert.showErrorMessage(
                        "Chưa chọn.",
                        "Hãy chọn thông tin bảo trì để xóa");
                return;
            }
            Maintenance m = new Maintenance(
                    Integer.parseInt(row.get(0).toString()),
                    Integer.parseInt(row.get(1).toString()),
                    new BigDecimal(row.get(3).toString()),
                    LocalDate.parse(row.get(4).toString(), Util.SQL_DATE_TIME_FORMATTER),
                    row.get(5).toString());
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource(
                        "/main/ui/addmaintenance/addMaintenance.fxml"));
                Parent parent = loader.load();

                AddMaintenanceController controller = loader.getController();
                controller.loadEntries(m);

                Stage stage = new Stage(StageStyle.DECORATED);
                stage.initOwner(getStage());
                stage.initModality(Modality.WINDOW_MODAL);
                stage.setResizable(false);

                Scene scene = new Scene(parent);
                scene.getStylesheets().add(getClass().getResource(
                        Setting.getInstance().getSTYLE_SHEET()).toString());

                stage.setScene(scene);
                stage.setTitle("Chỉnh sửa bảo trì");
                stage.show();
                Util.setWindowIcon(stage);

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
        Util.checkLogin(getStage());

        if (!Setting.IS_VERIFIED) {
            return;
        }
        ObservableList row;
        try {
            row = (ObservableList) tableView.getSelectionModel().getSelectedItems().get(0);
            if (row == null) {
                CustomAlert.showErrorMessage("Chưa chọn.", "Hãy chọn thông tin bảo trì để xóa");
                return;
            }

            Optional<ButtonType> answer
                    = CustomAlert.confirmDialog(
                            "Xóa bảo trì",
                            "Bạn có chắc muốn xóa thông tin bảo trì?")
                            .showAndWait();
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
        moTaCol.setMinWidth(400);

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

    public void comboBoxSelectFirst() {
        comboBox.getSelectionModel().selectFirst();
    }
    
    
}
