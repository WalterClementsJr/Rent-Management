package main.ui.addroom;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.beans.value.ChangeListener;
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
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import main.app.settings.Setting;
import main.database.DatabaseHandler;
import main.model.Complex;
import main.model.Room;
import main.ui.addcomplex.AddComplexController;
import main.ui.alert.CustomAlert;
import main.util.Util;

public class AddRoomController implements Initializable {

    @FXML
    private StackPane root;

    @FXML
    private ComboBox<Complex> comboBox;

    @FXML
    private TextField name;

    @FXML
    private Spinner<Integer> nOfPeople;

    @FXML
    private TextField price;

    @FXML
    private TextField size;

    @FXML
    private TextField deposit;

    @FXML
    private TextArea desc;

    @FXML
    private Button save;

    @FXML
    private Button cancel;

    // extra elements
    ObservableList<Complex> list = FXCollections.observableArrayList();
    DatabaseHandler dbHandler;
    Room currentRoom = null;
    private boolean isEditing = false;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // setup UI elements
        nOfPeople.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 1000, 1));

        price.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue,
                    String newValue) {
                if (newValue.matches("\\d{0,15}")) {
                    String value = newValue;
                } else {
                    price.setText(oldValue);
                    price.positionCaret(price.getLength());
                }
            }
        });

        size.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue,
                    String newValue) {
                if (newValue.matches("\\d{0,9}")) {
                    String value = newValue;
                } else {
                    size.setText(oldValue);
                    size.positionCaret(size.getLength());
                }
            }
        });

        deposit.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue,
                    String newValue) {
                if (newValue.matches("\\d{0,15}")) {
                    String value = newValue;
                } else {
                    deposit.setText(oldValue);
                    deposit.positionCaret(deposit.getLength());
                }
            }
        });

        dbHandler = DatabaseHandler.getInstance();
        loadData();
    }

    private void loadData() {
        list.clear();
        comboBox.getItems().clear();

        String query = "SELECT * FROM KHU";
        ResultSet rs = dbHandler.execQuery(query);

        try {
            while (rs.next()) {
                int id = rs.getInt("MAKHU");
                String ten = rs.getString("TENKHU");
                String diaChi = rs.getString("DIACHI");
                list.add(new Complex(id, ten, diaChi));
            }
        } catch (SQLException ex) {
            Logger.getLogger(AddRoomController.class.getName()).log(Level.SEVERE, null, ex);
        }
        comboBox.getItems().addAll(list);
    }

    @FXML
    private void handleCancel(ActionEvent evt) {
        getStage().close();
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
            scene.getStylesheets().add(getClass().getResource(Setting.getInstance().getSTYLE_SHEET()).toString());

            stage.setScene(scene);
            stage.setTitle("Chỉnh sửa khu nhà");
            stage.show();
            Util.setWindowIcon(stage);

            stage.setOnHiding((e) -> {
                handleRefresh(new ActionEvent());
            });
        } catch (IOException ex) {
            Logger.getLogger(AddRoomController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @FXML
    private void handleRefresh(ActionEvent event) {
        loadData();
    }

    @FXML
    private void handleAddRoom(ActionEvent evt) {
        Util.checkLogin(getStage());

        if (!Setting.IS_VERIFIED) {
            return;
        }
        
        if (isEditing) {
            handleEditRoom();
            return;
        }

        if (!checkEntries()) {
            return;
        }

        Complex chosenComplex = comboBox.getSelectionModel().getSelectedItem();
        String rName = name.getText().trim();
        int rNOfPeople = nOfPeople.getValue();
        BigDecimal rPrice = new BigDecimal(price.getText().trim());
        BigDecimal rDeposit = new BigDecimal(deposit.getText().trim());
        int rSize = Integer.parseInt(size.getText().trim());
        String rDescript =
                !desc.getText().isBlank() ?
                desc.getText().trim()
                : "";

        Room newRoom = new Room(rName, rNOfPeople, rPrice, rDeposit, rSize, rDescript, chosenComplex.getId());

        if (dbHandler.isRoomNameExist(-1, chosenComplex.getId(), rName)) {
            CustomAlert.showSimpleAlert(
                    "Lỗi",
                    "Phòng với tên này đã tồn tại trong khu " + chosenComplex.getTen());
            return;
        }

        if (dbHandler.insertNewRoom(newRoom)) {
            CustomAlert.showSimpleAlert("Thành công", "Đã thêm phòng");
            getStage().close();
        } else {
            CustomAlert.showErrorMessage(
                    "Lỗi",
                    "Không thể thêm phòng.\nHãy xem lại thông tin và thử lại.");
        }
    }

    private void handleEditRoom() {
        Util.checkLogin(getStage());

        if (!Setting.IS_VERIFIED) {
            return;
        }
        
        if (!checkEntries()) {
            return;
        }

        Complex chosenComplex = comboBox.getSelectionModel().getSelectedItem();
        String rName = name.getText().trim();
        int rNOfPeople = nOfPeople.getValue();
        BigDecimal rPrice = new BigDecimal(price.getText().trim());
        BigDecimal rDeposit = new BigDecimal(deposit.getText().trim());
        int rSize = Integer.parseInt(size.getText().trim());
        String rDescript =
                !desc.getText().isBlank() ?
                desc.getText().trim()
                : "";

        currentRoom.setTenPhong(rName);
        currentRoom.setSoNguoi(rNOfPeople);
        currentRoom.setGiaGoc(rPrice);
        currentRoom.setTienCoc(rDeposit);
        currentRoom.setDienTich(rSize);
        currentRoom.setMoTa(rDescript);
        currentRoom.setMaKhu(chosenComplex.getId());

        if (dbHandler.isRoomNameExist(currentRoom.getId(), chosenComplex.getId(), rName)) {
            CustomAlert.showSimpleAlert(
                    "Lỗi",
                    "Tên phòng đã tồn tại trong khu " + chosenComplex.getTen());
            return;
        }

        Optional<ButtonType> answer
                = CustomAlert.confirmDialog(
                        "Chỉnh sửa phòng",
                        "Xác nhận chỉnh sửa?").showAndWait();
        if (answer.get() == ButtonType.OK) {
            if (dbHandler.updateRoom(currentRoom)) {
                CustomAlert.showSimpleAlert(
                        "Thành công",
                        "Đã sửa thông tin phòng");
                currentRoom = null;
                getStage().close();
            } else {
                CustomAlert.showErrorMessage(
                        "Chỉnh sửa thất bại",
                        "Kiểm tra lại thông tin và thử lại sau");
            }
        }

    }

    private Stage getStage() {
        return (Stage) root.getScene().getWindow();
    }

    private boolean checkEntries() {
        if (comboBox.getValue() == null) {
            CustomAlert.showErrorMessage("Chưa chọn khu nhà", "");
            return false;
        } else if (name.getText().isBlank()) {
            CustomAlert.showErrorMessage("Tên phòng trống", "Hãy nhập tên phòng");
            return false;
        } else if (nOfPeople.getValue() == null) {
            CustomAlert.showErrorMessage("Số người trống", "Hãy nhập số người ở tối đa");
            return false;
        } else if (price.getText().isBlank()) {
            CustomAlert.showErrorMessage("Giá phòng trống", "");
            return false;
        } else if (deposit.getText().isBlank()) {
            CustomAlert.showErrorMessage("Tiền cọc trống", "");
            return false;
        } else if (size.getText().isBlank()) {
            CustomAlert.showErrorMessage("Diện tích trống", "");
            return false;
        }
        return true;
    }

    public void loadEntries(Room r) {
        for (Complex c : list) {
            if (r.getMaKhu() == c.getId()) {
                comboBox.setValue(c);
            }
        }

        name.setText(r.getTenPhong().trim());
        nOfPeople.getValueFactory().setValue(r.getSoNguoi());
        size.setText("" + r.getDienTich());
        deposit.setText(r.getTienCoc().stripTrailingZeros().toPlainString());
        price.setText(r.getGiaGoc().stripTrailingZeros().toPlainString());
        desc.setText(r.getMoTa());

        isEditing = true;
        currentRoom = r;
    }
}
