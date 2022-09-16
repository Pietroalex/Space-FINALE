package it.univaq.disim.oop.spacemusicunify.business.impl.ram;

import java.util.ArrayList;
import java.util.List;

import it.univaq.disim.oop.spacemusicunify.business.*;
import it.univaq.disim.oop.spacemusicunify.domain.*;

public class RAMUtenteGenericoServiceImpl implements UtenteGenericoService {
	private static List<Utente> storedUsers = new ArrayList<>();
	private static int idUser = 1;
	private static ViewSituations situation;

	@Override
	public void setSituation(ViewSituations sit) {
		situation = sit;
	}

	@Override
	public ViewSituations getSituation() {
		return situation;
	}

	@Override
	public void createNewUser(Utente utente) throws BusinessException, AlreadyExistingException {
		// controllo se l'utente già è presente
		for (Utente user : storedUsers) {
			if (user.getUsername().equals(utente.getUsername())) {
				throw new AlreadyExistingException();
			}
		}
		// utente nuovo
		
		utente.setId(idUser++);
		storedUsers.add(utente);
	}

	@Override
	public UtenteGenerico authenticate(String username, String password) throws BusinessException {
			if ("admin".equalsIgnoreCase(username)) {
				UtenteGenerico admin = new Amministratore();
				admin.setUsername(username);
				admin.setPassword(password);
				return admin;
			} else {
				for (Utente user : storedUsers) {
					if (user.getUsername().equals(username) && user.getPassword().equals(password)) {
						return user;
					}
				}
			}
			throw new UtenteGenericoNotFoundException();
	}

	@Override
	public List<Utente> getAllUsers() {
		return storedUsers;
	}

	@Override
	public List<Album> getAllAlbums() {
		SPACEMusicUnifyService SPACEMusicUnifyService = SpacemusicunifyBusinessFactory.getInstance()
				.getAmministratoreService();
		return SPACEMusicUnifyService.getAllAlbums();
	}

	@Override
	public List<Canzone> getAllSongs() {
		SPACEMusicUnifyService SPACEMusicUnifyService = SpacemusicunifyBusinessFactory.getInstance()
				.getAmministratoreService();
		return SPACEMusicUnifyService.getAllSongs();
	}

	@Override
	public List<Artista> getAllArtists() {
		SPACEMusicUnifyService SPACEMusicUnifyService = SpacemusicunifyBusinessFactory.getInstance()
				.getAmministratoreService();
		return SPACEMusicUnifyService.getAllArtists();
	}

}
