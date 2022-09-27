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
	private static String imagesDirectory;
	private static String filesMp3Directory;


	public static void setDirectory(String str1, String str2){
		imagesDirectory = str1;
		filesMp3Directory = str2;
	}

	public static Object findObjectById(String id, String file ) {

		Object object = null;

		String str = file.substring(file.indexOf("dati"+ File.separator)+ 5);

		switch(str) {
			case "songs.txt":

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


				try {
					FileData fileData = Utility.readAllRows(file);

					for (String[] colonne : fileData.getRighe()) {

						if(colonne[0].equals(id)) {

							object = findAlbum(colonne, file);

						}
					}
				} catch (IOException e) {
					throw new RuntimeException(e);
				}

				break;
			case "artists.txt":

				try {
					FileData fileData = Utility.readAllRows(file);

					for (String[] colonne : fileData.getRighe()) {
						if(colonne[0].equals(id)) {

							object = findArtist(colonne, file);

						}
					}
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
				break;

			default:

				try {
					FileData fileData = Utility.readAllRows(file);

					for (String[] columns : fileData.getRighe()) {
						if(columns[0].equals(id)) {

							object = findMultimedia(columns);

						}
					}
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
				break;

		}

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
		currentSong = song;
		song.setFileMp3((Audio)  UtilityObjectRetriever.findObjectById(colonne[2], file.replace(file.substring(file.indexOf("dati" + File.separator) + 5),"audios.txt")));
		song.setLyrics(colonne[3]);

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
			canzoneList.add((Song) UtilityObjectRetriever.findObjectById(canzoni, file.replace(file.substring(file.indexOf("dati" + File.separator) + 5),"songs.txt")));
		}
		album.setSongs(canzoneList);
		album.setRelease(LocalDate.parse(colonne[5]));
		currentAlbum = null;
		return album;
	}
	private static Multimedia findMultimedia(String[] column){
		Multimedia multimedia = null;

		if(currentArtist == null && currentAlbum == null && currentSong != null){
			multimedia = new Audio();
			multimedia.setId(Integer.parseInt(column[0]));
			multimedia.setData(filesMp3Directory+column[1]);
			multimedia.setOwnership(currentSong);
		}
		if(currentArtist != null && currentAlbum == null && currentSong == null){
			multimedia = new Picture();
			multimedia.setId(Integer.valueOf(column[0]));
			multimedia.setData(imagesDirectory+column[1]);
			((Picture) multimedia).setHeight(Integer.parseInt(column[2]));
			((Picture) multimedia).setWidth(Integer.parseInt(column[3]));
			multimedia.setOwnership(currentArtist);
		}
		if(currentArtist == null && currentAlbum != null && currentSong == null){
			multimedia = new Picture();
			multimedia.setId(Integer.valueOf(column[0]));
			multimedia.setData(imagesDirectory+column[1]);
			((Picture) multimedia).setHeight(Integer.parseInt(column[2]));
			((Picture) multimedia).setWidth(Integer.parseInt(column[3]));
			multimedia.setOwnership(currentAlbum);
		}
		if(currentArtist == null && currentAlbum != null && currentSong != null){
			multimedia = new Audio();
			multimedia.setId(Integer.parseInt(column[0]));
			multimedia.setData(filesMp3Directory+column[1]);
			multimedia.setOwnership(currentSong);
		}
		return multimedia;
	}

}

