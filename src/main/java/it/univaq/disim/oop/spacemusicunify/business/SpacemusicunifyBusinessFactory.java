package it.univaq.disim.oop.spacemusicunify.business;

import it.univaq.disim.oop.spacemusicunify.business.impl.file.FileSpacemusicunifyBusinessFactoryImpl;
import it.univaq.disim.oop.spacemusicunify.business.impl.ram.RAMSpacemusicunifyBusinessFactoryImpl;

public abstract class SpacemusicunifyBusinessFactory {

	//private static SpacemusicunifyBusinessFactory factory = new RAMSpacemusicunifyBusinessFactoryImpl();

	private static SpacemusicunifyBusinessFactory factory = new FileSpacemusicunifyBusinessFactoryImpl();
	
	public static SpacemusicunifyBusinessFactory getInstance() {
		return factory;
	}
	
	public abstract UserService getUserService();
	public abstract AlbumService getAlbumService();
	public abstract ArtistService getArtistService();
	public abstract MultimediaService getMultimediaService();
	public abstract ProductionService getProductionService();

}
