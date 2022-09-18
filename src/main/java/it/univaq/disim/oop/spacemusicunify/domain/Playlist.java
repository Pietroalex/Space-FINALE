package it.univaq.disim.oop.spacemusicunify.domain;

import java.util.HashSet;
import java.util.Set;

public class Playlist {
	
	private Integer id;
	private String title;
	private Set<Song> songList = new HashSet<>();
	private User user;
	
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
	
	public Set<Song> getSongList() {
		return songList;
	}
	
	public void setSongList(Set<Song> songList) {
		this.songList = songList;
	}
	
	public User getUser() {
		return user;
	}
	
	public void setUser(User user) {
		this.user = user;
	}
	
}
