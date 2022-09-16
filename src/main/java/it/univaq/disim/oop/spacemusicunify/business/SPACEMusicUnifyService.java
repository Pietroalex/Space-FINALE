package it.univaq.disim.oop.spacemusicunify.business;

import it.univaq.disim.oop.spacemusicunify.domain.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

public interface SPACEMusicUnifyService {

	//utente
	public void add(Utente utente) throws BusinessException;
	void modify(Integer id, String username, String password) throws BusinessException;

	public void delete(Utente utente) throws BusinessException;
	//picture
	public void add(Picture picture) throws BusinessException;
	void modify(Integer id, byte[] photo, int height, int width) throws BusinessException;

	public void delete(Picture picture) throws BusinessException;
	
	//artista
	public void add(Artista artista) throws BusinessException;
	void modify(Integer id, String stageName, String biography, int yearsOfActivity, Nazionalità nationality, Set<Picture> images) throws BusinessException;

	public void delete(Artista artista) throws BusinessException;


	//album
	public void add(Album album) throws BusinessException;
	void modify(Integer id, String title, Genere genre, LocalDate release, Picture cover, List<Canzone> songlist) throws BusinessException;

	public void delete(Album album) throws BusinessException;
	
	//canzone

	void add(Canzone canzone)throws BusinessException;

	public void modify(Integer id, String title, String duration, Genere genre, String mp3, String lyrics, Album album) throws BusinessException;

	public void delete(Canzone canzone) throws BusinessException;

    List<Artista> getAllArtists() ;

	void setAllDefaults();

	List<Album> getAllAlbums();

	List<Canzone> getAllSongs();

	//utenteservice
	public void addNewPlaylist(Playlist playlist) throws BusinessException;

	void modify(Integer id, String title, List<Canzone> songlist, Utente user)
			throws BusinessException;

	public List<Playlist> getAllPlaylists(Utente utente) throws BusinessException;
	public void deletePlaylist(Playlist playlist) throws BusinessException;
	public String getRicerca();
	public void setRicerca(String ricerca);
	void addSongToQueue(Utente utente, Canzone canzone);
	void deleteSongFromQueue(Utente utente, Canzone canzone);
	void updateCurrentSong(Utente utente, int position);
	void replaceCurrentSong(Utente utente, Canzone canzone);

	//utentegenericoService
	void setSituation(ViewSituations sit);

	ViewSituations getSituation();

	UtenteGenerico authenticate(String username, String password) throws BusinessException;
	List<Utente> getAllUsers();

	void createNewUser(Utente utente) throws BusinessException;


}
