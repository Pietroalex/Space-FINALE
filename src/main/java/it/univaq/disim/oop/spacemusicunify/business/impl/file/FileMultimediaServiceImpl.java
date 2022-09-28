package it.univaq.disim.oop.spacemusicunify.business.impl.file;

import it.univaq.disim.oop.spacemusicunify.business.*;
import it.univaq.disim.oop.spacemusicunify.domain.*;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;

public class FileMultimediaServiceImpl implements MultimediaService {
    private String picturesFile;
    private String audiosFile;
    private String picturesDirectory;
    private String mp3Directory;

    public FileMultimediaServiceImpl(String picturesFile, String audiosFile, String picturesDirectory, String mp3Directory){
        this.picturesFile = picturesFile;
        this.audiosFile = audiosFile;
        this.picturesDirectory = picturesDirectory;
        this.mp3Directory = mp3Directory;
    }

    @Override
    public void add(Audio audio) throws BusinessException{
        AlbumService albumService = SpacemusicunifyBusinessFactory.getInstance().getAlbumService();
        try {
            for(Song song : albumService.getSongList()) {

                if (song.getId().intValue() != ((Song) audio.getOwnership()).getId().intValue()) {

                    //canzone "DefaultSingles" di qualsiasi album "Singles" che ha genere "singles" può avere il file mp3 uguale a quelle di altri album "Singles" e diverso invece rispetto a tutte le altre canzoni in altri album.
                    if(((Song) audio.getOwnership()).getTitle().contains("DefaultSingles")){
                        if (!(song.getTitle().contains("DefaultSingles")) && song.getFileMp3().getData() == audio.getData()) {
                            throw new AlreadyExistingException();
                        }
                    } else {
                        //canzone non "DefaultSingles" di qualsiasi album deve avere il file mp3 diverso da quello di tutte le altre canzoni
                        if (song.getFileMp3().getData() == audio.getData()) {
                            throw new AlreadyExistingException();
                        }
                    }

                }
            }

            FileData fileData = Utility.readAllRows(audiosFile);
            try (PrintWriter writer = new PrintWriter(new File(audiosFile))) {
                long contatore = fileData.getContatore();
                writer.println((contatore + 1));
                for (String[] righe : fileData.getRighe()) {
                    writer.println(String.join(Utility.SEPARATORE_COLONNA, righe));
                }
                audio.setId((int) contatore);

                StringBuilder row = new StringBuilder();
                row.append(contatore);
                row.append(Utility.SEPARATORE_COLONNA);
                row.append(saveANDstore(audio.getData(), "audio"));
                row.append(Utility.SEPARATORE_COLONNA);
                row.append(((Song) audio.getOwnership()).getId() );

                writer.println(row.toString());

            }
        } catch (IOException e) {
            e.printStackTrace();
            throw new BusinessException(e);
        }
    }
    @Override
    public void delete(Audio audio) throws BusinessException{
        boolean check = false;

        try {
            FileData fileData = Utility.readAllRows(audiosFile);
            for(String[] righeCheck: fileData.getRighe()) {
                if(righeCheck[0].equals(audio.getId().toString())) {
                    Files.deleteIfExists(Paths.get(mp3Directory+File.separator+righeCheck[1]));
                    check = true;
                    //aggiorno il file audios.txt
                    try (PrintWriter writer = new PrintWriter(new File(audiosFile))) {
                        writer.println(fileData.getContatore());
                        for (String[] righe : fileData.getRighe()) {
                            if (righe[0].equals(audio.getId().toString())) {
                                //jump line
                                continue;
                            } else {
                                writer.println(String.join("§", righe));
                            }
                        }
                    }

                    break;
                }
            }
            if(!check)throw new BusinessException("audio not exist");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void add(Picture picture) throws BusinessException {
        AlbumService albumService = SpacemusicunifyBusinessFactory.getInstance().getAlbumService();
        try {
            if(picture.getOwnership() instanceof Artist) {
                for(Picture pictureCheck : ((Artist) picture.getOwnership()).getPictures()){
                    if(pictureCheck.getData() == picture.getData()){
                        throw new AlreadyExistingException();
                    }
                }
            } else {

                for(Album album : albumService.getAlbumList()) {
                    if (album.getId().intValue() != ((Album) picture.getOwnership()).getId().intValue()) {

                        //album "Inediti" di qualsiasi artista che ha genere "singoli" può avere la cover uguale a quelle di altri album "Inediti" e diversa invece rispetto a tutti gli altri album.
                        if(((Album) picture.getOwnership()).getGenre() == Genre.singles){
                            if (album.getGenre() != Genre.singles && album.getCover().getData() == picture.getData()) {
                                throw new AlreadyExistingException();
                            }
                        } else {
                            //album "nuovo" con genere diverso da "singoli" deve avere la cover diversa da quelle di tutti gli altri album, compresi "Inediti"
                            if (album.getCover().getData() == picture.getData()) {
                                throw new AlreadyExistingException();
                            }
                        }

                    }
                }
            }

            FileData fileData = Utility.readAllRows(picturesFile);
            try (PrintWriter writer = new PrintWriter(new File(picturesFile))) {
                long contatore = fileData.getContatore();
                writer.println((contatore + 1));
                for (String[] righe : fileData.getRighe()) {
                    writer.println(String.join(Utility.SEPARATORE_COLONNA, righe));
                }
                picture.setId((int) contatore);

                StringBuilder row = new StringBuilder();
                row.append(contatore);
                row.append(Utility.SEPARATORE_COLONNA);
                row.append(saveANDstore(picture.getData(), "image"));
                row.append(Utility.SEPARATORE_COLONNA);
                row.append(picture.getHeight());
                row.append(Utility.SEPARATORE_COLONNA);
                row.append(picture.getWidth());
                row.append(Utility.SEPARATORE_COLONNA);
                if(picture.getOwnership() instanceof Artist) {
                    row.append(((Artist) picture.getOwnership()).getId() );
                } else {
                    row.append(((Album) picture.getOwnership()).getId() );
                }
                writer.println(row.toString());

            }
        } catch (IOException e) {
            e.printStackTrace();
            throw new BusinessException(e);
        }
    }
    @Override
    public void delete(Picture picture) throws BusinessException {
        boolean check = false;

        try {
            FileData fileData = Utility.readAllRows(picturesFile);
            for(String[] righeCheck: fileData.getRighe()) {
                if(righeCheck[0].equals(picture.getId().toString())) {
                    Files.deleteIfExists(Paths.get(picturesDirectory+File.separator+righeCheck[1]));
                    check = true;
                    //aggiorno il file pictures.txt
                    try (PrintWriter writer = new PrintWriter(new File(picturesFile))) {
                        writer.println(fileData.getContatore());
                        for (String[] righe : fileData.getRighe()) {
                            if (righe[0].equals(picture.getId().toString())) {
                                //jump line
                                continue;
                            } else {
                                writer.println(String.join("§", righe));
                            }
                        }
                    }

                    break;
                }
            }
            if(!check)throw new ObjectNotFoundException("picture not exist");
        } catch (IOException e) {
            throw new BusinessException(e);
        }
    }
    @Override
    public Set<Picture> getAllPictures() throws BusinessException{
        Set<Picture> pictures = new HashSet<>();
        for(Artist artist : SpacemusicunifyBusinessFactory.getInstance().getArtistService().getArtistList()){
            pictures.addAll(artist.getPictures());
        }
        for(Album album : SpacemusicunifyBusinessFactory.getInstance().getAlbumService().getAlbumList()){
            pictures.add(album.getCover());
        }
        return pictures;
    }

    @Override
    public Set<Audio> getAllAudios() throws BusinessException {
        Set<Audio> audios = new HashSet<>();
        for(Song song : SpacemusicunifyBusinessFactory.getInstance().getAlbumService().getSongList()){
            audios.add(song.getFileMp3());
        }
        return audios;
    }

    public String saveANDstore(byte[] bytes, String type) {
        String existImage = picturesDirectory + "image";
        String existMp3 = mp3Directory + "audio";



        String newfile = null;
        String product = null;
        switch (type){

            case "image":


                String newfilePng = null;
                int numberPng = 1;
                boolean fileNotExistPng = false;
                while (!fileNotExistPng) {

                    newfilePng = existImage + numberPng + ".png";
                    System.out.println("iterazione " + newfilePng);
                    if (Files.exists(Paths.get(newfilePng))) {
                        System.out.println("file exist with " + numberPng);
                        numberPng++;
                    } else {
                        System.out.println("file not exist with " + numberPng);
                        fileNotExistPng = true;
                    }
                }
                System.out.println("last " + numberPng);
                newfile = newfilePng;
                product = type + numberPng + ".png";

                break;

            case "audio":

                String newfileMp3 = null;
                int numberMp3 = 1;
                boolean fileNotExistMp3 = false;
                while (!fileNotExistMp3) {

                    newfileMp3 = existMp3 + numberMp3 + ".mp3";
                    System.out.println("iterazione " + newfileMp3);
                    if (Files.exists(Paths.get(newfileMp3))) {
                        System.out.println("file exist with " + numberMp3);
                        numberMp3++;
                    } else {
                        System.out.println("file not exist with " + numberMp3);
                        fileNotExistMp3 = true;
                    }
                }
                System.out.println("last " + numberMp3);
                newfile = newfileMp3;
                product = type+numberMp3+".mp3";
                break;
        }


        try {
            Files.write(Paths.get(newfile), bytes);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return product;
    }
}
