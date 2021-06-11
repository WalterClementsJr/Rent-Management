package main.ui.listinvoice;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
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
import main.model.InvoiceData;
import main.ui.addinvoice.AddInvoiceController;
import main.ui.alert.CustomAlert;
import main.util.Util;

public class ListInvoiceController implements Initializable {

    @FXML
    private AnchorPane root;
    @FXML
    private TableView tableView;
    @FXML
    private MenuItem addInvoice;

    // extra elements
    DatabaseHandler handler = null;

    ObservableList listOfAllInvoices = FXCollections.observableArrayList();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        handler = DatabaseHandler.getInstance();

        initTableColumns();
        loadResultSetToList(handler.getInDebtContractsWithInvoiceInfo(), listOfAllInvoices);

        loadData();
    }

    @FXML
    private void handleAddInvoice(ActionEvent event) {
        Util.checkLogin(getStage());
        if (!Setting.IS_VERIFIED) {
            return;
        }

        ObservableList row;
        try {
            row = (ObservableList) tableView.getSelectionModel().getSelectedItems().get(0);
            if (row == null) {
                CustomAlert.showErrorMessage(
                        "Chưa chọn.",
                        "Hãy chọn một dòng để thêm hóa đơn");
                return;
            }

            int mahdong = Integer.parseInt(row.get(0).toString());
            LocalDate lastPayDate =
                    LocalDate.parse(row.get(11).toString(), Util.SQL_DATE_TIME_FORMATTER);
            LocalDate ngaytra =
                    LocalDate.parse(row.get(8).toString(), Util.SQL_DATE_TIME_FORMATTER);
            BigDecimal giagoc =
                    new BigDecimal(row.get(10).toString());
            int songay =
                    Integer.parseInt(row.get(12).toString());

            InvoiceData data =
                    new InvoiceData(mahdong, lastPayDate, ngaytra, giagoc, songay);
            try {
                FXMLLoader loader = new FXMLLoader(getClass()
                        .getResource("/main/ui/addinvoice/addInvoice.fxml"));
                Parent parent = loader.load();

                AddInvoiceController controller = loader.getController();
                controller.loadEntries(data);

                Stage stage = new Stage(StageStyle.DECORATED);
                stage.initOwner(getStage());
                stage.initModality(Modality.WINDOW_MODAL);

                Scene scene = new Scene(parent);
                scene.getStylesheets().add(getClass()
                        .getResource(Setting.getInstance().getSTYLE_SHEET()).toString());

                stage.setScene(scene);
                stage.setTitle("Thêm hóa đơn");
                stage.show();
                Util.setWindowIcon(stage);

                stage.setOnHiding((e) -> {
                    handleRefresh(new ActionEvent());
                });
            } catch (IOException ex) {
                Logger.getLogger(ListInvoiceController.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (IndexOutOfBoundsException ex) {
            CustomAlert.showErrorMessage(
                    "Chưa chọn.",
                    "Chọn một hợp đồng để thêm hóa đơn");
        }
    }

    private void loadData() {
        listOfAllInvoices.clear();
        loadAllInvoices();
        loadListToTable();
    }

    private void loadListToTable() {
        tableView.setItems(listOfAllInvoices);
    }

    public void loadAllInvoices() {
        loadResultSetToList(handler.getInDebtContractsWithInvoiceInfo(), listOfAllInvoices);
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
            Logger.getLogger(ListInvoiceController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void initTableColumns() {
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
                = new TableColumn<>("Số ngày nợ");

        tableView.getColumns().addAll(
                mahdongCol, maKhuCol, tenKhuCol, maphongCol, tenPhongCol, makhCol, tenkhachCol,
                ngayNhanCol, ngayTraCol, tienCocCol, giaGocCol, ngayttgannhatCol, songayCol);

        tenPhongCol.setMinWidth(200);
        tenkhachCol.setMinWidth(200);

        mahdongCol.setVisible(false);
        maKhuCol.setVisible(false);
        maphongCol.setVisible(false);
        ngayNhanCol.setVisible(false);
        ngayTraCol.setVisible(false);
        makhCol.setVisible(false);
        giaGocCol.setVisible(false);
        tienCocCol.setVisible(false);
//        songayCol.setVisible(false);
        ngayttgannhatCol.setVisible(false);

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

    @FXML
    void handleRefresh(ActionEvent event) {
        loadData();
    }
}
