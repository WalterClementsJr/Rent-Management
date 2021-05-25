package main.ui.addroomate;

import javafx.application.Application;
import static javafx.application.Application.launch;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import main.util.Util;


public class AddRoommateLoader extends Application{
    public static void main(String[] args) {
        launch(args);
    }
    
    @Override
    public void start(Stage stage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("addRoommate.fxml"));

        Scene scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource("/main/app/bootstrap3.css").toExternalForm());
        
        stage.setScene(scene);
        stage.setTitle(Util.APP_NAME);
        stage.show();

        Util.setWindowIcon(stage);
    }
}
