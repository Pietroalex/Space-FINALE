package it.univaq.disim.oop.spacemusicunify.business.impl.file;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import it.univaq.disim.oop.spacemusicunify.business.BusinessException;
import it.univaq.disim.oop.spacemusicunify.business.ProductionService;
import it.univaq.disim.oop.spacemusicunify.domain.Album;
import it.univaq.disim.oop.spacemusicunify.domain.Artist;
import it.univaq.disim.oop.spacemusicunify.domain.Production;

public class FileProductionServiceImpl implements ProductionService {
	
	private String productionsFile;
	
	public FileProductionServiceImpl(String productionsFile) {
		this.productionsFile = productionsFile;
	}

	@Override
	public List<Production> getAllProductions() throws BusinessException {
		List<Production> productionList = new ArrayList<>();
		System.out.println("productions");
		try {
			FileData fileData = Utility.readAllRows(productionsFile);

			for (String[] colonne : fileData.getRighe()) {

				Production production = new Production();
				Artist artist = new Artist();
				Album album = new Album();

				production.setId(Integer.parseInt(colonne[0]));
				artist.setId(Integer.parseInt(colonne[1]));
				album.setId(Integer.parseInt(colonne[2]));

				production.setArtist(artist);
				production.setAlbum(album);
				productionList.add(production);


			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

		System.out.println("finito");
		return productionList;

	}

	@Override
	public void add(Production production) throws BusinessException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void delete(Production production) throws BusinessException {
		// TODO Auto-generated method stub
		
	}

}
