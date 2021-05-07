package main.model;

import java.math.BigDecimal;
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
    private ObjectProperty<BigDecimal> giaGoc;
    private ObjectProperty<BigDecimal> tienCoc;
    private IntegerProperty dienTich;
    private StringProperty moTa;
    private IntegerProperty maKhu;

    public Room(int id, String tenPhong, int soNguoi, BigDecimal giaGoc, BigDecimal tienCoc, int dienTich, String moTa, int maKhu) {
        this.id = new SimpleIntegerProperty(id);
        this.tenPhong = new SimpleStringProperty(tenPhong);
        this.soNguoi = new SimpleIntegerProperty(soNguoi);
        this.moTa = new SimpleStringProperty(moTa);
        this.maKhu = new SimpleIntegerProperty(maKhu);
        this.giaGoc = new SimpleObjectProperty<BigDecimal>(giaGoc);
        this.tienCoc = new SimpleObjectProperty<BigDecimal>(tienCoc);
        this.dienTich = new SimpleIntegerProperty(dienTich);
    }

    public Room(String tenPhong, int soNguoi, BigDecimal giaGoc, BigDecimal tienCoc, int dienTich, String moTa, int maKhu) {
        this.tenPhong = new SimpleStringProperty(tenPhong);
        this.soNguoi = new SimpleIntegerProperty(soNguoi);
        this.moTa = new SimpleStringProperty(moTa);
        this.maKhu = new SimpleIntegerProperty(maKhu);
        this.giaGoc = new SimpleObjectProperty<>(giaGoc);
        this.tienCoc = new SimpleObjectProperty<>(tienCoc);
        this.dienTich = new SimpleIntegerProperty(dienTich);
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
    
    public int getDienTich() {
        return dienTich.get();
    }

    public void setDienTich(int dt) {
        this.dienTich.set(dt);
    }
    
    public BigDecimal getTienCoc() {
        return tienCoc.get();
    }

    public void setTienCoc(BigDecimal tienCoc) {
        this.tienCoc.set(tienCoc);
    }
    
    public String getMoTa() {
        return moTa.get();
    }

    public void setMoTa(String moTa) {
        this.moTa.set(moTa);
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
    
    public StringProperty moTaProperty() {
        return moTa;
    }
    
    @Override
    public String toString() {
        return getTenPhong();
    }

    public String debugString() {
        return "Room{" + "id=" + getId() + ", tenPhong=" + getTenPhong()
                + ", soNguoi=" + getSoNguoi() + ", giaGoc=" + getGiaGoc()
                + ", tienCoc=" + getTienCoc() + ", dienTich=" + getDienTich()
                + ", maKhu=" + getMaKhu() + '}';
    }
}
