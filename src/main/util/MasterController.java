package main.util;

import javafx.event.ActionEvent;
import javax.swing.plaf.RootPaneUI;
import main.ui.listcontract.ListContractController;
import main.ui.listcustomer.ListCustomerController;
import main.ui.listmaintenance.ListMaintenanceController;
import main.ui.listroom.ListRoomController;
import main.ui.main.MainController;

public final class MasterController {

    private static MasterController masterController = null;

    private MainController mainController = null;
    private ListCustomerController listCustomerController = null;
    private ListRoomController listRoomController = null;
    private ListContractController listContractController = null;
    private ListMaintenanceController listMaintenanceController = null;

    public MainController getMainController() {
        return mainController;
    }

    public ListCustomerController getListCustomerController() {
        return listCustomerController;
    }

    public ListRoomController getListRoomController() {
        return listRoomController;
    }

    public ListContractController getListContractController() {
        return listContractController;
    }

    public ListMaintenanceController getListMaintenanceController() {
        return listMaintenanceController;
    }

    public static MasterController getInstance() {
        if (masterController == null) {
            masterController = new MasterController();
        }
        return masterController;
    }

    public void registerMainController(MainController con) {
        this.mainController = con;
    }

    public void registerListCustomerController(ListCustomerController con) {
        this.listCustomerController = con;
    }

    public void registerListRoomController(ListRoomController con) {
        this.listRoomController = con;
    }

    public void registerListContractController(ListContractController con) {
        this.listContractController = con;
    }

    public void ListContractControllerRefresh() {
        listContractController.handleRefresh(new ActionEvent());
    }

    public void showListCustomer() {
        mainController.showNode(mainController.getRootPane().getChildren(),
                mainController.getlCustomer());
    }

}
