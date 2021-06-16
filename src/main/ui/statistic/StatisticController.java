/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main.ui.statistic;

import java.math.BigDecimal;
import java.net.URL;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.XYChart.Data;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseEvent;
import javafx.util.StringConverter;
import main.database.DatabaseHandler;
import main.model.Complex;
import main.ui.alert.CustomAlert;
import main.ui.listroom.ListRoomController;
import main.util.MasterController;

/**
 * FXML Controller class
 *
 * @author walker
 */
public class StatisticController implements Initializable {

    @FXML
    private PieChart customerChart;

    @FXML
    private ComboBox<String> filter;

    @FXML
    private Label pickLabel;

    @FXML
    private DatePicker datePicker;

    @FXML
    private BarChart revenueChart;

    @FXML
    private CategoryAxis xAxis;

    @FXML
    private NumberAxis yAxis;

    ObservableList<Complex> complexList = ListRoomController.complexList;

    ObservableList<PieChart.Data> customerChartData = FXCollections.observableArrayList();
    ObservableList<BarChart.Series> revChartData = FXCollections.observableArrayList();

    DatabaseHandler handler;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        MasterController.getInstance().registerStatisticController(this);
        handler = DatabaseHandler.getInstance();

        filter.getItems().addAll("Tháng", "Năm");
        filter.getSelectionModel().selectFirst();

        customerChart.setTitle("SỐ KHÁCH ĐANG Ở TRONG KHU");
        customerChart.setStartAngle(90);
        customerChart.setData(customerChartData);

        revenueChart.setAnimated(false);
        revenueChart.setData(revChartData);

        datePicker.getEditor().setEditable(false);
        datePicker.setConverter(new StringConverter<LocalDate>() {
            DateTimeFormatter dateFormatter
                    = DateTimeFormatter.ofPattern("yyyy");

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
        datePicker.setValue(LocalDate.now());

        loadCustomerChart();
        loadRevenueData();
    }

    @FXML
    void loadCustomerChart() {
        customerChart.getData().clear();

        complexList.forEach(c -> {
            int nOfCustomer = handler.getNumberOfCustomersInComplex(c.getId());
            if (nOfCustomer == -1) {
                CustomAlert.showErrorMessage(
                        "Đã có lỗi xảy ra",
                        "Hãy thử lại sau");
            }
            customerChartData.add(new PieChart.Data(
                    c.getTen(), nOfCustomer));
        });

        customerChartData.forEach(data
                -> data.nameProperty().bind(Bindings.concat(
                        data.getName(), ": ", (int) data.getPieValue(), " người")
                )
        );
    }

    @FXML
    void filterChanged(ActionEvent e) {
        loadRevenueData();
    }

    @FXML
    public void handleRefresh(ActionEvent e) {
        loadCustomerChart();
    }

    @FXML
    void dateChanged(ActionEvent e) {
        loadRevenueData();
    }

    void loadRevenueData() {
        switch (filter.getSelectionModel().getSelectedItem()) {
            case "Tháng" ->
                loadRevenueMonth();
            case "Năm" ->
                loadRevenueYear();
        }
    }

    void loadRevenueMonth() {
        revChartData.clear();
        LocalDate date = datePicker.getValue();

        revenueChart.getData().clear();
        revenueChart.setTitle(
                "DOANH THU TẤT CẢ CÁC THÁNG TRONG NĂM %d".formatted(date.getYear()));

        for (int i = 1; i <= 12; i++) {
            XYChart.Series complexSeries = new XYChart.Series();
            complexSeries.setName("Tháng %d".formatted(i));
            revChartData.add(complexSeries);
        }

        for (int i = 0; i < 12; i++) {
            for (int j = 0; j < complexList.size(); j++) {
                BigDecimal rev = handler.getTotalRevenueOfComplexInMonth(
                        complexList.get(j).getId(),
                        LocalDate.of(2021, i + 1, 1));

                if (rev == BigDecimal.valueOf(-1)) {
                    CustomAlert.showErrorMessage(
                            "Đã có lỗi xảy ra",
                            "Hãy thử lại sau");
                } else {
                    XYChart.Data data = new XYChart.Data(complexList.get(j).getTen(), rev);

                    revChartData.get(i).getData().add(data);
                    Tooltip.install(data.getNode(), new Tooltip(
                            new DecimalFormat("#,###")
                                    .format(Double.parseDouble(data.getYValue().toString()))));
                }
            }
        }
    }

    void loadRevenueYear() {
        LocalDate d = datePicker.getValue();

        revenueChart.getData().clear();
        revenueChart.setTitle(
                "DOANH THU TRONG NĂM %d".formatted(datePicker.getValue().getYear()));

        XYChart.Series complexSeries = new XYChart.Series();
        complexSeries.setName(("NĂM %d".formatted(d.getYear())));

        complexList.forEach(c -> {
            BigDecimal rev = handler.getTotalRevenueOfComplexInYear(c.getId(), d);
            if (rev == BigDecimal.valueOf(-1)) {
                CustomAlert.showErrorMessage(
                        "Đã có lỗi xảy ra",
                        "Hãy thử lại sau");
            } else {
                complexSeries.getData().add(new XYChart.Data(c.getTen(), rev));
            }
        });
        revenueChart.getData().add(complexSeries);
    }
}
