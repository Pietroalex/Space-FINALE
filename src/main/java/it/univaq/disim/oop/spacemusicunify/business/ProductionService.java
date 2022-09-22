package it.univaq.disim.oop.spacemusicunify.business;

import java.util.List;

import it.univaq.disim.oop.spacemusicunify.domain.Production;

public interface ProductionService {

	List<Production> getAllProductions() throws BusinessException;
	
	void add(Production production) throws BusinessException;
	
	void delete(Production production) throws BusinessException;
	
}
