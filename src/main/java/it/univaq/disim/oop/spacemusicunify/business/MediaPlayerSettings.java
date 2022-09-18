package it.univaq.disim.oop.spacemusicunify.business;

import it.univaq.disim.oop.spacemusicunify.domain.Song;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;

public class MediaPlayerSettings implements PlayerService {
	
	private MediaPlayer mediaPlayer;
	private Duration lastDuration;
	private boolean playerOnPlay;
	private Double volume;
	private boolean mute;
	private Song lastSong;
	private PlayerState playerState;
	
	private static MediaPlayerSettings instance;
	
	public static MediaPlayerSettings getInstance() {
		if(instance == null) { instance = new MediaPlayerSettings();}
		return instance;
	}
	private MediaPlayerSettings() {};

	@Override
	public MediaPlayer getMediaPlayer() {
		return mediaPlayer;
	}
	@Override
	public void startMediaPlayer(Media media) {
		this.mediaPlayer = new MediaPlayer(media);
	}
	@Override
	public Duration getLastDuration() {
		return lastDuration;
	}
	@Override
	public void setLastDuration(Duration lastDuration) {
		this.lastDuration = lastDuration;

	}
	@Override
	public Boolean getPlayerOnPlay() {
		return playerOnPlay;
	}
	@Override
	public void setPlayerOnPlay(Boolean playerOnPlay) {
		this.playerOnPlay = playerOnPlay;
	}
	@Override
	public Double getPlayerVolume() {
		return volume;
	}
	@Override
	public void setPlayerVolume(Double volume) {
		this.volume = volume;
	}
	@Override
	public Boolean isMute() {
		return mute;
	}
	@Override
	public void setMute(Boolean mute) {
		this.mute = mute;
	}
	@Override
	public Song getLastSong() {
		return lastSong;
	}
	@Override
	public void setLastSong(Song song) {
		this.lastSong = song;
	}
	@Override
	public PlayerState getPlayerState() {
		return playerState;
	}
	@Override
	public void setPlayerState(PlayerState playerState) {
		this.playerState = playerState;
	}
}
