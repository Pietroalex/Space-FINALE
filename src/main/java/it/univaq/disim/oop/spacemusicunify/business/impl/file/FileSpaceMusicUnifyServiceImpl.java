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
	private final String playersFile;

	public FileSpaceMusicUnifyServiceImpl(String usersFileName, String albumsFileName, String artistsFileName, String songsFileName, String playlistsFileName, String picturesDirectoryName, String filesMp3DirectoryName, String picturesFileName, String audiosFileName, String productionsFileName, String playersFileName) {
		usersFile = usersFileName;
		albumsFile = albumsFileName;
		artistsFile = artistsFileName;
		songsFile = songsFileName;
		playlistFile = playlistsFileName;
		picturesFile = picturesFileName;
		picturesDirectory = picturesDirectoryName;
		mp3Directory = filesMp3DirectoryName;
		productionFile = productionsFileName;
		audiosFile = audiosFileName;
		playersFile = playersFileName;
	}
	@Override
	public void setAllDefaults() throws BusinessException {
		try {

			Path path = Paths.get(artistsFile);
			if (Files.notExists(path)) {
				if (!(new File(artistsFile).createNewFile())) throw new BusinessException("artistFile creation error");
				FileWriter writer = new FileWriter(new File(artistsFile));
				writer.write("1" + "\n");
				writer.close();
			}

			path = Paths.get(audiosFile);
			if (Files.notExists(path)) {
				if (!(new File(audiosFile).createNewFile())) throw new BusinessException("audiosFile creation error");
				FileWriter writer = new FileWriter(new File(audiosFile));
				writer.write("1" + "\n");
				writer.close();
			}
			path = Paths.get(productionFile);
			if (Files.notExists(path)) {
				if (!(new File(productionFile).createNewFile())) throw new BusinessException("productionFile creation error");
				FileWriter writer = new FileWriter(new File(productionFile));
				writer.write("1" + "\n");
				writer.close();
			}
			path = Paths.get(albumsFile);
			if (Files.notExists(path)) {
				if (!(new File(albumsFile).createNewFile())) throw new BusinessException("albumsFile creation error");
				FileWriter writer = new FileWriter(new File(albumsFile));
				writer.write("1" + "\n");
				writer.close();
			}
			path = Paths.get(playlistFile);
			if (Files.notExists(path)) {
				if (!(new File(playlistFile).createNewFile())) throw new BusinessException("playlistFile creation error");
				FileWriter writer = new FileWriter(new File(playlistFile));
				writer.write("1" + "\n");
				writer.close();
			}
			path = Paths.get(playersFile);
			if (Files.notExists(path)) {
				if (!(new File(playersFile).createNewFile())) throw new BusinessException("playersFile creation error");
				FileWriter writer = new FileWriter(new File(playersFile));
				writer.write("2" + "\n");
				writer.close();
			}
			path = Paths.get(usersFile);
			if (Files.notExists(path)) {
				if (!(new File(usersFile).createNewFile())) throw new BusinessException("usersFile creation error");

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
				if (!(new File(picturesFile).createNewFile())) throw new BusinessException("picturesFile creation error");
				FileWriter writer = new FileWriter(new File(picturesFile));
				writer.write("1" + "\n");
				writer.close();
			}

			path = Paths.get(songsFile);
			if (Files.notExists(path)) {
				if (!(new File(songsFile).createNewFile())) throw new BusinessException("songsFile creation error");
				FileWriter writer = new FileWriter(new File(songsFile));
				writer.write("1" + "\n");
				writer.close();
			}

			path = Paths.get(picturesDirectory);
			if (Files.notExists(path)) {
				if (!(new File(picturesDirectory).mkdirs())) throw new BusinessException("picturesDirectory creation error");
			}

			path = Paths.get(mp3Directory);
			if (Files.notExists(path)) {
				if (!(new File(mp3Directory).mkdirs())) throw new BusinessException("mp3Directory creation error");
			}
		} catch (IOException e) {
			throw new BusinessException("File creation Problem");
		}

	}

}
