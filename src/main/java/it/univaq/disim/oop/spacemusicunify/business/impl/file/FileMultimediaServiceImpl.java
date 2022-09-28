package it.univaq.disim.oop.spacemusicunify.business.impl.file;

import it.univaq.disim.oop.spacemusicunify.business.*;
import it.univaq.disim.oop.spacemusicunify.domain.*;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
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
        try {
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
            if(!check)throw new BusinessException("audio inesistente");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void add(Picture picture) throws BusinessException {
        try {
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
            if(!check)throw new BusinessException("picture inesistente");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @Override
    public Set<Picture> getAllPictures() throws BusinessException{
        return null;
    }

    @Override
    public Set<Audio> getAllAudios() throws BusinessException {
        return null;
    }

    public String saveANDstore(byte[] bytes, String type) throws UnsupportedFileExtensionException {
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
