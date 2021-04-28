package main.ui.addApartmentComplex;

import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import main.database.DatabaseHandler;
import main.model.Complex;
import main.ui.listcustomer.ListCustomerController;

public class AddApartmentComplexController implements Initializable {

    @FXML
    private ComboBox<Complex> comboBox;

    @FXML
    private TextArea name;

    @FXML
    private TextArea address;

    @FXML
    private Button save;

    @FXML
    private Button cancel;

    
    // extra elements
    ObservableList<Complex> list = FXCollections.observableArrayList();
    DatabaseHandler dbHandler;
    
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        dbHandler = DatabaseHandler.getInstance();
        loadData();
    }
    
    @FXML
    private void loadData() {
        list.clear();
        
        String query = "SELECT * FROM KHU";
        ResultSet rs = dbHandler.execQuery(query);

        try {
            while (rs.next()) {
                int id = rs.getInt("MAKHU");
                String ten = rs.getString("TENKHU");
                String diaChi = rs.getString("DIACHI");
                
                System.out.println(id + ten + diaChi);

                list.add(new Complex(id, ten, diaChi));

            }
        } catch (SQLException ex) {
            Logger.getLogger(ListCustomerController.class.getName()).log(Level.SEVERE, null, ex);
        }

        comboBox.setItems(list);
    }
}
