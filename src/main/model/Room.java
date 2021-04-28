package main.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Room {

    private IntegerProperty id;
    private StringProperty tenPhong;
    private IntegerProperty soNguoi;

    private IntegerProperty maKhu;
    private ObjectProperty<BigDecimal> giaGoc;

    public Room(int id, String tenPhong, int soNguoi, int maKhu, BigDecimal giaGoc) {
        this.id = new SimpleIntegerProperty(id);
        this.tenPhong = new SimpleStringProperty(tenPhong);
        this.soNguoi = new SimpleIntegerProperty(soNguoi);;
        this.maKhu = new SimpleIntegerProperty(maKhu);
        this.giaGoc = new SimpleObjectProperty<BigDecimal>(giaGoc);
    }

    public Room(String tenPhong, int soNguoi, int maKhu, BigDecimal giaGoc) {
        this.tenPhong = new SimpleStringProperty(tenPhong);
        this.soNguoi = new SimpleIntegerProperty(soNguoi);;
        this.maKhu = new SimpleIntegerProperty(maKhu);
        this.giaGoc = new SimpleObjectProperty<BigDecimal>(giaGoc);
    }

    public int getId() {
        return id.get();
    }

    public void setId(int id) {
        this.id.set(id);
    }

    public String getTenPhong() {
        return tenPhong.get();
    }

    public void setTenPhong(String tenPhong) {
        this.tenPhong.set(tenPhong);
    }

    public int getSoNguoi() {
        return soNguoi.get();
    }

    public void setSoNguoi(int soNguoi) {
        this.soNguoi.set(soNguoi);
    }

    public int getMaKhu() {
        return maKhu.get();
    }

    public void setMaKhu(int maKhu) {
        this.maKhu.set(maKhu);
    }

    public BigDecimal getGiaGoc() {
        return giaGoc.get();
    }

    public void setGiaGoc(BigDecimal giaGoc) {
        this.giaGoc.set(giaGoc);
    }

    public IntegerProperty idProperty() {
        return id;
    }

    public StringProperty tenPhongProperty() {
        return tenPhong;
    }

    public IntegerProperty soNguoiProperty() {
        return soNguoi;
    }

    public IntegerProperty maKhuProperty() {
        return maKhu;
    }

    public ObjectProperty<BigDecimal> giaGocProperty() {
        return giaGoc;
    }

    @Override
    public String toString() {
        return "Room{" + "id=" + getId() + ", tenPhong=" + getTenPhong()
                + ", soNguoi=" + getSoNguoi() + ", maKhu=" + getMaKhu()
                + ", giaGoc=" + getGiaGoc() + '}';
    }
}
