package it.univaq.disim.oop.spacemusicunify.business.impl.file;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Utility {
	public static final String COLUMN_SEPARATOR = "§";
	
	public static final String[] trim(String[] s) {
		for(int i = 0; i<s.length; i++) {
			s[i] = s[i].trim(); 
		}
		return s;
	}
	
	public static FileData readAllRows(String file) throws IOException {
			
			FileData result = new FileData();
			
			try(BufferedReader reader = new BufferedReader(new FileReader(file))) {
				List<String []> rows = new ArrayList<>();
				long counter = Long.parseLong(reader.readLine());
				result.setCounter(counter);
				String row;
				while((row = reader.readLine()) != null) {
					rows.add(trim(row.split(COLUMN_SEPARATOR)));
				}
				
				result.setRows(rows);
			}

			return result;
		}
	
	public static List<String> readArray(String arrayColumn) {
		List<String> lists = new ArrayList<>();
		if("[]".equals(arrayColumn)){
			return lists;
		}
		String[] array = arrayColumn.split(",");
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
