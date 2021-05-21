package main.model;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Complex {

    private IntegerProperty id;
    private StringProperty ten;
    private StringProperty diaChi;

    public Complex(int id, String ten, String diaChi) {
        this.id = new SimpleIntegerProperty(id);
        this.ten = new SimpleStringProperty(ten);
        this.diaChi = new SimpleStringProperty(diaChi);
    }

    public Complex(String ten, String diaChi) {
        this.ten = new SimpleStringProperty(ten);
        this.diaChi = new SimpleStringProperty(diaChi);
    }

    public int getId() {
        return id.get();
    }

    public String getTen() {
        return ten.get();
    }

    public String getDiaChi() {
        return diaChi.get();
    }

    public void setId(int id) {
        this.id.set(id);
    }

    public void setTen(String ten) {
        this.ten.set(ten);
    }

    public void setDiaChi(String diaChi) {
        this.diaChi.set(diaChi);
    }

    public IntegerProperty idProperty() {
        return id;
    }

    public StringProperty tenProperty() {
        return ten;
    }

    public StringProperty diaChiProperty() {
        return diaChi;
    }

    @Override
    public String toString() {
        return getTen();
    }

    public String debugString() {
        return "Complex{" + "id=" + getId() + ", ten=" + getTen()
                + ", diaChi=" + getDiaChi() + '}';
    }

}
