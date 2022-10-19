package it.univaq.disim.oop.spacemusicunify.business.impl.ram;

import it.univaq.disim.oop.spacemusicunify.business.*;
import it.univaq.disim.oop.spacemusicunify.domain.*;

import java.util.*;

public class RAMUserServiceImpl implements UserService {

	private static Set<User> storedUsers = new HashSet<>();
	private static Set<Playlist> storedPlaylists = new HashSet<>();
	private static int idUser = 1;
	private static int id = 1; // da capire cos'è

	@Override
	public void add(User user) throws BusinessException {
		// controllo se l'utente già è presente
		for (User users : storedUsers) {
			if (users.getUsername().equals(user.getUsername()) || user.getUsername().contains("admin")) {
				throw new AlreadyExistingException("existing_user");
			}
		}
		// utente nuovo

		user.setId(idUser++);
		storedUsers.add(user);
		SpacemusicunifyBusinessFactory.getInstance().getPlayerService().add(user);
	}

	@Override
	public void modify(Integer id, String username, String password) throws BusinessException {
		Set<User> utenti = null;

		utenti = getAllUsers();

		for (User user : utenti) {
			if (user.getUsername().equals(username) && user.getId().intValue() != id.intValue()) {
				throw new AlreadyExistingException();
			}
		}
		for (User user : utenti) {
			if (user.getId().intValue() == id.intValue()) {
				user.setUsername(username);
				user.setPassword(password);
			}
		}
	}

	@Override
	public void delete(User user) throws BusinessException {


		Set<Playlist> playlists = null;
		try {
			playlists = getAllPlaylists(user);
			System.out.println(playlists);
		} catch (BusinessException e) {
			throw new RuntimeException(e);
		}

		for (User userCheck : getAllUsers()) {
			if (userCheck.getId().intValue() == user.getId().intValue()) {
				storedUsers.remove(userCheck);
				break;
			}
		}
		
		
		Set<Playlist> playlistList = playlists;
		for (Playlist playlist : playlistList) {
			if (playlist.getUser().equals(user)) {
				delete(playlist);
			}
		}
		
		SpacemusicunifyBusinessFactory.getInstance().getPlayerService().delete(user);
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
		throw new ObjectNotFoundException();
	}

	@Override
	public Set<User> getAllUsers() throws BusinessException {
		if(storedUsers == null) throw  new BusinessException();
		return new HashSet<>(storedUsers);
	}

	@Override
	public void add(Playlist playlist) throws BusinessException {
		playlist.setId(id++);
		if(!storedPlaylists.add(playlist)) throw new BusinessException();
	}
	
	@Override
	public void modify(Integer id, String title, Set<Song> songlist, User user, Playlist value) throws BusinessException {
		boolean check = false;
		for (Playlist playlist : storedPlaylists) {
			if (playlist.getId().intValue() == id.intValue()) {
				check = true;
				playlist.setSongList(songlist);
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
		if(check) {
			storedPlaylists.removeIf((Playlist playlistCheck) -> playlistCheck.getId().intValue() == playlist.getId().intValue());
		} else {
			throw new BusinessException("not_existing_playlist");
		}
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
		throw new BusinessException("not_existing_user");
	}
	
}
