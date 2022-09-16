package it.univaq.disim.oop.spacemusicunify.business.impl.file;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import it.univaq.disim.oop.spacemusicunify.business.AlreadyExistingException;
import it.univaq.disim.oop.spacemusicunify.business.BusinessException;
import it.univaq.disim.oop.spacemusicunify.business.UtenteService;
import it.univaq.disim.oop.spacemusicunify.domain.Canzone;
import it.univaq.disim.oop.spacemusicunify.domain.Playlist;
import it.univaq.disim.oop.spacemusicunify.domain.Utente;

public class FileUtenteServiceImpl implements UtenteService {
	private String fileUtenti;
	private String filePlaylist;
	private String fileCanzoni;
	private String ricerca;
	
	public FileUtenteServiceImpl(String fileUtenti, String filePlaylist, String fileCanzoni) {
		this.filePlaylist = filePlaylist;
		this.fileCanzoni = fileCanzoni;
		this.fileUtenti = fileUtenti;
	}

	@Override
	public void addNewPlaylist(Playlist playlist) throws BusinessException {
		try {
			FileData fileData = Utility.readAllRows(filePlaylist);
			for (String[] colonne : fileData.getRighe()) {
				if (colonne[2].equals(playlist.getTitle())) {
					throw new AlreadyExistingException("Already existing Playlist");
				}
			}
			try (PrintWriter writer = new PrintWriter(new File(filePlaylist))) {
				long contatore = fileData.getContatore();
				writer.println((contatore + 1));
				for (String[] righe : fileData.getRighe()) {
					writer.println(String.join(Utility.SEPARATORE_COLONNA, righe));
				}
				StringBuilder row = new StringBuilder();
				row.append(contatore);
				row.append(Utility.SEPARATORE_COLONNA);
				row.append(playlist.getTitle());
				row.append(Utility.SEPARATORE_COLONNA);
				row.append(playlist.getUser().getId());
				row.append(Utility.SEPARATORE_COLONNA);
				row.append(playlist.getSongList());
				writer.println(row.toString());

			}
		} catch (IOException e) {
			e.printStackTrace();
			throw new BusinessException(e);
		}
	}
	
	@Override
	public void modify(Integer id, String title, List<Canzone> songlist, Utente user) throws  BusinessException {
		try {
			FileData fileData = Utility.readAllRows(filePlaylist);
			List<String> idSongList = new ArrayList<>();
			for(Canzone canzone : songlist) {
				idSongList.add(canzone.getId().toString());
			}
			try (PrintWriter writer = new PrintWriter(new File(filePlaylist))) {
				writer.println(fileData.getContatore());
				for (String[] righe : fileData.getRighe()) {
					if (Long.parseLong(righe[0]) == id) {
						StringBuilder row = new StringBuilder();
						row.append(id);
						row.append(Utility.SEPARATORE_COLONNA);
						row.append(title);
						row.append(Utility.SEPARATORE_COLONNA);
						row.append(user.getId());
						row.append(Utility.SEPARATORE_COLONNA);
						row.append(idSongList);

						writer.println(row.toString());
					} else {
						writer.println(String.join(Utility.SEPARATORE_COLONNA, righe));
					}
				}

			}
		} catch (IOException e) {
			e.printStackTrace();
			throw new BusinessException(e);
		}
	}
	@Override
	public void deletePlaylist(Playlist playlist) throws BusinessException {
		boolean check = false;
		try {
			FileData fileData = Utility.readAllRows(filePlaylist);
			for (String[] righecontrollo : fileData.getRighe()) {
				if (righecontrollo[0].equals(playlist.getId().toString())) {
					check = true;

					try (PrintWriter writer = new PrintWriter(new File(filePlaylist))) {
						writer.println(fileData.getContatore());
						for (String[] righe : fileData.getRighe()) {
							if (righe[0].equals(playlist.getId().toString())) {
								//jump line
								continue;
							} else {
								writer.println(String.join("§", righe));
							}
						}
					}
					break;
				}

			}
		} catch(IOException e){
			e.printStackTrace();
		}

		if(!check)throw new BusinessException("Playlist inesistente");

	}
	@Override
	public List<Playlist> getAllPlaylists(Utente utente) {
		List<Playlist> playlistsUtente = new ArrayList<>();
		try {
			FileData fileData = Utility.readAllRows(filePlaylist);
			
			for (String[] colonne : fileData.getRighe()) {

				if(colonne[2].equals(utente.getId().toString())) {
					Playlist playlist = new Playlist();
					playlist.setId(Integer.parseInt(colonne[0]));
					playlist.setTitle(colonne[1]);
					playlist.setUser(utente);
					List<Canzone> listaCanzoni = new ArrayList<>();
					List<String> songList = Utility.leggiArray(colonne[3]);

					if(!(songList.contains("")	)){
						for (String canzoneString : songList) {
							listaCanzoni.add((Canzone) UtilityObjectRetriever.findObjectById(canzoneString, fileCanzoni));
						}
					}
					playlist.setSongList(listaCanzoni);
					
					playlistsUtente.add(playlist);
				}

				
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
		return playlistsUtente;
	}



	@Override
	public String getRicerca() {
		return ricerca;
	}
	@Override
	public void setRicerca(String ricerca) {
		this.ricerca = ricerca;
	}

	@Override
	public void addSongToQueue(Utente utente, Canzone canzone) {
		try {
			FileData fileData = Utility.readAllRows(fileUtenti);
			int cont = 0;
			for(String[] righe : fileData.getRighe()) {
				if(righe[0].equals(utente.getId().toString())) {
					List<String> songQueue = Utility.leggiArray(righe[5]);
					songQueue.add(String.valueOf(canzone.getId()));
					String[] row = new String[] {righe[0], righe[1], righe[2], righe[3], righe[4], songQueue.toString()};
					fileData.getRighe().set(cont, row);
					utente.getSongQueue().add(canzone);
					break;
				}
				cont++;
			}
			try (PrintWriter writer = new PrintWriter(new File(fileUtenti))) {
				writer.println(fileData.getContatore());
				for (String[] righe : fileData.getRighe()) {
					writer.println(String.join(Utility.SEPARATORE_COLONNA, righe));
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void deleteSongFromQueue(Utente utente, Canzone canzone) {
		try {
			FileData fileData = Utility.readAllRows(fileUtenti);
			int cont = 0;
			for(String[] righe : fileData.getRighe()) {
				if(righe[0].equals(utente.getId().toString())) {
					List<String> songQueue = Utility.leggiArray(righe[5]);
					songQueue.remove(String.valueOf(canzone.getId()));
					String[] row = new String[] {righe[0], righe[1], righe[2], righe[3], righe[4], songQueue.toString()};
					fileData.getRighe().set(cont, row);
					List<Canzone> lista = new ArrayList<>(utente.getSongQueue());
					for(Canzone song : utente.getSongQueue()) {
						if(song.getId().equals(canzone.getId())) lista.remove(song);
					}
					utente.setSongQueue(lista);
					break;
				}
				cont++;
			}
			try (PrintWriter writer = new PrintWriter(new File(fileUtenti))) {
				writer.println(fileData.getContatore());
				for (String[] righe : fileData.getRighe()) {
					writer.println(String.join(Utility.SEPARATORE_COLONNA, righe));
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void updateCurrentSong(Utente utente, int position) { //aggiorna la canzone corrente scorrendo la coda
		try {
			FileData fileData = Utility.readAllRows(fileUtenti);
			int cont = 0;
			for(String[] righe : fileData.getRighe()) {
				if(righe[0].equals(utente.getId().toString())) {
					String[] row = new String[] {righe[0], righe[1], righe[2], righe[3], String.valueOf(position), righe[5]};
					fileData.getRighe().set(cont, row);
					utente.setcurrentPosition(position);
					break;
				}
				cont++;
			}
			try (PrintWriter writer = new PrintWriter(new File(fileUtenti))) {
				writer.println(fileData.getContatore());
				for (String[] righe : fileData.getRighe()) {
					writer.println(String.join(Utility.SEPARATORE_COLONNA, righe));
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void replaceCurrentSong(Utente utente, Canzone canzone) { //aggiorna la canzone corrente con una canzone a scelta
		try {
			FileData fileData = Utility.readAllRows(fileUtenti);
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
			try (PrintWriter writer = new PrintWriter(new File(fileUtenti))) {
				writer.println(fileData.getContatore());
				for (String[] righe : fileData.getRighe()) {
					writer.println(String.join(Utility.SEPARATORE_COLONNA, righe));
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
