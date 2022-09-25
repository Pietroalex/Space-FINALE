package it.univaq.disim.oop.spacemusicunify.business.impl.file;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Utility {
	public static final String SEPARATORE_COLONNA = "§";
	
	public static final String[] trim(String[] s) {
		for(int i = 0; i<s.length; i++) {
			s[i] = s[i].trim(); 
		}
		return s;
	}
	
	public static FileData readAllRows(String file) throws IOException {
			
			FileData result = new FileData();
			
			try(BufferedReader reader = new BufferedReader(new FileReader(file))) {
				List<String []> righe = new ArrayList<>();
				long contatore = Long.parseLong(reader.readLine());
				result.setContatore(contatore);
				String linea;
				while((linea=reader.readLine()) != null) {
					righe.add(trim(linea.split(SEPARATORE_COLONNA)));
				}
				
				result.setRighe(righe);
			}

			return result;
		}
	
	public static List<String> leggiArray(String colonnaArray) {
		List<String> lists = new ArrayList<>();
		if("[]".equals(colonnaArray)){
			return lists;
		}
		String[] array = colonnaArray.split(",");
		for(String element : array ) {
			if (element.contains("[")) {
				element = element.substring(1);
			}
			if (element.contains("]")) {
				element = element.substring(0, element.length() - 1);
			}
			
			lists.add(element.trim());
		}
		
		return lists;
	}
}
