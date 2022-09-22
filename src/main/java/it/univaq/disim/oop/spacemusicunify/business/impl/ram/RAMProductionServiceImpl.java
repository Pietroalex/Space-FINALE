package it.univaq.disim.oop.spacemusicunify.business.impl.ram;

import it.univaq.disim.oop.spacemusicunify.business.*;
import it.univaq.disim.oop.spacemusicunify.domain.*;
import it.univaq.disim.oop.spacemusicunify.view.ViewSituations;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.*;

public class RAMProductionServiceImpl implements ProductionService {
	
	private static List<Production> storedProductions = new ArrayList<>();
	private static Integer idProduction = 1;

	@Override
	public List<Production> getAllProductions() throws BusinessException {
		return storedProductions;
	}

	@Override
	public void add(Production production) throws BusinessException {
		//da inserire id
		storedProductions.add(production);
	}

	@Override
	public void delete(Production production) throws BusinessException {
		// TODO Auto-generated method stub
		
	}
	
}
