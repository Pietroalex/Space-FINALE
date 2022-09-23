package it.univaq.disim.oop.spacemusicunify.business.impl.file;

import java.io.File;

import it.univaq.disim.oop.spacemusicunify.business.AlbumService;
import it.univaq.disim.oop.spacemusicunify.business.ArtistService;
import it.univaq.disim.oop.spacemusicunify.business.ProductionService;
import it.univaq.disim.oop.spacemusicunify.business.SPACEMusicUnifyService;
import it.univaq.disim.oop.spacemusicunify.business.SpacemusicunifyBusinessFactory;

public class FileSpacemusicunifyBusinessFactoryImpl extends SpacemusicunifyBusinessFactory{

	private final SPACEMusicUnifyService SPACEMusicUnifyService;
	private final ArtistService artistService;
	private final AlbumService albumService;
	private final ProductionService productionService;
	
	private static final String REPOSITORY_BASE = "src" + File.separator + "main" + File.separator + "resources" + File.separator + "dati";
	private static final String ALBUMS_FILE_NAME = REPOSITORY_BASE + File.separator + "albums.txt";
	private static final String ARTISTS_FILE_NAME = REPOSITORY_BASE + File.separator + "singleartists.txt";
	private static final String PLAYLISTS_FILE_NAME = REPOSITORY_BASE + File.separator + "playlists.txt";
	private static final String USERS_FILE_NAME = REPOSITORY_BASE + File.separator + "users.txt";
	private static final String SONGS_FILE_NAME = REPOSITORY_BASE + File.separator + "songs.txt";
	private static final String PRODUCTIONS_FILE_NAME =  REPOSITORY_BASE + File.separator + "productions.txt";
	private static final String PICTURES_FILE_NAME = REPOSITORY_BASE + File.separator + "pictures.txt";
	private static final String IMMAGINI_DIRECTORY = REPOSITORY_BASE + File.separator + "immagini" + File.separator;
	private static final String FILES_MP3_DIRECTORY = REPOSITORY_BASE + File.separator + "files_mp3" + File.separator;
	
	public FileSpacemusicunifyBusinessFactoryImpl() {
		SPACEMusicUnifyService = new FileSPACEMusicUnifyServiceImpl(USERS_FILE_NAME,ALBUMS_FILE_NAME,ARTISTS_FILE_NAME,SONGS_FILE_NAME, PLAYLISTS_FILE_NAME,IMMAGINI_DIRECTORY,FILES_MP3_DIRECTORY,PICTURES_FILE_NAME);
		artistService = new FileArtistServiceImpl(ARTISTS_FILE_NAME);
		albumService = new FileAlbumServiceImpl(ALBUMS_FILE_NAME, SONGS_FILE_NAME);
		productionService = new FileProductionServiceImpl(PRODUCTIONS_FILE_NAME);
		UtilityObjectRetriever.setDirectory(IMMAGINI_DIRECTORY, FILES_MP3_DIRECTORY);
	}

	@Override
	public SPACEMusicUnifyService getSPACEMusicUnifyService() {
		return SPACEMusicUnifyService;
	}

	@Override
	public AlbumService getAlbumService() {
		return albumService;
	}

	@Override
	public ArtistService getArtistService() {
		return artistService;
	}

	@Override
	public ProductionService getProductionService() {
		return productionService;
	}



}
