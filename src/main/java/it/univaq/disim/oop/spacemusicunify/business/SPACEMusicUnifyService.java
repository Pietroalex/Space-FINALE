package it.univaq.disim.oop.spacemusicunify.business;

import it.univaq.disim.oop.spacemusicunify.domain.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

public interface SPACEMusicUnifyService {

	void add(User utente) throws BusinessException;
	
	void modify(Integer id, String username, String password) throws BusinessException;

	void delete(User utente) throws BusinessException;
	
    List<Artist> getAllArtists() throws BusinessException;
    
    List<Album> getAllAlbums() throws BusinessException;

	void setAllDefaults();

	List<Song> getAllSongs() throws BusinessException;

	void addNewPlaylist(Playlist playlist) throws BusinessException;

	void modify(Integer id, String title, Set<Song> songlist, User user)
			throws BusinessException;

	List<Playlist> getAllPlaylists(User utente) throws BusinessException;
	
	void deletePlaylist(Playlist playlist) throws BusinessException;
	
	String getRicerca();
	
	void setRicerca(String ricerca);

	GeneralUser authenticate(String username, String password) throws BusinessException;
	
	List<User> getAllUsers();
	
}
