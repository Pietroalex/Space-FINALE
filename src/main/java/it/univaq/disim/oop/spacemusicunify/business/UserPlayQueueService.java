package it.univaq.disim.oop.spacemusicunify.business;

import it.univaq.disim.oop.spacemusicunify.domain.Song;
import it.univaq.disim.oop.spacemusicunify.domain.User;

public interface UserPlayQueueService {
	void addSongToQueue(User utente, Song canzone);
	void deleteSongFromQueue(User utente, Song canzone);
	void updateCurrentSong(User utente, int position);
	void replaceCurrentSong(User utente, Song canzone);
}
