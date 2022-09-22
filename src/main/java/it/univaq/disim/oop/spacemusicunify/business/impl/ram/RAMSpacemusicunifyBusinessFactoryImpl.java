package it.univaq.disim.oop.spacemusicunify.business.impl.ram;

import it.univaq.disim.oop.spacemusicunify.business.AlbumService;
import it.univaq.disim.oop.spacemusicunify.business.ArtistService;
import it.univaq.disim.oop.spacemusicunify.business.ProductionService;
import it.univaq.disim.oop.spacemusicunify.business.SPACEMusicUnifyService;
import it.univaq.disim.oop.spacemusicunify.business.SpacemusicunifyBusinessFactory;


public class RAMSpacemusicunifyBusinessFactoryImpl extends SpacemusicunifyBusinessFactory {

	private SPACEMusicUnifyService spaceMusicUnifyService;
	private AlbumService albumService;
	private ArtistService artistService;
	private ProductionService productionService;

	public RAMSpacemusicunifyBusinessFactoryImpl() {
		spaceMusicUnifyService = new RAMSPACEMusicUnifyServiceImpl();
		albumService = new RAMAlbumServiceImpl();
		artistService = new RAMArtistServiceImpl();
		productionService = new RAMProductionServiceImpl();
	}
	@Override
	public SPACEMusicUnifyService getSPACEMusicUnifyService() {
		return spaceMusicUnifyService;
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
