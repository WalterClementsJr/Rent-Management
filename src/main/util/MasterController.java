package main.util;

import javax.swing.plaf.RootPaneUI;
import main.ui.listcontract.ListContractController;
import main.ui.listcustomer.ListCustomerController;
import main.ui.listroom.ListRoomController;
import main.ui.main.MainController;

public final class MasterController {
    private static MasterController masterController = null;
    
    private  MainController mainController = null;
    private  ListCustomerController listCustomerController = null;
    private  ListRoomController listRoomController = null;
    private  ListContractController listContractController = null;
    
    public MasterController() {
        
    }
    
    public static MasterController getInstance() {
        if (masterController == null) masterController = new MasterController();
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
    
    public void showListCustomer() {
        mainController.showNode(mainController.getRootPane().getChildren(),
                mainController.getlCustomer());
    }
    
    
}
