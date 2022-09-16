package it.univaq.disim.oop.spacemusicunify.business.impl.ram;

import it.univaq.disim.oop.spacemusicunify.business.SPACEMusicUnifyService;
import it.univaq.disim.oop.spacemusicunify.business.SpacemusicunifyBusinessFactory;


public class RAMSpacemusicunifyBusinessFactoryImpl extends SpacemusicunifyBusinessFactory {

	private SPACEMusicUnifyService spaceMusicUnifyService;

	public RAMSpacemusicunifyBusinessFactoryImpl() {
		spaceMusicUnifyService = new RAMSPACEMusicUnifyServiceImpl();

	}
	@Override
	public SPACEMusicUnifyService getSPACEMusicUnifyService() {
		return spaceMusicUnifyService;
	}
}
