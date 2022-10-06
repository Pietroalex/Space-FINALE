package it.univaq.disim.oop.spacemusicunify.view;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;
import java.util.ArrayList;
import java.util.List;

import it.univaq.disim.oop.spacemusicunify.domain.Song;
import it.univaq.disim.oop.spacemusicunify.domain.User;

public class SpacemusicunifyPlayer {
	
	private MediaPlayer mediaPlayer;
	private double volume = 0.5; //percentage of volume
	private Duration duration = Duration.ZERO; //current time of played song
	private boolean mute = false; //player is mute or not
	private boolean play = false; //player is playing or paused
	private final ObservableList<Song> queue = FXCollections.observableArrayList(); //songs to play
	private int currentSong = 0; //current song loaded
	private User user;
	
	public SpacemusicunifyPlayer(User user) {
		this.user = user;
	}
	
	public void setVolume(double volume) {
		this.volume = volume;
		this.mediaPlayer.setVolume(volume);
	}
	
	public double getVolume() {
		return volume;
	}

	public Duration getDuration() {
		return duration;
	}

	public void setDuration(Duration duration) {
		this.duration = duration;
	}

	public boolean isMute() {
		return mute;
	}

	public void setMute(boolean mute) {
		this.mute = mute;
		this.mediaPlayer.setMute(mute);
	}

	public boolean isPlay() {
		return play;
	}

	public void setPlay(boolean play) {
		this.play = play;
		if(play) {
			this.mediaPlayer.play();
		} else {
			this.mediaPlayer.pause();
		}
	}

	public ObservableList<Song> getQueue() {
		return queue;
	}

	/*
	 * public void setQueue(List<Song> queue) { this.queue = queue; }
	 */

	public int getCurrentSong() {
		return currentSong;
	}

	public void setCurrentSong(int currentSong) {
		this.currentSong = currentSong;
	}

	public User getUser() {
		return user;
	}

	/*
	 * public void setUser(User user) { this.user = user; }
	 */

	public MediaPlayer getMediaPlayer() {
		return mediaPlayer;
	}

	public void setMediaPlayer(MediaPlayer mediaPlayer) {
		this.mediaPlayer = mediaPlayer;
	}
	
}
