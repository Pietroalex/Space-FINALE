package it.univaq.disim.oop.spacemusicunify.business;

import it.univaq.disim.oop.spacemusicunify.domain.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

public interface SPACEMusicUnifyService {

	//utente
	public void add(User utente) throws BusinessException;
	void modify(Integer id, String username, String password) throws BusinessException;

	public void delete(User utente) throws BusinessException;
	//picture
	public void add(Picture picture) throws BusinessException;
	void modify(Integer id, byte[] photo, int height, int width) throws BusinessException;

	public void delete(Picture picture) throws BusinessException;
	
	//artista
	public void add(Artist artista) throws BusinessException;
	void modify(Integer id, String stageName, String biography, int yearsOfActivity, Nationality nationality, Set<Picture> images) throws BusinessException;

	public void delete(Artist artista) throws BusinessException;


	//album
	public void add(Album album) throws BusinessException;
	void modify(Integer id, String title, Genre genre, LocalDate release, Picture cover, Set<Song> songlist) throws BusinessException;

	public void delete(Album album) throws BusinessException;
	
	//canzone

	void add(Song canzone)throws BusinessException;

	public void modify(Integer id, String title, String duration, Genre genre, byte[] mp3, String lyrics, Album album) throws BusinessException;

	public void delete(Song canzone) throws BusinessException;

    List<Artist> getAllArtists() ;

	void setAllDefaults();

	List<Album> getAllAlbums();

	List<Song> getAllSongs();

	//utenteservice
	public void addNewPlaylist(Playlist playlist) throws BusinessException;

	void modify(Integer id, String title, Set<Song> songlist, User user)
			throws BusinessException;

	public List<Playlist> getAllPlaylists(User utente) throws BusinessException;
	public void deletePlaylist(Playlist playlist) throws BusinessException;
	public String getRicerca();
	public void setRicerca(String ricerca);

	//utentegenericoService
	void setSituation(ViewSituations sit);

	ViewSituations getSituation();

	GeneralUser authenticate(String username, String password) throws BusinessException;
	List<User> getAllUsers();

	void createNewUser(User utente) throws BusinessException;
	
}
