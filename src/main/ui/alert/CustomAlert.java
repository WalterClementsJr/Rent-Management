package main.ui.alert;

import java.awt.AWTException;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.TrayIcon.MessageType;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.DialogPane;
import javafx.stage.Stage;
import javax.imageio.ImageIO;
import main.app.Main;
import main.app.settings.Setting;
import main.util.Util;

public class CustomAlert {

    public static void showSimpleAlert(String title, String content) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        setStyleForAlert(alert);
        alert.showAndWait();
    }

    public static Alert confirmDialog(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        CustomAlert.setStyleForAlert(alert);
        alert.setTitle(title);
        alert.setContentText(content);
        setStyleForAlert(alert);
        return alert;
    }

    public static void showErrorMessage(String title, String content) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle("Lỗi");
        alert.setHeaderText(title);
        alert.setContentText(content);
        setStyleForAlert(alert);
        alert.showAndWait();
    }

    public static void showTrayMessage(String title, String message) {
        try {
            SystemTray tray = SystemTray.getSystemTray();
            BufferedImage image = ImageIO.read(Alert.class.getResource(Util.APP_ICON_LOCATION));
            TrayIcon trayIcon = new TrayIcon(image, Util.APP_NAME);
            trayIcon.setImageAutoSize(true);
            trayIcon.setToolTip(Util.APP_NAME);
            tray.add(trayIcon);
            trayIcon.displayMessage(title, message, MessageType.INFO);
            tray.remove(trayIcon);
        } catch (AWTException | IOException exp) {
            exp.printStackTrace(System.out);
        }
    }

    public static void setStyleForAlert(Alert alert) {
        Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
        Util.setWindowIcon(stage);

        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.getStylesheets().add(Main.class.getResource(Setting.getInstance().getSTYLE_SHEET()).toExternalForm());
    }
}
