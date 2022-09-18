package it.univaq.disim.oop.spacemusicunify.domain;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Album {
	
	private Integer id;
	private String title;
	private Genre genre;
	private Picture cover;
	private List<Song> songList = new ArrayList<>();
	private LocalDate release;
	private Artist artist;


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
	public List<Song> getSongList() {
		return songList;
	}
	public void setSongList(List<Song> songList) {
		this.songList = songList;
	}
	public LocalDate getRelease() {
		return release;
	}
	public void setRelease(LocalDate release) {
		this.release = release;
	}
	public Artist getArtist() {
		return artist;
	}
	public void setArtist(Artist artista) {
		this.artist = artista;
	}

	public Picture getCover() {
		return cover;
	}

	public void setCover(Picture cover) {
		this.cover = cover;
	}
}
