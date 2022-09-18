package it.univaq.disim.oop.spacemusicunify.domain;

import java.util.ArrayList;
import java.util.List;

public class Playlist {
	private Integer id;
	private String title;
	private List<Song> songList = new ArrayList<>();
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
	public List<Song> getSongList() {
		return songList;
	}
	public void setSongList(List<Song> songList) {
		this.songList = songList;
	}
	public User getUser() {
		return user;
	}
	public void setUser(User user) {
		this.user = user;
	}
	
}
