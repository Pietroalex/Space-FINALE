package it.univaq.disim.oop.spacemusicunify.domain;

public class PlayQueue {
	
	private Integer id;
	private Utente user;
	private Canzone song;
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public Utente getUser() {
		return user;
	}
	public void setUser(Utente user) {
		this.user = user;
	}
	public Canzone getSong() {
		return song;
	}
	public void setSong(Canzone song) {
		this.song = song;
	}
}
