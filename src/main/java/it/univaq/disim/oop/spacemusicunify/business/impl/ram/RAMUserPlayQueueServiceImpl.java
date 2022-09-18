package it.univaq.disim.oop.spacemusicunify.business.impl.ram;

import it.univaq.disim.oop.spacemusicunify.business.UserPlayQueueService;
import it.univaq.disim.oop.spacemusicunify.domain.Song;
import it.univaq.disim.oop.spacemusicunify.domain.User;

public class RAMUserPlayQueueServiceImpl implements UserPlayQueueService{
	
	@Override
	public void addSongToQueue(User utente, Song canzone) {
		//utente.getSongQueue().add(canzone);
	}
	@Override
	public void deleteSongFromQueue(User utente, Song canzone) {
		//utente.getSongQueue().remove(canzone);
	}
	@Override
	public void updateCurrentSong(User utente, int position) {
		//utente.setcurrentPosition(position);
	}
	@Override
	public void replaceCurrentSong(User utente, Song canzone) {
		//utente.getSongQueue().set(utente.getcurrentPosition(), canzone);
	}
	
}
