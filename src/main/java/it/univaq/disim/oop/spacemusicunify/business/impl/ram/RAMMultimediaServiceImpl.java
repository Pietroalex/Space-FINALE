package it.univaq.disim.oop.spacemusicunify.business.impl.ram;

import java.util.HashSet;
import java.util.Set;

import it.univaq.disim.oop.spacemusicunify.business.BusinessException;
import it.univaq.disim.oop.spacemusicunify.business.MultimediaService;
import it.univaq.disim.oop.spacemusicunify.domain.Audio;
import it.univaq.disim.oop.spacemusicunify.domain.Picture;

public class RAMMultimediaServiceImpl implements MultimediaService {
   
    Set<Audio> storedAudios = new HashSet<>();
    private static int idAudio = 1;
    Set<Picture> storedPictures = new HashSet<>();
    private static int idPictures = 1;

    @Override
    public void add(Audio audio) throws BusinessException {
    	boolean check = false;
    	for(Audio audios : storedAudios) {
    		if(audios.getData() == audio.getData()) {
    			check = true;
    		}
    	}
    	if(!check) {
	        audio.setId(idAudio++);
	        storedAudios.add(audio);
    	}
    }
    
    @Override
    public void delete(Audio audio) throws BusinessException {
    	storedAudios.remove(audio);
    }

    @Override
    public void add(Picture picture) throws BusinessException {
        boolean check = false;
        for(Picture pictures : storedPictures) {
            if(pictures.getData() == picture.getData()) {
                check = true;
            }
        }
        if(check)throw new BusinessException("picture exist");
        else {
            picture.setId(idPictures++);
            storedPictures.add(picture);
        }
    }
    
    @Override
    public void delete(Picture picture) throws BusinessException {
    	storedPictures.remove(picture);
    }
    
    @Override
    public Set<Picture> getAllPictures() throws BusinessException {
        return storedPictures;
    }

    @Override
    public Set<Audio> getAllAudios() throws BusinessException {
        return storedAudios;
    }
    
}
