package main.ui.listroom;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;


public class ListRoomController implements Initializable {

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }    
    
    @FXML
    private void handleEditButton(ActionEvent event) {
        System.out.println("Editing in list room");
    }
    
    @FXML
    private void handleDeleteButton(ActionEvent event) {
        System.out.println("delet in list room");
    }
    
    @FXML
    private void handleAddButton(ActionEvent event) {
        System.out.println("add in list room");
    }

}
