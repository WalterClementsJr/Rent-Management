package main.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.StringProperty;


public class Maintenance {
    private IntegerProperty id;
    private IntegerProperty maPhong;
    private ObjectProperty<BigDecimal> chiPhi;
    private ObjectProperty<LocalDate> ngay;
    private StringProperty moTa;
    
    
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
    
    public BigDecimal getChiPhi() {
        return chiPhi.get();
    }

    public void setChiPhi(BigDecimal chiPhi) {
        this.chiPhi.set(chiPhi);
    }
    
    public LocalDate getNgay() {
        return ngay.get();
    }

    public void setNgay(LocalDate ngay) {
        this.ngay.set(ngay);
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
    
    public IntegerProperty maPhongProperty() {
        return maPhong;
    }
    
    public ObjectProperty<LocalDate> ngayProperty() {
        return ngay;
    }
    
    public ObjectProperty<BigDecimal> chiPhiProperty() {
        return chiPhi;
    }
    
    public StringProperty moTaProperty() {
        return moTa;
    }
    
    @Override
    public String toString() {
        return "Maintenance{" + "id=" + getId()
                + ", maPhong=" + getMaPhong() + ", chiPhi=" + getChiPhi()
                + ", ngay=" + getNgay() + ", moTa=" + getMoTa() + '}';
    }

    public String debugString() {
        return "Maintenance{" + "id=" + getId()
                + ", maPhong=" + getMaPhong() + ", chiPhi=" + getChiPhi()
                + ", ngay=" + getNgay() + ", moTa=" + getMoTa() + '}';
    }
}
