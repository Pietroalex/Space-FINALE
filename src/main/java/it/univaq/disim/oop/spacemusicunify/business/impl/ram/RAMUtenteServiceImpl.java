package it.univaq.disim.oop.spacemusicunify.business.impl.ram;

import java.util.ArrayList;
import java.util.List;

import it.univaq.disim.oop.spacemusicunify.business.*;
import it.univaq.disim.oop.spacemusicunify.domain.*;

public class RAMUtenteServiceImpl implements UtenteService {
	
	private List<Playlist> storedPlaylists = new ArrayList<>();
	private String ricerca;
	private static int id = 1;

	@Override
	public void addNewPlaylist(Playlist playlist) throws BusinessException {
		playlist.setId(id++);
		if(!storedPlaylists.add(playlist)) throw new BusinessException();
	}
	@Override
	public void modify(Integer id, String title, List<Canzone> songlist, Utente user)
			throws AlreadyTakenFieldException {

		for (Playlist playlist : storedPlaylists) {

			if (playlist.getId().intValue() == id.intValue()) {
				playlist.setSongList(songlist);

			}
		}

	}
	@Override
	public void deletePlaylist(Playlist playlist) throws BusinessException {
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
	public List<Playlist> getAllPlaylists(Utente utente) throws BusinessException {
		UtenteGenericoService utenteGenericoService = SpacemusicunifyBusinessFactory.getInstance().getUtenteGenerico();
		boolean controllo = false;
		for(Utente user: utenteGenericoService.getAllUsers()) {
			if(user.getId().intValue() == utente.getId().intValue()) {
				controllo = true;
				break;
			}
		}
		if(controllo) {
			List<Playlist> userPlaylists = new ArrayList<>();
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
	
	@Override
	public String getRicerca() {
		return ricerca;
	}
	@Override
	public void setRicerca(String ricerca) {
		this.ricerca = ricerca;
	}
	@Override
	public void addSongToQueue(Utente utente, Canzone canzone) {
		utente.getSongQueue().add(canzone);
	}
	@Override
	public void deleteSongFromQueue(Utente utente, Canzone canzone) {
		utente.getSongQueue().remove(canzone);
	}
	@Override
	public void updateCurrentSong(Utente utente, int position) {
		utente.setcurrentPosition(position);
	}
	@Override
	public void replaceCurrentSong(Utente utente, Canzone canzone) {
		utente.getSongQueue().set(utente.getcurrentPosition(), canzone);
	}

}
