package main.ui.main;

import java.awt.Desktop;
import java.io.File;
import javafx.event.ActionEvent;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import main.util.Util;

public class MainController implements Initializable {
    

    @FXML
    private VBox menu;

    @FXML
    private Button menuBtnHome;

    @FXML
    private Button menuBtnCustomer;

    @FXML
    private Button menuBtnRoom;

    @FXML
    private Button menuBtnPayment;

    @FXML
    private StackPane rootPane;

    
    // external Pane
    private AnchorPane lCustomer;
    private AnchorPane lRoom;

    private Scene lCustomerScene;
    private Scene lRoomScene;
    private Scene scene;
    
    
    private Stage getStage() {
        return (Stage) rootPane.getScene().getWindow();
    }
        
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // load external panes
        lCustomer = Util.loadPane(getClass().getResource("/main/ui/listcustomer/listCustomer.fxml"));
        lRoom = Util.loadPane(getClass().getResource("/main/ui/listroom/listRoom.fxml"));

        rootPane.getChildren().addAll(lCustomer, lRoom);
        showNode(rootPane.getChildren(), lCustomer);
    }
    
    @FXML
    private void loadListCustomer(ActionEvent event) throws IOException {
        showNode(rootPane.getChildren(), lCustomer);
        System.out.println("listCustomer");

    }

    @FXML
    private void loadListRoom(ActionEvent event) {
        showNode(rootPane.getChildren(), lRoom);
        System.out.println("listroom");
    }
    
    @FXML
    private void handleMenuFullScreen(ActionEvent event) {
        Stage stage = getStage();
        stage.setFullScreen(!stage.isFullScreen());
    }

    @FXML
    private void runCalculator(ActionEvent event) {
        try {
            Desktop.getDesktop().open(new File("./src/main/util/Calculator/calc.html"));
        } catch (IOException ex) {
            Logger.getLogger(MainController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    /**
     * 
     * @param nodes
     * @param nodeToShow
     * chuyển pane sang nodeToShow
     */
    private static void showNode(List<Node> nodes, Node nodeToShow) {
        nodes.forEach(node -> {
            if (node.equals(nodeToShow)) {
                node.setVisible(true);
            } else {
                node.setVisible(false);
            }
        });
    }
}
