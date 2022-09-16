package it.univaq.disim.oop.spacemusicunify.business;

import it.univaq.disim.oop.spacemusicunify.domain.Canzone;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;

public interface PlayerService{
	
	MediaPlayer getMediaPlayer();

	void startMediaPlayer(Media media);

    Duration getLastDuration();

	void setLastDuration(Duration lastDuration);

    Boolean getPlayerOnPlay();

	void setPlayerOnPlay(Boolean playerOnPlay);
	
	Double getPlayerVolume();
	
	void setPlayerVolume(Double volume);
	
	Boolean isMute();
	
	void setMute(Boolean mute);
	
	Canzone getLastSong();
	
	void setLastSong(Canzone canzone);

    PlayerState getPlayerState();

	void setPlayerState(PlayerState playerState);
}