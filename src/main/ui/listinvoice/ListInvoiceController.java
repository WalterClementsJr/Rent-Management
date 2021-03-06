package main.ui.listinvoice;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URL;
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
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ButtonType;
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
import main.util.MasterController;
import main.util.Util;

public class ListInvoiceController implements Initializable {

    @FXML
    private AnchorPane root;

    @FXML
    private TableView paidContract;

    @FXML
    private MenuItem refresh1;

    @FXML
    private MenuItem addInvoice1;

    @FXML
    private MenuItem delete1;

    @FXML
    private TableView indebtContract;

    @FXML
    private MenuItem refresh;

    @FXML
    private MenuItem addInvoice;

    @FXML
    private MenuItem delete;

    // extra elements
    DatabaseHandler handler = null;

    ObservableList listOfInDebtInvoices = FXCollections.observableArrayList();
    ObservableList listOfPaidInvoices = FXCollections.observableArrayList();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        MasterController.getInstance().registerListInvoiceController(this);
        handler = DatabaseHandler.getInstance();

        initInDebtTableColumns();
        initPaidTableColumns();
        loadAllInvoices();

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
            row = (ObservableList) indebtContract.getSelectionModel().getSelectedItems().get(0);
            if (row == null) {
                CustomAlert.showErrorMessage(
                        "Ch??a ch???n.",
                        "H??y ch???n m???t h???p ?????ng ????? th??m h??a ????n");
                return;
            }

            int mahdong = Integer.parseInt(row.get(0).toString());
            LocalDate lastPayDate
                    = LocalDate.parse(row.get(11).toString(), Util.SQL_DATE_TIME_FORMATTER);
            LocalDate ngaytra
                    = LocalDate.parse(row.get(8).toString(), Util.SQL_DATE_TIME_FORMATTER);
            BigDecimal giagoc
                    = new BigDecimal(row.get(10).toString());
            int songay
                    = Integer.parseInt(row.get(12).toString());

            InvoiceData data
                    = new InvoiceData(mahdong, lastPayDate, ngaytra, giagoc, songay);
            try {
                FXMLLoader loader = new FXMLLoader(getClass()
                        .getResource("/main/ui/addinvoice/addInvoice.fxml"));
                Parent parent = loader.load();

                AddInvoiceController controller = loader.getController();
                controller.loadEntries(data);

                Stage stage = new Stage(StageStyle.DECORATED);
                stage.initOwner(getStage());
                stage.initModality(Modality.WINDOW_MODAL);
                stage.setResizable(false);

                Scene scene = new Scene(parent);
                scene.getStylesheets().add(getClass()
                        .getResource(Setting.getInstance().getSTYLE_SHEET()).toString());

                stage.setScene(scene);
                stage.setTitle("Th??m h??a ????n");
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
                    "Ch??a ch???n.",
                    "Ch???n m???t h???p ?????ng ????? th??m h??a ????n");
        }
    }

    @FXML
    private void handleDeleteInvoice(ActionEvent event) {
        Util.checkLogin(getStage());
        if (!Setting.IS_VERIFIED) {
            return;
        }

        ObservableList row;
        try {
            row = (ObservableList) indebtContract.getSelectionModel().getSelectedItems().get(0);
            if (row == null) {
                CustomAlert.showErrorMessage(
                        "Ch??a ch???n",
                        "H??y ch???n m???t h??a ????n ????? x??a");
                return;
            }

            int mahdon = Integer.parseInt(row.get(13).toString());
            Optional<ButtonType> answer
                    = CustomAlert.confirmDialog(
                            "X??c nh???n x??a",
                            "B???n c?? ch???c mu???n x??a h??a ????n"
                    ).showAndWait();
            if (answer.get() == ButtonType.OK) {
                if (handler.deleteInvoice(mahdon)) {
                    CustomAlert.showSimpleAlert(
                            "X??a th??nh c??ng",
                            "???? x??a h??a ????n m???i nh???t c???a h??p ?????ng n??y");
                    handleRefresh(new ActionEvent());
                } else {
                    CustomAlert.showErrorMessage(
                            "Kh??ng th??? x??a h??a ????n n??y",
                            "???? c?? l???i x???y ra");
                }
            }

        } catch (IndexOutOfBoundsException ex) {
            CustomAlert.showErrorMessage(
                    "Ch??a ch???n.",
                    "Ch???n m???t h???p ?????ng ????? th??m h??a ????n");
        }
    }

    @FXML
    private void handleAddInvoice1(ActionEvent event) {
        Util.checkLogin(getStage());
        if (!Setting.IS_VERIFIED) {
            return;
        }

        ObservableList row;
        try {
            row = (ObservableList) paidContract.getSelectionModel().getSelectedItems().get(0);
            if (row == null) {
                CustomAlert.showErrorMessage(
                        "Ch??a ch???n.",
                        "H??y ch???n m???t h???p ?????ng ????? th??m h??a ????n");
                return;
            }

            int mahdong = Integer.parseInt(row.get(0).toString());
            LocalDate lastPayDate
                    = LocalDate.parse(row.get(11).toString(), Util.SQL_DATE_TIME_FORMATTER);
            LocalDate ngaytra
                    = LocalDate.parse(row.get(8).toString(), Util.SQL_DATE_TIME_FORMATTER);
            BigDecimal giagoc
                    = new BigDecimal(row.get(10).toString());
            int songay
                    = Integer.parseInt(row.get(12).toString());

            InvoiceData data
                    = new InvoiceData(mahdong, lastPayDate, ngaytra, giagoc, songay);
            try {
                FXMLLoader loader = new FXMLLoader(getClass()
                        .getResource("/main/ui/addinvoice/addInvoice.fxml"));
                Parent parent = loader.load();

                AddInvoiceController controller = loader.getController();
                controller.loadEntries(data);

                Stage stage = new Stage(StageStyle.DECORATED);
                stage.initOwner(getStage());
                stage.initModality(Modality.WINDOW_MODAL);
                stage.setResizable(false);

                Scene scene = new Scene(parent);
                scene.getStylesheets().add(getClass()
                        .getResource(Setting.getInstance().getSTYLE_SHEET()).toString());

                stage.setScene(scene);
                stage.setTitle("Th??m h??a ????n");
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
                    "Ch??a ch???n.",
                    "Ch???n m???t h???p ?????ng ????? th??m h??a ????n");
        }
    }

    @FXML
    private void handleDeleteInvoice1(ActionEvent event) {
        Util.checkLogin(getStage());
        if (!Setting.IS_VERIFIED) {
            return;
        }

        ObservableList row;
        try {
            row = (ObservableList) paidContract.getSelectionModel().getSelectedItems().get(0);
            if (row == null) {
                CustomAlert.showErrorMessage(
                        "Ch??a ch???n.",
                        "H??y ch???n m???t h??a ????n ????? x??a");
                return;
            }

            int mahdon = Integer.parseInt(row.get(13).toString());
            Optional<ButtonType> answer
                    = CustomAlert.confirmDialog(
                            "X??c nh???n x??a",
                            "B???n c?? ch???c mu???n x??a h??a ????n n??y"
                    ).showAndWait();
            if (answer.get() == ButtonType.OK) {
                if (handler.deleteInvoice(mahdon)) {
                    CustomAlert.showSimpleAlert(
                            "X??a th??nh c??ng",
                            "???? x??a h??a ????n m???i nh???t c???a h??p ?????ng n??y");
                    handleRefresh(new ActionEvent());
                } else {
                    CustomAlert.showErrorMessage(
                            "Kh??ng th??? x??a h??a ????n n??y",
                            "???? c?? l???i x???y ra");
                }
            }

        } catch (IndexOutOfBoundsException ex) {
            CustomAlert.showErrorMessage(
                    "Ch??a ch???n",
                    "Ch???n m???t h???p ?????ng ????? th??m h??a ????n");
        }
    }

    private void loadData() {
        listOfInDebtInvoices.clear();
        listOfPaidInvoices.clear();
        loadAllInvoices();
        loadListToTable();
    }

    private void loadListToTable() {
        indebtContract.setItems(listOfInDebtInvoices);
        paidContract.setItems(listOfPaidInvoices);
    }

    public void loadAllInvoices() {
        Util.loadResultSetToList(
                handler.getInDebtContractsWithInvoiceInfo(),
                listOfInDebtInvoices);
        Util.loadResultSetToList(
                handler.getPaidContractsWithInvoiceInfo(),
                listOfPaidInvoices);
    }

    public void initInDebtTableColumns() {
        TableColumn mahdongCol
                = new TableColumn<>("M?? h???p ?????ng");
        TableColumn maKhuCol
                = new TableColumn<>("M?? khu");
        TableColumn tenKhuCol
                = new TableColumn<>("Khu");
        TableColumn maphongCol
                = new TableColumn<>("M?? ph??ng");
        TableColumn tenPhongCol
                = new TableColumn<>("Ph??ng");
        TableColumn makhCol
                = new TableColumn<>("M?? kh??ch");
        TableColumn tenkhachCol
                = new TableColumn<>("Ch??? h???p ?????ng");
        TableColumn ngayNhanCol
                = new TableColumn<>("Ng??y nh???n");
        TableColumn ngayTraCol
                = new TableColumn<>("Ng??y tr???");
        TableColumn tienCocCol
                = new TableColumn<>("Ti???n c???c");
        TableColumn giaGocCol
                = new TableColumn<>("Gi?? g???c");
        TableColumn ngayttgannhatCol
                = new TableColumn<>("ngayttgannhat");
        TableColumn songayCol
                = new TableColumn<>("S??? ng??y n???");
        TableColumn hdonIdCol
                = new TableColumn<>("id");

        indebtContract.getColumns().addAll(
                mahdongCol, maKhuCol, tenKhuCol, maphongCol, tenPhongCol,
                makhCol, tenkhachCol, ngayNhanCol, ngayTraCol, tienCocCol,
                giaGocCol, ngayttgannhatCol, songayCol, hdonIdCol);

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
        ngayttgannhatCol.setVisible(false);
        hdonIdCol.setVisible(false);

        for (int i = 0; i < indebtContract.getColumns().size(); i++) {
            final int t = i;
            TableColumn col = (TableColumn) indebtContract.getColumns().get(i);
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

    public void initPaidTableColumns() {
        TableColumn mahdongCol
                = new TableColumn<>("M?? h???p ?????ng");
        TableColumn maKhuCol
                = new TableColumn<>("M?? khu");
        TableColumn tenKhuCol
                = new TableColumn<>("Khu");
        TableColumn maphongCol
                = new TableColumn<>("M?? ph??ng");
        TableColumn tenPhongCol
                = new TableColumn<>("Ph??ng");
        TableColumn makhCol
                = new TableColumn<>("M?? kh??ch");
        TableColumn tenkhachCol
                = new TableColumn<>("Ch??? h???p ?????ng");
        TableColumn ngayNhanCol
                = new TableColumn<>("Ng??y nh???n");
        TableColumn ngayTraCol
                = new TableColumn<>("Ng??y tr???");
        TableColumn tienCocCol
                = new TableColumn<>("Ti???n c???c");
        TableColumn giaGocCol
                = new TableColumn<>("Gi?? g???c");
        TableColumn ngayttgannhatCol
                = new TableColumn<>("ngayttgannhat");
        TableColumn songayCol
                = new TableColumn<>("S??? ng??y t???i ng??y ????ng ti???p theo");
        TableColumn hdonIdCol
                = new TableColumn<>("id");

        paidContract.getColumns().addAll(
                mahdongCol, maKhuCol, tenKhuCol, maphongCol, tenPhongCol,
                makhCol, tenkhachCol, ngayNhanCol, ngayTraCol, tienCocCol,
                giaGocCol, ngayttgannhatCol, songayCol, hdonIdCol);

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
        ngayttgannhatCol.setVisible(false);
        hdonIdCol.setVisible(false);

        for (int i = 0; i < paidContract.getColumns().size(); i++) {
            final int t = i;
            TableColumn col = (TableColumn) paidContract.getColumns().get(i);
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
                    }
                }
            };
        });
        songayCol.setCellFactory(column -> {
            return new TableCell<String, String>() {
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, true);

                    if (item == null || empty) {
                        setText(null);
                    } else {
                        int t = Integer.parseInt(item);
                        setText(Math.abs(t) + "");
                    }
                }
            };
        });
    }

    private Stage getStage() {
        return (Stage) root.getScene().getWindow();
    }

    @FXML
    public void handleRefresh(ActionEvent event) {
        loadData();
    }
}
