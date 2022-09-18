package it.univaq.disim.oop.spacemusicunify.domain;

public class PlayQueue {
	
	private Integer id;
	private User user;
	private Song song;
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public User getUser() {
		return user;
	}
	public void setUser(User user) {
		this.user = user;
	}
	public Song getSong() {
		return song;
	}
	public void setSong(Song song) {
		this.song = song;
	}
}
