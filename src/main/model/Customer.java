package main.model;

import java.time.LocalDate;
import java.util.Date;

/**
 *
 * @author walker
 */
public class Customer {
    
    private int id;
    private String hoTen;
    private boolean gioiTinh;
    private LocalDate ngaySinh;
    private String SDT;
    private String CMND;

    public Customer(int id, String hoTen, boolean gioiTinh, LocalDate ngaySinh, String SDT, String CMND) {
        this.id = id;
        this.hoTen = hoTen;
        this.gioiTinh = gioiTinh;
        this.ngaySinh = ngaySinh;
        this.SDT = SDT;
        this.CMND = CMND;
    }
    
    public Customer(String hoTen, boolean gioiTinh, LocalDate ngaySinh, String SDT, String CMND) {
        this.hoTen = hoTen;
        this.gioiTinh = gioiTinh;
        this.ngaySinh = ngaySinh;
        this.SDT = SDT;
        this.CMND = CMND;
    }
    
    public String getSDT() {
        return SDT;
    }

    public void setSDT(String SDT) {
        this.SDT = SDT;
    }
    public int getId() {
        return id;
    }

    public String getHoTen() {
        return hoTen;
    }

    public boolean getGioiTinh() {
        return gioiTinh;
    }

    public LocalDate getNgaySinh() {
        return ngaySinh;
    }

    public String getCMND() {
        return CMND;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setHoTen(String hoTen) {
        this.hoTen = hoTen;
    }

    public void setGioiTinh(boolean gioiTinh) {
        this.gioiTinh = gioiTinh;
    }

    public void setNgaySinh(LocalDate ngaySinh) {
        this.ngaySinh = ngaySinh;
    }

    public void setCMND(String CMND) {
        this.CMND = CMND;
    }
}
