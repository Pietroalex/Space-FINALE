package it.univaq.disim.oop.spacemusicunify.business.impl.ram;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import it.univaq.disim.oop.spacemusicunify.business.*;
import it.univaq.disim.oop.spacemusicunify.domain.*;

public class RAMMultimediaServiceImpl implements MultimediaService {
   
    Set<Audio> storedAudios = new HashSet<>();
    private static int idAudio = 1;
    Set<Picture> storedPictures = new HashSet<>();
    private static int idPictures = 1;

    @Override
    public void add(Audio audio) throws BusinessException {
        for(Audio audios : storedAudios) {
            if(Arrays.equals(audios.getData(), audio.getData())) {
                throw new AlreadyExistingException();
            }
        }
        audio.setId(idAudio++);
        storedAudios.add(audio);

    }
    
    @Override
    public void delete(Audio audio) throws BusinessException {
        boolean check = false;
        for(Audio audios : storedAudios) {
            if(Arrays.equals(audios.getData(), audio.getData())) {
                check = true;
                storedAudios.remove(audio);
                break;
            }
        }

        if(!check)throw new ObjectNotFoundException("audio not exist");
    }

    @Override
    public void add(Picture picture) throws BusinessException {
        AlbumService albumService = SpacemusicunifyBusinessFactory.getInstance().getAlbumService();
        if(picture.getOwnership() instanceof Artist) {
            for(Picture pictureCheck : ((Artist) picture.getOwnership()).getPictures()){
                if(Arrays.equals(pictureCheck.getData(), picture.getData())){
                    throw new AlreadyExistingException();
                }
            }
        } else {

            for(Album album : albumService.getAlbumList()) {
                if (album.getId().intValue() != ((Album) picture.getOwnership()).getId().intValue()) {

                    //album "Inediti" di qualsiasi artista che ha genere "singoli" può avere la cover uguale a quelle di altri album "Inediti" e diversa invece rispetto a tutti gli altri album.
                    if(((Album) picture.getOwnership()).getGenre() == Genre.singles){
                        if (album.getGenre() != Genre.singles && Arrays.equals(album.getCover().getData(), picture.getData())) {
                            throw new AlreadyExistingException();
                        }
                    } else {
                        //album "nuovo" con genere diverso da "singoli" deve avere la cover diversa da quelle di tutti gli altri album, compresi "Inediti"
                        if (Arrays.equals(album.getCover().getData(), picture.getData())) {
                            throw new AlreadyExistingException();
                        }
                    }

                }
            }
        }

        picture.setId(idPictures++);
        storedPictures.add(picture);

    }
    
    @Override
    public void delete(Picture picture) throws BusinessException {
        boolean check = false;
        for(Picture pictures : storedPictures) {
            if(Arrays.equals(pictures.getData(), picture.getData())) {
                check = true;
                storedPictures.remove(picture);
                break;
            }
        }

        if(!check)throw new ObjectNotFoundException("picture not exist");
    }
    
}
