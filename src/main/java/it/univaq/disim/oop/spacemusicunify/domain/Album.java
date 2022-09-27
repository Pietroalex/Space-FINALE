package it.univaq.disim.oop.spacemusicunify.domain;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Album {
	
	private Integer id;
	private String title;
	private Genre genre;
	private Picture cover;
	private Set<Song> songList = new HashSet<>();
	private LocalDate release;

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
	
	public Genre getGenre() {
		return genre;
	}
	
	public void setGenre(Genre genre) {
		this.genre = genre;
	}
	
	public Set<Song> getSongs() {
		return songList;
	}
	
	public void setSongs(Set<Song> songList) {
		this.songList = songList;
	}
	
	public LocalDate getRelease() {
		return release;
	}
	
	public void setRelease(LocalDate release) {
		this.release = release;
	}
	
	public Picture getCover() {
		return cover;
	}

	public void setCover(Picture cover) {
		this.cover = cover;
	}
	
}
