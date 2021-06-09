package main.ui.Calculator;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;

public class CalculatorController implements Initializable {

    @FXML
    private WebView webView;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        WebEngine engine = webView.getEngine();
//        File cal = new File("./src/main/util/Calculator/calc.html");
        engine.setJavaScriptEnabled(true);
//        engine.load(cal.toURI().toString());
        engine.load("https://testdrive-archive.azurewebsites.net/HTML5/OfflineCalculator/Default.html");
    }

}
