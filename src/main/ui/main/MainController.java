package main.ui.main;

import javafx.event.ActionEvent;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import main.util.Util;

public class MainController implements Initializable {
    
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
    private Stage getStage() {
        return (Stage) rootPane.getScene().getWindow();
    }
    
    @FXML
    private void loadListRoom(ActionEvent event) {
        showNode(rootPane.getChildren(), lRoom);
        System.out.println("listroom");
    }
    
    @FXML
    private void handleMenuClose(ActionEvent event) {
        getStage().close();
    }



    @FXML
    private void loadAddRoomWindow(ActionEvent event) {
        Util.loadWindow(getClass().getResource(
                "/main/ui/addroom/addRoom.fxml"),
                "Add New Room", null);
    }

    @FXML
    private void handleMenuFullScreen(ActionEvent event) {
        Stage stage = getStage();
        stage.setFullScreen(!stage.isFullScreen());
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
