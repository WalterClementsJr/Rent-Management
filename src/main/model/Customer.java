package main.model;

import java.util.Date;

/**
 *
 * @author walker
 */
public class Customer {
    
    private int id;
    private String hoTen;
    private String gioiTinh;
    private Date ngaySinh;
    private String SDT;
    private String CMND;

    public Customer(int id, String hoTen, String gioiTinh, Date ngaySinh, String SDT, String CMND) {
        this.id = id;
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

    public String getGioiTinh() {
        return gioiTinh;
    }

    public Date getNgaySinh() {
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

    public void setGioiTinh(String gioiTinh) {
        this.gioiTinh = gioiTinh;
    }

    public void setNgaySinh(Date ngaySinh) {
        this.ngaySinh = ngaySinh;
    }

    public void setCMND(String CMND) {
        this.CMND = CMND;
    }
}
