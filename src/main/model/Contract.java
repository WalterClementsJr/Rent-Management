package main.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;

public class Contract {
    private IntegerProperty id;
    private IntegerProperty maPhong;    
    private IntegerProperty maKH;
    private ObjectProperty<LocalDate> ngayNhan;
    private ObjectProperty<LocalDate> ngayTra;
    private ObjectProperty<BigDecimal> tienCoc;
    
    public Contract(int id, int maPhong, int maKH, LocalDate ngayNhan, LocalDate ngayTra, BigDecimal tienCoc) {
        this.id = new SimpleIntegerProperty(id);
        this.maPhong = new SimpleIntegerProperty(maPhong);
        this.maKH = new SimpleIntegerProperty(maKH);
        this.ngayNhan = new SimpleObjectProperty<>(ngayNhan);
        this.ngayTra = new SimpleObjectProperty<>(ngayTra);
        this.tienCoc = new SimpleObjectProperty<>(tienCoc);
    }
    
    public Contract(int maPhong, int maKH, LocalDate ngayNhan, LocalDate ngayTra, BigDecimal tienCoc) {
        this.maPhong = new SimpleIntegerProperty(maPhong);
        this.maKH = new SimpleIntegerProperty(maKH);
        this.ngayNhan = new SimpleObjectProperty<>(ngayNhan);
        this.ngayTra = new SimpleObjectProperty<>(ngayTra);
        this.tienCoc = new SimpleObjectProperty<>(tienCoc);
    }

    public int getId() {
        return id.get();
    }

    public void setId(int id) {
        this.id.set(id);
    }

    public int getMaPhong() {
        return maPhong.get();
    }

    public void setMaPhong(int maPhong) {
        this.maPhong.set(maPhong);
    }

    public int getMaKH() {
        return maKH.get();
    }

    public void setMaKH(int maKH) {
        this.maKH.set(maKH);
    }

    public LocalDate getNgayNhan() {
        return ngayNhan.get();
    }

    public void setNgayNhan(LocalDate ngayNhan) {
        this.ngayNhan.set(ngayNhan);
    }

    public LocalDate getNgayTra() {
        return ngayTra.get();
    }

    public void setNgayTra(LocalDate ngayTra) {
        this.ngayTra.set(ngayTra);
    }

    public BigDecimal getTienCoc() {
        return tienCoc.get();
    }

    public void setTienCoc(BigDecimal tienCoc) {
        this.tienCoc.set(tienCoc);
    }
    
    public IntegerProperty idProperty() {
        return id;
    }
    
    public IntegerProperty maPhongProperty() {
        return maPhong;
    }
    
    public IntegerProperty maKHProperty() {
        return maKH;
    }
    
    public ObjectProperty<LocalDate> ngayNhanProperty() {
        return ngayNhan;
    }
    
    public ObjectProperty<LocalDate> ngayTraProperty() {
        return ngayTra;
    }
    
    public ObjectProperty<BigDecimal> tienCocProperty() {
        return tienCoc;
    }
    
}
