package main.ui.listcontract;

import java.math.BigDecimal;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.util.ArrayList;
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
import main.model.Room;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.scene.control.TableCell;
import main.database.DatabaseHandler;
import main.model.Contract;
import main.model.Customer;
import main.ui.alert.CustomAlert;
import main.util.Util;

public class ListContractController implements Initializable {

    @FXML
    private AnchorPane root;

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

    // extra elements
    public static ObservableList listOfAllContracts = FXCollections.observableArrayList();
    public static ObservableList listOfActiveContracts = FXCollections.observableArrayList();
    public static ObservableList listOfOldContracts = FXCollections.observableArrayList();

    DatabaseHandler handler;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        handler = DatabaseHandler.getInstance();
        // TODO
        initContractTableColumns();
        loadResultSetToList(DatabaseHandler.getInstance().getAllContractsWithInfo(), listOfAllContracts);

        filter.getItems().addAll("Tất cả", "Còn hiệu lực", "Đã hết hạn");
        filter.getSelectionModel().selectFirst();

        loadData();
    }

    @FXML
    private void filterChanged(ActionEvent event) {
        loadListToTable();
    }

    private void loadListToTable() {
        switch (filter.getSelectionModel().getSelectedItem()) {
            case "Tất cả":
                contractTable.setItems(listOfAllContracts);
                break;
            case "Còn hiệu lực":
                contractTable.setItems(listOfActiveContracts);
                break;
            case "Đã hết hạn":
                contractTable.setItems(listOfOldContracts);
                break;
        }
    }

    private void loadData() {
        listOfAllContracts.clear();
        listOfActiveContracts.clear();
        listOfOldContracts.clear();

        loadAllContracts();
        loadActiveContracts();
        loadOldContracts();

        loadListToTable();
    }

    public void loadAllContracts() {
        loadResultSetToList(handler.getAllContractsWithInfo(), listOfAllContracts);
    }

    public void loadActiveContracts() {
        loadResultSetToList(handler.getActiveContractsWithInfo(), listOfActiveContracts);
    }

    public void loadOldContracts() {
        loadResultSetToList(handler.getOldContractsWithInfo(), listOfOldContracts);
    }

    @FXML
    void handleDeleteButton(ActionEvent event) {

    }

    @FXML
    void handleEditContract(ActionEvent event) {
        ObservableList list = contractTable.getSelectionModel().getSelectedItems();
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

            System.out.println(con.debugString());
        } catch (IndexOutOfBoundsException ex) {
            CustomAlert.showErrorMessage("Chưa chọn.", "Hãy chọn một hợp đồng để chỉnh sửa");
//            Logger.getLogger(ListContractController.class.getName()).log(Level.SEVERE, null, ex);
            return;
        }

    }

    @FXML
    void handleRefresh(ActionEvent event) {
        System.out.println("filter changed");
        loadData();
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
//        contractTable.setItems(list);
    }

    public void initContractTableColumns() {

        TableColumn mahdongCol
                = new TableColumn<>("Mã hợp đồng");
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

        contractTable.getColumns().addAll(
                mahdongCol, tenKhuCol, maphongCol, tenPhongCol, makhCol,
                tenkhachCol, ngayNhanCol, ngayTraCol, tienCocCol);

        tenPhongCol.setMinWidth(200);
        tenkhachCol.setMinWidth(200);
        
        mahdongCol.setVisible(false);
        maphongCol.setVisible(false);
        makhCol.setVisible(false);

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

//        idCol.setVisible(false);
//        makhuCol.setVisible(false);
//
//        moTaCol.setMinWidth(150);
    }
}
