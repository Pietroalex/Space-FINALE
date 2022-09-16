package it.univaq.disim.oop.spacemusicunify.business.impl.file;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import it.univaq.disim.oop.spacemusicunify.domain.*;

public class UtilityObjectRetriever extends Object {

	private static Artista currentArtist;
	private static Album currentAlbum;
	private static String immaginiDirectory;
	private static String filesMp3Directory;


	public static void setDirectory(String str1, String str2){
		immaginiDirectory = str1;
		filesMp3Directory = str2;
	}

	public static Object findObjectById(String id, String file ) {

		Object object = null;
		System.out.println(file);
		String str = file.substring(file.indexOf("dati"+ File.separator)+ 5);
		System.out.println(str);
		switch(str) {
			case "canzoni.txt": 
				System.out.println("canzone");
				try {
					FileData fileData = Utility.readAllRows(file);

					for (String[] colonne : fileData.getRighe()) {
						if (colonne[0].equals(id)){
							object = findSong(colonne, file);
						}
					}

				} catch (IOException e) {
					throw new RuntimeException(e);
				}

				
				break;
			case "albums.txt":
				System.out.println("album");

				try {
					FileData fileData = Utility.readAllRows(file);

					for (String[] colonne : fileData.getRighe()) {
						System.out.println("album ctr"+colonne[0]+" id "+ id);
						if(colonne[0].equals(id)) {
							System.out.println("cerco album");
							object = findAlbum(colonne, file);
							System.out.println("trovato album");
						}
					}
				} catch (IOException e) {
					throw new RuntimeException(e);
				}

				break;
			case "artisti.txt":
				System.out.println("artista");
				try {
					FileData fileData = Utility.readAllRows(file);

					for (String[] colonne : fileData.getRighe()) {
						if(colonne[0].equals(id)) {
							System.out.println("cerco artista");
							object = findArtist(colonne, file);
							System.out.println("trovato artista");
						}
					}
				} catch (IOException e) {
					throw new RuntimeException(e);
				}

				break;

		}
		System.out.println("finito");
		return object;
	}
	private static Canzone findSong(String[] colonne, String file){
		Canzone canzone = new Canzone();
		canzone.setId(Integer.parseInt(colonne[0]));
		canzone.setTitle(colonne[1]);
		canzone.setFileMp3(filesMp3Directory+colonne[2]);
		canzone.setLyrics(colonne[3]);
		if(currentAlbum == null) {

			canzone.setAlbum((Album) UtilityObjectRetriever.findObjectById(colonne[4], file.replace(file.substring(file.indexOf("dati" + File.separator) + 5), "albums.txt")));
		}else{
			canzone.setAlbum(currentAlbum);
		}
		canzone.setLength(colonne[5]);
		canzone.setGenre(Genere.valueOf(colonne[6]));
		return canzone;
	}
	private static Album findAlbum(String[] colonne, String file){
		Album album = new Album();
		album.setId(Integer.parseInt(colonne[0]));
		album.setTitle(colonne[1]);
		album.setGenre(Genere.valueOf(colonne[2]));
		/*album.setCover(immaginiDirectory+colonne[3]);*/
		Picture picture = new Picture();
		ByteArrayOutputStream outStreamObj = new ByteArrayOutputStream();
		try {
			outStreamObj.writeBytes(Files.readAllBytes(Paths.get(immaginiDirectory+"cover1.png")));
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		picture.setPhoto(outStreamObj.toByteArray());
		album.setCover(picture);
		album.setRelease(LocalDate.parse(colonne[4]));
		if(currentArtist != null) {
			if(colonne[5].equals(currentArtist.getId().toString())){
				album.setArtist(currentArtist);
			}else{
				album.setArtist((Artista)  UtilityObjectRetriever.findObjectById(colonne[5], file.replace(file.substring(file.indexOf("dati" + File.separator) + 5),"artisti.txt")));
			}
		} else {
			album.setArtist((Artista)  UtilityObjectRetriever.findObjectById(colonne[5], file.replace(file.substring(file.indexOf("dati" + File.separator) + 5),"artisti.txt")));
		}

		List<Canzone> canzoneList = new ArrayList<>();
		currentAlbum = album;
		for(String canzoni: Utility.leggiArray(colonne[6])){
			System.out.println("id "+canzoni);
			canzoneList.add((Canzone) UtilityObjectRetriever.findObjectById(canzoni, file.replace(file.substring(file.indexOf("dati" + File.separator) + 5),"canzoni.txt")));

		}
		album.setSongList(canzoneList);
		currentAlbum = null;
		return album;
	}


	private static Artista findArtist(String[] colonne, String file){
		Artista artista = new Artista();

		artista.setId(Integer.parseInt(colonne[0]));
		artista.setStageName(colonne[1]);
		artista.setYearsOfActivity(Integer.parseInt(colonne[2]));
		artista.setBiography(colonne[3]);
		artista.setNationality(Nazionalità.valueOf(colonne[4]));


		List<String> immagini = new ArrayList<>();
		for(String img : Utility.leggiArray(colonne[5])) {
			immagini.add(immaginiDirectory + img);
		}
		artista.setPictures(immagini);
		currentArtist = artista;
		Set<Album> albums = new HashSet<>();
		for(String album: Utility.leggiArray(colonne[6])){
			albums.add((Album) UtilityObjectRetriever.findObjectById(album, file.replace(file.substring(file.indexOf("dati" + File.separator) + 5), "albums.txt")));
		}
		artista.setDiscography(albums);
		return artista;
	}
}

