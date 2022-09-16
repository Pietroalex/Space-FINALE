package it.univaq.disim.oop.spacemusicunify.domain;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javafx.scene.image.Image;

public class Artista {

	private Integer id;
	private String stageName;
	private int yearsOfActivity;
	private String biography;
	private List<String> pictures = new ArrayList<>();
	private Nazionalità nationality;

	private Set<Album> discography = new HashSet<>();

	public String getStageName() {
		return stageName;
	}
	public void setStage_name(String stageName) {
		this.stageName = stageName;
	}
	public int getYearsOfActivity() {
		return yearsOfActivity;
	}
	public void setYearsOfActivity(int yearsOfActivity) {
		this.yearsOfActivity = yearsOfActivity;
	}
	public String getBiography() {
		return biography;
	}
	public void setBiography(String biography) {
		this.biography = biography;
	}
	
	public Nazionalità getNationality() {
		return nationality;
	}
	public void setNationality(Nazionalità nationality) {
		this.nationality = nationality;
	}
	public void setStageName(String stageName) {
		this.stageName = stageName;
	}
	public Set<Album> getDiscography() {
		return discography;
	}
	public void setDiscography(Set<Album> discography) {
		this.discography = discography;
	}
	public List<String> getPictures() {
		return pictures;
	}
	public void setPictures(List<String> picture) {
		this.pictures = picture;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}
}
