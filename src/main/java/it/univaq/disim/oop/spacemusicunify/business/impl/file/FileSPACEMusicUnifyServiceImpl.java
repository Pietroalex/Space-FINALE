package it.univaq.disim.oop.spacemusicunify.business.impl.file;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import it.univaq.disim.oop.spacemusicunify.business.*;
import it.univaq.disim.oop.spacemusicunify.domain.*;
import it.univaq.disim.oop.spacemusicunify.view.ViewSituations;
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
