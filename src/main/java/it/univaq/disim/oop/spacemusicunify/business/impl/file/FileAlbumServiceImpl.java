package it.univaq.disim.oop.spacemusicunify.business.impl.file;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import it.univaq.disim.oop.spacemusicunify.business.*;
import it.univaq.disim.oop.spacemusicunify.domain.*;
import javafx.util.Duration;

public class FileAlbumServiceImpl implements AlbumService {
	
	private String albumsFile;
	private String songsFile;
	private ArtistService artistService;
	private ProductionService productionService;
	
	public FileAlbumServiceImpl(String albumsFile, String songsFile) {
		this.albumsFile = albumsFile;
		this.songsFile = songsFile;
		SpacemusicunifyBusinessFactory factory = SpacemusicunifyBusinessFactory.getInstance();
		productionService = factory.getProductionService();
		artistService = factory.getArtistService();
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

			/*Set<Album> albumSet = album.getArtist().getDiscography();
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

			}*/
			try {
				add(canzone);
			} catch (BusinessException e) {
				e.printStackTrace();
			}

				//aggiorno il file degli artisti
				fileData = Utility.readAllRows(artistsFile);
				/*Artist artista = album.getArtist();*/
				int cont = 0;
				/*for (String[] righe : fileData.getRighe()) {
					if (righe[0].equals(artista.getId().toString())) {
						String[] row;
						List<String> albums = Utility.leggiArray(righe[6]);
						albums.add(album.getId().toString());
						row = new String[]{righe[0], righe[1], righe[2], righe[3], righe[4], righe[5], albums.toString()};

						fileData.getRighe().set(cont, row);
						break;
					}
					cont++;
				}*/
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
		/*for(Album albumCheck : album.getArtist().getDiscography()) {

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

					*//*Files.deleteIfExists(Paths.get(album.getCover()));*//*

				} catch (IOException e) {
					e.printStackTrace();
				}

				break;
			}
		}*/
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
					/*String[] row = {album.getId().toString(),album.getTitle(),album.getGenre().toString(), String.valueOf(cover.getId()),album.getRelease().toString(),album.getArtist().getId().toString(),listaCanzoni.toString()};
					fileData.getRighe().set(cont, row);*/
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
							/*String[] row = {album.getId().toString(),album.getTitle(),album.getGenre().toString(), String.valueOf(cover.getId()),album.getRelease().toString(),album.getArtist().getId().toString(),listaCanzoni.toString()};
							fileData.getRighe().set(cont, row);*/
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
	public List<Album> getAlbumList() throws BusinessException {
		List<Album> albumList = new ArrayList<>();
		try {
			FileData fileData = Utility.readAllRows(albumsFile);
			System.out.println("cerco album getAll");
			for (String[] righe : fileData.getRighe()) {
				albumList.add((Album) UtilityObjectRetriever.findObjectById(righe[0], albumsFile));
				System.out.println("aggiungo album getall");
			}
		} catch (IOException e) {
			e.printStackTrace();
			throw new BusinessException();
		}
		return albumList;
	}
	@Override
	public List<Song> getSongList() throws BusinessException{List<Song> songList = new ArrayList<>();
		try {
			FileData fileData = Utility.readAllRows(songsFile);
			System.out.println("cerco canzone");
			for(String[] righe : fileData.getRighe()) {
				songList.add((Song) UtilityObjectRetriever.findObjectById(righe[0], songsFile));
				System.out.println("aggiungo canzone");
			}
		} catch (IOException e) {
			e.printStackTrace();
			throw new BusinessException();
		}
		return songList;
	}
	@Override
	public List<Artist> findAllArtists(Album album) throws BusinessException {
		List<Artist> artists = artistService.getArtistaList();
		List<Artist> artistsFinal = new ArrayList<>();
		List<Production> productions = productionService.getAllProductions();
		for (Production production : productions){
			if (production.getAlbum().getId().intValue() == album.getId().intValue()){
				for (Artist artist : artists){
					if (artist.getId().intValue() == production.getArtist().getId().intValue()){
						artistsFinal.add(artist);
					}
				}
			}
		}
		return artistsFinal;
	}


}
