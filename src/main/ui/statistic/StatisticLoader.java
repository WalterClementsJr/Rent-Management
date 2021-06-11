/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main.ui.statistic;

import javafx.application.Application;
import static javafx.application.Application.launch;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import main.app.settings.Setting;
import main.util.Util;

/**
 *
 * @author walker
 */
public class StatisticLoader extends Application {
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("statistic.fxml"));

        Setting.getInstance();

        Scene scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource(
                Setting.getInstance().getSTYLE_SHEET()).toExternalForm());

        stage.setScene(scene);
        stage.setTitle(Util.APP_NAME);
        stage.show();

        Util.setWindowIcon(stage);
    }
}
