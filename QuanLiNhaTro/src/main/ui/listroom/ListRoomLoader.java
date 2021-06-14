package main.ui.listroom;

import javafx.application.Application;
import static javafx.application.Application.launch;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import main.app.settings.Setting;
import main.util.Util;

public class ListRoomLoader extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("listRoom.fxml"));

        Scene scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource(Setting.getInstance().getSTYLE_SHEET()).toExternalForm());

        stage.setScene(scene);
        stage.setTitle(Util.APP_NAME);
        stage.show();

        Util.setWindowIcon(stage);
    }

}
