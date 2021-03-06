package main.ui.statistic;

import java.math.BigDecimal;
import java.net.URL;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
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
    private ComboBox<String> filter1;

    @FXML
    private DatePicker datePicker1;

    @FXML
    private BarChart maintenanceChart;

    @FXML
    private CategoryAxis rXAxis;

    @FXML
    private NumberAxis rYAxis;

    @FXML
    private ComboBox<String> filter;

    @FXML
    private DatePicker datePicker;

    @FXML
    private BarChart revenueChart;

    @FXML
    private CategoryAxis xAxis;

    @FXML
    private NumberAxis yAxis;

    // extra components
    ObservableList<Complex> complexList = ListRoomController.complexList;

    ObservableList<PieChart.Data> customerChartData = FXCollections.observableArrayList();
    ObservableList<BarChart.Series> revChartData = FXCollections.observableArrayList();
    ObservableList<BarChart.Series> maintenanceChartData = FXCollections.observableArrayList();

    DatabaseHandler handler;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        MasterController.getInstance().registerStatisticController(this);
        handler = DatabaseHandler.getInstance();

        filter.getItems().addAll("Th??ng", "N??m");
        filter.getSelectionModel().selectFirst();
        filter1.getItems().addAll("Th??ng", "N??m");
        filter1.getSelectionModel().selectFirst();

        customerChart.setTitle("S??? KH??CH ??ANG ??? TRONG KHU");
        customerChart.setStartAngle(90);
        customerChart.setData(customerChartData);

        revenueChart.setAnimated(false);
        revenueChart.setData(revChartData);

        maintenanceChart.setAnimated(false);
        maintenanceChart.setData(maintenanceChartData);

        datePicker.getEditor().setEditable(false);
        datePicker1.getEditor().setEditable(false);

        StringConverter converter = new StringConverter<LocalDate>() {
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
        };
        datePicker.setConverter(converter);
        datePicker1.setConverter(converter);

        datePicker.setValue(LocalDate.now());
        datePicker1.setValue(LocalDate.now());

        loadCustomerChart();
        loadRevenueData();
        loadMaintenanceData();
    }

    @FXML
    void loadCustomerChart() {
        customerChart.getData().clear();

        complexList.forEach(c -> {
            int nOfCustomer = handler.getNumberOfCustomersInComplex(c.getId());
            if (nOfCustomer == -1) {
                CustomAlert.showErrorMessage(
                        "???? c?? l???i x???y ra",
                        "H??y th??? l???i sau");
            }
            customerChartData.add(new PieChart.Data(
                    c.getTen(), nOfCustomer));
        });

        customerChartData.forEach(data
                -> data.nameProperty().bind(Bindings.concat(
                        data.getName(), ": ", (int) data.getPieValue(), " ng?????i")
                )
        );
    }

    @FXML
    void filterChanged(ActionEvent e) {
        loadRevenueData();
    }

    @FXML
    void filter1Changed(ActionEvent e) {
        loadMaintenanceData();
    }

    @FXML
    void dateChanged(ActionEvent e) {
        loadRevenueData();
    }

    @FXML
    void date1Changed(ActionEvent e) {
        loadMaintenanceData();
    }

    @FXML
    public void handleRefresh(ActionEvent e) {
        loadCustomerChart();
        loadRevenueData();
        loadMaintenanceData();
    }

    void loadRevenueData() {
        switch (filter.getSelectionModel().getSelectedItem()) {
            case "Th??ng" ->
                loadRevenueMonth();
            case "N??m" ->
                loadRevenueYear();
        }
    }

    void loadRevenueMonth() {
        xAxis.setLabel("Khu");
        revChartData.clear();
        LocalDate date = datePicker.getValue();

        revenueChart.getData().clear();
        revenueChart.setTitle(
                "DOANH THU T???T C??? C??C TH??NG TRONG N??M %d".formatted(date.getYear()));

        for (int i = 1; i <= 12; i++) {
            XYChart.Series complexSeries = new XYChart.Series();
            complexSeries.setName("Th??ng %d".formatted(i));
            revChartData.add(complexSeries);
        }

        for (int i = 0; i < 12; i++) {
            for (int j = 0; j < complexList.size(); j++) {
                BigDecimal rev = handler.getTotalRevenueOfComplexInMonth(
                        complexList.get(j).getId(),
                        LocalDate.of(date.getYear(), i + 1, 1));

                if (rev == BigDecimal.valueOf(-1)) {
                    CustomAlert.showErrorMessage(
                            "???? c?? l???i x???y ra",
                            "H??y th??? l???i sau");
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
        xAxis.setLabel("N??m");
        LocalDate d = datePicker.getValue();

        revenueChart.getData().clear();
        revenueChart.setTitle(
                "DOANH THU TRONG N??M %d".formatted(datePicker.getValue().getYear()));
        for (int i = 0; i < complexList.size(); i++) {
            XYChart.Series complexSeries = new XYChart.Series();
            complexSeries.setName("Khu %s".formatted(complexList.get(i).getTen()));

            BigDecimal rev = handler.getTotalRevenueOfComplexInYear(
                    complexList.get(i).getId(), d);
            if (rev == BigDecimal.valueOf(-1)) {
                CustomAlert.showErrorMessage(
                        "???? c?? l???i x???y ra",
                        "H??y th??? l???i sau");
                return;
            }
            XYChart.Data data = new XYChart.Data("" + d.getYear(), rev);
            complexSeries.getData().add(data);
            revenueChart.getData().add(complexSeries);

            Tooltip.install(data.getNode(), new Tooltip(
                    new DecimalFormat("#,###")
                            .format(Double.parseDouble(data.getYValue().toString()))));
        }
    }

    void loadMaintenanceData() {
        switch (filter1.getSelectionModel().getSelectedItem()) {
            case "Th??ng" ->
                loadMaintenanceMonth();
            case "N??m" ->
                loadMaintenanceYear();
        }
    }

    void loadMaintenanceMonth() {
        rXAxis.setLabel("Khu");
        maintenanceChartData.clear();
        LocalDate date = datePicker1.getValue();

        maintenanceChart.getData().clear();
        maintenanceChart.setTitle(
                "PH?? B???O TR?? T???T C??? C??C TH??NG TRONG N??M %d".formatted(date.getYear()));

        for (int i = 1; i <= 12; i++) {
            XYChart.Series complexSeries = new XYChart.Series();
            complexSeries.setName("Th??ng %d".formatted(i));
            maintenanceChartData.add(complexSeries);
        }

        for (int i = 0; i < 12; i++) {
            for (int j = 0; j < complexList.size(); j++) {
                BigDecimal rev = handler.getMaintenaceFeeOfComplexInMonth(
                        complexList.get(j).getId(),
                        LocalDate.of(date.getYear(), i + 1, 1));

                if (rev == BigDecimal.valueOf(-1)) {
                    CustomAlert.showErrorMessage(
                            "???? c?? l???i x???y ra",
                            "H??y th??? l???i sau");
                } else {
                    XYChart.Data data = new XYChart.Data(complexList.get(j).getTen(), rev);

                    maintenanceChartData.get(i).getData().add(data);
                    Tooltip.install(data.getNode(), new Tooltip(
                            new DecimalFormat("#,###")
                                    .format(Double.parseDouble(data.getYValue().toString()))));
                }
            }
        }
    }

    void loadMaintenanceYear() {
        rXAxis.setLabel("N??m");
        LocalDate d = datePicker1.getValue();

        maintenanceChart.getData().clear();
        maintenanceChart.setTitle(
                "PH?? B???O TR?? TRONG N??M %d".formatted(datePicker.getValue().getYear()));
        for (int i = 0; i < complexList.size(); i++) {
            XYChart.Series series = new XYChart.Series();
            series.setName("Khu %s".formatted(complexList.get(i).getTen()));

            BigDecimal rev = handler.getMaintenanceFeeOfComplexInYear(
                    complexList.get(i).getId(), d);
            if (rev == BigDecimal.valueOf(-1)) {
                CustomAlert.showErrorMessage(
                        "???? c?? l???i x???y ra",
                        "H??y th??? l???i sau");
                return;
            }
            XYChart.Data data = new XYChart.Data("" + d.getYear(), rev);
            series.getData().add(data);
            maintenanceChart.getData().add(series);

            Tooltip.install(data.getNode(), new Tooltip(
                    new DecimalFormat("#,###")
                            .format(Double.parseDouble(data.getYValue().toString()))));
        }
    }
}
