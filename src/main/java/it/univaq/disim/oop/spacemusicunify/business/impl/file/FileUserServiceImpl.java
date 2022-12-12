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
	private final String playlistsFile;


	public FileUserServiceImpl(String fileUser, String filePlaylist) {
		this.usersFile = fileUser;
		this.playlistsFile = filePlaylist;
	}



	@Override
	public void add(User user) throws  BusinessException {
		try {
			FileData fileData = Utility.readAllRows(usersFile);
			for (String[] columns : fileData.getRows()) {
				if (columns[2].equals(user.getUsername()) || user.getUsername().contains("admin")) {
					throw new AlreadyExistingException("New User, Already Existing user with this username");
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
				if (columns[2].equals(username) && Integer.parseInt(columns[0]) != id) {
					throw new AlreadyExistingException("Modify User, Already Existing user with this username");
				}
			}
			try (PrintWriter writer = new PrintWriter(new File(usersFile))) {
				writer.println(fileData.getCounter());
				for (String[] rows : fileData.getRows()) {
					if (Long.parseLong(rows[0]) == id) {
						StringBuilder row = new StringBuilder();
						row.append(id);
						row.append(Utility.COLUMN_SEPARATOR);
						row.append(rows[1]);
						row.append(Utility.COLUMN_SEPARATOR);
						row.append(username);
						row.append(Utility.COLUMN_SEPARATOR);
						row.append(password);
						writer.println(row.toString());
					} else {
						writer.println(String.join(Utility.COLUMN_SEPARATOR, rows));
					}
				}

			}
		} catch (IOException e) {
			throw new BusinessException(e);
		}
	}

	@Override
	public void delete(User user) throws BusinessException {
		boolean check = false;
		try {
			FileData fileData = Utility.readAllRows(usersFile);
			for (String[] columns : fileData.getRows()) {
				if ( Integer.parseInt(columns[0]) == user.getId() ) {
					check = true;
					//eliminazione delle playlists
					Set<Playlist> playlists = getAllPlaylists(user);
					for(Playlist playlist : playlists) {
						delete(playlist);
					}
					SpacemusicunifyBusinessFactory.getInstance().getPlayerService().delete(user);

					try (PrintWriter writer = new PrintWriter(new File(usersFile))) {
						writer.println(fileData.getCounter());
						for (String[] rows : fileData.getRows()) {
							if (Long.parseLong(rows[0]) != user.getId()) {
								writer.println(String.join(Utility.COLUMN_SEPARATOR, rows));
							}
						}
					}

					break;
				}
			}
		} catch (IOException e) {
			throw new BusinessException(e);
		}
		if(!check) throw new BusinessException("Object not found, This user doesn't exist");
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
			throw new UserNotFoundException("User Not Found");
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
			FileData fileData = Utility.readAllRows(playlistsFile);
			for (String[] rowsCheck : fileData.getRows()) {
				if (rowsCheck[1].equals(playlist.getTitle()) && Integer.parseInt(rowsCheck[3]) == playlist.getUser().getId()) {
					throw new AlreadyExistingException("This playlist already exists in the users playlists");
				}
			}
			try (PrintWriter writer = new PrintWriter(new File(playlistsFile))) {
				long counter = fileData.getCounter();
				writer.println((counter + 1));
				for (String[] rows : fileData.getRows()) {
					writer.println(String.join(Utility.COLUMN_SEPARATOR, rows));
				}
				StringBuilder row = new StringBuilder();
				row.append(counter);
				row.append(Utility.COLUMN_SEPARATOR);
				row.append(playlist.getTitle());
				row.append(Utility.COLUMN_SEPARATOR);
				row.append(playlist.getSongList());
				row.append(Utility.COLUMN_SEPARATOR);
				row.append(playlist.getUser().getId());
				writer.println(row.toString());

			}
		} catch (IOException e) {
			throw new BusinessException(e);
		}
	}

	@Override
	public void modify( Set<Song> songs, Playlist playlist) throws  BusinessException {
		boolean check = false;
		try {
			FileData fileData = Utility.readAllRows(playlistsFile);
			for (String[] rowsCheck : fileData.getRows()) {
				if (Integer.parseInt(rowsCheck[0]) == playlist.getId()) {
					check = true;
					List<String> idSongList = new ArrayList<>();
					for (Song song : songs) {
						idSongList.add(song.getId().toString());
					}
					try (PrintWriter writer = new PrintWriter(new File(playlistsFile))) {
						writer.println(fileData.getCounter());
						for (String[] rows : fileData.getRows()) {
							if (Long.parseLong(rows[0]) == playlist.getId()) {
								StringBuilder row = new StringBuilder();
								row.append(playlist.getId());
								row.append(Utility.COLUMN_SEPARATOR);
								row.append(rows[1]);
								row.append(Utility.COLUMN_SEPARATOR);
								row.append(idSongList);
								row.append(Utility.COLUMN_SEPARATOR);
								row.append(rows[3]);
								writer.println(row.toString());
							} else {
								writer.println(String.join(Utility.COLUMN_SEPARATOR, rows));
							}
						}
						playlist.setSongList(songs);
					}
					break;
				}
			}
		} catch (IOException e) {
			throw new BusinessException(e);
		}
		if(!check) throw new BusinessException("Object not found, This playlist doesn't exist");
	}
	@Override
	public void delete(Playlist playlist) throws BusinessException {
		boolean check = false;
		try {

			FileData fileData = Utility.readAllRows(playlistsFile);
			for (String[] rowsCheck : fileData.getRows()) {
				if (rowsCheck[0].equals(playlist.getId().toString())) {
					check = true;

					try (PrintWriter writer = new PrintWriter(new File(playlistsFile))) {
						writer.println(fileData.getCounter());
						for (String[] rows : fileData.getRows()) {
							if (rows[0].equals(playlist.getId().toString())) {
								//jump line
								continue;
							} else {
								writer.println(String.join("§", rows));
							}
						}
					}
					break;
				}

			}
		} catch(IOException e){
			throw new BusinessException(e);
		}

		if(!check)throw new BusinessException("Object not found, This playlist doesn't exist");

	}
	@Override
	public Set<Playlist> getAllPlaylists(User user) throws BusinessException{
		Set<Playlist> playlists = new HashSet<>();
		try {
			FileData fileData = Utility.readAllRows(playlistsFile);
			for (String[] rows : fileData.getRows()) {
				if(rows[3].equals(user.getId().toString())) {
					playlists.add((Playlist) UtilityObjectRetriever.findObjectById(rows[0], playlistsFile));
				}
			}
		} catch (IOException e) {
			throw new BusinessException(e);
		}
		return playlists;
	}

}
