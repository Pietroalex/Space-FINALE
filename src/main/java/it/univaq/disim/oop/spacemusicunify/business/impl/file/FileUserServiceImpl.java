package it.univaq.disim.oop.spacemusicunify.business.impl.file;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import it.univaq.disim.oop.spacemusicunify.business.*;
import it.univaq.disim.oop.spacemusicunify.domain.*;
import it.univaq.disim.oop.spacemusicunify.view.SpacemusicunifyPlayer;

public class FileUserServiceImpl implements UserService {

	private final String usersFile;
	private final String albumsFile;
	private final String artistsFile;
	private final String songsFile;
	private final String playlistFile;
	private final String picturesFile;
	private final String picturesDirectory;
	private final String mp3Directory;


	private String ricerca;

	public FileUserServiceImpl(String fileUtenti, String fileAlbums, String fileArtisti, String fileCanzoni, String filePlaylist, String cartellaImmagini, String cartellaFilesMP3, String filePictures) {
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
	public void add(User utente) throws  BusinessException {
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
				Set<Playlist> playlists = getAllPlaylists(utente);
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

	

	

/*	@Override
	public Set<Artist> getAllArtists() throws BusinessException {
		try {
			return SpacemusicunifyBusinessFactory.getInstance().getArtistService().getArtistList();
		} catch (BusinessException e) {
			throw new BusinessException();
		}
	}
	@Override
	public Set<Album> getAllAlbums() throws BusinessException {
		try {
			return SpacemusicunifyBusinessFactory.getInstance().getAlbumService().getAlbumList();
		} catch (BusinessException e) {
			throw new BusinessException();
		}
	}
	@Override
	public Set<Song> getAllSongs() throws BusinessException{
		try {
			return SpacemusicunifyBusinessFactory.getInstance().getAlbumService().getSongList();
		} catch (BusinessException e) {
			throw new BusinessException();
		}
	}*/


	@Override
	public GeneralUser authenticate(String username, String password) throws UtenteGenericoNotFoundException, BusinessException {
		try {
			FileData fileData = Utility.readAllRows(usersFile);
			for(String[] colonne : fileData.getRighe()) {
				if(colonne[2].equals(username) && colonne[3].equals(password)) {
					GeneralUser utente = null;
					switch (colonne[1]) {
						case "user" :
							utente = new User();

							//((User) utente).setcurrentPosition(Integer.parseInt(colonne[4]));
/*							List<String> songQueue = Utility.leggiArray(colonne[5]);
							List<Song> queueList = new ArrayList<>();
							for(String string : songQueue) {
								queueList.add((Song) UtilityObjectRetriever.findObjectById(string, songsFile));
							}*/
							//((User) utente).setSongQueue(queueList);
							break;

						case "admin" :
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
	public Set<User> getAllUsers() {
		Set<User> utenteList = new HashSet<>();
		try {
			FileData fileData = Utility.readAllRows(usersFile);
			fileData.getRighe().remove(0);

			for (String[] colonne : fileData.getRighe()) {
				User utente = new User();
				utente.setId(Integer.parseInt(colonne[0]));
				utente.setUsername(colonne[2]);
				utente.setPassword(colonne[3]);
				//((User) utente).setcurrentPosition(Integer.parseInt(colonne[4]));
				/*List<String> songQueue = Utility.leggiArray(colonne[5]);
				List<Song> queueList = new ArrayList<>();
				for(String string : songQueue) {
					queueList.add((Song) UtilityObjectRetriever.findObjectById(string, songsFile));
				}*/
				//((User) utente).setSongQueue(queueList);
				utenteList.add(utente);
			}

		} catch (IOException e) {
			e.printStackTrace();
		}

		return utenteList;
	}

	@Override
	public SpacemusicunifyPlayer getPlayer(User user) throws BusinessException {
		return null;
	}

	@Override
	public void add(Playlist playlist) throws BusinessException {
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
	public Set<Playlist> getAllPlaylists(User utente) {
		Set<Playlist> playlistsUtente = new HashSet<>();
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
