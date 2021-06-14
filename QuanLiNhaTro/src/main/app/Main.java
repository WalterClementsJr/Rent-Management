package main.app;

import java.util.Locale;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import main.app.settings.Setting;
import main.database.DatabaseHandler;
import main.util.Util;

public class Main extends Application {
//    -Djava.library.path=javafx\bin --module-path "library\javafx\lib" --add-modules=ALL-MODULE-PATH --add-modules javafx.controls,javafx.fxml
//    java -jar --module-path C:\Users\walker\Documents\NetBeansProjects\QuanLiNhaTro\library\javafx\lib --add-modules=javafx.controls,javafx.fxml QuanLiNhaTro.jar
//    java -jar -Djava.library.path=library\javafx\bin --module-path C:\Users\walker\Documents\NetBeansProjects\QuanLiNhaTro\library\javafx\lib --add-modules=javafx.controls,javafx.fxml,javafx.base,javafx.web --add-exports javafx.graphics/com.sun.javafx.sg.prism=ALL-UNNAMED
//    -Djava.library.path=library\javafx\bin --module-path C:\Users\walker\Documents\NetBeansProjects\QuanLiNhaTro\library\javafx\lib --add-modules=javafx.controls,javafx.fxml,javafx.base,javafx.web --add-exports javafx.graphics/com.sun.javafx.sg.prism=ALL-UNNAMED

    // jar file commandline
//    java -jar --module-path C:\Users\walker\Documents\NetBeansProjects\QuanLiNhaTro\library\javafx\lib --add-modules=javafx.controls,javafx.fxml QuanLiNhaTro.jar
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        // set datepicker's language to Vietnamese
        Locale.setDefault(new Locale("vi", "VN"));
        Setting s = Setting.getInstance();

        Parent root = FXMLLoader.load(
                getClass().getResource("/main/ui/main/main.fxml"));

        Scene scene = new Scene(root);
        scene.getStylesheets().add(
                getClass().getResource(s.getSTYLE_SHEET()).toExternalForm());

        stage.setScene(scene);
        stage.setTitle(Util.APP_NAME);
        stage.show();

        Util.setWindowIcon(stage);
        stage.setResizable(false);

        DatabaseHandler.getInstance();
    }

}
