package it.univaq.disim.oop.spacemusicunify.business.impl.file;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import it.univaq.disim.oop.spacemusicunify.business.*;
import it.univaq.disim.oop.spacemusicunify.domain.*;
import javafx.scene.image.Image;
import javafx.util.Duration;

public class FileSPACEMusicUnifyServiceImpl implements SPACEMusicUnifyService {

	private String fileUtenti;
	private String fileAlbums;
	private String fileArtisti;
	private String fileCanzoni;
	private String filePlaylist;
	private String cartellaImmagini;
	private String cartellaFilesMP3;

	public FileSPACEMusicUnifyServiceImpl(String fileUtenti, String fileAlbums, String fileArtisti, String fileCanzoni, String filePlaylist, String cartellaImmagini, String cartellaFilesMP3) {
		this.fileUtenti = fileUtenti;
		this.fileAlbums = fileAlbums;
		this.fileArtisti = fileArtisti;
		this.fileCanzoni = fileCanzoni;
		this.filePlaylist = filePlaylist;
		this.cartellaImmagini = cartellaImmagini;
		this.cartellaFilesMP3 = cartellaFilesMP3;
	}

	public String saveANDstore(String source, String type) throws UnsupportedFileExtensionException {
		String existCover = cartellaImmagini + "cover";
		String existMp3 = cartellaFilesMP3 + "audio";
		String existArtistph = cartellaImmagini + type;

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
	public void add(Utente utente) throws  BusinessException {
		UtenteGenericoService utenteService = SpacemusicunifyBusinessFactory.getInstance().getUtenteGenerico();
		utenteService.createNewUser(utente);
	}

	@Override
	public void modify(Integer id, String username, String password) throws BusinessException {
		try {
			FileData fileData = Utility.readAllRows(fileUtenti);
			for (String[] colonne : fileData.getRighe()) {
				if (colonne[2].equals(username) && Integer.parseInt(colonne[0]) != id ) {
					throw new AlreadyTakenFieldException();
				}
			}
			try (PrintWriter writer = new PrintWriter(new File(fileUtenti))) {
				writer.println(fileData.getContatore());
				for (String[] righe : fileData.getRighe()) {
					if (Long.parseLong(righe[0]) == id) {
						StringBuilder row = new StringBuilder();
						row.append(id);
						row.append(Utility.SEPARATORE_COLONNA);
						row.append(TipologiaUtente.utente.toString());
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
	public void delete(Utente utente) throws BusinessException {
		UtenteService utenteService = SpacemusicunifyBusinessFactory.getInstance().getUtenteService();
		Boolean controllo = false;
		try {
			FileData fileData = Utility.readAllRows(fileUtenti);
			for (String[] colonne : fileData.getRighe()) {
				if ( Integer.parseInt(colonne[0]) == utente.getId() ) {
					controllo = true;
					break;
				}
			}

			if(controllo){
				try (PrintWriter writer = new PrintWriter(new File(fileUtenti))) {
					writer.println(fileData.getContatore());
					for (String[] righe : fileData.getRighe()) {
						if (Long.parseLong(righe[0]) != utente.getId()) {
							writer.println(String.join(Utility.SEPARATORE_COLONNA, righe));
						}
					}
				}
				//eliminazione delle playlists
				List<Playlist> playlists = utenteService.getAllPlaylists(utente);
				for(Playlist playlist : playlists) {
					utenteService.deletePlaylist(playlist);
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
	public void add(Artista artista) throws BusinessException {
		
		try {
			FileData fileDataArtisti = Utility.readAllRows(fileArtisti);
			for(String[] colonne: fileDataArtisti.getRighe()) {
				if(colonne[1].equals(artista.getStageName())) {
					throw new AlreadyExistingException();
				}
			}
			
			
			FileData fileDataCanzoni = Utility.readAllRows(fileCanzoni);
			
			FileData fileDataAlbum = Utility.readAllRows(fileAlbums);
			
			List<String> albums = new ArrayList<>();
			albums.add(String.valueOf(fileDataAlbum.getContatore()));
			
			List<String> canzoneList = new ArrayList<>();
			canzoneList.add(String.valueOf(fileDataCanzoni.getContatore()));
			
			//creo l'album Inediti
			Album album = new Album();
			album.setTitle("Inediti");
			album.setGenre(Genere.singoli);
			/*album.setCover(saveANDstore(("src" + File.separator + "main" + File.separator + "resources" + File.separator + "viste" + File.separator + "RAMfiles" + File.separator + "cover.png"), "cover"));
			*/
			Picture picture = new Picture();
			picture.setId(5);
			album.setCover(picture);
			album.setRelease(LocalDate.now());
			
			//creo la canzone di default
			Canzone canzone = new Canzone();
			canzone.setTitle("Our Sympathy of "+artista.getStageName());
			canzone.setLyrics("ElDlive");
			canzone.setLength("04:02");
			canzone.setGenre(Genere.pop);
			canzone.setFileMp3(saveANDstore(("src" + File.separator + "main" + File.separator + "resources" + File.separator + "viste" + File.separator + "RAMfiles" + File.separator + "our_sympathy.mp3"), "audio"));
			
			//scrivo file artista
			try (PrintWriter writerArtista = new PrintWriter(new File(fileArtisti))) {
				long contatore = fileDataArtisti.getContatore();
				writerArtista.println(contatore + 1);
				for (String[] righe : fileDataArtisti.getRighe()) {
					writerArtista.println(String.join(Utility.SEPARATORE_COLONNA, righe));
				}
				List<String> imageList = new ArrayList<>();
				for(String image : artista.getPictures()){
					imageList.add(saveANDstore(image, artista.getStageName()));
				}
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
			try (PrintWriter writerCanzone = new PrintWriter(new File(fileCanzoni))) {
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
			try (PrintWriter writerAlbum = new PrintWriter(new File(fileAlbums))) {
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
	public void modify(Integer id, String stageName, String biography, int yearsOfActivity, Nazionalità nationality, List<String> images) throws BusinessException {
		try {
			System.out.println("Modifico artista");
			FileData fileData = Utility.readAllRows(fileArtisti);
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
					for(String newImage : images){
						System.out.println("current new image "+ newImage+ " current image "+savedImages);
						for(String string : savedImages) {
							if (newImage.contains(string)) {
								System.out.println("già c'è");
								imagelist.add(string);
							}
						}
					}

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

					for(String image : images){
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
					}
					String[] row = new String[]{righe[0], stageName,  String.valueOf(yearsOfActivity), biography, String.valueOf(nationality), imagelist.toString(), righe[6]};
					fileData.getRighe().set(cont, row);
					System.out.println("row "+row[5]);

					break;
				}
				cont++;
			}
			try (PrintWriter writer = new PrintWriter(new File(fileArtisti))) {
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
	public void delete(Artista artista) throws BusinessException {
		boolean check = false;
		System.out.println("artista da eliminare: "+artista.getId());
		try {
			FileData fileData = Utility.readAllRows(fileArtisti);
			for(String[] righeCheck: fileData.getRighe()) {
				if(righeCheck[0].equals(artista.getId().toString())) {

					check = true;
					//aggiorno il file artisti.txt
					try (PrintWriter writer = new PrintWriter(new File(fileArtisti))) {
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
					for(String immagini : artista.getPictures()){
						Files.deleteIfExists(Paths.get(immagini));
					}
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
		Picture cover = null;
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
		cover = album.getCover();
		FileData fileData;
		//scrivo il file album
		try {
			fileData = Utility.readAllRows(fileAlbums);
			album.setId(Integer.parseInt(String.valueOf(fileData.getContatore())));
			//creo la canzone
			Canzone canzone = new Canzone();
			canzone.setAlbum(album);
			canzone.setTitle("new song of '" + album.getTitle() + "'");
			canzone.setLyrics("Lyrics");
			canzone.setLength("04:02");
			canzone.setGenre(album.getGenre());
			canzone.setFileMp3("src" + File.separator + "main" + File.separator + "resources" + File.separator + "viste" + File.separator + "RAMfiles" + File.separator + "our_sympathy.mp3");

			Set<Album> albumSet = album.getArtist().getDiscography();
			albumSet.add(album);
			album.getArtist().setDiscography(albumSet);
			try (PrintWriter writerAlbum = new PrintWriter(new File(fileAlbums))) {
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
				row.append(cover);
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
				fileData = Utility.readAllRows(fileArtisti);
				Artista artista = album.getArtist();
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
				try (PrintWriter writer = new PrintWriter(new File(fileArtisti))) {
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
	public void modify(Integer id, String title, Genere genre, LocalDate release, Picture cover, List<Canzone> songlist) throws BusinessException {
		String oldGenre = null;
		try {
			System.out.println("Modifico Album");
			FileData fileData = Utility.readAllRows(fileAlbums);
			for(String[] colonne: fileData.getRighe()) {
				if(genre != Genere.singoli) {
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
			try (PrintWriter writer = new PrintWriter(new File(fileAlbums))) {
				writer.println(fileData.getContatore());
				for (String[] righe : fileData.getRighe()) {
					writer.println(String.join(Utility.SEPARATORE_COLONNA, righe));
				}
			}

			if(oldGenre != null && genre != Genere.singoli) {
				if (!oldGenre.equals(String.valueOf(genre)) ) {
					for (Canzone canzone : songlist) {
						canzone.setGenre(genre);
						modify(canzone.getId(), canzone.getTitle(), canzone.getLength(), canzone.getGenre(), canzone.getFileMp3(), canzone.getLyrics(), canzone.getAlbum());
					}
				}
			}
			songlist.get(0).getAlbum().setCover(cover);
			songlist.get(0).getAlbum().setTitle(title);
			songlist.get(0).getAlbum().setGenre(genre);
			songlist.get(0).getAlbum().setRelease(release);
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
					FileData fileData = Utility.readAllRows(fileAlbums);
					//aggiorno il file album.txt
					try (PrintWriter writer = new PrintWriter(new File(fileAlbums))) {
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
					fileData = Utility.readAllRows(fileArtisti);
					Artista artista = album.getArtist();
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
					try(PrintWriter writer = new PrintWriter(new File(fileArtisti))){
						writer.println(fileData.getContatore());
						for (String[] righe : fileData.getRighe()) {
							writer.println(String.join(Utility.SEPARATORE_COLONNA, righe));
						}
					}
					for(Canzone canzone : album.getSongList()){
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
	public void add(Canzone canzone) throws BusinessException {
		try {
			FileData fileData = Utility.readAllRows(fileCanzoni);
			for(String[] righe: fileData.getRighe()) {
				if(righe[1].equals(canzone.getTitle())) {
					throw new AlreadyExistingException();
				}
			}
			try(PrintWriter writer = new PrintWriter(new File(fileCanzoni))){
				long contatore = fileData.getContatore();
				canzone.setId(Integer.parseInt(String.valueOf(contatore)));
				writer.println(contatore + 1);
				for (String[] righe : fileData.getRighe()) {
					writer.println(String.join(Utility.SEPARATORE_COLONNA, righe));
				}
				String tempMp3 = saveANDstore(canzone.getFileMp3(), "audio");
				canzone.setFileMp3(cartellaFilesMP3+tempMp3);
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
			fileData = Utility.readAllRows(fileAlbums);
			boolean check = false;
			int cont = 0;
			for(String[] righe: fileData.getRighe()) {
				if(righe[0].equals(album.getId().toString())) {
					check = true;
					List<String> listaCanzoni = Utility.leggiArray(righe[6]);
					listaCanzoni.add(canzone.getId().toString());
					List<Canzone> canzoneList = album.getSongList();
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
			try(PrintWriter writer = new PrintWriter(new File(fileAlbums))){
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
	public void modify(Integer id, String title, String length, Genere genre, String mp3, String lyrics, Album album)
			throws BusinessException {
		System.out.println("in esecuzione modify");
		boolean check = false;
		try {
			System.out.println("Modifico Canzone");
			FileData fileData = Utility.readAllRows(fileCanzoni);
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
					if(mp3.contains(righe[2])){
						row = new String[]{id.toString(), title, righe[2], lyrics, righe[4], length, genre.toString()};
					}else{
						Files.delete(Paths.get(cartellaFilesMP3+righe[2]));
						row = new String[]{id.toString(), title, tempMp3 = saveANDstore(mp3, "audio"), lyrics, righe[4], length, genre.toString()};
					}
					List<Canzone> list = album.getSongList();
					for(Canzone canzone: list){
						if(canzone.getId() == id){
							canzone.setId(id);
							canzone.setTitle(title);
							canzone.setLength(length);
							canzone.setGenre(genre);
							canzone.setFileMp3(cartellaFilesMP3+tempMp3);
							canzone.setLyrics(lyrics);
							canzone.setAlbum(album);
						}
					}
					album.setSongList(list);
					fileData.getRighe().set(cont, row);
					break;
				}
				cont++;
			}
			if(!check)throw new BusinessException("canzone non trovata");
			try(PrintWriter writer = new PrintWriter(new File(fileCanzoni))){
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
	public void delete(Canzone canzone) throws BusinessException {
		boolean check = false;
		System.out.println("canzone da eliminare: "+canzone.getId());
		for(Canzone song : getAllSongs()) {
			System.out.println("canzone in file: "+song.getId());
			if(canzone.getId().intValue() == song.getId().intValue()) {
				check = true;
				try {
					FileData fileData = Utility.readAllRows(fileCanzoni);
					//aggiorno il file canzoni.txt
					try (PrintWriter writer = new PrintWriter(new File(fileCanzoni))) {
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
					fileData = Utility.readAllRows(fileAlbums);
					Album album = canzone.getAlbum();
					int cont = 0;
					for(String[] righe: fileData.getRighe()) {
						if(righe[0].equals(album.getId().toString())) {
							List<String> listaCanzoni = Utility.leggiArray(righe[6]);
							listaCanzoni.remove(canzone.getId().toString());
							List<Canzone> canzoneList = album.getSongList();
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
					try(PrintWriter writer = new PrintWriter(new File(fileAlbums))){
						writer.println(fileData.getContatore());
						for (String[] righe : fileData.getRighe()) {
							writer.println(String.join(Utility.SEPARATORE_COLONNA, righe));
						}
					}
					fileData = Utility.readAllRows(filePlaylist);

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
					try(PrintWriter writer = new PrintWriter(new File(filePlaylist))){
						writer.println(fileData.getContatore());
						for (String[] righe : fileData.getRighe()) {
							writer.println(String.join(Utility.SEPARATORE_COLONNA, righe));
						}
					}
					
					fileData = Utility.readAllRows(fileUtenti);

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
					try(PrintWriter writer = new PrintWriter(new File(fileUtenti))){
						writer.println(fileData.getContatore());
						for (String[] righe : fileData.getRighe()) {
							writer.println(String.join(Utility.SEPARATORE_COLONNA, righe));
						}
					}
					
					//rimuovo il file mp3 dalla cartella
					Files.delete(Paths.get(canzone.getFileMp3()));
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
		Path path = Paths.get(fileUtenti);
		if (Files.notExists(path)) {
			try {
				if (new File(fileUtenti).createNewFile()) System.out.println("file utenti creato");

				FileWriter writer = new FileWriter(new File(fileUtenti));
				writer.write("3" + "\n");
				writer.write("1§amministratore§admin§admin" + "\n");
				writer.write("2§utente§utente§123456§0§[]");
				writer.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		path = Paths.get(fileAlbums);
		if (Files.notExists(path)) {
			try {
				if (new File(fileAlbums).createNewFile()) System.out.println("file album creato correttamente");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		path = Paths.get(fileArtisti);
		if (Files.notExists(path)) {
			try {
				if (new File(fileArtisti).createNewFile()) System.out.println("file artisti creato correttamente");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		path = Paths.get(fileCanzoni);
		if (Files.notExists(path)) {
			try {
				if (new File(fileCanzoni).createNewFile()) System.out.println("file canzoni creato correttamente");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		path = Paths.get(cartellaImmagini);
		if (Files.notExists(path)) {
			if (new File(cartellaImmagini).mkdirs()) System.out.println("cartella immagini creata correttamente");
		}
		
		path = Paths.get(cartellaFilesMP3);
		if (Files.notExists(path)) {
			if (new File(cartellaFilesMP3).mkdirs()) System.out.println("cartella filesMP3 creata correttamente");
		}
		
	}
	@Override
	public List<Artista> getAllArtists() {
		List<Artista> artistaList = new ArrayList<>();
		try {
			FileData fileData = Utility.readAllRows(fileArtisti);
			System.out.println("cerco artista getAll");
			for (String[] colonne : fileData.getRighe()) {
				artistaList.add((Artista) UtilityObjectRetriever.findObjectById(colonne[0], fileArtisti));
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
			FileData fileData = Utility.readAllRows(fileAlbums);
			System.out.println("cerco album getAll");
			for(String[] righe : fileData.getRighe()) {
				albumList.add((Album) UtilityObjectRetriever.findObjectById(righe[0], fileAlbums));
				System.out.println("aggiungo album getall");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return albumList;
	}
	@Override
	public List<Canzone> getAllSongs() {
		List<Canzone> songList = new ArrayList<>();
		try {
			FileData fileData = Utility.readAllRows(fileCanzoni);
			System.out.println("cerco canzone");
			for(String[] righe : fileData.getRighe()) {
				songList.add((Canzone) UtilityObjectRetriever.findObjectById(righe[0], fileCanzoni));
				System.out.println("aggiungo canzone");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return songList;
	}

	@Override
	public void addNewPlaylist(Playlist playlist) throws BusinessException {

	}

	@Override
	public void modify(Integer id, String title, List<Canzone> songlist, Utente user) throws BusinessException {

	}

	@Override
	public List<Playlist> getAllPlaylists(Utente utente) throws BusinessException {
		return null;
	}

	@Override
	public void deletePlaylist(Playlist playlist) throws BusinessException {

	}

	@Override
	public String getRicerca() {
		return null;
	}

	@Override
	public void setRicerca(String ricerca) {

	}

	@Override
	public void addSongToQueue(Utente utente, Canzone canzone) {

	}

	@Override
	public void deleteSongFromQueue(Utente utente, Canzone canzone) {

	}

	@Override
	public void updateCurrentSong(Utente utente, int position) {

	}

	@Override
	public void replaceCurrentSong(Utente utente, Canzone canzone) {

	}

	@Override
	public void setSituation(ViewSituations sit) {

	}

	@Override
	public ViewSituations getSituation() {
		return null;
	}

	@Override
	public UtenteGenerico authenticate(String username, String password) throws BusinessException {
		return null;
	}

	@Override
	public List<Utente> getAllUsers() {
		return null;
	}

	@Override
	public void createNewUser(Utente utente) throws BusinessException {

	}

}
