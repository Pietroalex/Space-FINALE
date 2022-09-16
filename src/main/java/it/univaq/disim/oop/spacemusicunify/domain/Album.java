package it.univaq.disim.oop.spacemusicunify.domain;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Album {
	
	private Integer id;
	private String title;
	private Genere genre;
	private Picture cover;
	private List<Canzone> songList = new ArrayList<>();
	private LocalDate release;
	private Artista artist;


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
	public Genere getGenre() {
		return genre;
	}
	public void setGenre(Genere genre) {
		this.genre = genre;
	}
	public List<Canzone> getSongList() {
		return songList;
	}
	public void setSongList(List<Canzone> songList) {
		this.songList = songList;
	}
	public LocalDate getRelease() {
		return release;
	}
	public void setRelease(LocalDate release) {
		this.release = release;
	}
	public Artista getArtist() {
		return artist;
	}
	public void setArtist(Artista artista) {
		this.artist = artista;
	}

	public Picture getCover() {
		return cover;
	}

	public void setCover(Picture cover) {
		this.cover = cover;
	}
}
