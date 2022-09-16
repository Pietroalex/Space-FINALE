package it.univaq.disim.oop.spacemusicunify.domain;

import java.util.ArrayList;
import java.util.List;

public class Utente extends UtenteGenerico {
	
	private List<Canzone> songQueue = new ArrayList<>();
	private int currentPosition;

	public int getcurrentPosition() {
		return currentPosition;
	}
	public void setcurrentPosition(int currentPosition) {
		this.currentPosition = currentPosition;
	}
	public List<Canzone> getSongQueue() {
		return songQueue;
	}
	public void setSongQueue(List<Canzone> songQueue) {
		this.songQueue = songQueue;
	}
	public Canzone getcurrentSong() {
		if(currentPosition < songQueue.size()) {
			return songQueue.get(currentPosition);
		}
		return null;
	}
	
}
