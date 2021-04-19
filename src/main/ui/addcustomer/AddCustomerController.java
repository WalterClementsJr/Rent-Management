/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main.ui.addcustomer;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.StackPane;
import main.model.Customer;
/**
 * FXML Controller class
 *
 * @author walker
 */
public class AddCustomerController implements Initializable {
    
    @FXML
    private StackPane rootPane;

    @FXML
    private TextField name;

    @FXML
    private RadioButton btnMale;

    @FXML
    private RadioButton btnFemale;

    @FXML
    private TextField sdt;

    @FXML
    private TextField cmnd;

    @FXML
    private DatePicker date;

    @FXML
    private Button save;

    @FXML
    private Button cancel;

    private boolean isEditing = false;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        ToggleGroup toggleGroup = new ToggleGroup();

        btnMale.setToggleGroup(toggleGroup);
        btnFemale.setToggleGroup(toggleGroup);
    }    
    
//    public void loadUI(Customer c) {
//        name.setText(c.getHoTen());
//        id.setText(c.getId());
//        author.setText(c.getAuthor());
//        publisher.setText(c.getPublisher());
//        id.setEditable(false);
//        isEditing = true;
//    }
}
