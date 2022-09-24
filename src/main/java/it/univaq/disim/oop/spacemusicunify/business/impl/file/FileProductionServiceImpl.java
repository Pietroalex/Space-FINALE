package it.univaq.disim.oop.spacemusicunify.business.impl.file;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import it.univaq.disim.oop.spacemusicunify.business.BusinessException;
import it.univaq.disim.oop.spacemusicunify.business.ProductionService;
import it.univaq.disim.oop.spacemusicunify.domain.Album;
import it.univaq.disim.oop.spacemusicunify.domain.Artist;
import it.univaq.disim.oop.spacemusicunify.domain.Production;
import it.univaq.disim.oop.spacemusicunify.domain.Song;

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
		try {
			FileData fileData = Utility.readAllRows(productionsFile);
			try (PrintWriter writer = new PrintWriter(new File(productionsFile))) {
				long contatore = fileData.getContatore();
				writer.println((contatore));
				for (String[] righe : fileData.getRighe()) {
					writer.println(String.join(Utility.SEPARATORE_COLONNA, righe));
				}
				production.setId((int) contatore);

				StringBuilder row = new StringBuilder();
				row.append(contatore);
				row.append(Utility.SEPARATORE_COLONNA);
				row.append(production.getArtist().getId());
				row.append(Utility.SEPARATORE_COLONNA);
				row.append(production.getAlbum().getId());

				writer.println(row.toString());

			}
		} catch (IOException e) {
			e.printStackTrace();
			throw new BusinessException(e);
		}
		
	}

	@Override
	public void delete(Production production) throws BusinessException {
		// TODO Auto-generated method stub
		
	}

}
