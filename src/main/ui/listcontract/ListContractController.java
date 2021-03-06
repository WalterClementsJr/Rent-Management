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
import java.util.function.Predicate;
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
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableCell;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import main.app.settings.Setting;
import main.database.DatabaseHandler;
import main.model.Complex;
import main.model.Contract;
import main.model.InvoiceData;
import main.ui.addcontract.AddContractController;
import main.ui.addinvoice.AddInvoiceController;
import main.ui.addroommate.AddRoommateController;
import main.ui.alert.CustomAlert;
import main.ui.listinvoice.ListInvoiceController;
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
    private TextField filterField;

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

    public static ObservableList listOfAllContracts = FXCollections.observableArrayList();
    public static ObservableList listOfActiveContracts = FXCollections.observableArrayList();
    public static ObservableList listOfOldContracts = FXCollections.observableArrayList();

    FilteredList allConFilteredList;
    FilteredList activeConFilteredList;
    FilteredList oldConFilteredList;

    public static ObservableList listOfAllRoommates = FXCollections.observableArrayList();
    public static ObservableList listOfActiveRoommates = FXCollections.observableArrayList();
    public static ObservableList listOfOldRoommates = FXCollections.observableArrayList();

    FilteredList allRmFilteredList;
    FilteredList activeRmFilteredList;
    FilteredList oldRmFilteredList;

    DatabaseHandler handler;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        MasterController.getInstance().registerListContractController(this);
        handler = DatabaseHandler.getInstance();

        comboBox.setItems(complexList);
        comboBox.getSelectionModel().selectFirst();

        initContractTableColumns();
        initRoommateTableColumns();

        filter.getItems().addAll(Util.FILTER_ALL, Util.FILTER_ACTIVE, Util.FILTER_OLD);
        filter.getSelectionModel().selectFirst();

        loadContractData();
        loadRoommatesData();

        // set up filter search results
        allConFilteredList = new FilteredList<>(listOfAllContracts);
        activeConFilteredList = new FilteredList<>(listOfActiveContracts);
        oldConFilteredList = new FilteredList<>(listOfOldContracts);
        allRmFilteredList = new FilteredList<>(listOfAllRoommates);
        activeRmFilteredList = new FilteredList<>(listOfActiveRoommates);
        oldRmFilteredList = new FilteredList<>(listOfOldRoommates);

        setContractFilterFieldProperty(allConFilteredList);
        setContractFilterFieldProperty(activeConFilteredList);
        setContractFilterFieldProperty(oldConFilteredList);
        setRoommateFilterFieldProperty(allRmFilteredList);
        setRoommateFilterFieldProperty(activeRmFilteredList);
        setRoommateFilterFieldProperty(oldRmFilteredList);

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

        try {
            complexTooltip.setText(comboBox.getSelectionModel().getSelectedItem().getDescription());
        } catch (NullPointerException ex) {
        }

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
            case Util.FILTER_ALL -> contractTable.setItems(allConFilteredList);
            case Util.FILTER_ACTIVE -> contractTable.setItems(activeConFilteredList);
            case Util.FILTER_OLD -> contractTable.setItems(oldConFilteredList);
        }
    }

    private void loadRoommatesToTable() {
        switch (filter.getSelectionModel().getSelectedItem()) {
            case Util.FILTER_ALL -> roommateTable.setItems(allRmFilteredList);
            case Util.FILTER_ACTIVE -> roommateTable.setItems(activeRmFilteredList);
            case Util.FILTER_OLD -> roommateTable.setItems(oldRmFilteredList);
        }
    }

    private void loadContractData() {
        try {
            listOfAllContracts.clear();
            listOfActiveContracts.clear();
            listOfOldContracts.clear();

            int id = comboBox.getSelectionModel().getSelectedItem().getId();

            loadAllContracts(id);
            loadActiveContracts(id);
            loadOldContracts(id);

            loadContractsToTable();
        } catch (Exception e) {
        }
    }

    private void loadRoommatesData() {
        try {
            listOfAllRoommates.clear();
            listOfActiveRoommates.clear();
            listOfOldRoommates.clear();

            int id = comboBox.getSelectionModel().getSelectedItem().getId();

            loadAllRoommates(id);
            loadActiveRoommates(id);
            loadOldRoommates(id);

            loadRoommatesToTable();
        } catch (Exception e) {
        }
    }

    public void loadAllContracts(int id) {
        Util.loadResultSetToList(handler.getAllContractsWithInfo(id), listOfAllContracts);
    }

    public void loadActiveContracts(int id) {
        Util.loadResultSetToList(handler.getActiveContractsWithInfo(id), listOfActiveContracts);
    }

    public void loadOldContracts(int id) {
        Util.loadResultSetToList(handler.getOldContractsWithInfo(id), listOfOldContracts);
    }

    public void loadAllRoommates(int id) {
        Util.loadResultSetToList(handler.getAllRoommatesWithInfo(id), listOfAllRoommates);
    }

    public void loadActiveRoommates(int id) {
        Util.loadResultSetToList(handler.getActiveRoommatesWithInfo(id), listOfActiveRoommates);
    }

    public void loadOldRoommates(int id) {
        Util.loadResultSetToList(handler.getOldRoommatesWithInfo(id), listOfOldRoommates);
    }

    @FXML
    void handleDeleteButton(ActionEvent event) {
        Util.checkLogin(getStage());

        if (!Setting.IS_VERIFIED) {
            return;
        }

        ObservableList row;
        try {
            row = (ObservableList) contractTable.getSelectionModel().getSelectedItems().get(0);
            if (row == null) {
                CustomAlert.showErrorMessage(
                        "Ch??a ch???n.",
                        "H??y ch???n m???t h???p ?????ng ????? x??a");
                return;
            }
            Optional<ButtonType> answer
                    = CustomAlert.confirmDialog(
                            "X??a h???p ?????ng",
                            "B???n c?? ch???c mu???n x??a h???p ?????ng n??y c??ng c??c th??ng tin li??n quan"
                            + "\n(Bao g???m th??ng tin kh??ch ??? gh??p v?? h??a ????n)?")
                            .showAndWait();
            if (answer.get() == ButtonType.OK) {
                if (handler.deleteContract(Integer.parseInt(row.get(0).toString()))) {
                    CustomAlert.showSimpleAlert(
                            "X??a th??nh c??ng", "???? x??a h???p ?????ng");
                    handleRefresh(new ActionEvent());

                    MasterController.getInstance().getListCustomerController()
                            .handleRefresh(new ActionEvent());
                } else {
                    CustomAlert.showErrorMessage(
                            "Kh??ng th??? x??a", "???? c?? l???i x???y ra");
                }
            }
        } catch (IndexOutOfBoundsException ex) {
            CustomAlert.showErrorMessage("Ch??a ch???n", "H??y ch???n m???t h???p ?????ng ????? x??a");
        }
    }

    @FXML
    void handleEditContract(ActionEvent event) {
        Util.checkLogin(getStage());

        if (!Setting.IS_VERIFIED) {
            return;
        }

        ObservableList selectedRow;
        try {
            selectedRow = (ObservableList) contractTable.getSelectionModel().getSelectedItems().get(0);
            if (selectedRow == null) {
                CustomAlert.showErrorMessage(
                        "Ch??a ch???n.", "H??y ch???n m???t h???p ?????ng ????? ch???nh s???a");
                return;
            }
            Contract con = new Contract(
                    Integer.parseInt(selectedRow.get(0).toString()),
                    Integer.parseInt(selectedRow.get(3).toString()),
                    Integer.parseInt(selectedRow.get(5).toString()),
                    LocalDate.parse(selectedRow.get(7).toString(), Util.SQL_DATE_TIME_FORMATTER),
                    LocalDate.parse(selectedRow.get(8).toString(), Util.SQL_DATE_TIME_FORMATTER),
                    new BigDecimal(selectedRow.get(9).toString()));
            try {
                FXMLLoader loader = new FXMLLoader(getClass()
                        .getResource("/main/ui/addcontract/addContract.fxml"));
                Parent parent = loader.load();

                AddContractController controller = loader.getController();
                controller.loadEntries(con);

                Stage stage = new Stage(StageStyle.DECORATED);
                stage.initOwner(getStage());
                stage.initModality(Modality.WINDOW_MODAL);
                stage.setResizable(false);

                Scene scene = new Scene(parent);
                scene.getStylesheets().add(getClass()
                        .getResource(Setting.getInstance().getSTYLE_SHEET()).toString());

                stage.setScene(scene);
                stage.setTitle("Ch???nh s???a h???p ?????ng");
                stage.show();
                Util.setWindowIcon(stage);

                stage.setOnHiding((e) -> {
                    handleRefresh(new ActionEvent());
                });
            } catch (IOException ex) {
                Logger.getLogger(ListContractController.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (IndexOutOfBoundsException ex) {
            CustomAlert.showErrorMessage(
                    "Ch??a ch???n.",
                    "H??y ch???n m???t h???p ?????ng ????? ch???nh s???a");
        }
    }

    @FXML
    private void handleAddInvoice(ActionEvent event) {
        Util.checkLogin(getStage());
        if (!Setting.IS_VERIFIED) {
            return;
        }

        ObservableList row;
        try {
            row = (ObservableList) contractTable.getSelectionModel().getSelectedItems().get(0);
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
    public void handleRefresh(ActionEvent event) {
        loadContractData();
        loadRoommatesData();
    }

    public void comboBoxSelectFirst() {
        comboBox.getSelectionModel().selectFirst();
    }

    @FXML
    public void handleRefreshRoommates(ActionEvent event) {
        loadRoommatesData();
    }

    @FXML
    public void handleReturn(ActionEvent e) {
        Util.checkLogin(getStage());
        if (!Setting.IS_VERIFIED) {
            return;
        }

        ObservableList selectedRow;
        try {
            selectedRow = (ObservableList) contractTable.getSelectionModel().getSelectedItems().get(0);
            if (selectedRow == null) {
                CustomAlert.showErrorMessage(
                        "Ch??a ch???n.",
                        "H??y ch???n m???t h???p ?????ng ????? k???t th??c h???p ?????ng");
                return;
            }
            int selectedConId = Integer.parseInt((String) selectedRow.get(0));
            List activeConRow;
            int activeConId;

            // check contract ngaynhan > today set ngaytra to ngaynhan, else set ngaytra to today
            LocalDate ngaynhan = LocalDate.parse(selectedRow.get(7).toString(), Util.SQL_DATE_TIME_FORMATTER);

            for (int i = 0; i < listOfActiveContracts.size(); i++) {
                activeConRow = (List) listOfActiveContracts.get(i);
                activeConId = Integer.parseInt((String) activeConRow.get(0));
                if (activeConId == selectedConId) {
                    if (handler.isContractEndable(selectedConId)) {
                        Optional<ButtonType> answer
                                = CustomAlert.confirmDialog(
                                        "K???t th??c h???p ?????ng",
                                        "B???n c?? ch???c mu???n k???t th??c h???p ?????ng n??y?")
                                        .showAndWait();
                        if (answer.get() == ButtonType.OK) {
                            if (LocalDate.now().isAfter(ngaynhan)) {
                                if (handler.endContractToday(selectedConId)) {
                                    CustomAlert.showSimpleAlert(
                                            "Th??nh c??ng",
                                            "???? k???t th??c h???p ?????ng");
                                    MasterController.getInstance().getListCustomerController()
                                            .handleRefresh(new ActionEvent());
                                    MasterController.getInstance().getListRoomController()
                                            .handleRefresh(new ActionEvent());
                                    handleRefresh(new ActionEvent());
                                } else {
                                    CustomAlert.showErrorMessage(
                                            "Kh??ng th??? th???c hi???n", "???? c?? l???i x???y ra");
                                }
                            } else {
                                if (handler.endContractAtDate(selectedConId, ngaynhan)) {
                                    CustomAlert.showSimpleAlert(
                                            "Th??nh c??ng",
                                            "???? k???t th??c h???p ?????ng");
                                    MasterController.getInstance().getListCustomerController()
                                            .handleRefresh(new ActionEvent());
                                    MasterController.getInstance().getListRoomController()
                                            .handleRefresh(new ActionEvent());
                                    handleRefresh(new ActionEvent());
                                } else {
                                    CustomAlert.showErrorMessage(
                                            "Kh??ng th??? th???c hi???n", "???? c?? l???i x???y ra");
                                }
                            }
                        }
                    } else {
                        CustomAlert.showErrorMessage(
                                "Kh??ng th??? th???c hi???n",
                                "H???p ?????ng ch??a thanh to??n ?????.\n"
                                + "H??y thanh to??n ?????y ????? cho h???p ?????ng n??y v?? th??? l???i sau");
                    }
                    return;
                }
            }
            CustomAlert.showErrorMessage(
                    "Kh??ng th??? th???c hi???n",
                    "H???p ?????ng ???? h???t h???n");
        } catch (IndexOutOfBoundsException ex) {
            CustomAlert.showErrorMessage("Ch??a ch???n", "H??y ch???n m???t h???p ?????ng ????? x??a");
        }
    }

    @FXML
    public void handleAddRoommate(ActionEvent evt) {
        Util.checkLogin(getStage());

        if (!Setting.IS_VERIFIED) {
            return;
        }

        ObservableList selectedRow;
        try {
            selectedRow = (ObservableList) contractTable.getSelectionModel().getSelectedItems().get(0);
            if (selectedRow == null) {
                CustomAlert.showErrorMessage(
                        "Ch??a ch???n.",
                        "H??y ch???n m???t h???p ?????ng th??m kh??ch ??? gh??p");
                return;
            }
            int selectedConId = Integer.parseInt((String) selectedRow.get(0));
            List activeConRow;
            int activeConId;

            for (int i = 0; i < listOfActiveContracts.size(); i++) {
                activeConRow = (List) listOfActiveContracts.get(i);
                activeConId = Integer.parseInt((String) activeConRow.get(0));
                if (activeConId == selectedConId) {
                    if (handler.isRoomFull(Integer.parseInt((String) selectedRow.get(3)))) {
                        CustomAlert.showErrorMessage(
                                "Ph??ng %s ???? ????? ng?????i".formatted(selectedRow.get(4).toString()),
                                "H??y ch???n ph??ng kh??c");
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
                        stage.setResizable(false);

                        Scene scene = new Scene(parent);
                        scene.getStylesheets().add(getClass()
                                .getResource(Setting.getInstance().getSTYLE_SHEET()).toString());

                        stage.setScene(scene);
                        stage.setTitle("Th??m kh??ch ??? gh??p");
                        stage.show();
                        Util.setWindowIcon(stage);

                        stage.setOnHiding((e) -> {
                            handleRefresh(new ActionEvent());
                        });
                        return;
                    } catch (IOException ex) {
                        Logger.getLogger(ListContractController.class.getName()).log(Level.SEVERE, null, ex);
                        return;
                    }
                }
            }
            CustomAlert.showErrorMessage(
                    "Kh??ng th??? th???c hi???n",
                    "H???p ?????ng ???? h???t h???n");
        } catch (IndexOutOfBoundsException ex) {
            CustomAlert.showErrorMessage(
                    "Ch??a ch???n", "H??y ch???n m???t h???p ?????ng ????? th??m");
        }
    }

    @FXML
    public void handleEditRoommate(ActionEvent e) {
        Util.checkLogin(getStage());

        if (!Setting.IS_VERIFIED) {
            return;
        }

        ObservableList selectedRow;
        try {
            selectedRow = (ObservableList) roommateTable.getSelectionModel().getSelectedItems().get(0);
            if (selectedRow == null) {
                CustomAlert.showErrorMessage(
                        "Ch??a ch???n.",
                        "H??y ch???n th??ng tin kh??ch ??? gh??p ????? ch???nh s???a");
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
                stage.setResizable(false);

                Scene scene = new Scene(parent);
                scene.getStylesheets().add(getClass()
                        .getResource(Setting.getInstance().getSTYLE_SHEET()).toString());

                stage.setScene(scene);
                stage.setTitle("Ch???nh s???a th???i h???n ??? gh??p");
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
                    "Ch??a ch???n",
                    "\nH??y ch???n th??ng tin kh??ch ??? gh??p ????? ch???nh s???a");
        }
    }

    @FXML
    public void handleRoommateReturn(ActionEvent e) {
        Util.checkLogin(getStage());

        if (!Setting.IS_VERIFIED) {
            return;
        }
        ObservableList selectedRow;
        try {
            selectedRow = (ObservableList) roommateTable.getSelectionModel().getSelectedItems().get(0);
            if (selectedRow == null) {
                CustomAlert.showErrorMessage(
                        "Ch??a ch???n.",
                        "H??y ch???n th??ng tin kh??ch ??? gh??p ????? ch???nh s???a");
                return;
            }
            int mahdk = Integer.parseInt((String) selectedRow.get(0));

            if (handler.endRoommateStayingPeriod(mahdk)) {
                CustomAlert.showSimpleAlert(
                        "Th??nh c??ng", "???? cho kh??ch tr??? ph??ng");
                MasterController.getInstance().getListCustomerController()
                        .handleRefresh(new ActionEvent());
                handleRefresh(new ActionEvent());
            } else {
                CustomAlert.showErrorMessage(
                        "Th???t b???i",
                        "Kh??ng th??? tr??? ph??ng.\nH??y xem l???i th??ng tin v?? th??? l???i.");
            }
        } catch (IndexOutOfBoundsException ex) {
            CustomAlert.showErrorMessage(
                    "Ch??a ch???n",
                    "\nH??y ch???n th??ng tin kh??ch ??? gh??p ????? tr??? ph??ng");
        }
    }

    @FXML
    public void handleDeleteRoommate(ActionEvent e) {
        Util.checkLogin(getStage());

        if (!Setting.IS_VERIFIED) {
            return;
        }
        ObservableList selectedRow;
        try {
            selectedRow = (ObservableList) roommateTable.getSelectionModel().getSelectedItems().get(0);
            if (selectedRow == null) {
                CustomAlert.showErrorMessage(
                        "Ch??a ch???n.",
                        "H??y ch???n th??ng tin kh??ch ??? gh??p ????? x??a");
                return;
            }
            Optional<ButtonType> answer
                    = CustomAlert.confirmDialog(
                            "X??a th??ng tin kh??ch ??? gh??p",
                            "B???n c?? ch???c mu???n x??a?").showAndWait();
            if (answer.get() == ButtonType.OK) {
                if (handler.deleteRoommate(Integer.parseInt((String) selectedRow.get(0)))) {
                    CustomAlert.showSimpleAlert(
                            "X??a th??nh c??ng", "???? x??a kh??ch ??? gh??p");
                    roommateTable.getItems().remove(selectedRow);
                    MasterController.getInstance().getListCustomerController()
                            .handleRefresh(new ActionEvent());
                } else {
                    CustomAlert.showErrorMessage(
                            "Kh??ng th??? x??a", "???? c?? l???i x???y ra");
                }
            }
        } catch (IndexOutOfBoundsException ex) {
            CustomAlert.showErrorMessage(
                    "Ch??a ch???n",
                    "H??y ch???n th??ng tin kh??ch ??? gh??p ????? x??a");
        }
    }

    public void initContractTableColumns() {
        TableColumn mahdongCol
                = new TableColumn<>("M?? h???p ?????ng");
        TableColumn maKhuCol
                = new TableColumn<>("M?? khu");
        TableColumn tenKhuCol
                = new TableColumn<>("T??n khu");
        TableColumn maphongCol
                = new TableColumn<>("M?? ph??ng");
        TableColumn tenPhongCol
                = new TableColumn<>("T??n ph??ng");
        TableColumn makhCol
                = new TableColumn<>("M?? kh??ch");
        TableColumn tenkhachCol
                = new TableColumn<>("T??n kh??ch");
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
                = new TableColumn<>("S??? ng??y");

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
                = new TableColumn<>("M?? hdk");
        TableColumn mahdongCol
                = new TableColumn<>("M?? h???p ?????ng");
        TableColumn maKhuCol
                = new TableColumn<>("M?? khu");
        TableColumn tenKhuCol
                = new TableColumn<>("T??n khu");
        TableColumn maphongCol
                = new TableColumn<>("M?? ph??ng");
        TableColumn tenPhongCol
                = new TableColumn<>("T??n ph??ng");//5
        TableColumn makhCol
                = new TableColumn<>("M?? kh??ch");
        TableColumn tenkhCol
                = new TableColumn<>("Ch??? h???p ?????ng");//7
        TableColumn maRoommateCol
                = new TableColumn<>("M?? kh gh??p");
        TableColumn tenRoommateCol
                = new TableColumn<>("T??n kh??ch");//9
        TableColumn ngayNhanCol
                = new TableColumn<>("Ng??y nh???n");
        TableColumn ngayTraCol
                = new TableColumn<>("Ng??y tr???");
        TableColumn ngayVaoCol
                = new TableColumn<>("Ng??y v??o");//12
        TableColumn ngayDiCol
                = new TableColumn<>("Ng??y ??i");//13

        roommateTable.getColumns().addAll(
                idhdk, mahdongCol, maKhuCol, tenKhuCol, maphongCol, tenPhongCol,
                makhCol, tenkhCol, maRoommateCol, tenRoommateCol,
                ngayNhanCol, ngayTraCol, ngayVaoCol, ngayDiCol);

        tenPhongCol.setMinWidth(150);
        tenkhCol.setMinWidth(150);
        tenRoommateCol.setMinWidth(200);

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

        ngayVaoCol.setCellFactory(column -> {
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
        ngayDiCol.setCellFactory(column -> {
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
    private void clearFilter() {
        filterField.clear();
    }

    /**
     * set condition for contract filtered lists
     * @param f contract list to be filtered
     */
    void setContractFilterFieldProperty(FilteredList f) {
        filterField.textProperty().addListener((observable, oldValue, newValue) -> {
            f.setPredicate((Object t) -> {
                ObservableList row = (ObservableList) t;

                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }
                String filterText = newValue.toLowerCase();
                String ngaynhan = Util.DATE_TIME_FORMATTER.format(
                        Util.SQL_DATE_TIME_FORMATTER.parse(row.get(7).toString()));
                String ngaytra = Util.DATE_TIME_FORMATTER.format(
                        Util.SQL_DATE_TIME_FORMATTER.parse(row.get(8).toString()));

                return row.get(4).toString().toLowerCase().contains(filterText)
                        || row.get(6).toString().toLowerCase().contains(filterText)
                        || ngaynhan.contains(filterText)
                        || ngaytra.contains(filterText);
            });
        });
    }

    /**
     * set condition for contract filtered lists
     * @param f roommate table list to be filtered
     */
    void setRoommateFilterFieldProperty(FilteredList f) {
        filterField.textProperty().addListener((observable, oldValue, newValue) -> {
            f.setPredicate((Object t) -> {
                ObservableList row = (ObservableList) t;

                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }
                String filterText = newValue.toLowerCase();
                String ngaynhan = Util.DATE_TIME_FORMATTER.format(
                        Util.SQL_DATE_TIME_FORMATTER.parse(row.get(12).toString()));
                String ngaytra = Util.DATE_TIME_FORMATTER.format(
                        Util.SQL_DATE_TIME_FORMATTER.parse(row.get(13).toString()));
                
                return row.get(5).toString().toLowerCase().contains(filterText)
                        || row.get(7).toString().toLowerCase().contains(filterText)
                        || row.get(9).toString().toLowerCase().contains(filterText)
                        || ngaynhan.contains(filterText)
                        || ngaytra.contains(filterText);
            });
        });
    }
}
