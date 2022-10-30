package it.univaq.disim.oop.spacemusicunify.business;

import it.univaq.disim.oop.spacemusicunify.domain.Song;
import it.univaq.disim.oop.spacemusicunify.domain.User;
import it.univaq.disim.oop.spacemusicunify.view.SpacemusicunifyPlayer;
import javafx.util.Duration;


public interface PlayerService {
	
	SpacemusicunifyPlayer getPlayer(User user) throws BusinessException;
	
	void updateDuration(SpacemusicunifyPlayer player, Duration duration) throws BusinessException;
	
	void updateVolume(SpacemusicunifyPlayer player, Double volume) throws BusinessException;
	void updateMute(SpacemusicunifyPlayer player, boolean mute) throws BusinessException;
	
	void add(User user) throws BusinessException;
	
	void delete(User user) throws BusinessException;
	
	void addSongToQueue(SpacemusicunifyPlayer player, Song song) throws BusinessException;
	
	void deleteSongFromQueue(SpacemusicunifyPlayer player, Song song) throws BusinessException;
	
	void updateCurrentSong(SpacemusicunifyPlayer player, int position) throws BusinessException; //action that navigates the play queue
	
	void replaceCurrentSong(SpacemusicunifyPlayer player, Song song) throws BusinessException; //action that modifies the play queue

}