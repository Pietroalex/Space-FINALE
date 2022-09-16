package it.univaq.disim.oop.spacemusicunify.business.impl.ram;

import it.univaq.disim.oop.spacemusicunify.business.SPACEMusicUnifyService;
import it.univaq.disim.oop.spacemusicunify.business.SpacemusicunifyBusinessFactory;
import it.univaq.disim.oop.spacemusicunify.business.UtenteGenericoService;
import it.univaq.disim.oop.spacemusicunify.business.UtenteService;


public class RAMSpacemusicunifyBusinessFactoryImpl extends SpacemusicunifyBusinessFactory {

	private SPACEMusicUnifyService SPACEMusicUnifyService;
	private UtenteGenericoService utenteGenericoService;
	private UtenteService utenteService;

	public RAMSpacemusicunifyBusinessFactoryImpl() {
		SPACEMusicUnifyService = new RAMSPACEMusicUnifyServiceImpl();
		utenteGenericoService = new RAMUtenteGenericoServiceImpl();
		utenteService = new RAMUtenteServiceImpl();
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
