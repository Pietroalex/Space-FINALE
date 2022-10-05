package it.univaq.disim.oop.spacemusicunify.business.impl.file;

import java.util.List;

public class FileData {
	private long counter;
	private List<String[]> rows;
	
	public long getCounter() {
		return counter;
	}
	public void setCounter(long counter) {
		this.counter = counter;
	}
	public List<String[]> getRows() {
		return rows;
	}
	public void setRows(List<String[]> rows) {
		this.rows = rows;
	}
}
