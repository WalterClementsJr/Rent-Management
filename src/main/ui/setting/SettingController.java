package main.ui.setting;

import java.net.URL;
import java.util.Arrays;
import java.util.ResourceBundle;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import main.app.settings.Setting;
import main.ui.alert.CustomAlert;
import main.util.Util;
import main.util.Util.Themes;

public class SettingController implements Initializable {

    @FXML
    private AnchorPane root;

    @FXML
    private PasswordField currentPwd;

    @FXML
    private PasswordField newPwd1;

    @FXML
    private PasswordField newPwd2;

    @FXML
    private ProgressBar passwordBar;

    @FXML
    private Label notify;

    @FXML
    private Button ok;

    @FXML
    private ComboBox<Themes> themeComboBox;

    // extras
    Setting setting;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        setting = Setting.getInstance();

        Themes[] themes = Util.Themes.values();

        themeComboBox.getItems().addAll(Arrays.asList(themes));
        themeComboBox.getSelectionModel().selectFirst();
        setComboBox(setting.getSTYLE_SHEET());

        // password strength dometer
        newPwd1.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(
                    ObservableValue<? extends String> observable,
                    String oldValue,
                    String newValue) {
                if (newValue.matches("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}$")) {
                    passwordBar.setProgress(0.99);
                    passwordBar.getStyleClass().clear();
                    passwordBar.getStyleClass().add("progress-bar");
                    passwordBar.getStyleClass().add("success");
                    notify.setText("M???t kh???u m???nh");
                } else if (newValue.matches("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=\\S+$).{8,}$")) {
                    passwordBar.setProgress(0.6);
                    passwordBar.getStyleClass().clear();
                    passwordBar.getStyleClass().add("progress-bar");
                    passwordBar.getStyleClass().add("warning");
                    notify.setText("M???t kh???u ch??a ????? m???nh");
                } else if (!newValue.isBlank()) {
                    passwordBar.setProgress(0.25);
                    passwordBar.getStyleClass().clear();
                    passwordBar.getStyleClass().add("progress-bar");
                    passwordBar.getStyleClass().add("danger");
                    notify.setText("M???t kh???u y???u");
                }
            }
        });
    }

    private boolean checkEntries() {
        if (setting.checkPassword(currentPwd.getText())) {
            if (newPwd1.getText().isBlank()) {
                CustomAlert.showErrorMessage(
                        "M???t kh???u tr???ng",
                        "H??y nh???p m???t kh???u kh??c");
            } else if (newPwd1.getText().equals(newPwd2.getText())) {
                if (setting.checkPassword(newPwd1.getText())) {
                    CustomAlert.showErrorMessage(
                            "M???t kh???u m???i tr??ng m???t kh???u c??",
                            "H??y nh???p m???t kh???u kh??c");
                    clearEntries();
                    return false;
                } else {
                    return true;
                }
            } else {
                CustomAlert.showErrorMessage(
                        "M???t kh???u m???i kh??ng tr??ng kh???p",
                        "H??y nh???p l???i");
                clearEntries();
                return false;
            }
        } else {
            CustomAlert.showErrorMessage(
                    "M???t kh???u kh??ng ch??nh x??c",
                    "H??y nh???p l???i m???t kh???u");
            clearEntries();
        }
        return false;
    }

    private void clearEntries() {
        currentPwd.clear();
        newPwd1.clear();
        newPwd2.clear();
    }

    @FXML
    private void handleSave(ActionEvent e) {
        if (!checkEntries()) {
            return;
        }
        setting.setPassword(newPwd1.getText());
        clearEntries();
        CustomAlert.showSimpleAlert("Th??nh c??ng", "???? ?????i m???t kh???u");
    }

    private void setComboBox(String styleSheet) {
        for (Themes t : Themes.values()) {
            if (t.getLocation().equals(styleSheet)) {
                themeComboBox.getSelectionModel()
                        .select(t);
            }
        }
    }

    @FXML
    private void themeChanged(ActionEvent event) {
        setting.setSTYLE_SHEET(
                themeComboBox.getSelectionModel()
                        .getSelectedItem().getLocation());
    }

    private Stage getStage() {
        return (Stage) root.getScene().getWindow();
    }
}
