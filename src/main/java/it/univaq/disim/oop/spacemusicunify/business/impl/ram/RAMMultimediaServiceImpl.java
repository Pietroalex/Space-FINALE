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
        AlbumService albumService = SpacemusicunifyBusinessFactory.getInstance().getAlbumService();

        for(Song song : albumService.getSongList()) {

            if (song.getId().intValue() != ((Song) audio.getOwnership()).getId().intValue()) {

                //canzone "DefaultSingles" di qualsiasi album "Singles" che ha genere "singles" può avere il file mp3 uguale a quelle di altri album "Singles" e diverso invece rispetto a tutte le altre canzoni in altri album.
                if(((Song) audio.getOwnership()).getTitle().contains("DefaultSingles")){
                    if (!(song.getTitle().contains("DefaultSingles")) && Arrays.equals(song.getFileMp3().getData(), audio.getData())) {
                        ((Song) audio.getOwnership()).setId(null);
                        throw new AlreadyExistingException("existing_singles_audio");
                    }
                } else {
                    //canzone non "DefaultSingles" e non la canzone di default di un nuovo album ma qualsiasi canzone di qualsiasi album deve avere il file mp3 diverso da quello di tutte le altre canzoni
                    if (!(((Song) audio.getOwnership()).getAlbum().getSongs().isEmpty()) && Arrays.equals(song.getFileMp3().getData(), audio.getData())) {
                        ((Song) audio.getOwnership()).setId(null);
                        throw new AlreadyExistingException("existing_new_audio");
                    }
                }

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
                break;
            }
        }
        if(!check)throw new ObjectNotFoundException("audio not exist");
        else storedAudios.removeIf((Audio audioCheck) -> Arrays.equals(audioCheck.getData(), audio.getData()));
    }

    @Override
    public void add(Picture picture) throws BusinessException {
        AlbumService albumService = SpacemusicunifyBusinessFactory.getInstance().getAlbumService();
        if(picture.getOwnership() instanceof Artist) {
            for(Picture pictureCheck : ((Artist) picture.getOwnership()).getPictures()){
                if(pictureCheck.getId() != null && Arrays.equals(pictureCheck.getData(), picture.getData())){
                    ((Artist) picture.getOwnership()).setId(null);
                    throw new AlreadyExistingException("existing_artist_picture");
                }
            }
        } else {

            for(Album album : albumService.getAlbumList()) {
                if (album.getId().intValue() != ((Album) picture.getOwnership()).getId().intValue()) {

                    //album "Inediti" di qualsiasi artista che ha genere "singoli" può avere la cover uguale a quelle di altri album "Inediti" e diversa invece rispetto a tutti gli altri album.
                    if(((Album) picture.getOwnership()).getGenre() == Genre.singles){
                        if (album.getGenre() != Genre.singles && Arrays.equals(album.getCover().getData(), picture.getData())) {
                            ((Album) picture.getOwnership()).setId(null);
                            throw new AlreadyExistingException("existing_singles_album_picture");
                        }
                    } else {
                        //album "nuovo" con genere diverso da "singoli" deve avere la cover diversa da quelle di tutti gli altri album, compresi "Inediti"
                        if (Arrays.equals(album.getCover().getData(), picture.getData())) {
                            ((Album) picture.getOwnership()).setId(null);
                            throw new AlreadyExistingException("existing_new_album_picture");
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
                break;
            }
        }
        if(!check)throw new ObjectNotFoundException("picture not exist");
        else storedPictures.removeIf((Picture pictureCheck) -> Arrays.equals(pictureCheck.getData(), picture.getData()));
    }
    
}
