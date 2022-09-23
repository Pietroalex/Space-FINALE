package it.univaq.disim.oop.spacemusicunify.business;

import it.univaq.disim.oop.spacemusicunify.domain.Audio;
import it.univaq.disim.oop.spacemusicunify.domain.Picture;

import java.util.List;

public interface MultimediaService {

    void add(Audio audio) throws BusinessException;

    void delete(Audio audio) throws BusinessException;

    void add(Picture picture) throws BusinessException;

    void delete(Picture picture) throws BusinessException;

    List<Picture> getAllPictures() throws BusinessException;
    List<Audio> getAllAudios() throws BusinessException;
}
