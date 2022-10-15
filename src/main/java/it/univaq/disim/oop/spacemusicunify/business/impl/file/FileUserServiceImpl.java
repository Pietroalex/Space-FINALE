package it.univaq.disim.oop.spacemusicunify.business.impl.file;

import java.io.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import it.univaq.disim.oop.spacemusicunify.business.*;
import it.univaq.disim.oop.spacemusicunify.domain.*;

public class FileUserServiceImpl implements UserService {

	private final String usersFile;

	private final String songsFile;
	private final String playlistFile;


	public FileUserServiceImpl(String fileUtenti, String fileCanzoni, String filePlaylist) {
		this.usersFile = fileUtenti;
		this.songsFile = fileCanzoni;
		this.playlistFile = filePlaylist;

	}



	@Override
	public void add(User user) throws  BusinessException {
		try {
			FileData fileData = Utility.readAllRows(usersFile);
			for (String[] columns : fileData.getRows()) {
				if (columns[2].equals(user.getUsername()) || user.getUsername().contains("admin")) {
					throw new AlreadyExistingException("existing_user");
				}
			}
			try (PrintWriter writer = new PrintWriter(new File(usersFile))) {
				long counter = fileData.getCounter();
				writer.println((counter + 1));
				for (String[] rows : fileData.getRows()) {
					writer.println(String.join(Utility.COLUMN_SEPARATOR, rows));
				}
				user.setId(Integer.parseInt(String.valueOf(fileData.getCounter())));
				StringBuilder row = new StringBuilder();
				row.append(counter);
				row.append(Utility.COLUMN_SEPARATOR);
				row.append("user");
				row.append(Utility.COLUMN_SEPARATOR);
				row.append(user.getUsername());
				row.append(Utility.COLUMN_SEPARATOR);
				row.append(user.getPassword());
				writer.println(row.toString());

			}
			SpacemusicunifyBusinessFactory.getInstance().getPlayerService().add(user);
		} catch (IOException e) {
			throw new BusinessException(e);
		}
	}

	@Override
	public void modify(Integer id, String username, String password) throws BusinessException {
		try {
			FileData fileData = Utility.readAllRows(usersFile);
			for (String[] columns : fileData.getRows()) {
				if (columns[2].equals(username) && Integer.parseInt(columns[0]) != id ) {
					throw new AlreadyTakenFieldException();
				}
			}
			try (PrintWriter writer = new PrintWriter(new File(usersFile))) {
				writer.println(fileData.getCounter());
				for (String[] righe : fileData.getRows()) {
					if (Long.parseLong(righe[0]) == id) {
						StringBuilder row = new StringBuilder();
						row.append(id);
						row.append(Utility.COLUMN_SEPARATOR);
						row.append(username);
						row.append(Utility.COLUMN_SEPARATOR);
						row.append(password);
						writer.println(row.toString());
					} else {
						writer.println(String.join(Utility.COLUMN_SEPARATOR, righe));
					}
				}

			}
		} catch (IOException e) {
			throw new BusinessException(e);
		}
	}

	@Override
	public void delete(User user) throws BusinessException {
		boolean control = false;
		try {
			FileData fileData = Utility.readAllRows(usersFile);
			for (String[] colonne : fileData.getRows()) {
				if ( Integer.parseInt(colonne[0]) == user.getId() ) {
					control = true;
					break;
				}
			}

			if(control){
				try (PrintWriter writer = new PrintWriter(new File(usersFile))) {
					writer.println(fileData.getCounter());
					for (String[] righe : fileData.getRows()) {
						if (Long.parseLong(righe[0]) != user.getId()) {
							writer.println(String.join(Utility.COLUMN_SEPARATOR, righe));
						}
					}
				}
				//eliminazione delle playlists
				Set<Playlist> playlists = getAllPlaylists(user);
				for(Playlist playlist : playlists) {
					delete(playlist);
				}
				SpacemusicunifyBusinessFactory.getInstance().getPlayerService().delete(user);
			}else{
				throw new BusinessException("not_existing_user");
			}
		} catch (IOException e) {
			throw new BusinessException(e);
		}
	}

	@Override
	public GeneralUser authenticate(String username, String password) throws BusinessException {
		try {
			FileData fileData = Utility.readAllRows(usersFile);
			for(String[] columns : fileData.getRows()) {
				if(columns[2].equals(username) && columns[3].equals(password)) {
					GeneralUser user = null;
					switch (columns[1]) {
						case "user" :
							user = new User();
							RunTimeService.setCurrentUser((User) user);


							break;

						case "admin" :
							user = new Administrator();
							break;

						default :
							break;
					}
					if(user != null) {
						user.setId(Integer.parseInt(columns[0]));
						user.setUsername(columns[2]);
						user.setPassword(columns[3]);
						if(user instanceof User) RunTimeService.setPlayer(SpacemusicunifyBusinessFactory.getInstance().getPlayerService().getPlayer((User) user));


					} else { throw new BusinessException("file_read_error"); }

					return user;
				}
			}
			throw new ObjectNotFoundException("no_user");
		} catch (IOException e) {
			throw new BusinessException(e);
		}
	}

	@Override
	public Set<User> getAllUsers() throws BusinessException {
		Set<User> users = new HashSet<>();
		try {
			FileData fileData = Utility.readAllRows(usersFile);
			for (String[] columns : fileData.getRows()) {
				if(!(columns[1].equals("admin"))) users.add((User) UtilityObjectRetriever.findObjectById(columns[0], usersFile));
			}

		} catch (IOException e) {
			throw new BusinessException(e);
		}

		return users;
	}

	@Override
	public void add(Playlist playlist) throws BusinessException {
		try {
			FileData fileData = Utility.readAllRows(playlistFile);
			for (String[] colonne : fileData.getRows()) {
				if (colonne[2].equals(playlist.getTitle())) {
					throw new AlreadyExistingException("Already existing Playlist");
				}
			}
			try (PrintWriter writer = new PrintWriter(new File(playlistFile))) {
				long contatore = fileData.getCounter();
				writer.println((contatore + 1));
				for (String[] righe : fileData.getRows()) {
					writer.println(String.join(Utility.COLUMN_SEPARATOR, righe));
				}
				StringBuilder row = new StringBuilder();
				row.append(contatore);
				row.append(Utility.COLUMN_SEPARATOR);
				row.append(playlist.getTitle());
				row.append(Utility.COLUMN_SEPARATOR);
				row.append(playlist.getUser().getId());
				row.append(Utility.COLUMN_SEPARATOR);
				row.append(playlist.getSongList());
				writer.println(row.toString());

			}
		} catch (IOException e) {
			e.printStackTrace();
			throw new BusinessException(e);
		}
	}

	@Override
	public void modify(Integer id, String title, Set<Song> songlist, User user, Playlist playlist) throws  BusinessException {
		try {
			FileData fileData = Utility.readAllRows(playlistFile);
			List<String> idSongList = new ArrayList<>();
			for(Song canzone : songlist) {
				idSongList.add(canzone.getId().toString());
			}
			try (PrintWriter writer = new PrintWriter(new File(playlistFile))) {
				writer.println(fileData.getCounter());
				for (String[] righe : fileData.getRows()) {
					if (Long.parseLong(righe[0]) == id) {
						StringBuilder row = new StringBuilder();
						row.append(id);
						row.append(Utility.COLUMN_SEPARATOR);
						row.append(title);
						row.append(Utility.COLUMN_SEPARATOR);
						row.append(user.getId());
						row.append(Utility.COLUMN_SEPARATOR);
						row.append(idSongList);

						writer.println(row.toString());
					} else {
						writer.println(String.join(Utility.COLUMN_SEPARATOR, righe));
					}
				}
				playlist.setSongList(songlist);
			}
		} catch (IOException e) {
			throw new BusinessException(e);
		}
	}
	@Override
	public void delete(Playlist playlist) throws BusinessException {
		boolean check = false;
		try {
			FileData fileData = Utility.readAllRows(playlistFile);
			for (String[] righecontrollo : fileData.getRows()) {
				if (righecontrollo[0].equals(playlist.getId().toString())) {
					check = true;

					try (PrintWriter writer = new PrintWriter(new File(playlistFile))) {
						writer.println(fileData.getCounter());
						for (String[] righe : fileData.getRows()) {
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
	public Set<Playlist> getAllPlaylists(User utente) throws BusinessException{
		Set<Playlist> playlistsUtente = new HashSet<>();
		try {
			FileData fileData = Utility.readAllRows(playlistFile);

			for (String[] colonne : fileData.getRows()) {

				if(colonne[2].equals(utente.getId().toString())) {
					Playlist playlist = new Playlist();
					playlist.setId(Integer.parseInt(colonne[0]));
					playlist.setTitle(colonne[1]);
					playlist.setUser(utente);
					Set<Song> listaCanzoni = new HashSet<>();
					List<String> songList = Utility.readArray(colonne[3]);

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
			throw new BusinessException(e);
		}
		return playlistsUtente;
	}

}
