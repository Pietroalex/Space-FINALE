package it.univaq.disim.oop.spacemusicunify.domain;

import java.util.HashSet;
import java.util.Set;

public class Artist {

	private Integer id;
	private String stageName;
	private int yearsOfActivity;
	private String biography;
	private Set<Picture> pictures = new HashSet<>();

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
	
	public void setStageName(String stageName) {
		this.stageName = stageName;
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
	
}
