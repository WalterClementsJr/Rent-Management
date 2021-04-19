package main.ui.listcustomer;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableView;
import javafx.stage.Stage;
import main.ui.addcustomer.AddCustomerController;
import main.ui.main.MainController;
import main.util.Util;

public class ListCustomerController implements Initializable {

    
    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private TableView<?> tableView;

    @FXML
    private MenuItem edit;

    @FXML
    private MenuItem delete;

    @FXML
    private Button btnEdit;

    @FXML
    private Button btnAdd;

    @FXML
    private Button btnDelete;

    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }    
    
    @FXML
    private void loadAddCustomerWindow(ActionEvent event) {
        System.out.println("load add customer window");
        Util.loadWindow(getClass().getResource(
                "/main/ui/addcustomer/addCustomer.fxml"),
                "Add New Customer", null);
    }
    
    @FXML
    void loadEditCustomerWindow(ActionEvent event) {
        System.out.println("editing customer");

//        Customer selectedForEdit = tableView.getSelectionModel().getSelectedItem();
//        if (selectedForEdit == null) {
//            AlertMaker.showErrorMessage("No book selected", "Please select a book for edit.");
//            return;
//        }
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/main/ui/addcustomer/addCustomer.fxml"));
            Parent parent = (Parent) loader.load();

            AddCustomerController controller = (AddCustomerController) loader.getController();
//            controller.inflateUI(selectedForEdit);

//            Stage stage = new Stage(StageStyle.DECORATED);
            Stage stage = new Stage();

            stage.setTitle("Edit Customer");
            stage.setScene(new Scene(parent));
            stage.show();
//            Util.setStageIcon(stage);

//            stage.setOnHiding((e) -> {
//                handleRefresh(new ActionEvent());
//            });

        } catch (IOException ex) {
            Logger.getLogger(MainController.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("cannot load edit window");
        }
    }
    
    @FXML
    private void handleEditButton(ActionEvent event) {
        System.out.println("editing in list customer");
    }
}
