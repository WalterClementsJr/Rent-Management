package main.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import main.util.Util;

public class InvoiceData {

    int mahdong;
    LocalDate lastPayDate;
    LocalDate ngaytra;
    BigDecimal giagoc;
    int songay;

    public InvoiceData(int mahdong, LocalDate lastPayDate, LocalDate ngaytra, BigDecimal giagoc, int songay) {
        this.mahdong = mahdong;
        this.lastPayDate = lastPayDate;
        this.ngaytra = ngaytra;
        this.giagoc = giagoc;
        this.songay = songay;
    }

    public int getMahdong() {
        return mahdong;
    }

    public void setMahdong(int mahdong) {
        this.mahdong = mahdong;
    }

    public LocalDate getLastPayDate() {
        return lastPayDate;
    }

    public void setLastPayDate(LocalDate lastPayDate) {
        this.lastPayDate = lastPayDate;
    }

    public LocalDate getNgaytra() {
        return ngaytra;
    }

    public void setNgaytra(LocalDate ngaytra) {
        this.ngaytra = ngaytra;
    }

    public BigDecimal getGiagoc() {
        return giagoc;
    }

    public void setGiagoc(BigDecimal giagoc) {
        this.giagoc = giagoc;
    }

    public int getSongay() {
        return songay;
    }

    public void setSongay(int songay) {
        this.songay = songay;
    }

}
