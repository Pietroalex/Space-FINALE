package it.univaq.disim.oop.spacemusicunify.business.impl.file;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import it.univaq.disim.oop.spacemusicunify.business.*;
import it.univaq.disim.oop.spacemusicunify.domain.*;

public class FileAlbumServiceImpl implements AlbumService {
	
	private String albumsFile;
	private String songsFile;
	private MultimediaService multimediaService;
	private ProductionService productionService;
	private Set<Artist> choosenArtists = new HashSet<>();
	
	public FileAlbumServiceImpl(String albumsFile, String songsFile, ProductionService productionService, MultimediaService multimediaService) {
		this.albumsFile = albumsFile;
		this.songsFile = songsFile;
		this.productionService = productionService;
		this.multimediaService = multimediaService;
	}
	
	@Override
	public void add(Album album) throws BusinessException {
		//album: title, genre, release, artist, cover
		for (Album album1 : getAlbumList()) {
			if (album1.getTitle().equals(album.getTitle())) {
				throw new AlreadyExistingException();
			}
		}

		FileData fileData;
		//scrivo il file album
		try {

			fileData = Utility.readAllRows(albumsFile);
			album.setId(Integer.parseInt(String.valueOf(fileData.getContatore())));
			multimediaService.add(album.getCover());
			//creo la canzone
			Song canzone = new Song();
			canzone.setAlbum(album);
			canzone.setTitle("new song of '" + album.getTitle() + "'");
			canzone.setLyrics("Lyrics");
			canzone.setLength("04:02");
			if(album.getGenre() == Genre.singoli){
				canzone.setGenre(Genre.pop);
			} else {
				canzone.setGenre(album.getGenre());
			}

			Audio audio = new Audio();
			audio.setData("src" + File.separator + "main" + File.separator + "resources" + File.separator + "dati" + File.separator + "RAMfiles" + File.separator + "our_sympathy.mp3");
			audio.setOwnership(canzone);
			canzone.setFileMp3(audio);

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
				row.append("[]");
				row.append(Utility.SEPARATORE_COLONNA);
				row.append(album.getRelease());


				writerAlbum.println(row.toString());
			}
			add(canzone);

				Set<Artist> artists = getChoosenArtists();
			for(Artist artist : artists){
				Production production = new Production();
				production.setArtist(artist);
				production.setAlbum(album);
				productionService.add(production);
			}

			} catch (IOException e) {
				e.printStackTrace();
			}
	}

	@Override
	public void modify(Integer id, String title, Genre genre, Picture tempPicture,  Set<Song> songlist, LocalDate release, Album album) throws BusinessException {
		String oldGenre = null;
		try {

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

					Picture savePicture;

					String[] row;
					if(tempPicture != null){
						savePicture = tempPicture;
						multimediaService.delete(album.getCover());
						multimediaService.add(tempPicture);
					}else{
						savePicture = album.getCover();
					}
					row = new String[]{righe[0], title, String.valueOf(genre), savePicture.getId().toString(), righe[4],String.valueOf(release)};
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
						modify(canzone.getId(), canzone.getTitle(), null, canzone.getLyrics(), canzone.getAlbum(), canzone.getLength(), canzone.getGenre(), canzone);
					}
				}
			}
			/*songlist.iterator().next().getAlbum().setCover(tempPicture);
			songlist.iterator().next().getAlbum().setTitle(title);
			songlist.iterator().next().getAlbum().setGenre(genre);
			songlist.iterator().next().getAlbum().setRelease(release);*/
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void delete(Album album) throws BusinessException {
		boolean check = false;

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
				multimediaService.add(canzone.getFileMp3());
				writer.println(contatore + 1);
				for (String[] righe : fileData.getRighe()) {
					writer.println(String.join(Utility.SEPARATORE_COLONNA, righe));
				}

				StringBuilder row = new StringBuilder();
				row.append(contatore);
				row.append(Utility.SEPARATORE_COLONNA);
				row.append(canzone.getTitle());
				row.append(Utility.SEPARATORE_COLONNA);
				row.append(canzone.getFileMp3().getId());
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
					List<String> listaCanzoni = Utility.leggiArray(righe[4]);
					listaCanzoni.add(canzone.getId().toString());
					Set<Song> canzoneList = album.getSongList();
					canzoneList.add(canzone);
					album.setSongList(canzoneList);

					String[] row = {album.getId().toString(),album.getTitle(),album.getGenre().toString(), String.valueOf(album.getCover().getId()),listaCanzoni.toString(),album.getRelease().toString()};
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
	public void modify(Integer id, String title, Audio tempAudio,String lyrics,Album album,String length,Genre genre, Song song)
			throws BusinessException {

		boolean check = false;
		try {

			FileData fileData = Utility.readAllRows(songsFile);
			for(String[] colonne: fileData.getRighe()) {
				if (colonne[1].equals(title) && !colonne[0].equals(id.toString())) {
					throw new AlreadyTakenFieldException();
				}
			}
			int cont = 0;
			for(String[] righe: fileData.getRighe()) {
				if(righe[0].equals(id.toString())) {
					Audio saveAudio;

					check = true;
					String[] row;
					if(tempAudio != null){
						saveAudio = tempAudio;
						multimediaService.delete(song.getFileMp3());
						multimediaService.add(tempAudio);
					}else{
						saveAudio = song.getFileMp3();
					}
					row = new String[]{righe[0], title, saveAudio.getId().toString(), lyrics, righe[4], length, String.valueOf(genre)};
					fileData.getRighe().set(cont, row);

					song.setId(id);
					song.setTitle(title);
					song.setLength(length);
					song.setGenre(genre);
					song.setFileMp3(saveAudio);
					song.setLyrics(lyrics);
					song.setAlbum(album);
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
		/*boolean check = false;

		for(Song song : getSongList()) {

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
							*//*cover = cover.substring(cover.indexOf("immagini" + File.separator)+ 9);*//*
							*//*String[] row = {album.getId().toString(),album.getTitle(),album.getGenre().toString(), String.valueOf(cover.getId()),album.getRelease().toString(),album.getArtist().getId().toString(),listaCanzoni.toString()};
							fileData.getRighe().set(cont, row);*//*
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


									for(int i = 0; i < listaCanzoni.size(); i++ ){
										if(listaCanzoni.get(i).equals(canzone.getId().toString())){
											if (Integer.parseInt(righe[4]) > i ){


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
		if(!check)throw new BusinessException("canzone inesistente");*/
	}
	@Override
	public Set<Album> getAlbumList() throws BusinessException {
		Set<Album> albumList = new HashSet<>();
		try {
			FileData fileData = Utility.readAllRows(albumsFile);

			for (String[] righe : fileData.getRighe()) {
				albumList.add((Album) UtilityObjectRetriever.findObjectById(righe[0], albumsFile));

			}
		} catch (IOException e) {
			e.printStackTrace();
			throw new BusinessException();
		}
		return albumList;
	}
	@Override
	public Set<Song> getSongList() throws BusinessException {
		Set<Song> songList = new HashSet<>();
		try {
			FileData fileData = Utility.readAllRows(songsFile);
			for(String[] righe : fileData.getRighe()) {
				songList.add((Song) UtilityObjectRetriever.findObjectById(righe[0], songsFile));
			}
		} catch (IOException e) {
			e.printStackTrace();
			throw new BusinessException();
		}
		return songList;
	}
	@Override
	public Set<Artist> findAllArtists(Album album) throws BusinessException {
		ArtistService artistService = SpacemusicunifyBusinessFactory.getInstance().getArtistService();
		Set<Artist> artists = artistService.getArtistList();
		Set<Artist> artistsFinal = new HashSet<>();
		Set<Production> productions = productionService.getAllProductions();
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
	@Override
	public Set<Production> findAllProductions(Album album) throws BusinessException {
		ProductionService productionService = SpacemusicunifyBusinessFactory.getInstance().getProductionService();
		Set<Production> productions = productionService.getAllProductions();
		Set<Production> productionList = new HashSet<>();
		for (Production production : productions){
			if (production.getAlbum().getId().intValue() == album.getId().intValue()){
				productionList.add(production);
			}
		}

		return productionList;
	}
	@Override
	public Set<Artist> getChoosenArtists() {
		Set<Artist> artists = choosenArtists;
		choosenArtists = null;
		return artists;
	}
	@Override
	public void setChoosenArtists(Set<Artist> choosenArtists) {
		this.choosenArtists = choosenArtists;
	}
}
