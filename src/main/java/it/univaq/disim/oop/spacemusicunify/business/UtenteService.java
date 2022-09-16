package it.univaq.disim.oop.spacemusicunify.business;

import java.util.List;

import it.univaq.disim.oop.spacemusicunify.domain.Canzone;
import it.univaq.disim.oop.spacemusicunify.domain.Playlist;
import it.univaq.disim.oop.spacemusicunify.domain.Utente;

public interface UtenteService {
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
}
