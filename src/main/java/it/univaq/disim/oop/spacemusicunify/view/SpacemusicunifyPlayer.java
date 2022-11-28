package it.univaq.disim.oop.spacemusicunify.view;

import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.image.Image;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaPlayer.Status;
import javafx.util.Duration;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import it.univaq.disim.oop.spacemusicunify.domain.Song;
import it.univaq.disim.oop.spacemusicunify.domain.User;

public class SpacemusicunifyPlayer {
	
	private MediaPlayer mediaPlayer;
	private double volume; //percentage of volume
	private Duration duration; //current time of played song
	private boolean mute; //player is mute or not
	private boolean play; //player is playing or paused
	private ObservableList<Song> queue = FXCollections.observableArrayList(); //songs to play
	private int currentSong; //current song loaded
	private User user;
	private ListChangeListener<? super Song> changeListener;
	
	public SpacemusicunifyPlayer(User user) {
		this.volume = 0.5;
		this.duration = Duration.ZERO;
		this.mute = false;
		this.play = false;
		this.currentSong = 0;
		this.user = user;
		this.changeListener = null;
	}
	
	public void setVolume(double volume) {
		this.volume = volume;
		if(mediaPlayer != null) this.mediaPlayer.setVolume(volume);
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
		if(mediaPlayer != null) this.mediaPlayer.setMute(mute);
	}

	public boolean isPlay() {
		return play;
	}

	public void setPlay(boolean play) {
		this.play = play;
		if(mediaPlayer != null) {
			if (play) {
				this.mediaPlayer.play();
			} else {
				this.mediaPlayer.pause();
			}
		}
	}

	public ObservableList<Song> getQueue() {
		return queue;
	}

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
	
	public ListChangeListener<? super Song> getChangeListener(){
		return changeListener;
	}

	public void setChangeListener(ListChangeListener<? super Song> changeListener) {
		this.changeListener = changeListener;
	}
	
}
