package it.univaq.disim.oop.spacemusicunify.business.impl.ram;

import it.univaq.disim.oop.spacemusicunify.business.*;
import it.univaq.disim.oop.spacemusicunify.domain.*;

import java.io.File;
import java.util.*;

public class RAMUserServiceImpl implements UserService {

	private static Set<User> storedUsers = new HashSet<>();
	private static Set<Playlist> storedPlaylists = new HashSet<>();
	private static int idUser = 1;
	private static int id = 1; // da capire cos'è

	private static String path = "src"+ File.separator + "main" + File.separator + "resources" + File.separator + "data" + File.separator + "RAMfiles" + File.separator;
	private static String pathmp3 = "src"+ File.separator + "main" + File.separator + "resources" + File.separator + "data" + File.separator + "RAMfiles" + File.separator;

	@Override
	public void add(User newUser) throws BusinessException {
		// controllo se l'utente già è presente
		for (User user : storedUsers) {
			if (user.getUsername().equals(newUser.getUsername())) {
				throw new AlreadyExistingException();
			}
		}
		// utente nuovo

		newUser.setId(idUser++);
		storedUsers.add(newUser);
		SpacemusicunifyBusinessFactory.getInstance().getPlayerService().add(newUser);
	}

	@Override
	public void modify(Integer id, String username, String password) throws AlreadyTakenFieldException {
		Set<User> utenti = null;
		try {
			utenti = getAllUsers();


		for (User user : utenti) {
			if (user.getUsername().equals(username) && user.getId().intValue() != id.intValue()) {
				throw new AlreadyTakenFieldException();
			}
		}
		for (User user : utenti) {
			if (user.getId().intValue() == id.intValue()) {
				user.setUsername(username);
				user.setPassword(password);
			}
		}
	} catch (BusinessException e) {
		throw new RuntimeException(e);
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
	public void modify(Integer id, String title, Set<Song> songlist, User user)
			throws AlreadyTakenFieldException {

		for (Playlist playlist : storedPlaylists) {

			if (playlist.getId().intValue() == id.intValue()) {
				playlist.setSongList(songlist);

			}
		}

	}
	
	@Override
	public void delete(Playlist playlist) throws BusinessException {
		boolean controllo = false;
		for(Playlist play : storedPlaylists) {
			if(play.getId().intValue() == playlist.getId().intValue()) {
				controllo = true;
				break;
			}
		}
		if(controllo) {
			storedPlaylists.remove(playlist);
		} else {
			throw new BusinessException();
		}
	}
	
	@Override
	public Set<Playlist> getAllPlaylists(User utente) throws BusinessException {

		boolean controllo = false;
		for(User user: getAllUsers()) {
			if(user.getId().intValue() == utente.getId().intValue()) {
				controllo = true;
				break;
			}
		}
		if(controllo) {
			Set<Playlist> userPlaylists = new HashSet<>();
			//prendere solo le playlist dell'utente passato

			for(Playlist playList: storedPlaylists) {
				if(playList.getUser() == utente) {
					userPlaylists.add(playList);
				}
			}
			return userPlaylists;
		}
		throw new BusinessException();
	}
	
}
