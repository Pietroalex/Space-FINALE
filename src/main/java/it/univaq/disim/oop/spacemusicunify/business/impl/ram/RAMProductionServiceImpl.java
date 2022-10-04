package it.univaq.disim.oop.spacemusicunify.business.impl.ram;

import it.univaq.disim.oop.spacemusicunify.business.*;
import it.univaq.disim.oop.spacemusicunify.business.impl.file.FileData;
import it.univaq.disim.oop.spacemusicunify.business.impl.file.Utility;
import it.univaq.disim.oop.spacemusicunify.domain.*;
import it.univaq.disim.oop.spacemusicunify.view.ViewSituations;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.*;

public class RAMProductionServiceImpl implements ProductionService {
	
	private static Set<Production> storedProductions = new HashSet<>();
	private static Integer idProduction = 1;

	@Override
	public Set<Production> getAllProductions() throws BusinessException {
		return storedProductions;
	}

	@Override
	public void add(Production production) throws BusinessException {

		production.setId(idProduction++);
		storedProductions.add(production);

	}

	@Override
	public void delete(Production production) throws BusinessException {
		boolean check = false;

		for(Production productions : storedProductions) {
			if(productions.getId().intValue() == production.getId().intValue()) {
				check = true;
				storedProductions.remove(production);
				break;
			}
		}

		if(!check)throw new BusinessException("production not exist");

	}

}
