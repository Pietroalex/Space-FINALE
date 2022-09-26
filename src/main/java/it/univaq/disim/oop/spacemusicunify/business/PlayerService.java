package it.univaq.disim.oop.spacemusicunify.business;

import java.util.List;

import it.univaq.disim.oop.spacemusicunify.domain.Song;
import it.univaq.disim.oop.spacemusicunify.domain.User;
import it.univaq.disim.oop.spacemusicunify.view.SpacemusicunifyPlayer;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;

public interface PlayerService {

    PlayerState getPlayerState();
    
    SpacemusicunifyPlayer getPlayer(User user) throws BusinessException;
    
    List<SpacemusicunifyPlayer> getAllPlayers();

	void setPlayerState(PlayerState playerState);
	
	void addSongToQueue(SpacemusicunifyPlayer player, Song canzone) throws BusinessException;
	
	void deleteSongFromQueue(SpacemusicunifyPlayer player, Song canzone) throws BusinessException;
	
	void updateCurrentSong(SpacemusicunifyPlayer player, int position) throws BusinessException;
	
	void replaceCurrentSong(SpacemusicunifyPlayer player, Song canzone) throws BusinessException;
	
}