package it.univaq.disim.oop.spacemusicunify.business;

import java.util.Set;

import it.univaq.disim.oop.spacemusicunify.domain.Production;

public interface ProductionService {

	Set<Production> getAllProductions() throws BusinessException;
	
	void add(Production production) throws BusinessException;
	
	void delete(Production production) throws BusinessException;
	
}
