package it.univaq.disim.oop.spacemusicunify.business.impl.file;

import java.io.File;

import it.univaq.disim.oop.spacemusicunify.business.*;

public class FileSpacemusicunifyBusinessFactoryImpl extends SpacemusicunifyBusinessFactory{

	private final UserService userService;
	private final ArtistService artistService;
	private final AlbumService albumService;
	private final MultimediaService multimediaService;
	private final ProductionService productionService;
	
	private static final String REPOSITORY_BASE = "src" + File.separator + "main" + File.separator + "resources" + File.separator + "dati";
	private static final String ALBUMS_FILE_NAME = REPOSITORY_BASE + File.separator + "albums.txt";
	private static final String ARTISTS_FILE_NAME = REPOSITORY_BASE + File.separator + "singleartists.txt";
	private static final String PLAYLISTS_FILE_NAME = REPOSITORY_BASE + File.separator + "playlists.txt";
	private static final String USERS_FILE_NAME = REPOSITORY_BASE + File.separator + "users.txt";
	private static final String SONGS_FILE_NAME = REPOSITORY_BASE + File.separator + "songs.txt";
	private static final String PRODUCTIONS_FILE_NAME =  REPOSITORY_BASE + File.separator + "productions.txt";
	private static final String PICTURES_FILE_NAME = REPOSITORY_BASE + File.separator + "pictures.txt";
	private static final String AUDIOS_FILE_NAME = REPOSITORY_BASE + File.separator + "audios.txt";
	private static final String PICTURES_DIRECTORY = REPOSITORY_BASE + File.separator + "images" + File.separator;
	private static final String FILES_MP3_DIRECTORY = REPOSITORY_BASE + File.separator + "audios" + File.separator;
	
	public FileSpacemusicunifyBusinessFactoryImpl() {
		userService = new FileUserServiceImpl(USERS_FILE_NAME,ALBUMS_FILE_NAME,ARTISTS_FILE_NAME,SONGS_FILE_NAME, PLAYLISTS_FILE_NAME,PICTURES_DIRECTORY,FILES_MP3_DIRECTORY,PICTURES_FILE_NAME);
		productionService = new FileProductionServiceImpl(PRODUCTIONS_FILE_NAME);
		multimediaService = new FileMultimediaServiceImpl(PICTURES_FILE_NAME, AUDIOS_FILE_NAME, PICTURES_DIRECTORY, FILES_MP3_DIRECTORY);
		artistService = new FileArtistServiceImpl(ARTISTS_FILE_NAME, productionService, multimediaService);
		albumService = new FileAlbumServiceImpl(ALBUMS_FILE_NAME, SONGS_FILE_NAME, productionService, multimediaService);
		UtilityObjectRetriever.setDirectory(PICTURES_DIRECTORY, FILES_MP3_DIRECTORY);
	}

	@Override
	public UserService getUserService() {
		return userService;
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
	public MultimediaService getMultimediaService() {
		return multimediaService;
	}

	@Override
	public ProductionService getProductionService() {
		return productionService;
	}

	@Override
	public SPACEMusicUnifyService getSPACEMusicUnifyService() {
		return null; //mmmmmmmmhhhhh
	}



}
