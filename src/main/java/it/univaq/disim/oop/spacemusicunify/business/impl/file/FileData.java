package it.univaq.disim.oop.spacemusicunify.business.impl.file;

import java.util.List;

public class FileData {
	private long contatore;
	private List<String[]> righe;
	
	public long getContatore() {
		return contatore;
	}
	public void setContatore(long contatore) {
		this.contatore = contatore;
	}
	public List<String[]> getRighe() {
		return righe;
	}
	public void setRighe(List<String[]> righe) {
		this.righe = righe;
	}
}
