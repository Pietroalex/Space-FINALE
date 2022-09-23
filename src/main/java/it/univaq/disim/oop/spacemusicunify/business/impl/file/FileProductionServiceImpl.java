package it.univaq.disim.oop.spacemusicunify.business.impl.file;

import java.util.List;

import it.univaq.disim.oop.spacemusicunify.business.BusinessException;
import it.univaq.disim.oop.spacemusicunify.business.ProductionService;
import it.univaq.disim.oop.spacemusicunify.domain.Production;

public class FileProductionServiceImpl implements ProductionService {
	
	private String productionsFile;
	
	public FileProductionServiceImpl(String productionsFile) {
		this.productionsFile = productionsFile;
	}

	@Override
	public List<Production> getAllProductions() throws BusinessException {
		// TODO Auto-generated method stub
		return null;
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
