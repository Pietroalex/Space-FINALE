package it.univaq.disim.oop.spacemusicunify.business.impl.ram;

import it.univaq.disim.oop.spacemusicunify.business.*;


public class RAMSpacemusicunifyBusinessFactoryImpl extends SpacemusicunifyBusinessFactory {

	private UserService userService;
	private AlbumService albumService;
	private ArtistService artistService;
	private ProductionService productionService;

	public RAMSpacemusicunifyBusinessFactoryImpl() {
		userService = new RAMUserServiceImpl();
		albumService = new RAMAlbumServiceImpl();
		artistService = new RAMArtistServiceImpl();
		productionService = new RAMProductionServiceImpl();
	}
	@Override
	public UserService getUserService() {
		return null;
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
		return null;
	}
	@Override
	public ProductionService getProductionService() {
		return productionService;
	}
	
}
