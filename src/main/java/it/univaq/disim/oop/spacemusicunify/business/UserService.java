package it.univaq.disim.oop.spacemusicunify.business;

import it.univaq.disim.oop.spacemusicunify.domain.*;

import java.util.List;
import java.util.Set;

public interface UserService {

	void add(User utente) throws BusinessException;
	
	void modify(Integer id, String username, String password) throws BusinessException;

	void delete(User utente) throws BusinessException;

	void add(Playlist playlist) throws BusinessException;

	void modify(Integer id, String title, Set<Song> songlist, User user) throws BusinessException;

	Set<Playlist> getAllPlaylists(User utente) throws BusinessException;
	
	void deletePlaylist(Playlist playlist) throws BusinessException;
	
	String getRicerca();
	
	void setRicerca(String ricerca);

	GeneralUser authenticate(String username, String password) throws BusinessException;
	
	Set<User> getAllUsers();
	
}
