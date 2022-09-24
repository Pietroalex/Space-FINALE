package it.univaq.disim.oop.spacemusicunify.business.impl.ram;

import it.univaq.disim.oop.spacemusicunify.business.*;


public class RAMSpacemusicunifyBusinessFactoryImpl extends SpacemusicunifyBusinessFactory {

	private SPACEMusicUnifyService spaceMusicUnifyService;
	private UserService userService;
	private AlbumService albumService;
	private ArtistService artistService;
	private MultimediaService multimediaService;
	private ProductionService productionService;

	public RAMSpacemusicunifyBusinessFactoryImpl() {
		spaceMusicUnifyService = new RAMSpaceMusicUnifyImpl();
		userService = new RAMUserServiceImpl();
		productionService = new RAMProductionServiceImpl();
		multimediaService = new RAMMultimediaServiceImpl();
		artistService = new RAMArtistServiceImpl(multimediaService, productionService);
		albumService = new RAMAlbumServiceImpl(multimediaService, productionService);
	}

	@Override
	public SPACEMusicUnifyService getSPACEMusicUnifyService() {
		return spaceMusicUnifyService;
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
	
}
