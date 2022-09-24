package it.univaq.disim.oop.spacemusicunify.business.impl.file;

import it.univaq.disim.oop.spacemusicunify.business.*;
import it.univaq.disim.oop.spacemusicunify.domain.*;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class FileSpaceMusicUnifyServiceImpl implements SPACEMusicUnifyService {

	private final String usersFile;
	private final String albumsFile;
	private final String artistsFile;
	private final String songsFile;
	private final String playlistFile;
	private final String picturesFile;
	private final String picturesDirectory;
	private final String mp3Directory;


	private String ricerca;

	public FileSpaceMusicUnifyServiceImpl(String fileUtenti, String fileAlbums, String fileArtisti, String fileCanzoni, String filePlaylist, String cartellaImmagini, String cartellaFilesMP3, String filePictures) {
		this.usersFile = fileUtenti;
		this.albumsFile = fileAlbums;
		this.artistsFile = fileArtisti;
		this.songsFile = fileCanzoni;
		this.playlistFile = filePlaylist;
		this.picturesFile = filePictures;
		this.picturesDirectory = cartellaImmagini;
		this.mp3Directory = cartellaFilesMP3;
	}




	
	@Override
	public void setAllDefaults() {
		Path path = Paths.get(usersFile);
		if (Files.notExists(path)) {
			try {
				if (new File(usersFile).createNewFile()) System.out.println("file utenti creato");

				FileWriter writer = new FileWriter(new File(usersFile));
				writer.write("3" + "\n");
				writer.write("1§amministratore§admin§admin" + "\n");
				writer.write("2§utente§utente§123456§0§[]");
				writer.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		path = Paths.get(albumsFile);
		if (Files.notExists(path)) {
			try {
				if (new File(albumsFile).createNewFile()) System.out.println("file album creato correttamente");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		path = Paths.get(artistsFile);
		if (Files.notExists(path)) {
			try {
				if (new File(artistsFile).createNewFile()) System.out.println("file artisti creato correttamente");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		path = Paths.get(picturesFile);
		if (Files.notExists(path)) {
			try {
				if (new File(picturesFile).createNewFile()) System.out.println("file pictures creato correttamente");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		path = Paths.get(songsFile);
		if (Files.notExists(path)) {
			try {
				if (new File(songsFile).createNewFile()) System.out.println("file canzoni creato correttamente");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		path = Paths.get(picturesDirectory);
		if (Files.notExists(path)) {
			if (new File(picturesDirectory).mkdirs()) System.out.println("cartella immagini creata correttamente");
		}
		
		path = Paths.get(mp3Directory);
		if (Files.notExists(path)) {
			if (new File(mp3Directory).mkdirs()) System.out.println("cartella filesMP3 creata correttamente");
		}
		
	}

}
