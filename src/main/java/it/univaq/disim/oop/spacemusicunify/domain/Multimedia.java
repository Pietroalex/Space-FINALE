package it.univaq.disim.oop.spacemusicunify.domain;

import it.univaq.disim.oop.spacemusicunify.business.BusinessException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

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

    public void setData(String source) throws BusinessException {
        byte[] bytes;
        try {
            ByteArrayOutputStream outStreamObj = new ByteArrayOutputStream();

            outStreamObj.writeBytes(Files.readAllBytes(Paths.get(source)));
            bytes = outStreamObj.toByteArray();

            outStreamObj.close();
        } catch (IOException e) {
            throw new BusinessException(e);
        }
        this.data = bytes;
    }
    public Object getOwnership() {
        return ownership;
    }

    public void setOwnership(Object ownership) {
        this.ownership = ownership;
    }


}
