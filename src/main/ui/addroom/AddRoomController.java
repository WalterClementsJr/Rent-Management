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
                if (!newValue.matches("\\d{0,10}")) {
                    price.setText(oldValue);
                    price.positionCaret(price.getLength());
                }
            }
        });

        name.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue,
                    String newValue) {
                if (!newValue.matches(".{0,50}")) {
                    name.setText(oldValue);
                    name.positionCaret(name.getLength());
                }
            }
        });

        size.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue,
                    String newValue) {
                if (!newValue.matches("\\d{0,9}")) {
                    size.setText(oldValue);
                    size.positionCaret(size.getLength());
                }
            }
        });

        desc.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue,
                    String newValue) {
                if (!newValue.matches("^[\\n\\s\\S\\w\\W\\d\\D]{0,200}$")) {
                    desc.setText(oldValue);
                    desc.positionCaret(desc.getLength());
                }
            }
        });

        deposit.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue,
                    String newValue) {
                if (!newValue.matches("\\d{0,10}")) {
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

        ResultSet rs = dbHandler.selectAllComplex();
        try {
            while (rs.next()) {
                int id = rs.getInt("MAKHU");
                String ten = rs.getString("TENKHU");
                String diaChi = rs.getString("DIACHI");
                list.add(new Complex(id, ten, diaChi));
            }
            rs.close();
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
        String rDescript
                = !desc.getText().isBlank()
                ? desc.getText().trim()
                : "";

        Room newRoom = new Room(rName, rNOfPeople, rPrice, rDeposit, rSize, rDescript, chosenComplex.getId());

        if (dbHandler.isRoomNameExist(-1, chosenComplex.getId(), rName)) {
            CustomAlert.showSimpleAlert(
                    "L???i",
                    "Ph??ng v???i t??n n??y ???? t???n t???i trong khu " + chosenComplex.getTen());
            return;
        }

        if (dbHandler.insertNewRoom(newRoom)) {
            CustomAlert.showSimpleAlert("Th??nh c??ng", "???? th??m ph??ng");
            getStage().close();
        } else {
            CustomAlert.showErrorMessage(
                    "L???i",
                    "Kh??ng th??? th??m ph??ng.\nH??y xem l???i th??ng tin v?? th??? l???i.");
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
        String rDescript
                = !desc.getText().isBlank()
                ? desc.getText().trim()
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
                    "L???i",
                    "T??n ph??ng ???? t???n t???i trong khu " + chosenComplex.getTen());
            return;
        }

        Optional<ButtonType> answer
                = CustomAlert.confirmDialog(
                        "Ch???nh s???a ph??ng",
                        "X??c nh???n ch???nh s???a?").showAndWait();
        if (answer.get() == ButtonType.OK) {
            if (dbHandler.updateRoom(currentRoom)) {
                CustomAlert.showSimpleAlert(
                        "Th??nh c??ng",
                        "???? s???a th??ng tin ph??ng");
                currentRoom = null;
                getStage().close();
            } else {
                CustomAlert.showErrorMessage(
                        "Ch???nh s???a th???t b???i",
                        "Ki???m tra l???i th??ng tin v?? th??? l???i sau");
            }
        }

    }

    private Stage getStage() {
        return (Stage) root.getScene().getWindow();
    }

    private boolean checkEntries() {
        if (comboBox.getValue() == null) {
            CustomAlert.showErrorMessage("Ch??a ch???n khu nh??", "");
            return false;
        } else if (name.getText().isBlank()) {
            CustomAlert.showErrorMessage("T??n ph??ng tr???ng", "H??y nh???p t??n ph??ng");
            return false;
        } else if (nOfPeople.getValue() == null) {
            CustomAlert.showErrorMessage("S??? ng?????i tr???ng", "H??y nh???p s??? ng?????i ??? t???i ??a");
            return false;
        } else if (price.getText().isBlank()) {
            CustomAlert.showErrorMessage("Gi?? ph??ng tr???ng", "H??y nh???p l???i");
            return false;
        } else if (deposit.getText().isBlank()) {
            CustomAlert.showErrorMessage("Ti???n c???c tr???ng", "H??y nh???p l???i");
            return false;
        } else if (size.getText().isBlank()) {
            CustomAlert.showErrorMessage("Di???n t??ch tr???ng", "H??y nh???p l???i");
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
