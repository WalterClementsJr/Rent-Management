package main.model;

public class Room {

    private int id;
    private boolean status;
    private int type;

    public Room(int id, boolean status, int type) {
        this.id = id;
        this.status = status;
        this.type = type;
    }

    public int getId() {
        return id;
    }

    public boolean getStatus() {
        return status;
    }

    public int getType() {
        return type;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public void setType(int type) {
        this.type = type;
    }
}
