package it.univaq.disim.oop.spacemusicunify.business.impl.file;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import it.univaq.disim.oop.spacemusicunify.business.*;
import it.univaq.disim.oop.spacemusicunify.domain.*;
import javafx.scene.image.Image;
import javafx.util.Duration;

public class FileSPACEMusicUnifyServiceImpl implements SPACEMusicUnifyService {

	private final String usersFile;
	private final String albumsFile;
	private final String artistsFile;
	private final String songsFile;
	private final String playlistFile;
	private final String picturesFile;
	private final String picturesDirectory;
	private final String mp3Directory;

	private static ViewSituations situation;
	private String ricerca;

	public FileSPACEMusicUnifyServiceImpl(String fileUtenti, String fileAlbums, String fileArtisti, String fileCanzoni, String filePlaylist, String cartellaImmagini, String cartellaFilesMP3, String filePictures) {
		this.usersFile = fileUtenti;
		this.albumsFile = fileAlbums;
		this.artistsFile = fileArtisti;
		this.songsFile = fileCanzoni;
		this.playlistFile = filePlaylist;
		this.picturesFile = filePictures;
		this.picturesDirectory = cartellaImmagini;
		this.mp3Directory = cartellaFilesMP3;
	}

	public String saveANDstore(String source, String type) throws UnsupportedFileExtensionException {
		String existCover = picturesDirectory + "cover";
		String existMp3 = mp3Directory + "audio";
		String existArtistph = picturesDirectory + type;

		String path = source;
		System.out.println(path);
		String str = path.substring(path.length()-4);
		String newfile;
		String product;
		switch (str){

			case ".png":

				if(!(type.equals("cover"))) {
					String newfilePng = null;
					int numberPng = 1;
					boolean fileNotExistPng = false;
					while (!fileNotExistPng) {

						newfilePng = existArtistph + numberPng + ".png";
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
				}else {
					String newfilePng = null;
					int numberPng = 1;
					boolean fileNotExistPng = false;
					while (!fileNotExistPng) {

						newfilePng = existCover + numberPng + ".png";
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
					product = "cover" + numberPng + ".png";
				}
				break;


			case ".jpg":

				if(!(type.equals("cover"))) {
					String newfileJpg = null;
					int numberJpg = 1;
					boolean fileNotExistJpg = false;
					while (!fileNotExistJpg) {

						newfileJpg = existArtistph + numberJpg + ".jpg";
						System.out.println("iterazione " + newfileJpg);
						if (Files.exists(Paths.get(newfileJpg))) {
							System.out.println("file exist with " + numberJpg);
							numberJpg++;
						} else {
							System.out.println("file not exist with " + numberJpg);
							fileNotExistJpg = true;
						}
					}
					System.out.println("last " + numberJpg);
					newfile = newfileJpg;
					product = type+numberJpg+".jpg";
				}else {
					String newfileJpg = null;
					int numberJpg = 1;
					boolean fileNotExistJpg = false;
					while (!fileNotExistJpg) {

						newfileJpg = existCover + numberJpg + ".jpg";
						System.out.println("iterazione " + newfileJpg);
						if (Files.exists(Paths.get(newfileJpg))) {
							System.out.println("file exist with " + numberJpg);
							numberJpg++;
						} else {
							System.out.println("file not exist with " + numberJpg);
							fileNotExistJpg = true;
						}
					}
					System.out.println("last " + numberJpg);
					newfile = newfileJpg;
					product = "cover" + numberJpg + ".jpg";
				}
				break;

			case ".mp3":

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

			default:
				throw new UnsupportedFileExtensionException();
		}
		try {
			Files.copy(Paths.get(path),
				(new File(newfile)).toPath(),
				StandardCopyOption.REPLACE_EXISTING);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		return product;
	}

	@Override
	public void add(User utente) throws  BusinessException {
		createNewUser(utente);
	}

	@Override
	public void modify(Integer id, String username, String password) throws BusinessException {
		try {
			FileData fileData = Utility.readAllRows(usersFile);
			for (String[] colonne : fileData.getRighe()) {
				if (colonne[2].equals(username) && Integer.parseInt(colonne[0]) != id ) {
					throw new AlreadyTakenFieldException();
				}
			}
			try (PrintWriter writer = new PrintWriter(new File(usersFile))) {
				writer.println(fileData.getContatore());
				for (String[] righe : fileData.getRighe()) {
					if (Long.parseLong(righe[0]) == id) {
						StringBuilder row = new StringBuilder();
						row.append(id);
						row.append(Utility.SEPARATORE_COLONNA);
						row.append(username);
						row.append(Utility.SEPARATORE_COLONNA);
						row.append(password);
						row.append(Utility.SEPARATORE_COLONNA);
						row.append(righe[4]);
						row.append(Utility.SEPARATORE_COLONNA);
						row.append(righe[5]);
						writer.println(row.toString());
					} else {
						writer.println(String.join(Utility.SEPARATORE_COLONNA, righe));
					}
				}

			}
		} catch (IOException e) {
			e.printStackTrace();
			throw new BusinessException(e);
		}
	}

	@Override
	public void delete(User utente) throws BusinessException {
		Boolean controllo = false;
		try {
			FileData fileData = Utility.readAllRows(usersFile);
			for (String[] colonne : fileData.getRighe()) {
				if ( Integer.parseInt(colonne[0]) == utente.getId() ) {
					controllo = true;
					break;
				}
			}

			if(controllo){
				try (PrintWriter writer = new PrintWriter(new File(usersFile))) {
					writer.println(fileData.getContatore());
					for (String[] righe : fileData.getRighe()) {
						if (Long.parseLong(righe[0]) != utente.getId()) {
							writer.println(String.join(Utility.SEPARATORE_COLONNA, righe));
						}
					}
				}
				//eliminazione delle playlists
				List<Playlist> playlists = getAllPlaylists(utente);
				for(Playlist playlist : playlists) {
					deletePlaylist(playlist);
				}
			}else{
				throw new BusinessException("Not existing User");
			}
		} catch (IOException e) {
			e.printStackTrace();
			throw new BusinessException(e);
		}
	}

	@Override
	public void add(Picture picture) throws BusinessException {
		try {
			FileData fileData = Utility.readAllRows(picturesFile);
/*			for (String[] colonne : fileData.getRighe()) {
				if (colonne[0].equals(picture.getId().toString())) {
					throw new AlreadyExistingException("Already existing picture");
				}
			}*/
			try (PrintWriter writer = new PrintWriter(new File(picturesFile))) {
				long contatore = fileData.getContatore();
				writer.println((contatore));
				for (String[] righe : fileData.getRighe()) {
					writer.println(String.join(Utility.SEPARATORE_COLONNA, righe));
				}
				StringBuilder row = new StringBuilder();
				row.append(contatore);
				picture.setId((int) contatore);
				row.append(Utility.SEPARATORE_COLONNA);
				File file = new File(String.valueOf(new ByteArrayInputStream(picture.getPhoto())));
				row.append(saveANDstore(file.getAbsolutePath(), "cover"));
				row.append(Utility.SEPARATORE_COLONNA);
				row.append(picture.getHeight());
				row.append(Utility.SEPARATORE_COLONNA);
				row.append(picture.getWidth());
				writer.println(row.toString());

			}
		} catch (IOException e) {
			e.printStackTrace();
			throw new BusinessException(e);
		}
	}

	@Override
	public void modify(Integer id, byte[] photo, int height, int width) throws BusinessException {

	}

	@Override
	public void delete(Picture picture) throws BusinessException {

	}

	@Override
	public void add(Artist artista) throws BusinessException {
		
		try {
			FileData fileDataArtisti = Utility.readAllRows(artistsFile);
			for(String[] colonne: fileDataArtisti.getRighe()) {
				if(colonne[1].equals(artista.getStageName())) {
					throw new AlreadyExistingException();
				}
			}
			
			
			FileData fileDataCanzoni = Utility.readAllRows(songsFile);
			
			FileData fileDataAlbum = Utility.readAllRows(albumsFile);
			
			List<String> albums = new ArrayList<>();
			albums.add(String.valueOf(fileDataAlbum.getContatore()));
			
			List<String> canzoneList = new ArrayList<>();
			canzoneList.add(String.valueOf(fileDataCanzoni.getContatore()));
			
			//creo l'album Inediti
			Album album = new Album();
			album.setTitle("Inediti");
			album.setGenre(Genre.singoli);
			/*album.setCover(saveANDstore(("src" + File.separator + "main" + File.separator + "resources" + File.separator + "dati" + File.separator + "RAMfiles" + File.separator + "cover.png"), "cover"));
			*/
			Picture picture = new Picture();
			picture.setId(5);
			album.setCover(picture);
			album.setRelease(LocalDate.now());
			
			//creo la canzone di default
			Song canzone = new Song();
			canzone.setTitle("Our Sympathy of "+artista.getStageName());
			canzone.setLyrics("ElDlive");
			canzone.setLength("04:02");
			canzone.setGenre(Genre.pop);
			/*canzone.setFileMp3(saveANDstore(("src" + File.separator + "main" + File.separator + "resources" + File.separator + "dati" + File.separator + "RAMfiles" + File.separator + "our_sympathy.mp3"), "audio"));
			*/
			//scrivo file artista
			try (PrintWriter writerArtista = new PrintWriter(new File(artistsFile))) {
				long contatore = fileDataArtisti.getContatore();
				writerArtista.println(contatore + 1);
				for (String[] righe : fileDataArtisti.getRighe()) {
					writerArtista.println(String.join(Utility.SEPARATORE_COLONNA, righe));
				}
				List<String> imageList = new ArrayList<>();
/*				for(String image : artista.getPictures()){
					imageList.add(saveANDstore(image, artista.getStageName()));
				}*/
				StringBuilder rowArtista = new StringBuilder();
				rowArtista.append(contatore);
				rowArtista.append(Utility.SEPARATORE_COLONNA);
				rowArtista.append(artista.getStageName());
				rowArtista.append(Utility.SEPARATORE_COLONNA);
				rowArtista.append(artista.getYearsOfActivity());
				rowArtista.append(Utility.SEPARATORE_COLONNA);
				rowArtista.append(artista.getBiography());
				rowArtista.append(Utility.SEPARATORE_COLONNA);
				rowArtista.append(artista.getNationality());
				rowArtista.append(Utility.SEPARATORE_COLONNA);
				rowArtista.append(imageList);
				rowArtista.append(Utility.SEPARATORE_COLONNA);
				rowArtista.append(albums);
				writerArtista.println(rowArtista.toString());
			}
			//scrivo file canzone
			try (PrintWriter writerCanzone = new PrintWriter(new File(songsFile))) {
				long contatore = fileDataCanzoni.getContatore();
				writerCanzone.println(contatore + 1);
				for (String[] righe : fileDataCanzoni.getRighe()) {
					writerCanzone.println(String.join(Utility.SEPARATORE_COLONNA, righe));
				}
				StringBuilder row = new StringBuilder();
				row.append(contatore);
				row.append(Utility.SEPARATORE_COLONNA);
				row.append(canzone.getTitle());
				row.append(Utility.SEPARATORE_COLONNA);
				row.append(canzone.getFileMp3());
				row.append(Utility.SEPARATORE_COLONNA);
				row.append(canzone.getLyrics());
				row.append(Utility.SEPARATORE_COLONNA);
				row.append(fileDataAlbum.getContatore());
				row.append(Utility.SEPARATORE_COLONNA);
				row.append(canzone.getLength());
				row.append(Utility.SEPARATORE_COLONNA);
				row.append(canzone.getGenre());
				writerCanzone.println(row.toString());
			}
			//scrivo file album
			try (PrintWriter writerAlbum = new PrintWriter(new File(albumsFile))) {
				long contatore = fileDataAlbum.getContatore();
				writerAlbum.println(contatore + 1);
				for (String[] righe : fileDataAlbum.getRighe()) {
					writerAlbum.println(String.join(Utility.SEPARATORE_COLONNA, righe));
				}
				StringBuilder row = new StringBuilder();
				row.append(contatore);
				row.append(Utility.SEPARATORE_COLONNA);
				row.append(album.getTitle());
				row.append(Utility.SEPARATORE_COLONNA);
				row.append(album.getGenre());
				row.append(Utility.SEPARATORE_COLONNA);
				row.append(album.getCover());
				row.append(Utility.SEPARATORE_COLONNA);
				row.append(album.getRelease());
				row.append(Utility.SEPARATORE_COLONNA);
				row.append(fileDataArtisti.getContatore());
				row.append(Utility.SEPARATORE_COLONNA);
				row.append(canzoneList);
				writerAlbum.println(row.toString());
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void modify(Integer id, String stageName, String biography, int yearsOfActivity, Nationality nationality, Set<Picture> images) throws BusinessException {
		try {
			System.out.println("Modifico artista");
			FileData fileData = Utility.readAllRows(artistsFile);
			for(String[] colonne: fileData.getRighe()) {
				if(colonne[1].equals(stageName) && !colonne[0].equals(id.toString())) {
					throw new AlreadyTakenFieldException();
				}
			}
			int cont = 0;
			for (String[] righe : fileData.getRighe()) {
				if (righe[0].equals(id.toString())) {
					List<String> imagelist = new ArrayList<>();
					List<String> savedImages = Utility.leggiArray(righe[5]);
					List<String> removedImages = new ArrayList<>();
					List<String> toAddImages = new ArrayList<>();
					/*for(Picture newImage : images){
						System.out.println("current new image "+ newImage+ " current image "+savedImages);
						for(String string : savedImages) {
							if (newImage.contains(string)) {
								System.out.println("già c'è");
								imagelist.add(string);
							}
						}
					}*/

					for(String image : savedImages){
						System.out.println("current check image "+ image+ " current image "+savedImages);
						boolean checktoremove = true;
						for(String string : imagelist) {

							if (string.contains(image)) {
								System.out.println(string+" presente");
								checktoremove = false;
								break;
							}
						}
						if(checktoremove){
							removedImages.add(image);
						}
					}

					/*for(String image : images){
						System.out.println("current check image "+ image+ " current image "+savedImages);
						boolean checktoadd = true;
						for(String string : savedImages) {

							if (image.contains(string)) {
								System.out.println(string+" presente");
								checktoadd = false;
								break;
							}
						}
						if(checktoadd){
							toAddImages.add(image);
						}
					}
					for(String image : removedImages){
						images.removeIf(image::equals);
						Files.deleteIfExists(Paths.get(cartellaImmagini+image));
					}
					System.out.println("lista finale "+imagelist);
					System.out.println("lista finale delete "+removedImages);
					System.out.println("lista finale toadd "+toAddImages);
					for (String toAdd : toAddImages) {
						imagelist.add(saveANDstore(toAdd, stageName));
					}*/
					String[] row = new String[]{righe[0], stageName,  String.valueOf(yearsOfActivity), biography, String.valueOf(nationality), imagelist.toString(), righe[6]};
					fileData.getRighe().set(cont, row);
					System.out.println("row "+row[5]);

					break;
				}
				cont++;
			}
			try (PrintWriter writer = new PrintWriter(new File(artistsFile))) {
				writer.println(fileData.getContatore());
				for (String[] righe : fileData.getRighe()) {
					writer.println(String.join(Utility.SEPARATORE_COLONNA, righe));
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
			throw new BusinessException();
		}
	}

	@Override
	public void delete(Artist artista) throws BusinessException {
		boolean check = false;
		System.out.println("artista da eliminare: "+artista.getId());
		try {
			FileData fileData = Utility.readAllRows(artistsFile);
			for(String[] righeCheck: fileData.getRighe()) {
				if(righeCheck[0].equals(artista.getId().toString())) {

					check = true;
					//aggiorno il file artisti.txt
					try (PrintWriter writer = new PrintWriter(new File(artistsFile))) {
						writer.println(fileData.getContatore());
						for (String[] righe : fileData.getRighe()) {
							if (righe[0].equals(artista.getId().toString())) {
								//jump line
								continue;
							} else {
								writer.println(String.join("§", righe));
							}
						}
					}
					List<Album> albumList = new ArrayList<>();
					//aggiorno il file album.txt
					for (Album controllo : artista.getDiscography()) {
						Album album = new Album();
						album.setId(controllo.getId());
						album.setArtist(controllo.getArtist());
						album.setCover(controllo.getCover());
						album.setTitle(controllo.getTitle());
						album.setGenre(controllo.getGenre());
						album.setSongList(controllo.getSongList());
						album.setRelease(controllo.getRelease());
						albumList.add(album);
					}
					for(Album album : albumList){
						delete(album);
					}
					/*for(String immagini : artista.getPictures()){
						Files.deleteIfExists(Paths.get(immagini));
					}*/
					break;
				}
			}
			if(!check)throw new BusinessException("artista inesistente");
		} catch (IOException e) {
		e.printStackTrace();
		}
	}

	@Override
	public void add(Album album) throws BusinessException {
		//album: title, genre, release, artist, cover
		for (Album album1 : getAllAlbums()) {
			if (album1.getTitle().equals(album.getTitle())) {
				System.out.println("controllo");
				throw new AlreadyExistingException();
			}
		}
/*		try {
			cover = saveANDstore(album.getCover(), "cover");
			album.setCover(cartellaImmagini+cover);
		} catch (UnsupportedFileExtensionException e1) {
			e1.printStackTrace();
		}*/
		FileData fileData;
		//scrivo il file album
		try {
			add(album.getCover());
			fileData = Utility.readAllRows(albumsFile);
			album.setId(Integer.parseInt(String.valueOf(fileData.getContatore())));
			//creo la canzone
			Song canzone = new Song();
			canzone.setAlbum(album);
			canzone.setTitle("new song of '" + album.getTitle() + "'");
			canzone.setLyrics("Lyrics");
			canzone.setLength("04:02");
			canzone.setGenre(album.getGenre());
			//canzone.setFileMp3("src" + File.separator + "main" + File.separator + "resources" + File.separator + "dati" + File.separator + "RAMfiles" + File.separator + "our_sympathy.mp3");

			Set<Album> albumSet = album.getArtist().getDiscography();
			albumSet.add(album);
			album.getArtist().setDiscography(albumSet);
			try (PrintWriter writerAlbum = new PrintWriter(new File(albumsFile))) {
				long contatore = fileData.getContatore();
				writerAlbum.println(contatore + 1);
				for (String[] righe : fileData.getRighe()) {
					writerAlbum.println(String.join(Utility.SEPARATORE_COLONNA, righe));
				}
				StringBuilder row = new StringBuilder();
				row.append(contatore);
				row.append(Utility.SEPARATORE_COLONNA);
				row.append(album.getTitle());
				row.append(Utility.SEPARATORE_COLONNA);
				row.append(album.getGenre());
				row.append(Utility.SEPARATORE_COLONNA);
				row.append(album.getCover().getId());
				row.append(Utility.SEPARATORE_COLONNA);
				row.append(album.getRelease());
				row.append(Utility.SEPARATORE_COLONNA);
				row.append(album.getArtist().getId());
				row.append(Utility.SEPARATORE_COLONNA);
				row.append("[]");
				writerAlbum.println(row.toString());

			}
			try {
				add(canzone);
			} catch (BusinessException e) {
				e.printStackTrace();
			}

				//aggiorno il file degli artisti
				fileData = Utility.readAllRows(artistsFile);
				Artist artista = album.getArtist();
				int cont = 0;
				for (String[] righe : fileData.getRighe()) {
					if (righe[0].equals(artista.getId().toString())) {
						String[] row;
						List<String> albums = Utility.leggiArray(righe[6]);
						albums.add(album.getId().toString());
						row = new String[]{righe[0], righe[1], righe[2], righe[3], righe[4], righe[5], albums.toString()};

						fileData.getRighe().set(cont, row);
						break;
					}
					cont++;
				}
				try (PrintWriter writer = new PrintWriter(new File(artistsFile))) {
					writer.println(fileData.getContatore());
					for (String[] righe : fileData.getRighe()) {
						writer.println(String.join(Utility.SEPARATORE_COLONNA, righe));
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
	}

	@Override
	public void modify(Integer id, String title, Genre genre, LocalDate release, Picture cover, Set<Song> songlist) throws BusinessException {
		String oldGenre = null;
		try {
			System.out.println("Modifico Album");
			FileData fileData = Utility.readAllRows(albumsFile);
			for(String[] colonne: fileData.getRighe()) {
				if(genre != Genre.singoli) {
					if (colonne[1].equals(title) && !colonne[0].equals(id.toString())) {
						throw new AlreadyTakenFieldException();
					}
				}
			}

			int cont = 0;
			for (String[] righe : fileData.getRighe()) {
				if (righe[0].equals(id.toString())) {
					oldGenre = righe[2];

					String[] row;
					/*if(cover.contains(righe[3])){
						System.out.println("contiene");
						 row = new String[]{righe[0], title, String.valueOf(genre), righe[3], String.valueOf(release), righe[5], righe[6]};
					}else{
						System.out.println("non contiene");
						Files.deleteIfExists(Paths.get(cartellaImmagini+righe[3]));
						row = new String[]{righe[0], title, String.valueOf(genre), saveANDstore(cover, "cover"), String.valueOf(release), righe[5], righe[6]};
					}*/
					row = new String[]{righe[0], title, String.valueOf(genre), String.valueOf(cover.getId()), String.valueOf(release), righe[5], righe[6]};
					fileData.getRighe().set(cont, row);


					break;
				}
				cont++;
			}
			try (PrintWriter writer = new PrintWriter(new File(albumsFile))) {
				writer.println(fileData.getContatore());
				for (String[] righe : fileData.getRighe()) {
					writer.println(String.join(Utility.SEPARATORE_COLONNA, righe));
				}
			}

			if(oldGenre != null && genre != Genre.singoli) {
				if (!oldGenre.equals(String.valueOf(genre)) ) {
					for (Song canzone : songlist) {
						canzone.setGenre(genre);
						modify(canzone.getId(), canzone.getTitle(), canzone.getLength(), canzone.getGenre(), canzone.getFileMp3(), canzone.getLyrics(), canzone.getAlbum());
					}
				}
			}
			songlist.iterator().next().getAlbum().setCover(cover);
			songlist.iterator().next().getAlbum().setTitle(title);
			songlist.iterator().next().getAlbum().setGenre(genre);
			songlist.iterator().next().getAlbum().setRelease(release);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void delete(Album album) throws BusinessException {
		boolean check = false;
		System.out.println("album da eliminare: "+album.getId());
		for(Album albumCheck : album.getArtist().getDiscography()) {

			if(album.getId().intValue() == albumCheck.getId().intValue()) {
				check = true;
				try {
					FileData fileData = Utility.readAllRows(albumsFile);
					//aggiorno il file album.txt
					try (PrintWriter writer = new PrintWriter(new File(albumsFile))) {
						writer.println(fileData.getContatore());
						for (String[] righe : fileData.getRighe()) {
							if (righe[0].equals(album.getId().toString())) {
								//jump line
								continue;
							} else {
								writer.println(String.join("§", righe));
							}
						}
					}
					//aggiorno il file artisti.txt
					fileData = Utility.readAllRows(artistsFile);
					Artist artista = album.getArtist();
					int cont = 0;
					for(String[] righe: fileData.getRighe()) {
						if(righe[0].equals(artista.getId().toString())) {
							List<String> listaAlbums = Utility.leggiArray(righe[6]);
							listaAlbums.remove(album.getId().toString());

							String[] row = {righe[0],righe[1],righe[2],righe[3],righe[4],righe[5],listaAlbums.toString()};
							fileData.getRighe().set(cont, row);
							break;
						}
						cont++;
					}
					try(PrintWriter writer = new PrintWriter(new File(artistsFile))){
						writer.println(fileData.getContatore());
						for (String[] righe : fileData.getRighe()) {
							writer.println(String.join(Utility.SEPARATORE_COLONNA, righe));
						}
					}
					for(Song canzone : album.getSongList()){
						delete(canzone);
					}

					Set<Album> albumSet = artista.getDiscography();
					albumSet.remove(album);
					artista.setDiscography(albumSet);

					/*Files.deleteIfExists(Paths.get(album.getCover()));*/

				} catch (IOException e) {
					e.printStackTrace();
				}

				break;
			}
		}
		if(!check)throw new BusinessException("album inesistente");
	}

	@Override
	public void add(Song canzone) throws BusinessException {
		try {
			FileData fileData = Utility.readAllRows(songsFile);
			for(String[] righe: fileData.getRighe()) {
				if(righe[1].equals(canzone.getTitle())) {
					throw new AlreadyExistingException();
				}
			}
			try(PrintWriter writer = new PrintWriter(new File(songsFile))){
				long contatore = fileData.getContatore();
				canzone.setId(Integer.parseInt(String.valueOf(contatore)));
				writer.println(contatore + 1);
				for (String[] righe : fileData.getRighe()) {
					writer.println(String.join(Utility.SEPARATORE_COLONNA, righe));
				}
				/*String tempMp3 = saveANDstore(canzone.getFileMp3(), "audio");
				canzone.setFileMp3(cartellaFilesMP3+tempMp3);*/
				String tempMp3 = "aaaaaa";
				StringBuilder row = new StringBuilder();
				row.append(contatore);
				row.append(Utility.SEPARATORE_COLONNA);
				row.append(canzone.getTitle());
				row.append(Utility.SEPARATORE_COLONNA);
				row.append(tempMp3);
				row.append(Utility.SEPARATORE_COLONNA);
				row.append(canzone.getLyrics());
				row.append(Utility.SEPARATORE_COLONNA);
				row.append(canzone.getAlbum().getId());
				row.append(Utility.SEPARATORE_COLONNA);
				row.append(canzone.getLength());
				row.append(Utility.SEPARATORE_COLONNA);
				row.append(canzone.getGenre());
				writer.println(row.toString());
			}
			//aggiorno l'album
			Album album = canzone.getAlbum();
			fileData = Utility.readAllRows(albumsFile);
			boolean check = false;
			int cont = 0;
			for(String[] righe: fileData.getRighe()) {
				if(righe[0].equals(album.getId().toString())) {
					check = true;
					List<String> listaCanzoni = Utility.leggiArray(righe[6]);
					listaCanzoni.add(canzone.getId().toString());
					Set<Song> canzoneList = album.getSongList();
					canzoneList.add(canzone);
					album.setSongList(canzoneList);
					Picture cover = album.getCover();
					/*cover = cover.substring(cover.indexOf("immagini" + File.separator)+ 9);*/
					String[] row = {album.getId().toString(),album.getTitle(),album.getGenre().toString(), String.valueOf(cover.getId()),album.getRelease().toString(),album.getArtist().getId().toString(),listaCanzoni.toString()};
					fileData.getRighe().set(cont, row);
					break;
				}
				cont++;
			}
			if(!check)throw new BusinessException("album non trovato");
			try(PrintWriter writer = new PrintWriter(new File(albumsFile))){
				writer.println(fileData.getContatore());
				for (String[] righe : fileData.getRighe()) {
					writer.println(String.join(Utility.SEPARATORE_COLONNA, righe));
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}

	@Override
	public void modify(Integer id, String title, String length, Genre genre, byte[] mp3, String lyrics, Album album)
			throws BusinessException {
		System.out.println("in esecuzione modify");
		boolean check = false;
		try {
			System.out.println("Modifico Canzone");
			FileData fileData = Utility.readAllRows(songsFile);
			for(String[] colonne: fileData.getRighe()) {
				if (colonne[1].equals(title) && !colonne[0].equals(id.toString())) {
					throw new AlreadyTakenFieldException();
				}
			}
			int cont = 0;
			for(String[] righe: fileData.getRighe()) {
				if(righe[0].equals(id.toString())) {
					String tempMp3 = righe[2];

					check = true;
					String[] row;
				/**
					if(mp3.contains(righe[2])){ 
						row = new String[]{id.toString(), title, righe[2], lyrics, righe[4], length, genre.toString()};
					}else{
						/*Files.delete(Paths.get(mp3Directory+righe[2]));*
						row = new String[]{id.toString(), title, tempMp3 /*= saveANDstore(mp3, "audio")*, lyrics, righe[4], length, genre.toString()};
					}
				*/
					Set<Song> list = album.getSongList();
					for(Song canzone: list){
						if(canzone.getId() == id){
							canzone.setId(id);
							canzone.setTitle(title);
							canzone.setLength(length);
							canzone.setGenre(genre);
							/*canzone.setFileMp3(mp3Directory+tempMp3);*/
							canzone.setLyrics(lyrics);
							canzone.setAlbum(album);
						}
					}
					album.setSongList(list);
					//fileData.getRighe().set(cont, row);
					break;
				}
				cont++;
			}
			if(!check)throw new BusinessException("canzone non trovata");
			try(PrintWriter writer = new PrintWriter(new File(songsFile))){
				writer.println(fileData.getContatore());
				for (String[] righe : fileData.getRighe()) {
					writer.println(String.join(Utility.SEPARATORE_COLONNA, righe));
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	@Override
	public void delete(Song canzone) throws BusinessException {
		boolean check = false;
		System.out.println("canzone da eliminare: "+canzone.getId());
		for(Song song : getAllSongs()) {
			System.out.println("canzone in file: "+song.getId());
			if(canzone.getId().intValue() == song.getId().intValue()) {
				check = true;
				try {
					FileData fileData = Utility.readAllRows(songsFile);
					//aggiorno il file canzoni.txt
					try (PrintWriter writer = new PrintWriter(new File(songsFile))) {
						writer.println(fileData.getContatore());
						for (String[] righe : fileData.getRighe()) {
							if (righe[0].equals(canzone.getId().toString())) {
								//jump line
								continue;
							} else {
								writer.println(String.join("§", righe));
							}
						}
					}
					//aggiorno il file albums.txt
					fileData = Utility.readAllRows(albumsFile);
					Album album = canzone.getAlbum();
					int cont = 0;
					for(String[] righe: fileData.getRighe()) {
						if(righe[0].equals(album.getId().toString())) {
							List<String> listaCanzoni = Utility.leggiArray(righe[6]);
							listaCanzoni.remove(canzone.getId().toString());
							Set<Song> canzoneList = album.getSongList();
							canzoneList.remove(canzone);
							album.setSongList(canzoneList);
							Picture cover = album.getCover();
							/*cover = cover.substring(cover.indexOf("immagini" + File.separator)+ 9);*/
							String[] row = {album.getId().toString(),album.getTitle(),album.getGenre().toString(), String.valueOf(cover.getId()),album.getRelease().toString(),album.getArtist().getId().toString(),listaCanzoni.toString()};
							fileData.getRighe().set(cont, row);
							break;
						}
						cont++;
					}
					try(PrintWriter writer = new PrintWriter(new File(albumsFile))){
						writer.println(fileData.getContatore());
						for (String[] righe : fileData.getRighe()) {
							writer.println(String.join(Utility.SEPARATORE_COLONNA, righe));
						}
					}
					fileData = Utility.readAllRows(playlistFile);

					cont = 0;
					for(String[] righe: fileData.getRighe()) {
						List<String> listaCanzoni = Utility.leggiArray(righe[3]);
						if(listaCanzoni.contains(canzone.getId().toString())) {
							listaCanzoni.remove(canzone.getId().toString());

							String[] row = {righe[0],righe[1],righe[2],listaCanzoni.toString()};
							fileData.getRighe().set(cont, row);
							break;
						}
						cont++;
					}
					try(PrintWriter writer = new PrintWriter(new File(playlistFile))){
						writer.println(fileData.getContatore());
						for (String[] righe : fileData.getRighe()) {
							writer.println(String.join(Utility.SEPARATORE_COLONNA, righe));
						}
					}
					
					fileData = Utility.readAllRows(usersFile);

					cont = 0;
					for(String[] righe: fileData.getRighe()) {
						if(!righe[1].equals("amministratore")){
							List<String> listaCanzoni = Utility.leggiArray(righe[5]);
							if(listaCanzoni.contains(canzone.getId().toString())) {
								if(canzone.getId().toString().equals(listaCanzoni.get(Integer.parseInt(righe[4])))){	//canzone in corso uguale a quella selezionata
									if(listaCanzoni.size() > 1 ){				//più canzoni in riproduzione

										if(Integer.parseInt(righe[4]) + 1 == listaCanzoni.size()){		//ultima canzone in coda uguale a canzone in corso
											 righe[4] = String.valueOf(Integer.parseInt(righe[4]) - 1);

										}else {																			//canzone corrente tra prima posizione e penultima
											if(Integer.parseInt(righe[4]) != 0){
												//canzone corrente non in prima posizione
												righe[4] = String.valueOf(Integer.parseInt(righe[4]) - 1);

											}else{												//canzone corrente in prima posizione
												MediaPlayerSettings.getInstance().setLastDuration(Duration.ZERO);
											}
										}

									}else{									//una sola canzone in riproduzione
										MediaPlayerSettings.getInstance().setLastDuration(Duration.ZERO);
									}
								}else{																				//canzone in corso diversa da quella selezionata

									System.out.println("canzone prima di quella selezionata");
									for(int i = 0; i < listaCanzoni.size(); i++ ){
										if(listaCanzoni.get(i).equals(canzone.getId().toString())){
											if (Integer.parseInt(righe[4]) > i ){
												System.out.println("canzone prima trovata ");

												righe[4] = String.valueOf(Integer.parseInt(righe[4]) - 1);
												break;
											}
										}
									}

								}
								listaCanzoni.remove(canzone.getId().toString());
								String[] row = {righe[0],righe[1],righe[2],righe[3], righe[4], listaCanzoni.toString()};
								fileData.getRighe().set(cont, row);
								break;
							}
						}
						cont++;
					}
					try(PrintWriter writer = new PrintWriter(new File(usersFile))){
						writer.println(fileData.getContatore());
						for (String[] righe : fileData.getRighe()) {
							writer.println(String.join(Utility.SEPARATORE_COLONNA, righe));
						}
					}
					
					//rimuovo il file mp3 dalla cartella
				//	Files.delete(Paths.get(canzone.getFileMp3()));
				} catch (IOException e) {
					e.printStackTrace();
				}
				
				break;
			}
		}
		if(!check)throw new BusinessException("canzone inesistente");
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
	@Override
	public List<Artist> getAllArtists() {
		List<Artist> artistaList = new ArrayList<>();
		try {
			FileData fileData = Utility.readAllRows(artistsFile);
			System.out.println("cerco artista getAll");
			for (String[] colonne : fileData.getRighe()) {
				artistaList.add((Artist) UtilityObjectRetriever.findObjectById(colonne[0], artistsFile));
				System.out.println("aggiungo artista getall");
			}

		} catch (IOException e) {
			e.printStackTrace();
		}

		return artistaList;
	}
	@Override
	public List<Album> getAllAlbums() {
		List<Album> albumList = new ArrayList<>();
		try {
			FileData fileData = Utility.readAllRows(albumsFile);
			System.out.println("cerco album getAll");
			for(String[] righe : fileData.getRighe()) {
				albumList.add((Album) UtilityObjectRetriever.findObjectById(righe[0], albumsFile));
				System.out.println("aggiungo album getall");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return albumList;
	}
	@Override
	public List<Song> getAllSongs() {
		List<Song> songList = new ArrayList<>();
		try {
			FileData fileData = Utility.readAllRows(songsFile);
			System.out.println("cerco canzone");
			for(String[] righe : fileData.getRighe()) {
				songList.add((Song) UtilityObjectRetriever.findObjectById(righe[0], songsFile));
				System.out.println("aggiungo canzone");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return songList;
	}
	@Override
	public void setSituation(ViewSituations sit) {
		situation = sit;
	}

	@Override
	public ViewSituations getSituation() {
		return situation;
	}

	@Override
	public GeneralUser authenticate(String username, String password) throws UtenteGenericoNotFoundException, BusinessException {
		try {
			FileData fileData = Utility.readAllRows(usersFile);
			for(String[] colonne : fileData.getRighe()) {
				if(colonne[2].equals(username) && colonne[3].equals(password)) {
					GeneralUser utente = null;
					switch (colonne[1]) {
						case "utente" :
							utente = new User();

							//((User) utente).setcurrentPosition(Integer.parseInt(colonne[4]));
							List<String> songQueue = Utility.leggiArray(colonne[5]);
							List<Song> queueList = new ArrayList<>();
							for(String string : songQueue) {
								queueList.add((Song) UtilityObjectRetriever.findObjectById(string, songsFile));
							}
							//((User) utente).setSongQueue(queueList);
							break;

						case "amministratore" :
							utente = new Administrator();
							break;

						default :
							break;
					}
					if(utente != null) {
						utente.setId(Integer.parseInt(colonne[0]));
						utente.setUsername(colonne[2]);
						utente.setPassword(colonne[3]);
					} else { throw new BusinessException("errore nella lettura del file"); }
					return utente;
				}
			}
			throw new UtenteGenericoNotFoundException();
		} catch (IOException e) {
			e.printStackTrace();
			throw new BusinessException();
		}
	}

	@Override
	public List<User> getAllUsers() {
		List<User> utenteList = new ArrayList<>();
		try {
			FileData fileData = Utility.readAllRows(usersFile);
			fileData.getRighe().remove(0);

			for (String[] colonne : fileData.getRighe()) {
				User utente = new User();
				utente.setId(Integer.parseInt(colonne[0]));
				utente.setUsername(colonne[2]);
				utente.setPassword(colonne[3]);
				//((User) utente).setcurrentPosition(Integer.parseInt(colonne[4]));
				List<String> songQueue = Utility.leggiArray(colonne[5]);
				List<Song> queueList = new ArrayList<>();
				for(String string : songQueue) {
					queueList.add((Song) UtilityObjectRetriever.findObjectById(string, songsFile));
				}
				//((User) utente).setSongQueue(queueList);
				utenteList.add(utente);
			}

		} catch (IOException e) {
			e.printStackTrace();
		}

		return utenteList;
	}

	@Override
	public void createNewUser(User utente) throws AlreadyExistingException, BusinessException {
		try {
			FileData fileData = Utility.readAllRows(usersFile);
			for (String[] colonne : fileData.getRighe()) {
				if (colonne[2].equals(utente.getUsername())) {
					throw new AlreadyExistingException("Already existing user");
				}
			}
			try (PrintWriter writer = new PrintWriter(new File(usersFile))) {
				long contatore = fileData.getContatore();
				writer.println((contatore + 1));
				for (String[] righe : fileData.getRighe()) {
					writer.println(String.join(Utility.SEPARATORE_COLONNA, righe));
				}
				StringBuilder row = new StringBuilder();
				row.append(contatore);
				row.append(Utility.SEPARATORE_COLONNA);
				row.append(utente.getUsername());
				row.append(Utility.SEPARATORE_COLONNA);
				row.append(utente.getPassword());
				row.append(Utility.SEPARATORE_COLONNA);
				row.append("0");
				row.append(Utility.SEPARATORE_COLONNA);
				row.append("[]");
				writer.println(row.toString());

			}
		} catch (IOException e) {
			e.printStackTrace();
			throw new BusinessException(e);
		}
	}





	@Override
	public void addNewPlaylist(Playlist playlist) throws BusinessException {
		try {
			FileData fileData = Utility.readAllRows(playlistFile);
			for (String[] colonne : fileData.getRighe()) {
				if (colonne[2].equals(playlist.getTitle())) {
					throw new AlreadyExistingException("Already existing Playlist");
				}
			}
			try (PrintWriter writer = new PrintWriter(new File(playlistFile))) {
				long contatore = fileData.getContatore();
				writer.println((contatore + 1));
				for (String[] righe : fileData.getRighe()) {
					writer.println(String.join(Utility.SEPARATORE_COLONNA, righe));
				}
				StringBuilder row = new StringBuilder();
				row.append(contatore);
				row.append(Utility.SEPARATORE_COLONNA);
				row.append(playlist.getTitle());
				row.append(Utility.SEPARATORE_COLONNA);
				row.append(playlist.getUser().getId());
				row.append(Utility.SEPARATORE_COLONNA);
				row.append(playlist.getSongList());
				writer.println(row.toString());

			}
		} catch (IOException e) {
			e.printStackTrace();
			throw new BusinessException(e);
		}
	}

	@Override
	public void modify(Integer id, String title, Set<Song> songlist, User user) throws  BusinessException {
		try {
			FileData fileData = Utility.readAllRows(playlistFile);
			List<String> idSongList = new ArrayList<>();
			for(Song canzone : songlist) {
				idSongList.add(canzone.getId().toString());
			}
			try (PrintWriter writer = new PrintWriter(new File(playlistFile))) {
				writer.println(fileData.getContatore());
				for (String[] righe : fileData.getRighe()) {
					if (Long.parseLong(righe[0]) == id) {
						StringBuilder row = new StringBuilder();
						row.append(id);
						row.append(Utility.SEPARATORE_COLONNA);
						row.append(title);
						row.append(Utility.SEPARATORE_COLONNA);
						row.append(user.getId());
						row.append(Utility.SEPARATORE_COLONNA);
						row.append(idSongList);

						writer.println(row.toString());
					} else {
						writer.println(String.join(Utility.SEPARATORE_COLONNA, righe));
					}
				}

			}
		} catch (IOException e) {
			e.printStackTrace();
			throw new BusinessException(e);
		}
	}
	@Override
	public void deletePlaylist(Playlist playlist) throws BusinessException {
		boolean check = false;
		try {
			FileData fileData = Utility.readAllRows(playlistFile);
			for (String[] righecontrollo : fileData.getRighe()) {
				if (righecontrollo[0].equals(playlist.getId().toString())) {
					check = true;

					try (PrintWriter writer = new PrintWriter(new File(playlistFile))) {
						writer.println(fileData.getContatore());
						for (String[] righe : fileData.getRighe()) {
							if (righe[0].equals(playlist.getId().toString())) {
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
		} catch(IOException e){
			e.printStackTrace();
		}

		if(!check)throw new BusinessException("Playlist inesistente");

	}
	@Override
	public List<Playlist> getAllPlaylists(User utente) {
		List<Playlist> playlistsUtente = new ArrayList<>();
		try {
			FileData fileData = Utility.readAllRows(playlistFile);

			for (String[] colonne : fileData.getRighe()) {

				if(colonne[2].equals(utente.getId().toString())) {
					Playlist playlist = new Playlist();
					playlist.setId(Integer.parseInt(colonne[0]));
					playlist.setTitle(colonne[1]);
					playlist.setUser(utente);
					Set<Song> listaCanzoni = new HashSet<>();
					List<String> songList = Utility.leggiArray(colonne[3]);

					if(!(songList.contains("")	)){
						for (String canzoneString : songList) {
							listaCanzoni.add((Song) UtilityObjectRetriever.findObjectById(canzoneString, songsFile));
						}
					}
					playlist.setSongList(listaCanzoni);

					playlistsUtente.add(playlist);
				}


			}

		} catch (IOException e) {
			e.printStackTrace();
		}
		return playlistsUtente;
	}



	@Override
	public String getRicerca() {
		return ricerca;
	}
	@Override
	public void setRicerca(String ricerca) {
		this.ricerca = ricerca;
	}

}
