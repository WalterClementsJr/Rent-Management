/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main.ui.login;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import main.app.settings.Setting;
import main.ui.alert.CustomAlert;

/**
 * FXML Controller class
 *
 * @author walker
 */
public class LoginController implements Initializable {

    @FXML
    private Label icon;
    @FXML
    private AnchorPane root;
    @FXML
    private PasswordField password;
    @FXML
    private Button cancel;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // add enter listener for password field
        password.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                handleVerify(new ActionEvent());
            }
        });
    }

    @FXML
    private void handleVerify(ActionEvent event) {
        if (Setting.getInstance().checkPassword(password.getText())) {
            Setting.IS_VERIFIED = true;
            getStage().close();
        } else {
            CustomAlert.showErrorMessage(
                    "Sai mật khẩu",
                    "Hãy nhập lại mật khẩu");
            password.clear();
        }
    }

    @FXML
    private void handleCancel(ActionEvent event) {
        getStage().close();
    }

    private Stage getStage() {
        return (Stage) root.getScene().getWindow();
    }
}
