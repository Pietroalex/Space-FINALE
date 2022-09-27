package it.univaq.disim.oop.spacemusicunify.business;

import it.univaq.disim.oop.spacemusicunify.domain.Song;
import it.univaq.disim.oop.spacemusicunify.view.SpacemusicunifyPlayer;

public interface PlayerService {

    PlayerState getPlayerState();
    
	void setPlayerState(PlayerState playerState);
	
	void addSongToQueue(SpacemusicunifyPlayer player, Song canzone) throws BusinessException;
	
	void deleteSongFromQueue(SpacemusicunifyPlayer player, Song canzone) throws BusinessException;
	
	void updateCurrentSong(SpacemusicunifyPlayer player, int position) throws BusinessException;
	
	void replaceCurrentSong(SpacemusicunifyPlayer player, Song canzone) throws BusinessException;
	
}