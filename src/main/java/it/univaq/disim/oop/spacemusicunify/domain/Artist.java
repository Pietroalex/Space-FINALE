package it.univaq.disim.oop.spacemusicunify.domain;

import java.util.HashSet;
import java.util.Set;

public class Artist {

	private Integer id;
	private String name;
	private int yearsOfActivity;
	private String biography;
	private Set<Picture> pictures = new HashSet<>();
	private Nationality nationality;
	private Set<Artist> bandMembers = new HashSet<>();
	
	public Nationality getNationality() {
		return nationality;
	}
	
	public void setNationality(Nationality nationality) {
		this.nationality = nationality;
	}

	public String getName() {
		return name;
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
	
	public void setName(String name) {
		this.name = name;
	}
	
	public Set<Picture> getPictures() {
		return pictures;
	}
	
	public void setPictures(Set<Picture> pictures) {
		this.pictures = pictures;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Set<Artist> getBandMembers() {
		return bandMembers;
	}

	public void setBandMembers(Set<Artist> bandMembers) {
		this.bandMembers = bandMembers;
	}
	
}
