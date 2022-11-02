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
	private Set<Artist> chosenArtists;
	
	public FileAlbumServiceImpl(String albumsFile, String songsFile, ProductionService productionService, MultimediaService multimediaService) {
		this.albumsFile = albumsFile;
		this.songsFile = songsFile;
		this.productionService = productionService;
		this.multimediaService = multimediaService;
	}
	
	@Override
	public void add(Album album) throws BusinessException {
		try {
			FileData fileData = Utility.readAllRows(albumsFile);
			for(String[] column: fileData.getRows()) {
				if ( album.getTitle().contains("Singles") && album.getGenre() != Genre.singles){
					throw new AlreadyExistingException("The title must not contain 'Singles'");
				}
				if (column[1].equals(album.getTitle()) ) {
					throw new AlreadyExistingException("Already Existing album with this title");
				}
			}
		//scrivo il file album

			album.setId(Integer.parseInt(String.valueOf(fileData.getCounter())));
			multimediaService.add(album.getCover());
			//creo la canzone
			Song canzone = new Song();
			canzone.setAlbum(album);
			if(album.getSongs() == null)canzone.setTitle("Default"+album.getTitle());
			else canzone.setTitle("Default song of "+album.getTitle());
			canzone.setLyrics("Lyrics");
			canzone.setLength("04:02");
			if(album.getGenre() == Genre.singles){
				canzone.setGenre(Genre.pop);
			} else {
				canzone.setGenre(album.getGenre());
			}

			Audio audio = new Audio();
			audio.setData("src" + File.separator + "main" + File.separator + "resources" + File.separator + "data" + File.separator + "RAMfiles" + File.separator + "our_sympathy.mp3");
			audio.setOwnership(canzone);
			canzone.setFileMp3(audio);

			try (PrintWriter writerAlbum = new PrintWriter(new File(albumsFile))) {
				long contatore = fileData.getCounter();
				writerAlbum.println(contatore + 1);
				for (String[] righe : fileData.getRows()) {
					writerAlbum.println(String.join(Utility.COLUMN_SEPARATOR, righe));
				}
				StringBuilder row = new StringBuilder();
				row.append(contatore);
				row.append(Utility.COLUMN_SEPARATOR);
				row.append(album.getTitle());
				row.append(Utility.COLUMN_SEPARATOR);
				row.append(album.getGenre());
				row.append(Utility.COLUMN_SEPARATOR);
				row.append(album.getCover().getId());
				row.append(Utility.COLUMN_SEPARATOR);
				row.append("[]");
				row.append(Utility.COLUMN_SEPARATOR);
				row.append(album.getRelease());


				writerAlbum.println(row.toString());
			}
			add(canzone);

				Set<Artist> artists = getChosenArtists();
				if(artists != null) {
					for (Artist artist : artists) {
						Production production = new Production();
						production.setArtist(artist);
						production.setAlbum(album);
						productionService.add(production);
					}
				}

			} catch (IOException e) {
				throw new BusinessException(e);
			}
	}

	@Override
	public void modify(Integer id, String title, Genre genre, Picture tempPicture,  Set<Song> songs, LocalDate release, Album album) throws BusinessException {
		String oldGenre = null;
		try {

			FileData fileData = Utility.readAllRows(albumsFile);
			for(String[] columns: fileData.getRows()) {
					if ( album.getTitle().contains("Singles") && album.getGenre() != Genre.singles){
						throw new AlreadyExistingException("The title must not contain 'Singles'");
					}
					if (columns[1].equals(title) && !columns[0].equals(id.toString()) ){
						throw new AlreadyExistingException("Already Existing album with this title");
					}
			}

			int cont = 0;
			for (String[] righe : fileData.getRows()) {
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
					fileData.getRows().set(cont, row);
					break;
				}
				cont++;
			}
			try (PrintWriter writer = new PrintWriter(new File(albumsFile))) {
				writer.println(fileData.getCounter());
				for (String[] rows : fileData.getRows()) {
					writer.println(String.join(Utility.COLUMN_SEPARATOR, rows));
				}
			}

			if(oldGenre != null && genre != Genre.singles) {
				if (!oldGenre.equals(String.valueOf(genre)) ) {
					for (Song song : songs) {
						song.setGenre(genre);
						modify(song.getId(), song.getTitle(), null, song.getLyrics(), song.getAlbum(), song.getLength(), song.getGenre(), song);
					}
				}
			}

		} catch (IOException e) {
			throw new BusinessException(e);
		}
	}

	@Override
	public void delete(Album album) throws BusinessException {
		boolean check = false;
		try {

			FileData fileData = Utility.readAllRows(albumsFile);
			for(String[] columns: fileData.getRows()) {

				for(Production production1 : productionService.getAllProductions()){
					if(production1.getAlbum().getId().intValue() == album.getId().intValue()){
						productionService.delete(production1);
					}
				}
				if (columns[0].equals(album.getId().toString()) ){
					check = true;
					Set<Song> songs = new HashSet<>(album.getSongs());
					for(Song song : songs){
						delete(song);
					}
					multimediaService.delete(album.getCover());



						//aggiorno il file album.txt
					try (PrintWriter writer = new PrintWriter(new File(albumsFile))) {
						writer.println(fileData.getCounter());
						for (String[] righe : fileData.getRows()) {
							if (righe[0].equals(album.getId().toString())) {
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
		} catch (IOException e) {
			throw new BusinessException(e);
		}
		if(!check)throw new ObjectNotFoundException("This album doesn't exist");
	}
	
	@Override
	public void add(Song song) throws BusinessException {
		try {

			FileData fileData = Utility.readAllRows(songsFile);
			for(String[] rows: fileData.getRows()) {
				if ( song.getTitle().contains("DefaultSingles") && song.getAlbum().getGenre() != Genre.singles){
					throw new AlreadyExistingException("The title must not contain 'DefaultSingles'");
				}
				if(rows[1].equals(song.getTitle())) {
					throw new AlreadyExistingException("Already Existing song with this title");
				}
			}

			song.setId(Integer.parseInt(String.valueOf(fileData.getCounter())));
			multimediaService.add(song.getFileMp3());

			try(PrintWriter writer = new PrintWriter(new File(songsFile))){
				long counter = fileData.getCounter();
				writer.println(counter + 1);
				for (String[] righe : fileData.getRows()) {
					writer.println(String.join(Utility.COLUMN_SEPARATOR, righe));
				}

				StringBuilder row = new StringBuilder();
				row.append(counter);
				row.append(Utility.COLUMN_SEPARATOR);
				row.append(song.getTitle());
				row.append(Utility.COLUMN_SEPARATOR);
				row.append(song.getFileMp3().getId());
				row.append(Utility.COLUMN_SEPARATOR);
				row.append(song.getLyrics());
				row.append(Utility.COLUMN_SEPARATOR);
				row.append(song.getAlbum().getId());
				row.append(Utility.COLUMN_SEPARATOR);
				row.append(song.getLength());
				row.append(Utility.COLUMN_SEPARATOR);
				row.append(song.getGenre());
				writer.println(row.toString());
			}
			//aggiorno l'album
			Album album = song.getAlbum();
			fileData = Utility.readAllRows(albumsFile);
			boolean check = false;
			int cont = 0;
			for(String[] righe: fileData.getRows()) {
				if(righe[0].equals(album.getId().toString())) {
					check = true;
					List<String> listaCanzoni = Utility.readArray(righe[4]);
					listaCanzoni.add(song.getId().toString());
					Set<Song> canzoneList;
					if(album.getSongs() == null) canzoneList = new HashSet<>();
					else canzoneList = album.getSongs();
					canzoneList.add(song);
					album.setSongs(canzoneList);

					String[] row = {album.getId().toString(),album.getTitle(),album.getGenre().toString(), String.valueOf(album.getCover().getId()),listaCanzoni.toString(),album.getRelease().toString()};
					fileData.getRows().set(cont, row);
					break;
				}
				cont++;
			}
			if(!check)throw new ObjectNotFoundException("This album doesn't exist");

			try(PrintWriter writer = new PrintWriter(new File(albumsFile))){
				writer.println(fileData.getCounter());
				for (String[] rows : fileData.getRows()) {
					writer.println(String.join(Utility.COLUMN_SEPARATOR, rows));
				}
			}
		} catch (IOException e) {
			throw new BusinessException(e);
		}

	}

	@Override
	public void modify(Integer id, String title, Audio tempAudio,String lyrics,Album album,String length,Genre genre, Song song) throws BusinessException {

		boolean check = false;
		try {

			FileData fileData = Utility.readAllRows(songsFile);
			for(String[] columns: fileData.getRows()) {
				if ( song.getTitle().contains("DefaultSingles") && song.getAlbum().getGenre() != Genre.singles){
					throw new AlreadyExistingException("The title must not contain 'DefaultSingles'");
				}
				if(columns[1].equals(song.getTitle())) {
					throw new AlreadyExistingException("Already Existing song with this title");
				}
			}
			int cont = 0;
			for(String[] righe: fileData.getRows()) {
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
					fileData.getRows().set(cont, row);

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
			if(!check)throw new BusinessException("This song doesn't exist");

			try(PrintWriter writer = new PrintWriter(new File(songsFile))){
				writer.println(fileData.getCounter());
				for (String[] righe : fileData.getRows()) {
					writer.println(String.join(Utility.COLUMN_SEPARATOR, righe));
				}
			}
		} catch (IOException e) {
			throw new BusinessException(e);
		}

	}

	@Override
	public void delete(Song song) throws BusinessException {
		boolean check = false;
		Album album = song.getAlbum();
		Set<Song> songs = new HashSet<>(album.getSongs());
		for(Song songCheck : songs) {

			if(songCheck.getId().intValue() == song.getId().intValue()) {
				check = true;

				UserService userService = SpacemusicunifyBusinessFactory.getInstance().getUserService();
				PlayerService playerService = SpacemusicunifyBusinessFactory.getInstance().getPlayerService();

				for(User user : userService.getAllUsers()){
					for(Song songCheck2 : playerService.getPlayer(user).getQueue()) {
						if(songCheck2.getId().intValue() == song.getId().intValue()){
							playerService.deleteSongFromQueue(playerService.getPlayer(user), song);
							break;
						}
					}
					for(Playlist playlist : userService.getAllPlaylists(user)){
						Set<Song> playlistSongList = playlist.getSongList();
						playlistSongList.removeIf((Song songCheck2) -> songCheck2.getId().intValue() == song.getId().intValue());
						userService.modify(playlistSongList, playlist);
					}
				}

				try {
					FileData fileData = Utility.readAllRows(songsFile);
					//aggiorno il file canzoni.txt
					try (PrintWriter writer = new PrintWriter(new File(songsFile))) {
						writer.println(fileData.getCounter());
						for (String[] righe : fileData.getRows()) {
							if (righe[0].equals(song.getId().toString())) {
								//jump line
								continue;
							} else {
								writer.println(String.join("§", righe));
							}
						}
					}
					multimediaService.delete(song.getFileMp3());
					//aggiorno il file albums.txt
					fileData = Utility.readAllRows(albumsFile);
					int cont = 0;
					for(String[] righe: fileData.getRows()) {
						if(righe[0].equals(album.getId().toString())) {
							List<String> songListIDS = Utility.readArray(righe[4]);
							songListIDS.remove(song.getId().toString());
							Set<Song> songList = album.getSongs();
							songList.remove(song);
							album.setSongs(songList);

							String[] row = {righe[0], righe[1], righe[2], righe[3], songListIDS.toString(),righe[5]};
							fileData.getRows().set(cont, row);
							break;
						}
						cont++;
					}
					try(PrintWriter writer = new PrintWriter(new File(albumsFile))){
						writer.println(fileData.getCounter());
						for (String[] righe : fileData.getRows()) {
							writer.println(String.join(Utility.COLUMN_SEPARATOR, righe));
						}
					}


				} catch (IOException e) {
					throw new BusinessException(e);
				}
				
				break;
			}
		}
		if(!check)throw new ObjectNotFoundException("This song doesn't exist");
	}
	@Override
	public Set<Album> getAlbumList() throws BusinessException {
		Set<Album> albumList = new HashSet<>();
		try {
			FileData fileData = Utility.readAllRows(albumsFile);

			for (String[] rows : fileData.getRows()) {
				albumList.add((Album) UtilityObjectRetriever.findObjectById(rows[0], albumsFile));

			}
		} catch (IOException e) {
			throw new BusinessException(e);
		}
		return albumList;
	}
	@Override
	public Set<Song> getSongList() throws BusinessException {
		Set<Song> songList = new HashSet<>();
		try {

			FileData fileData = Utility.readAllRows(songsFile);

			for(String[] rows : fileData.getRows()) {

				songList.add((Song) UtilityObjectRetriever.findObjectById(rows[0], songsFile));
			}
		} catch (IOException e) {
			throw new BusinessException(e);
		}
		return songList;

	}
	@Override
	public Set<Artist> findAllArtists(Album album) throws BusinessException {
		Set<Artist> artists = new HashSet<>();
		for (Production production : findAllProductions(album)){
			artists.add(production.getArtist());
		}
		if(artists.isEmpty()) throw new ObjectNotFoundException("There is no artist for this album");
		return artists;
	}
	@Override
	public Set<Production> findAllProductions(Album album) throws BusinessException {
		Set<Production> productionList = new HashSet<>();
		for (Production production : productionService.getAllProductions()){
			if (production.getAlbum().getId().intValue() == album.getId().intValue()){
				productionList.add(production);
			}
		}
		if(productionList.isEmpty()) throw new ObjectNotFoundException("There is no production for this album");
		return productionList;
	}
	@Override
	public Set<Artist> getChosenArtists() {
		return chosenArtists;
	}
	@Override
	public void setChosenArtists(Set<Artist> chosenArtists) {
		this.chosenArtists = chosenArtists;
	}
}
