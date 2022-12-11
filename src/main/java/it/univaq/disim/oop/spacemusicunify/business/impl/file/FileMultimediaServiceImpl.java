package it.univaq.disim.oop.spacemusicunify.business.impl.file;

import it.univaq.disim.oop.spacemusicunify.business.*;
import it.univaq.disim.oop.spacemusicunify.domain.*;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;

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
                    if(((Song) audio.getOwnership()).getAlbum().getGenre() == Genre.singles){
                        if ((!((Song) audio.getOwnership()).getTitle().contains("DefaultSingles") && !(song.getTitle().contains("DefaultSingles")) && Arrays.equals(song.getFileMp3().getData(), audio.getData()))) {
                            ((Song) audio.getOwnership()).setId(null);
                            throw new AlreadyExistingException("New Song, Already Existing song with this audio");
                        }
                    } else {
                        //canzone non "DefaultSingles" e non la canzone di default di un nuovo album ma qualsiasi canzone di qualsiasi album deve avere il file mp3 diverso da quello di tutte le altre canzoni
                        if (!(((Song) audio.getOwnership()).getAlbum().getSongs().isEmpty()) && Arrays.equals(song.getFileMp3().getData(), audio.getData())) {
                            ((Song) audio.getOwnership()).setId(null);
                            throw new AlreadyExistingException("New Song, Already Existing song with this audio");
                        }
                    }

                }
            }

            FileData fileData = Utility.readAllRows(audiosFile);
            try (PrintWriter writer = new PrintWriter(new File(audiosFile))) {
                long contatore = fileData.getCounter();
                writer.println((contatore + 1));
                for (String[] righe : fileData.getRows()) {
                    writer.println(String.join(Utility.COLUMN_SEPARATOR, righe));
                }
                audio.setId((int) contatore);

                StringBuilder row = new StringBuilder();
                row.append(contatore);
                row.append(Utility.COLUMN_SEPARATOR);
                row.append(saveANDstore(audio.getData(), "audio"));
                row.append(Utility.COLUMN_SEPARATOR);
                row.append(((Song) audio.getOwnership()).getId() );

                writer.println(row.toString());

            }
        } catch (IOException e) {
            throw new BusinessException(e);
        }
    }
    @Override
    public void modify(Audio audio) throws BusinessException {
        AlbumService albumService = SpacemusicunifyBusinessFactory.getInstance().getAlbumService();
        try {
            for(Song song : albumService.getSongList()) {

                if (song.getId().intValue() != ((Song) audio.getOwnership()).getId().intValue()) {

                    //canzone "DefaultSingles" di qualsiasi album "Singles" che ha genere "singles" può avere il file mp3 uguale a quelle di altri album "Singles" e diverso invece rispetto a tutte le altre canzoni in altri album.
                    if(((Song) audio.getOwnership()).getAlbum().getGenre() == Genre.singles){
                        if ((!((Song) audio.getOwnership()).getTitle().contains("DefaultSingles") && !(song.getTitle().contains("DefaultSingles")) && Arrays.equals(song.getFileMp3().getData(), audio.getData()))) {
                            throw new AlreadyExistingException("Modify Song, Already Existing song with this audio");
                        }
                    } else {
                        //canzone non "DefaultSingles" e non la canzone di default di un nuovo album ma qualsiasi canzone di qualsiasi album deve avere il file mp3 diverso da quello di tutte le altre canzoni
                        if (!(((Song) audio.getOwnership()).getAlbum().getSongs().isEmpty()) && Arrays.equals(song.getFileMp3().getData(), audio.getData())) {
                            throw new AlreadyExistingException("Modify Song, Already Existing song with this audio");
                        }
                    }

                }
            }

            FileData fileData = Utility.readAllRows(audiosFile);
            try (PrintWriter writer = new PrintWriter(new File(audiosFile))) {
                long contatore = fileData.getCounter();
                writer.println((contatore + 1));
                for (String[] righe : fileData.getRows()) {
                    writer.println(String.join(Utility.COLUMN_SEPARATOR, righe));
                }
                audio.setId((int) contatore);

                StringBuilder row = new StringBuilder();
                row.append(contatore);
                row.append(Utility.COLUMN_SEPARATOR);
                row.append(saveANDstore(audio.getData(), "audio"));
                row.append(Utility.COLUMN_SEPARATOR);
                row.append(((Song) audio.getOwnership()).getId() );

                writer.println(row.toString());

            }
        } catch (IOException e) {
            throw new BusinessException(e);
        }
    }
    @Override
    public void delete(Audio audio) throws BusinessException{
        boolean check = false;

        try {
            FileData fileData = Utility.readAllRows(audiosFile);
            for(String[] righeCheck: fileData.getRows()) {
                if(righeCheck[0].equals(audio.getId().toString())) {
                    Files.deleteIfExists(Paths.get(mp3Directory+File.separator+righeCheck[1]));
                    check = true;
                    //aggiorno il file audios.txt
                    try (PrintWriter writer = new PrintWriter(new File(audiosFile))) {
                        writer.println(fileData.getCounter());
                        for (String[] righe : fileData.getRows()) {
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
            if(!check)throw new BusinessException("Object not found, This audio doesn't exist");
        } catch (IOException e) {
            throw new BusinessException(e);
        }
    }

    @Override
    public void add(Picture picture) throws BusinessException {
        AlbumService albumService = SpacemusicunifyBusinessFactory.getInstance().getAlbumService();
        try {
            if(picture.getOwnership() instanceof Artist) {
                for(Picture pictureCheck : ((Artist) picture.getOwnership()).getPictures()){
                    if(pictureCheck.getId() != null && Arrays.equals(pictureCheck.getData(), picture.getData())){
                        ((Artist) picture.getOwnership()).setId(null);
                        throw new AlreadyExistingException("New Artist, Duplicated picture for this artist");
                    }
                }
            } else {

                for(Album album : albumService.getAlbumList()) {
                    if (album.getId().intValue() != ((Album) picture.getOwnership()).getId().intValue()) {

                        //album "Inediti" di qualsiasi artista che ha genere "singoli" può avere la cover uguale a quelle di altri album "Inediti" e diversa invece rispetto a tutti gli altri album.
                        if(((Album) picture.getOwnership()).getGenre() == Genre.singles){
                            if (album.getGenre() != Genre.singles && Arrays.equals(album.getCover().getData(), picture.getData())) {
                                ((Album) picture.getOwnership()).setId(null);
                                throw new AlreadyExistingException("New Album, Already Existing album with this cover");
                            }
                        } else {
                            //album "nuovo" con genere diverso da "singoli" deve avere la cover diversa da quelle di tutti gli altri album, compresi "Inediti"
                            if (Arrays.equals(album.getCover().getData(), picture.getData())) {
                                ((Album) picture.getOwnership()).setId(null);
                                throw new AlreadyExistingException("New Album, Already Existing album with this cover");
                            }
                        }

                    }
                }
            }

            FileData fileData = Utility.readAllRows(picturesFile);
            try (PrintWriter writer = new PrintWriter(new File(picturesFile))) {
                long contatore = fileData.getCounter();
                writer.println((contatore + 1));
                for (String[] righe : fileData.getRows()) {
                    writer.println(String.join(Utility.COLUMN_SEPARATOR, righe));
                }
                picture.setId((int) contatore);

                StringBuilder row = new StringBuilder();
                row.append(contatore);
                row.append(Utility.COLUMN_SEPARATOR);
                row.append(saveANDstore(picture.getData(), "image"));
                row.append(Utility.COLUMN_SEPARATOR);
                row.append(picture.getHeight());
                row.append(Utility.COLUMN_SEPARATOR);
                row.append(picture.getWidth());
                row.append(Utility.COLUMN_SEPARATOR);
                if(picture.getOwnership() instanceof Artist) {
                    row.append(((Artist) picture.getOwnership()).getId() );
                } else {
                    row.append(((Album) picture.getOwnership()).getId() );
                }
                writer.println(row.toString());

            }
        } catch (IOException e) {
            throw new BusinessException(e);
        }
    }
    @Override
    public void modify(Picture picture) throws BusinessException {
        AlbumService albumService = SpacemusicunifyBusinessFactory.getInstance().getAlbumService();
        try {
            if(picture.getOwnership() instanceof Artist) {
                for(Picture pictureCheck : ((Artist) picture.getOwnership()).getPictures()){
                    if(pictureCheck.getId() != null && Arrays.equals(pictureCheck.getData(), picture.getData())){

                        throw new AlreadyExistingException("Modify Artist, Duplicated picture for this artist");
                    }
                }
            } else {

                for(Album album : albumService.getAlbumList()) {
                    if (album.getId().intValue() != ((Album) picture.getOwnership()).getId().intValue()) {

                        //album "Inediti" di qualsiasi artista che ha genere "singoli" può avere la cover uguale a quelle di altri album "Inediti" e diversa invece rispetto a tutti gli altri album.
                        if(((Album) picture.getOwnership()).getGenre() == Genre.singles){
                            if (album.getGenre() != Genre.singles && Arrays.equals(album.getCover().getData(), picture.getData())) {

                                throw new AlreadyExistingException("Modify Album, Already Existing album with this cover");
                            }
                        } else {
                            //album "nuovo" con genere diverso da "singoli" deve avere la cover diversa da quelle di tutti gli altri album, compresi "Inediti"
                            if (Arrays.equals(album.getCover().getData(), picture.getData())) {

                                throw new AlreadyExistingException("Modify Album, Already Existing album with this cover");
                            }
                        }

                    }
                }
            }

            FileData fileData = Utility.readAllRows(picturesFile);
            try (PrintWriter writer = new PrintWriter(new File(picturesFile))) {
                long contatore = fileData.getCounter();
                writer.println((contatore + 1));
                for (String[] righe : fileData.getRows()) {
                    writer.println(String.join(Utility.COLUMN_SEPARATOR, righe));
                }
                picture.setId((int) contatore);

                StringBuilder row = new StringBuilder();
                row.append(contatore);
                row.append(Utility.COLUMN_SEPARATOR);
                row.append(saveANDstore(picture.getData(), "image"));
                row.append(Utility.COLUMN_SEPARATOR);
                row.append(picture.getHeight());
                row.append(Utility.COLUMN_SEPARATOR);
                row.append(picture.getWidth());
                row.append(Utility.COLUMN_SEPARATOR);
                if(picture.getOwnership() instanceof Artist) {
                    row.append(((Artist) picture.getOwnership()).getId() );
                } else {
                    row.append(((Album) picture.getOwnership()).getId() );
                }
                writer.println(row.toString());

            }
        } catch (IOException e) {
            throw new BusinessException(e);
        }
    }
    @Override
    public void delete(Picture picture) throws BusinessException {
        boolean check = false;

        try {
            FileData fileData = Utility.readAllRows(picturesFile);
            for(String[] rowsCheck: fileData.getRows()) {
                if(rowsCheck[0].equals(picture.getId().toString())) {
                    Files.deleteIfExists(Paths.get(picturesDirectory+File.separator+rowsCheck[1]));
                    check = true;
                    //aggiorno il file pictures.txt
                    try (PrintWriter writer = new PrintWriter(new File(picturesFile))) {
                        writer.println(fileData.getCounter());
                        for (String[] righe : fileData.getRows()) {
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
            if(!check)throw new BusinessException("Object not found, This picture doesn't exist");
        } catch (IOException e) {
            throw new BusinessException(e);
        }
    }

    public String saveANDstore(byte[] bytes, String type) throws BusinessException {
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
                    if (Files.exists(Paths.get(newfilePng))) {
                        numberPng++;
                    } else {
                        fileNotExistPng = true;
                    }
                }
                newfile = newfilePng;
                product = type + numberPng + ".png";

                break;

            case "audio":

                String newfileMp3 = null;
                int numberMp3 = 1;
                boolean fileNotExistMp3 = false;
                while (!fileNotExistMp3) {

                    newfileMp3 = existMp3 + numberMp3 + ".mp3";
                    if (Files.exists(Paths.get(newfileMp3))) {
                        numberMp3++;
                    } else {
                        fileNotExistMp3 = true;
                    }
                }
                newfile = newfileMp3;
                product = type+numberMp3+".mp3";
                break;
        }


        try {
            Files.write(Paths.get(newfile), bytes);
        } catch (IOException e) {
            throw new BusinessException(e);
        }
        return product;
    }
}
