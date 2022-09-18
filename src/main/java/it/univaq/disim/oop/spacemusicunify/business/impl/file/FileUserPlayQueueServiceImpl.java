package it.univaq.disim.oop.spacemusicunify.business.impl.file;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import it.univaq.disim.oop.spacemusicunify.business.UserPlayQueueService;
import it.univaq.disim.oop.spacemusicunify.domain.Song;
import it.univaq.disim.oop.spacemusicunify.domain.User;

public class FileUserPlayQueueServiceImpl implements UserPlayQueueService{
	
	@Override
	public void addSongToQueue(User utente, Song canzone) {
		/*
		 * try { FileData fileData = Utility.readAllRows(usersFile); int cont = 0;
		 * for(String[] righe : fileData.getRighe()) {
		 * if(righe[0].equals(utente.getId().toString())) { List<String> songQueue =
		 * Utility.leggiArray(righe[5]); songQueue.add(String.valueOf(canzone.getId()));
		 * String[] row = new String[] {righe[0], righe[1], righe[2], righe[3],
		 * righe[4], songQueue.toString()}; fileData.getRighe().set(cont, row);
		 * utente.getSongQueue().add(canzone); break; } cont++; } try (PrintWriter
		 * writer = new PrintWriter(new File(usersFile))) {
		 * writer.println(fileData.getContatore()); for (String[] righe :
		 * fileData.getRighe()) { writer.println(String.join(Utility.SEPARATORE_COLONNA,
		 * righe)); } } } catch (IOException e) { e.printStackTrace(); }
		 */
	}

	@Override
	public void deleteSongFromQueue(User utente, Song canzone) {
		/*
		 * try { FileData fileData = Utility.readAllRows(usersFile); int cont = 0;
		 * for(String[] righe : fileData.getRighe()) {
		 * if(righe[0].equals(utente.getId().toString())) { List<String> songQueue =
		 * Utility.leggiArray(righe[5]);
		 * songQueue.remove(String.valueOf(canzone.getId())); String[] row = new
		 * String[] {righe[0], righe[1], righe[2], righe[3], righe[4],
		 * songQueue.toString()}; fileData.getRighe().set(cont, row); List<Song> lista =
		 * new ArrayList<>(utente.getSongQueue()); for(Song song :
		 * utente.getSongQueue()) { if(song.getId().equals(canzone.getId()))
		 * lista.remove(song); } utente.setSongQueue(lista); break; } cont++; } try
		 * (PrintWriter writer = new PrintWriter(new File(usersFile))) {
		 * writer.println(fileData.getContatore()); for (String[] righe :
		 * fileData.getRighe()) { writer.println(String.join(Utility.SEPARATORE_COLONNA,
		 * righe)); } } } catch (IOException e) { e.printStackTrace(); }
		 */
	}

	@Override
	public void updateCurrentSong(User utente, int position) { //aggiorna la canzone corrente scorrendo la coda
		/*
		 * try { FileData fileData = Utility.readAllRows(usersFile); int cont = 0;
		 * for(String[] righe : fileData.getRighe()) {
		 * if(righe[0].equals(utente.getId().toString())) { String[] row = new String[]
		 * {righe[0], righe[1], righe[2], righe[3], String.valueOf(position), righe[5]};
		 * fileData.getRighe().set(cont, row); utente.setcurrentPosition(position);
		 * break; } cont++; } try (PrintWriter writer = new PrintWriter(new
		 * File(usersFile))) { writer.println(fileData.getContatore()); for (String[]
		 * righe : fileData.getRighe()) {
		 * writer.println(String.join(Utility.SEPARATORE_COLONNA, righe)); } } } catch
		 * (IOException e) { e.printStackTrace(); }
		 */
	}

	@Override
	public void replaceCurrentSong(User utente, Song canzone) { //aggiorna la canzone corrente con una canzone a scelta
		/*try {
			FileData fileData = Utility.readAllRows(usersFile);
			int cont = 0;
			for(String[] righe : fileData.getRighe()) {
				if(righe[0].equals(utente.getId().toString())) {
					List<String> songQueue = Utility.leggiArray(righe[5]);
					songQueue.set(Integer.parseInt(righe[4]), String.valueOf(canzone.getId()));
					String[] row = new String[] {righe[0], righe[1], righe[2], righe[3], righe[4], songQueue.toString()};
					fileData.getRighe().set(cont, row);
					utente.getSongQueue().set(utente.getcurrentPosition(), canzone);
					break;
				}
				cont++;
			}
			try (PrintWriter writer = new PrintWriter(new File(usersFile))) {
				writer.println(fileData.getContatore());
				for (String[] righe : fileData.getRighe()) {
					writer.println(String.join(Utility.SEPARATORE_COLONNA, righe));
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		*/
	}
	
}
