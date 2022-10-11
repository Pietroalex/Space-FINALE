package it.univaq.disim.oop.spacemusicunify.business.impl.file;

import it.univaq.disim.oop.spacemusicunify.business.*;
import it.univaq.disim.oop.spacemusicunify.domain.*;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FileSpaceMusicUnifyServiceImpl implements SPACEMusicUnifyService {

	private final String usersFile;
	private final String albumsFile;
	private final String artistsFile;
	private final String songsFile;
	private final String playlistFile;
	private final String productionFile;
	private final String audiosFile;
	private final String picturesFile;
	private final String picturesDirectory;
	private final String mp3Directory;

	private static final String REPOSITORY_BASE = "src" + File.separator + "main" + File.separator + "resources" + File.separator + "data";
	private static final String playersFile = REPOSITORY_BASE + File.separator + "players.txt";


	public FileSpaceMusicUnifyServiceImpl(String fileUtenti, String fileAlbums, String fileArtisti, String fileCanzoni, String filePlaylist, String cartellaImmagini, String cartellaFilesMP3, String filePictures, String audiosFileName, String productionsFileName) {
		this.usersFile = fileUtenti;
		this.albumsFile = fileAlbums;
		this.artistsFile = fileArtisti;
		this.songsFile = fileCanzoni;
		this.playlistFile = filePlaylist;
		this.picturesFile = filePictures;
		this.picturesDirectory = cartellaImmagini;
		this.mp3Directory = cartellaFilesMP3;
		this.productionFile = productionsFileName;
		this.audiosFile = audiosFileName;
	}




	
	@Override
	public void setAllDefaults() throws BusinessException {
		try {

			Path path = Paths.get(artistsFile);
			if (Files.notExists(path)) {
				if (new File(artistsFile).createNewFile()) System.out.println("file artisti creato correttamente");
				FileWriter writer = new FileWriter(new File(artistsFile));
				writer.write("1" + "\n");
				writer.close();
			}

			path = Paths.get(audiosFile);
			if (Files.notExists(path)) {
				if (new File(audiosFile).createNewFile()) System.out.println("file album creato correttamente");
				FileWriter writer = new FileWriter(new File(audiosFile));
				writer.write("1" + "\n");
				writer.close();
			}
			path = Paths.get(productionFile);
			if (Files.notExists(path)) {
				if (new File(productionFile).createNewFile()) System.out.println("file productions creato correttamente");
				FileWriter writer = new FileWriter(new File(productionFile));
				writer.write("1" + "\n");
				writer.close();
			}
			path = Paths.get(albumsFile);
			if (Files.notExists(path)) {
				if (new File(albumsFile).createNewFile()) System.out.println("file album creato correttamente");
				FileWriter writer = new FileWriter(new File(albumsFile));
				writer.write("1" + "\n");
				writer.close();
			}
			path = Paths.get(playlistFile);
			if (Files.notExists(path)) {
				if (new File(playlistFile).createNewFile()) System.out.println("file album creato correttamente");
				FileWriter writer = new FileWriter(new File(playlistFile));
				writer.write("1" + "\n");
				writer.close();
			}


			path = Paths.get(playersFile);
			if (Files.notExists(path)) {
				if (new File(playersFile).createNewFile()) System.out.println("file players creato correttamente");
				FileWriter writer = new FileWriter(new File(playersFile));
				writer.write("1" + "\n");
				writer.close();
			}
			path = Paths.get(usersFile);
			if (Files.notExists(path)) {
				if (new File(usersFile).createNewFile()) System.out.println("file utenti creato");

				FileWriter writer = new FileWriter(new File(usersFile));
				writer.write("3" + "\n");
				writer.write("1§admin§admin§admin" + "\n");
				writer.write("2§user§utente§123456"+ "\n");
				writer.close();

				User user = new User();
				user.setId(2);
				user.setUsername("utente");
				user.setPassword("123456");
				SpacemusicunifyBusinessFactory.getInstance().getPlayerService().add(user);
			}


			path = Paths.get(picturesFile);
			if (Files.notExists(path)) {
				if (new File(picturesFile).createNewFile()) System.out.println("file pictures creato correttamente");
				FileWriter writer = new FileWriter(new File(picturesFile));
				writer.write("1" + "\n");
				writer.close();
			}

			path = Paths.get(songsFile);
			if (Files.notExists(path)) {
				if (new File(songsFile).createNewFile()) System.out.println("file canzoni creato correttamente");
				FileWriter writer = new FileWriter(new File(songsFile));
				writer.write("1" + "\n");
				writer.close();
			}

			path = Paths.get(picturesDirectory);
			if (Files.notExists(path)) {
				if (new File(picturesDirectory).mkdirs()) System.out.println("cartella immagini creata correttamente");
			}

			path = Paths.get(mp3Directory);
			if (Files.notExists(path)) {
				if (new File(mp3Directory).mkdirs()) System.out.println("cartella filesMP3 creata correttamente");
			}
		} catch (IOException e) {
			throw new BusinessException("File creation Problem");
		}

	}

}
