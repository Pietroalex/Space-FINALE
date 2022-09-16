package it.univaq.disim.oop.spacemusicunify.business;

import it.univaq.disim.oop.spacemusicunify.business.impl.file.FileSpacemusicunifyBusinessFactoryImpl;

public abstract class SpacemusicunifyBusinessFactory {

	//private static SpacemusicunifyBusinessFactory factory = new RAMSpacemusicunifyBusinessFactoryImpl();

    private static SpacemusicunifyBusinessFactory factory = new FileSpacemusicunifyBusinessFactoryImpl();
	public static SpacemusicunifyBusinessFactory getInstance() {

		return factory;
	}
	
	public abstract UtenteService getUtenteService();
	public abstract SPACEMusicUnifyService getAmministratoreService();
	public abstract UtenteGenericoService getUtenteGenerico();
}
