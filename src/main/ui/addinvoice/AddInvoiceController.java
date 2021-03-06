package main.ui.addinvoice;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.DateCell;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import main.database.DatabaseHandler;
import main.model.Invoice;
import main.model.InvoiceData;
import main.ui.alert.CustomAlert;
import main.ui.listinvoice.ListInvoiceController;
import main.ui.main.MainController;
import main.util.MasterController;
import main.util.Util;

public class AddInvoiceController implements Initializable {

    @FXML
    private AnchorPane root;
    @FXML
    private DatePicker startDate;
    @FXML
    private DatePicker endDate;
    @FXML
    private TextField rent;
    @FXML
    private Button save;
    @FXML
    private Button cancel;
    @FXML
    private Button calc;

    InvoiceData currentdata = null;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        startDate.setConverter(new StringConverter<LocalDate>() {
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("d-M-yyyy");

            {
                startDate.setPromptText("".toLowerCase());
            }

            @Override
            public String toString(LocalDate date) {
                if (date != null) {
                    return dateFormatter.format(date);
                } else {
                    return "";
                }
            }

            @Override
            public LocalDate fromString(String string) {
                if (string != null && !string.isEmpty()) {
                    return LocalDate.parse(string, dateFormatter);
                } else {
                    return null;
                }
            }
        });
        endDate.setConverter(new StringConverter<LocalDate>() {
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("d-M-yyyy");

            {
                endDate.setPromptText("".toLowerCase());
            }

            @Override
            public String toString(LocalDate date) {
                if (date != null) {
                    return dateFormatter.format(date);
                } else {
                    return "";
                }
            }

            @Override
            public LocalDate fromString(String string) {
                if (string != null && !string.isEmpty()) {
                    return LocalDate.parse(string, dateFormatter);
                } else {
                    return null;
                }
            }
        });

        // deposit field format
        rent.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue,
                    String newValue) {
                if (!newValue.matches("\\d{0,10}")) {
                    rent.setText(oldValue);
                    rent.positionCaret(rent.getLength());
                }
            }
        });

        Image editimg
                = new Image(MainController.class.getResourceAsStream(
                        "/main/resources/icons/calculator.png"));
        calc.setGraphic(new ImageView(editimg));
    }

    @FXML
    private void handleAdd(ActionEvent event) {
        if (!checkEntries()) {
            return;
        }

        Invoice newInvoice = new Invoice(
                currentdata.getMahdong(),
                new BigDecimal(rent.getText()),
                endDate.getValue());

        if (DatabaseHandler.getInstance().insertNewInvoice(newInvoice)) {
            CustomAlert.showSimpleAlert(
                    "Th??nh c??ng", "???? th??m h??a ????n");
            MasterController.getInstance().getListInvoiceController().handleRefresh(new ActionEvent());
            getStage().close();
        } else {
            CustomAlert.showErrorMessage(
                    "Th???t b???i",
                    "H??y ki???m l???i tra th??ng tin v?? th??? l???i");
        }
    }

    @FXML
    private void handleCancel(ActionEvent event) {
        getStage().close();
    }

    private Stage getStage() {
        return (Stage) root.getScene().getWindow();
    }

    @FXML
    private void calculateBill(ActionEvent e) {
        BigDecimal suggestion = Util.getRent(
                currentdata.getGiagoc(),
                startDate.getValue(),
                endDate.getValue());

        rent.setText(suggestion.setScale(-3, RoundingMode.CEILING).stripTrailingZeros().toPlainString());
    }

    public void loadEntries(InvoiceData data) {
        startDate.setValue(data.getLastPayDate());

        if (data.getLastPayDate().plusMonths(1).isBefore(data.getNgaytra())) {
            endDate.setValue(data.getLastPayDate().plusMonths(1));
        } else {
            endDate.setValue(data.getNgaytra());
        }

        endDate.setDayCellFactory(param -> new DateCell() {
            @Override
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                setDisable(
                        date.compareTo(startDate.getValue()) < 0
                        || date.compareTo(data.getNgaytra()) > 0);
            }
        });

        // calculate rent money as suggestion
        BigDecimal suggestion = Util.getRent(
                data.getGiagoc(),
                startDate.getValue(),
                endDate.getValue());
        rent.setText(suggestion.setScale(-3, RoundingMode.CEILING).stripTrailingZeros().toPlainString());

        startDate.setDisable(true);
        startDate.setStyle("-fx-opacity: 0.8");
        startDate.getEditor().setStyle("-fx-opacity: 0.6");

        currentdata = data;
    }

    private boolean checkEntries() {
        if (endDate.getValue() == null) {
            CustomAlert.showErrorMessage("Ch??a nh???p h???n thanh to??n", "");
            return false;
        } else if (rent.getText().isBlank()) {
            CustomAlert.showErrorMessage("S??? ti???n tr???ng", "H??y nh???p s??? ti???n");
            return false;
        }
        return true;
    }

    @FXML
    void handleOpenCalc(ActionEvent event) {
        try {
            Runtime.getRuntime().exec(
                    "C:\\Windows\\System32\\calc.exe", null,
                    new File("C:\\Windows\\System32")).waitFor();
        } catch (IOException ex) {
            Logger.getLogger(ListInvoiceController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InterruptedException ex) {
            Logger.getLogger(AddInvoiceController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
