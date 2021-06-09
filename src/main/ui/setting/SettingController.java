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
                    notify.setText("Mật khẩu mạnh");
                } else if (newValue.matches("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=\\S+$).{8,}$")) {
                    passwordBar.setProgress(0.6);
                    passwordBar.getStyleClass().clear();
                    passwordBar.getStyleClass().add("progress-bar");
                    passwordBar.getStyleClass().add("warning");
                    notify.setText("Mật khẩu chưa đủ mạnh");
                } else if (!newValue.isBlank()) {
                    passwordBar.setProgress(0.25);
                    passwordBar.getStyleClass().clear();
                    passwordBar.getStyleClass().add("progress-bar");
                    passwordBar.getStyleClass().add("danger");
                    notify.setText("Mật khẩu yếu");
                }
            }
        });
    }

    private boolean checkEntries() {
        if (setting.checkPassword(currentPwd.getText())) {
            if (newPwd1.getText().isBlank()) {
                CustomAlert.showErrorMessage(
                        "Mật khẩu trống",
                        "Hãy nhập mật khẩu khác");
            } else if (newPwd1.getText().equals(newPwd2.getText())) {
                if (setting.checkPassword(newPwd1.getText())) {
                    CustomAlert.showErrorMessage(
                            "Mật khẩu mới trùng mật khẩu cũ",
                            "Hãy nhập mật khẩu khác");
                    clearEntries();
                    return false;
                } else {
                    return true;
                }
            } else {
                CustomAlert.showErrorMessage(
                        "Mật khẩu mới không trùng khớp",
                        "Hãy nhập lại");
                clearEntries();
                return false;
            }
        } else {
            CustomAlert.showErrorMessage(
                    "Mật khẩu không chính xác",
                    "Hãy nhập lại mật khẩu");
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
        CustomAlert.showSimpleAlert("Thành công", "Đã đổi mật khẩu");
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
