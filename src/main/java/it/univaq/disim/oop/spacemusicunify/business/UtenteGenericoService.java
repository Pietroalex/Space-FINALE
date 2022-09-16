package it.univaq.disim.oop.spacemusicunify.business;

import java.util.List;

import it.univaq.disim.oop.spacemusicunify.domain.*;

public interface UtenteGenericoService {
	void setSituation(ViewSituations sit);

	ViewSituations getSituation();

	UtenteGenerico authenticate(String username, String password) throws BusinessException;
	List<Utente> getAllUsers();

	List<Artista> getAllArtists() throws BusinessException;

	void createNewUser(Utente utente) throws BusinessException;

	List<Album> getAllAlbums() throws BusinessException;

	List<Canzone> getAllSongs() throws BusinessException;


}
