package main.model;

import java.time.LocalDate;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import main.util.Util;

/**
 *
 * @author walker
 */
public class Customer {

    private IntegerProperty id;
    private StringProperty hoTen;
    private BooleanProperty gioiTinh;
    private ObjectProperty<LocalDate> ngaySinh;
    private StringProperty SDT;
    private StringProperty CMND;

    public Customer(int id, String hoTen, boolean gioiTinh, LocalDate ngaySinh, String SDT, String CMND) {
        this.id = new SimpleIntegerProperty(id);
        this.hoTen = new SimpleStringProperty(hoTen);
        this.gioiTinh = new SimpleBooleanProperty(gioiTinh);
        this.ngaySinh = new SimpleObjectProperty<>(ngaySinh);
        this.SDT = new SimpleStringProperty(SDT);
        this.CMND = new SimpleStringProperty(CMND);
    }

    public Customer(String hoTen, boolean gioiTinh, LocalDate ngaySinh, String SDT, String CMND) {
        this.hoTen = new SimpleStringProperty(hoTen);
        this.gioiTinh = new SimpleBooleanProperty(gioiTinh);
        this.ngaySinh = new SimpleObjectProperty<>(ngaySinh);
        this.SDT = new SimpleStringProperty(SDT);
        this.CMND = new SimpleStringProperty(CMND);
    }

    public String getSDT() {
        return SDT.get();
    }

    public void setSDT(String SDT) {
        this.SDT.set(SDT);
    }

    public int getId() {
        return id.get();
    }

    public String getHoTen() {
        return hoTen.get();
    }

    public boolean getGioiTinh() {
        return gioiTinh.get();
    }

    public LocalDate getNgaySinh() {
//        return ngaySinh.get();
        return ngaySinh.get();
    }

    public String getCMND() {
        return CMND.get();
    }

    public void setId(int id) {
        this.id.set(id);
    }

    public void setHoTen(String hoTen) {
        this.hoTen.set(hoTen);
    }

    public void setGioiTinh(boolean gioiTinh) {
        this.gioiTinh.set(gioiTinh);
    }

    public void setNgaySinh(LocalDate ngaySinh) {
        this.ngaySinh.set(ngaySinh);
    }

    public void setCMND(String CMND) {
        this.CMND.set(CMND);
    }
    
    public IntegerProperty idProperty() {
        return id;
    }
    
    public StringProperty hotenProperty() {
        return hoTen;
    }
    
    public BooleanProperty gioiTinhProperty() {
        return gioiTinh;
    }
    
    public ObjectProperty<LocalDate> ngaySinhProperty() {
        return ngaySinh;
    }
    
    public StringProperty CMNDProperty() {
        return CMND;
    }
    
    public StringProperty SDTProperty() {
        return SDT;
    }

    @Override
    public String toString() {
        return this.getHoTen() + ", " + this.getSDT();
    }
    
    public String debugString() {
        return "Customer:" + "id=" + getId() + ", hoTen=" + getHoTen()
                + ", gioiTinh=" + getGioiTinh() + ", ngaySinh="+ getNgaySinh().format(Util.DATE_TIME_FORMATTER)
                + ", SDT=" + getSDT() + ", CMND=" + getCMND();
    }
        
}
