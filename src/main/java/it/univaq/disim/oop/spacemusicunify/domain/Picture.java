package it.univaq.disim.oop.spacemusicunify.domain;

public class Picture {
	
	private Integer id;
    private byte[] photo;
    private int height;
    private int width;
    private Object ownership;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public byte[] getPhoto() {
        return photo;
    }

    public void setPhoto(byte[] photo) {
        this.photo = photo;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

	public Object getOwnership() {
		return ownership;
	}

	public void setOwnership(Object ownership) {
		this.ownership = ownership;
	}
	
}
