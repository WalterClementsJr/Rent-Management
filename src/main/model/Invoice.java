
package main.model;

import java.math.BigDecimal;
import java.time.LocalDate;

public class Invoice {
    
    
    int mahdong;
    BigDecimal soTien;
    LocalDate ngayTT;

    public Invoice(int mahdong, BigDecimal soTien, LocalDate ngayTT) {
        this.mahdong = mahdong;
        this.soTien = soTien;
        this.ngayTT = ngayTT;
    }

    public int getMahdong() {
        return mahdong;
    }

    public void setMahdong(int mahdong) {
        this.mahdong = mahdong;
    }

    public BigDecimal getSoTien() {
        return soTien;
    }

    public void setSoTien(BigDecimal soTien) {
        this.soTien = soTien;
    }

    public LocalDate getNgayTT() {
        return ngayTT;
    }

    public void setNgayTT(LocalDate ngayTT) {
        this.ngayTT = ngayTT;
    }

}