package it.univaq.disim.oop.spacemusicunify.business.impl.file;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import it.univaq.disim.oop.spacemusicunify.business.BusinessException;
import it.univaq.disim.oop.spacemusicunify.business.ProductionService;
import it.univaq.disim.oop.spacemusicunify.domain.Album;
import it.univaq.disim.oop.spacemusicunify.domain.Artist;
import it.univaq.disim.oop.spacemusicunify.domain.Production;

public class FileProductionServiceImpl implements ProductionService {
	
	private String productionsFile;
	private String artistsFileName;
	private String albumsFileName;
	
	public FileProductionServiceImpl(String productionsFile, String artistsFileName, String albumsFileName) {
		this.productionsFile = productionsFile;
		this.artistsFileName = artistsFileName;
		this.albumsFileName = albumsFileName;
	}

	@Override
	public Set<Production> getAllProductions() throws BusinessException {
		Set<Production> productionList = new HashSet<>();

		try {
			FileData fileData = Utility.readAllRows(productionsFile);

			for (String[] colonne : fileData.getRighe()) {

				Production production = new Production();
				Artist artist = (Artist) UtilityObjectRetriever.findObjectById(colonne[1], artistsFileName);
				Album album = (Album) UtilityObjectRetriever.findObjectById(colonne[2], albumsFileName);

				production.setId(Integer.parseInt(colonne[0]));

				production.setArtist(artist);
				production.setAlbum(album);
				productionList.add(production);


			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}


		return productionList;

	}

	@Override
	public void add(Production production) throws BusinessException {
		try {
			FileData fileData = Utility.readAllRows(productionsFile);
			try (PrintWriter writer = new PrintWriter(new File(productionsFile))) {
				long contatore = fileData.getContatore();
				writer.println((contatore + 1));
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
