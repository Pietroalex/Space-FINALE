package it.univaq.disim.oop.spacemusicunify.business.impl.file;

import java.io.File;

import it.univaq.disim.oop.spacemusicunify.business.SPACEMusicUnifyService;
import it.univaq.disim.oop.spacemusicunify.business.SpacemusicunifyBusinessFactory;

public class FileSpacemusicunifyBusinessFactoryImpl extends SpacemusicunifyBusinessFactory{

	private final SPACEMusicUnifyService SPACEMusicUnifyService;
	
	private static final String REPOSITORY_BASE = "src" + File.separator + "main" + File.separator + "resources" + File.separator + "dati";
	private static final String ALBUM_FILE_NAME = REPOSITORY_BASE + File.separator + "albums.txt";
	private static final String ARTISTI_FILE_NAME = REPOSITORY_BASE + File.separator + "singleartists.txt";
	private static final String PLAYLIST_FILE_NAME = REPOSITORY_BASE + File.separator + "playlists.txt";
	private static final String UTENTI_FILE_NAME = REPOSITORY_BASE + File.separator + "users.txt";
	private static final String CANZONI_FILE_NAME = REPOSITORY_BASE + File.separator + "songs.txt";
	private static final String PICTURES_FILE_NAME = REPOSITORY_BASE + File.separator + "pictures.txt";
	private static final String IMMAGINI_DIRECTORY = REPOSITORY_BASE + File.separator + "immagini" + File.separator;
	private static final String FILES_MP3_DIRECTORY = REPOSITORY_BASE + File.separator + "files_mp3" + File.separator;
	
	public FileSpacemusicunifyBusinessFactoryImpl() {
		SPACEMusicUnifyService = new FileSPACEMusicUnifyServiceImpl(UTENTI_FILE_NAME,ALBUM_FILE_NAME,ARTISTI_FILE_NAME,CANZONI_FILE_NAME, PLAYLIST_FILE_NAME,IMMAGINI_DIRECTORY,FILES_MP3_DIRECTORY,PICTURES_FILE_NAME);
		UtilityObjectRetriever.setDirectory(IMMAGINI_DIRECTORY, FILES_MP3_DIRECTORY);
	}

	@Override
	public SPACEMusicUnifyService getSPACEMusicUnifyService() {
		return SPACEMusicUnifyService;
	}



}
