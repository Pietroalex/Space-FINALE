package it.univaq.disim.oop.spacemusicunify.business.impl.ram;

import it.univaq.disim.oop.spacemusicunify.business.*;
import it.univaq.disim.oop.spacemusicunify.domain.*;
import it.univaq.disim.oop.spacemusicunify.view.ViewDispatcher;

import java.util.*;

public class RAMUserServiceImpl implements UserService {

	private static Set<User> storedUsers = new HashSet<>();
	private static Set<Playlist> storedPlaylists = new HashSet<>();
	private static int idUser = 1;
	private static int idPlaylist = 1;

	@Override
	public void add(User user) throws BusinessException {
		// controllo se l'utente già è presente
		for (User users : storedUsers) {
			if ( user.getUsername().contains("admin")) {
				throw new AlreadyExistingException("No User can contain ADMIN username in its own");
			}
			if (users.getUsername().equals(user.getUsername())) {
				throw new AlreadyExistingException("New User, Already Existing user with this username");
			}
		}
		// utente nuovo

		user.setId(idUser++);
		storedUsers.add(user);
		SpacemusicunifyBusinessFactory.getInstance().getPlayerService().add(user);
	}

	@Override
	public void modify(Integer id, String username, String password) throws BusinessException {
		Set<User> users = getAllUsers();

		for (User userCheck : users) {
			if ( username.contains("admin")) {
				throw new AlreadyExistingException("No User can contain ADMIN username in its own");
			}
			if (userCheck.getUsername().equals(username) && userCheck.getId().intValue() != id.intValue()) {
				throw new AlreadyExistingException("Modify User, Already Existing user with this username");
			}
		}
		for (User user : users) {
			if (user.getId().intValue() == id.intValue()) {
				user.setUsername(username);
				user.setPassword(password);
			}
		}
	}

	@Override
	public void delete(User user) throws BusinessException {
		boolean check = false;
		try {
			for (User userCheck : getAllUsers()) {
				if (userCheck.getId().intValue() == user.getId().intValue()) {
					check = true;

					Set<Playlist> playlists = getAllPlaylists(user);
					for(Playlist playlist : playlists) {
						delete(playlist);
					}
					SpacemusicunifyBusinessFactory.getInstance().getPlayerService().delete(user);

					break;
				}
			}

		} catch (BusinessException e) {
		ViewDispatcher.getInstance().renderError(e);
		}
		if(!check) throw new BusinessException("Object not found, This user doesn't exist");
	}

	@Override
	public GeneralUser authenticate(String username, String password) throws BusinessException {
		if ("admin".equalsIgnoreCase(username)) {
			GeneralUser admin = new Administrator();
			admin.setUsername(username);
			admin.setPassword(password);
			return admin;
		} else {
			for (User user : getAllUsers()) {
				if (user.getUsername().equals(username) && user.getPassword().equals(password)) {
					RunTimeService.setCurrentUser(user);
					RunTimeService.setPlayer(SpacemusicunifyBusinessFactory.getInstance().getPlayerService().getPlayer(user));
					return user;
				}
			}
		}
		throw new BusinessException("Object not found, User Not Found");
	}

	@Override
	public Set<User> getAllUsers() throws BusinessException {
		if(storedUsers == null) throw  new BusinessException("Error in user storage");
		return new HashSet<>(storedUsers);
	}

	@Override
	public void add(Playlist playlist) throws BusinessException {
		for (Playlist playlistCheck : storedPlaylists) {
			if (playlistCheck.getTitle().equals(playlist.getTitle()) && playlistCheck.getUser().getId().intValue() == playlist.getUser().getId().intValue()) {
				throw new AlreadyExistingException("This playlist already exists in the user playlists");
			}
		}

		playlist.setId(idPlaylist++);
		if(!storedPlaylists.add(playlist)) throw new BusinessException();
	}
	
	@Override
	public void modify(Set<Song> songs, Playlist playlist) throws BusinessException {
		boolean check = false;
		for (Playlist playlists : storedPlaylists) {
			if (playlists.getId().intValue() == playlist.getId()) {
				check = true;
				playlist.setSongList(songs);
				break;
			}
		}
		if(!check) throw new BusinessException("not_existing_playlist");

	}
	
	@Override
	public void delete(Playlist playlist) throws BusinessException {
		boolean check = false;
		for(Playlist play : storedPlaylists) {
			if(play.getId().intValue() == playlist.getId().intValue()) {
				check = true;
				break;
			}
		}
		if(!check) throw new BusinessException("Object not found, This playlist doesn't exist");
		storedPlaylists.removeIf((Playlist playlistCheck) -> playlistCheck.getId().intValue() == playlist.getId().intValue());
	}
	
	@Override
	public Set<Playlist> getAllPlaylists(User user) throws BusinessException {

		boolean check = false;
		for(User users: getAllUsers()) {
			if(users.getId().intValue() == user.getId().intValue()) {
				check = true;
				break;
			}
		}
		if(check) {
			Set<Playlist> userPlaylists = new HashSet<>();

			for(Playlist playList: storedPlaylists) {
				if(playList.getUser().getId().intValue() == user.getId().intValue()) {
					userPlaylists.add(playList);
				}
			}
			return userPlaylists;
		}
		throw new BusinessException("Object not found, This user doesn't exist");
	}
	
}
