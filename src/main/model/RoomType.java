
package main.model;

public class RoomType {
    
    public static void main(String[] args) {
        String t = "  \t \n  ";
        System.out.println(t.isBlank());
    }

    private int id;
    private long giaTien;
    private String moTa;

    public RoomType(int id, long giaTien, String moTa) {
        this.id = id;
        this.giaTien = giaTien;
        this.moTa = moTa;
    }

    public int getId() {
        return id;
    }

    public long getGiaTien() {
        return giaTien;
    }

    public String getMoTa() {
        return moTa;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setGiaTien(long giaTien) {
        this.giaTien = giaTien;
    }

    public void setMoTa(String moTa) {
        this.moTa = moTa;
    }

    
}
