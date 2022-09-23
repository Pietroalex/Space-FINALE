package it.univaq.disim.oop.spacemusicunify.business.impl.file;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import it.univaq.disim.oop.spacemusicunify.domain.*;

public class UtilityObjectRetriever extends Object {

	private static Artist currentArtist;
	private static Album currentAlbum;
	private static Song currentSong;
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
			case "songs.txt":
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
			case "artists.txt":
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
			case "pictures.txt":
				System.out.println("pictures");
				try {
					FileData fileData = Utility.readAllRows(file);

					for (String[] colonne : fileData.getRighe()) {
						if(colonne[0].equals(id)) {
							System.out.println("cerco picture");
							object = findMultimedia(colonne);
							System.out.println("trovato pictur");
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


	private static Artist findArtist(String[] colonne, String file) {
		Artist artist = new Artist();
		artist.setId(Integer.parseInt(colonne[0]));
		artist.setName(colonne[1]);
		artist.setYearsOfActivity(Integer.parseInt(colonne[2]));
		artist.setBiography(colonne[3]);
		currentArtist = artist;
		Set<Picture> pictures = new HashSet<>();
		List<String> picturesSource = Utility.leggiArray(colonne[4]);
		for(String pictureId : picturesSource){
			pictures.add( (Picture) UtilityObjectRetriever.findObjectById(pictureId, file.replace(file.substring(file.indexOf("dati" + File.separator) + 5), "pictures.txt")));
		}
		artist.setPictures(pictures);
		artist.setNationality(Nationality.valueOf(colonne[5]));
		Set<Artist> bandMembers = new HashSet<>();
		List<String> bandMembersIds = Utility.leggiArray(colonne[6]);
		for(String artistId : bandMembersIds){
			bandMembers.add( (Artist) UtilityObjectRetriever.findObjectById(artistId, file.replace(file.substring(file.indexOf("dati" + File.separator) + 5), "artists.txt")));
		}
		artist.setBandMembers(bandMembers);
		currentArtist = null;
		return artist;
	}



	private static Song findSong(String[] colonne, String file){
		Song song = new Song();
		song.setId(Integer.parseInt(colonne[0]));
		song.setTitle(colonne[1]);
		song.setFileMp3(byteArrayExtractor(filesMp3Directory+colonne[2]));
		song.setLyrics(colonne[3]);
		currentSong = song;
		if(currentAlbum == null) {
			song.setAlbum((Album) UtilityObjectRetriever.findObjectById(colonne[4], file.replace(file.substring(file.indexOf("dati" + File.separator) + 5), "albums.txt")));
		}else{
			song.setAlbum(currentAlbum);
		}
		song.setLength(colonne[5]);
		song.setGenre(Genre.valueOf(colonne[6]));
		currentSong = null;
		return song;
	}
	private static Album findAlbum(String[] colonne, String file){
		Album album = new Album();
		album.setId(Integer.parseInt(colonne[0]));
		album.setTitle(colonne[1]);
		album.setGenre(Genre.valueOf(colonne[2]));
		currentAlbum = album;

		album.setCover(((Picture)  UtilityObjectRetriever.findObjectById(colonne[3], file.replace(file.substring(file.indexOf("dati" + File.separator) + 5),"pictures.txt"))));
		Set<Song> canzoneList = new HashSet<>();

		for(String canzoni: Utility.leggiArray(colonne[4])){
			System.out.println("id "+canzoni);
			canzoneList.add((Song) UtilityObjectRetriever.findObjectById(canzoni, file.replace(file.substring(file.indexOf("dati" + File.separator) + 5),"songs.txt")));
		}
		album.setSongList(canzoneList);
		album.setRelease(LocalDate.parse(colonne[5]));
		currentAlbum = null;
		return album;
	}
	private static Multimedia findMultimedia(String[] colonne){
		Multimedia multimedia;

		if(currentArtist != null){
			multimedia = new Picture();
			multimedia.setOwnership(currentArtist);
		}else if(currentAlbum != null){
			multimedia = new Picture();
			multimedia.setOwnership(currentAlbum);
		}

		multimedia.setId(Integer.valueOf(colonne[0]));


		return multimedia;
	}
	private static byte[] byteArrayExtractor(String source){
		byte[] bytes;
		try {
			ByteArrayOutputStream outStreamObj = new ByteArrayOutputStream();

			outStreamObj.writeBytes(Files.readAllBytes(Paths.get(source)));
			bytes = outStreamObj.toByteArray();

			outStreamObj.close();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		return bytes;
	}
}

