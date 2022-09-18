package it.univaq.disim.oop.spacemusicunify.domain;


public class Song {
	private Integer id;
	private String title;
	private String fileMp3;
	private String lyrics;
	private Album album;
	private String length;
	private Genre genre;


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
	public String getFileMp3() {
		return fileMp3;
	}
	public void setFileMp3(String fileMp3){
			this.fileMp3 = fileMp3;
	}
	public String getLyrics() {
		return lyrics;
	}
	public void setLyrics(String lyrics) {
		this.lyrics = lyrics;
	}
	public Album getAlbum() {
		return album;
	}
	public void setAlbum(Album album) {
		this.album = album;
	}


	public String getLength() {
		return length;
	}

	public void setLength(String length) {
		this.length = length;
	}
	public Genre getGenre() {
		return genre;
	}
	public void setGenre(Genre genre) {
		this.genre = genre;
	}
}
