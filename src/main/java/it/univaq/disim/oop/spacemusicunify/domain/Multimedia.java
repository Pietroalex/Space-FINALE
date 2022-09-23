package it.univaq.disim.oop.spacemusicunify.domain;

public class Multimedia {
    private Integer id;
    private byte[] data;
    private Object ownership;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }
    public Object getOwnership() {
        return ownership;
    }

    public void setOwnership(Object ownership) {
        this.ownership = ownership;
    }


}
