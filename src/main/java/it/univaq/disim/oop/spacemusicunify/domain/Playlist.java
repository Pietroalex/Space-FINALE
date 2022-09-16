package it.univaq.disim.oop.spacemusicunify.domain;

import java.util.ArrayList;
import java.util.List;

public class Playlist {
	private Integer id;
	private String title;
	private List<Canzone> songList = new ArrayList<>();
	private Utente user;
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public List<Canzone> getSongList() {
		return songList;
	}
	public void setSongList(List<Canzone> songList) {
		this.songList = songList;
	}
	public Utente getUser() {
		return user;
	}
	public void setUser(Utente user) {
		this.user = user;
	}
	
}
