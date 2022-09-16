package it.univaq.disim.oop.spacemusicunify.business.impl.file;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import it.univaq.disim.oop.spacemusicunify.business.*;
import it.univaq.disim.oop.spacemusicunify.domain.*;
import javafx.scene.image.Image;

public class FileUtenteGenericoServiceImpl implements UtenteGenericoService {

	private static ViewSituations situation;
	private String utentiFileName;
	private String fileCanzoni;
	
	public FileUtenteGenericoServiceImpl(String utentiFIleName, String fileCanzoni) {
		this.utentiFileName = utentiFIleName;
		this.fileCanzoni = fileCanzoni;
	}
	
	@Override
	public void setSituation(ViewSituations sit) {
		situation = sit;		
	}

	@Override
	public ViewSituations getSituation() {
		return situation;
	}

	@Override
	public UtenteGenerico authenticate(String username, String password) throws UtenteGenericoNotFoundException, BusinessException {
		try {
			FileData fileData = Utility.readAllRows(utentiFileName);
			for(String[] colonne : fileData.getRighe()) {
				if(colonne[2].equals(username) && colonne[3].equals(password)) {
					UtenteGenerico utente = null;
					switch (colonne[1]) {
						case "utente" : 
							utente = new Utente();

							((Utente) utente).setcurrentPosition(Integer.parseInt(colonne[4]));
							List<String> songQueue = Utility.leggiArray(colonne[5]);
							List<Canzone> queueList = new ArrayList<>();
							for(String string : songQueue) {
								queueList.add((Canzone) UtilityObjectRetriever.findObjectById(string, fileCanzoni));
							}
							((Utente) utente).setSongQueue(queueList);
							break;
				    
						case "amministratore" :
							utente = new Amministratore();
						    break;
						
						default : 
							break;     
					}
					if(utente != null) {
						utente.setId(Integer.parseInt(colonne[0]));
						utente.setUsername(colonne[2]);
						utente.setPassword(colonne[3]);
					} else { throw new BusinessException("errore nella lettura del file"); } 
					return utente;
				}
			}
			throw new UtenteGenericoNotFoundException();
		} catch (IOException e) {
			e.printStackTrace();
			throw new BusinessException();
		}
	}

	@Override
	public List<Utente> getAllUsers() {
		List<Utente> utenteList = new ArrayList<>();
		try {
			FileData fileData = Utility.readAllRows(utentiFileName);
			fileData.getRighe().remove(0);

				for (String[] colonne : fileData.getRighe()) {
					Utente utente = new Utente();
					utente.setId(Integer.parseInt(colonne[0]));
					utente.setUsername(colonne[2]);
					utente.setPassword(colonne[3]);
					((Utente) utente).setcurrentPosition(Integer.parseInt(colonne[4]));
					List<String> songQueue = Utility.leggiArray(colonne[5]);
					List<Canzone> queueList = new ArrayList<>();
					for(String string : songQueue) {
						queueList.add((Canzone) UtilityObjectRetriever.findObjectById(string, fileCanzoni));
					}
					((Utente) utente).setSongQueue(queueList);
					utenteList.add(utente);
				}

		} catch (IOException e) {
			e.printStackTrace();
		}

		return utenteList;
	}

	@Override
	public List<Artista> getAllArtists() throws BusinessException {
		SPACEMusicUnifyService SPACEMusicUnifyService = SpacemusicunifyBusinessFactory.getInstance().getAmministratoreService();
		return SPACEMusicUnifyService.getAllArtists();
	}

	@Override
	public void createNewUser(Utente utente) throws AlreadyExistingException, BusinessException {
		try {
			FileData fileData = Utility.readAllRows(utentiFileName);
			for (String[] colonne : fileData.getRighe()) {
				if (colonne[2].equals(utente.getUsername())) {
					throw new AlreadyExistingException("Already existing user");
				}
			}
		try (PrintWriter writer = new PrintWriter(new File(utentiFileName))) {
			long contatore = fileData.getContatore();
			writer.println((contatore + 1));
			for (String[] righe : fileData.getRighe()) {
				writer.println(String.join(Utility.SEPARATORE_COLONNA, righe));
			}
			StringBuilder row = new StringBuilder();
			row.append(contatore);
			row.append(Utility.SEPARATORE_COLONNA);
			row.append(TipologiaUtente.utente);
			row.append(Utility.SEPARATORE_COLONNA);
			row.append(utente.getUsername());
			row.append(Utility.SEPARATORE_COLONNA);
			row.append(utente.getPassword());
			row.append(Utility.SEPARATORE_COLONNA);
			row.append("0");
			row.append(Utility.SEPARATORE_COLONNA);
			row.append("[]");
			writer.println(row.toString());

		}
			} catch (IOException e) {
				e.printStackTrace();
				throw new BusinessException(e);
			}
	}

	@Override
	public List<Album> getAllAlbums() throws BusinessException {
		SPACEMusicUnifyService SPACEMusicUnifyService = SpacemusicunifyBusinessFactory.getInstance().getAmministratoreService();
		return SPACEMusicUnifyService.getAllAlbums();
	}

	@Override
	public List<Canzone> getAllSongs() throws BusinessException {
		SPACEMusicUnifyService SPACEMusicUnifyService = SpacemusicunifyBusinessFactory.getInstance().getAmministratoreService();
		return SPACEMusicUnifyService.getAllSongs();
	}

}
