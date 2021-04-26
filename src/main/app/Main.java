package main.app;


import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import main.util.Util;

public class Main extends Application {

    public static void main(String[] args) {
        launch(args);
    }
    
    @Override
    public void start(Stage stage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/main/ui/main/main.fxml"));

        Scene scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource("bootstrap3.css").toExternalForm());
        
        stage.setScene(scene);
        stage.setTitle(Util.APP_NAME);
        stage.show();

        Util.setWindowIcon(stage);
        
        System.out.println(getClass().getResource("bootstrap3.css").toExternalForm());

        new Thread(() -> {
        }).start();
    }
    
}
