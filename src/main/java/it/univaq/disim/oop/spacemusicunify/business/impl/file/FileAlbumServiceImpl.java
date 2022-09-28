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
			if(album.getSongs() == null)canzone.setTitle("Default"+album.getTitle());
			else canzone.setTitle("Default "+album.getTitle());
			canzone.setLyrics("Lyrics");
			canzone.setLength("04:02");
			if(album.getGenre() == Genre.singles){
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
				if(artists != null) {
					for (Artist artist : artists) {
						Production production = new Production();
						production.setArtist(artist);
						production.setAlbum(album);
						productionService.add(production);
					}
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
				if(genre != Genre.singles) {
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

			if(oldGenre != null && genre != Genre.singles) {
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

		for(Album albumCheck : getAlbumList()) {

			if(albumCheck.getId().intValue() ==  album.getId().intValue()) {
				check = true;

				for(Song song : album.getSongs()){
					delete(song);
				}

				for(Production production1 : productionService.getAllProductions()){
					if(production1.getAlbum().getId().intValue() == album.getId().intValue()){
						productionService.delete(production1);
					}
				}
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
					multimediaService.delete(album.getCover());

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
				if(righe[1].equals(canzone.getTitle()) || canzone.getTitle().contains("DefaultSingles") && canzone.getAlbum().getSongs() != null) {
					throw new AlreadyExistingException();
				}
			}

			canzone.setId(Integer.parseInt(String.valueOf(fileData.getContatore())));
			multimediaService.add(canzone.getFileMp3());

			try(PrintWriter writer = new PrintWriter(new File(songsFile))){
				long contatore = fileData.getContatore();
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
					Set<Song> canzoneList;
					if(album.getSongs() == null) canzoneList = new HashSet<>();
					else canzoneList = album.getSongs();
					canzoneList.add(canzone);
					album.setSongs(canzoneList);

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

						multimediaService.add(tempAudio);
						multimediaService.delete(song.getFileMp3());
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
			throw new BusinessException(e);
		}

	}

	@Override
	public void delete(Song canzone) throws BusinessException {
		boolean check = false;
		Album album = canzone.getAlbum();

		for(Song songCheck : album.getSongs()) {

			if(canzone.getId().intValue() == songCheck.getId().intValue()) {
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
					multimediaService.delete(canzone.getFileMp3());
					//aggiorno il file albums.txt
					fileData = Utility.readAllRows(albumsFile);
					int cont = 0;
					for(String[] righe: fileData.getRighe()) {
						if(righe[0].equals(album.getId().toString())) {
							List<String> songList = Utility.leggiArray(righe[4]);
							songList.remove(canzone.getId().toString());
							Set<Song> canzoneList = album.getSongs();
							canzoneList.remove(canzone);
							album.setSongs(canzoneList);

							String[] row = {righe[0], righe[1], righe[2], righe[3], songList.toString(),righe[5]};
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
					UserService userService = SpacemusicunifyBusinessFactory.getInstance().getUserService();

					for(User user : userService.getAllUsers()){
						for(Playlist playlist : userService.getAllPlaylists(user)){
							Set<Song> songs = playlist.getSongList();
							songs.removeIf((Song song) -> song.getId().intValue() == canzone.getId().intValue());
							userService.modify(playlist.getId(), playlist.getTitle(), songs, user);
						}
					}

				} catch (IOException e) {
					e.printStackTrace();
				}
				
				break;
			}
		}
		if(!check)throw new BusinessException("canzone inesistente");
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
