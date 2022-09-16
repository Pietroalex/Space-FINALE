package it.univaq.disim.oop.spacemusicunify.business.impl.file;

import java.io.File;

import it.univaq.disim.oop.spacemusicunify.business.SPACEMusicUnifyService;
import it.univaq.disim.oop.spacemusicunify.business.SpacemusicunifyBusinessFactory;
import it.univaq.disim.oop.spacemusicunify.business.UtenteGenericoService;
import it.univaq.disim.oop.spacemusicunify.business.UtenteService;

public class FileSpacemusicunifyBusinessFactoryImpl extends SpacemusicunifyBusinessFactory{

	private SPACEMusicUnifyService SPACEMusicUnifyService;
	private UtenteGenericoService utenteGenericoService;
	private UtenteService utenteService;
	
	private static final String REPOSITORY_BASE = "src" + File.separator + "main" + File.separator + "resources" + File.separator + "dati";
	private static final String ALBUM_FILE_NAME = REPOSITORY_BASE + File.separator + "albums.txt";
	private static final String ARTISTI_FILE_NAME = REPOSITORY_BASE + File.separator + "artisti.txt";
	private static final String PLAYLIST_FILE_NAME = REPOSITORY_BASE + File.separator + "playlists.txt";
	private static final String UTENTI_FILE_NAME = REPOSITORY_BASE + File.separator + "utenti.txt";
	private static final String CANZONI_FILE_NAME = REPOSITORY_BASE + File.separator + "canzoni.txt";
	private static final String IMMAGINI_DIRECTORY = REPOSITORY_BASE + File.separator + "immagini" + File.separator;
	private static final String FILES_MP3_DIRECTORY = REPOSITORY_BASE + File.separator + "files_mp3" + File.separator;
	
	public FileSpacemusicunifyBusinessFactoryImpl() {
		SPACEMusicUnifyService = new FileSPACEMusicUnifyServiceImpl(UTENTI_FILE_NAME,ALBUM_FILE_NAME,ARTISTI_FILE_NAME,CANZONI_FILE_NAME, PLAYLIST_FILE_NAME,IMMAGINI_DIRECTORY,FILES_MP3_DIRECTORY);
		utenteGenericoService = new FileUtenteGenericoServiceImpl(UTENTI_FILE_NAME,CANZONI_FILE_NAME);
		utenteService = new FileUtenteServiceImpl(UTENTI_FILE_NAME,PLAYLIST_FILE_NAME,CANZONI_FILE_NAME);
		UtilityObjectRetriever.setDirectory(IMMAGINI_DIRECTORY, FILES_MP3_DIRECTORY);
	}
	
	@Override
	public UtenteService getUtenteService() {
		return utenteService;
	}

	@Override
	public SPACEMusicUnifyService getAmministratoreService() {
		return SPACEMusicUnifyService;
	}

	@Override
	public UtenteGenericoService getUtenteGenerico() {
		return utenteGenericoService;
	}

}
