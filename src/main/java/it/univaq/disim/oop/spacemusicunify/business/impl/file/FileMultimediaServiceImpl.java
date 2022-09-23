package it.univaq.disim.oop.spacemusicunify.business.impl.file;

import it.univaq.disim.oop.spacemusicunify.business.*;
import it.univaq.disim.oop.spacemusicunify.domain.Album;
import it.univaq.disim.oop.spacemusicunify.domain.Artist;
import it.univaq.disim.oop.spacemusicunify.domain.Picture;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class FileMultimediaServiceImpl implements MultimediaService {
    private MultimediaService multimediaService;
    private String picturesFile;
    private String picturesDirectory;
    private String mp3Directory;

    public FileMultimediaServiceImpl(String picturesFile, String picturesDirectory, String mp3Directory){
        SpacemusicunifyBusinessFactory factory = SpacemusicunifyBusinessFactory.getInstance();
        multimediaService = factory.getMultimediaService();
        this.picturesFile = picturesFile;
        this.picturesDirectory = picturesDirectory;
        this.mp3Directory = mp3Directory;
    }

    @Override
    public void add(byte[] bytes) throws BusinessException{
        try {
            saveANDstore(bytes, "audio");
        } catch (UnsupportedFileExtensionException e) {
            throw new BusinessException();
        }
    }
    @Override
    public void delete(byte[] bytes) throws BusinessException{

    }

    @Override
    public void add(Picture picture) throws BusinessException {
        try {
            FileData fileData = Utility.readAllRows(picturesFile);
			for (String[] colonne : fileData.getRighe()) {
				if (colonne[0].equals(picture.getId().toString())) {
					throw new AlreadyExistingException("Already existing picture");
				}
			}
            try (PrintWriter writer = new PrintWriter(new File(picturesFile))) {
                long contatore = fileData.getContatore();
                writer.println((contatore));
                for (String[] righe : fileData.getRighe()) {
                    writer.println(String.join(Utility.SEPARATORE_COLONNA, righe));
                }
                picture.setId((int) contatore);

                StringBuilder row = new StringBuilder();
                row.append(contatore);
                row.append(Utility.SEPARATORE_COLONNA);
                row.append(saveANDstore(picture.getPhoto(), "image"));
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

    }
    @Override
    public List<Picture> getAllPictures() throws BusinessException{
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
                product = "audio"+numberMp3+".mp3";
                break;
        }
/*        try {

            Files.copy(Paths.get(path),
                    (new File(newfile)).toPath(),
                    StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }*/

        try {
            Files.write(Paths.get("src" + File.separator + "main" + File.separator + "resources" + File.separator + "dati"+File.separator+type+"s"+File.separator+newfile), bytes);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return product;
    }
}
