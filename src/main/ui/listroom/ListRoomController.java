package main.ui.listroom;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;


public class ListRoomController implements Initializable {

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
    
    @FXML
    private void handleRefresh(ActionEvent event) {
        loadData();
    }
    
    /**
     * 
     * @param event 
     * TODO remove this later
     */
    @FXML
    private void doSomething(ActionEvent event) {
        System.out.println("Doing something");
    }
    
    private void loadData() {
        System.out.println("Load data");
    }

}
