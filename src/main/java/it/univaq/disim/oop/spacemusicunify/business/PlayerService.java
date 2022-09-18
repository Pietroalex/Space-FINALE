package it.univaq.disim.oop.spacemusicunify.business;

import it.univaq.disim.oop.spacemusicunify.domain.Song;
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
	
	Song getLastSong();
	
	void setLastSong(Song canzone);

    PlayerState getPlayerState();

	void setPlayerState(PlayerState playerState);
}