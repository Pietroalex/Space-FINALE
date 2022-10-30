package it.univaq.disim.oop.spacemusicunify.business.impl.file;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import it.univaq.disim.oop.spacemusicunify.business.BusinessException;
import it.univaq.disim.oop.spacemusicunify.business.ObjectNotFoundException;
import it.univaq.disim.oop.spacemusicunify.domain.*;
import it.univaq.disim.oop.spacemusicunify.view.SpacemusicunifyPlayer;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;

public class UtilityObjectRetriever {

	private static Artist currentArtist;
	private static Album currentAlbum;
	private static Song currentSong;
	private static String usersFileName;
	private static String albumsFileName;
	private static String artistsFileName;
	private static String songsFileName;
	private static String ramFiles;
	private static String picturesDirectory;
	private static String filesMp3Directory;
	private static String picturesFileName;
	private static String audiosFileName;

	public static void setDirectory(String str1, String str2, String str3, String str4, String str5, String str6, String str7, String str8, String str9){
		usersFileName = str1;
		albumsFileName = str2;
		artistsFileName = str3;
		songsFileName = str4;
		ramFiles = str5 + File.separator + "RAMfiles" + File.separator + "attack.mp3";
		picturesDirectory = str6;
		filesMp3Directory = str7;
		picturesFileName = str8;
		audiosFileName = str9;
	}

	public static Object findObjectById(String id, String file ) throws BusinessException {
		Object object = null;
		String substring = file.substring(file.indexOf("data" + File.separator)+ 5);
		try {
			FileData fileData = Utility.readAllRows(file);
			switch(substring) {
				case "songs.txt":
					for (String[] columns : fileData.getRows()) {
						if (columns[0].equals(id)) object = findSong(columns);
					}
					if(object == null) throw new ObjectNotFoundException("File error, Song with ID "+id+ " not found");
					break;

				case "albums.txt":
					for (String[] columns : fileData.getRows()) {
						if(columns[0].equals(id)) object = findAlbum(columns);
					}
					if(object == null) throw new ObjectNotFoundException("File error, Album with ID "+id+ " not found");
					break;

				case "artists.txt":
					for (String[] columns : fileData.getRows()) {
						if(columns[0].equals(id)) object = findArtist(columns);
					}
					if(object == null) throw new ObjectNotFoundException("File error, Artist with ID "+id+ " not found");
					break;

				case "players.txt":
					for (String[] columns : fileData.getRows()) {
						if(columns[0].equals(id)) object = findPlayer(columns);
					}
					if(object == null) throw new ObjectNotFoundException("File error, Player with ID "+id+ " not found");
					break;

				case "users.txt":
					for (String[] columns : fileData.getRows()) {
						if(columns[0].equals(id)) object = findUser(columns);
					}
					if(object == null) throw new ObjectNotFoundException("File error, User with ID "+id+ " not found");
					break;

				case "playlists.txt":
					for (String[] columns : fileData.getRows()) {
						if(columns[0].equals(id)) object = findPlaylist(columns);
					}
					if(object == null) throw new ObjectNotFoundException("File error, Playlist with ID "+id+ " not found");
					break;

				case "productions.txt":
					for (String[] columns : fileData.getRows()) {
						if(columns[0].equals(id)) object = findProduction(columns);
					}
					if(object == null) throw new ObjectNotFoundException("File error, Production with ID "+id+ " not found");
					break;
				default:
					for (String[] columns : fileData.getRows()) {
						if(columns[0].equals(id)) object = findMultimedia(columns);
					}
					if(object == null) throw new ObjectNotFoundException("File error, Multimedia with ID "+id+ " not found");
					break;

			}
		} catch (IOException e) {
			throw new BusinessException(e);
		}

		return object;
	}

	private static Production findProduction(String[] columns) throws BusinessException {
		Production production = new Production();
		production.setId(Integer.parseInt(columns[0]));
		production.setArtist((Artist) UtilityObjectRetriever.findObjectById(columns[1], artistsFileName));
		production.setAlbum((Album) UtilityObjectRetriever.findObjectById(columns[2], albumsFileName));
		return production;
	}
	private static Playlist findPlaylist(String[] columns) throws BusinessException {
		Playlist playlist = new Playlist();
		playlist.setId(Integer.valueOf(columns[0]));
		playlist.setUser(((User) UtilityObjectRetriever.findObjectById(columns[3], usersFileName)));
		playlist.setTitle(columns[1]);
		Set<Song> songs = new HashSet<>();
		for (String song : Utility.readArray(columns[2])) {
			songs.add((Song) UtilityObjectRetriever.findObjectById(song, songsFileName));
		}
		playlist.setSongList(songs);
		return playlist;
	}

	private static User findUser(String[] columns) {
		User user = new User();
		user.setId(Integer.parseInt(columns[0]));
		user.setUsername(columns[2]);
		user.setPassword(columns[3]);
		return user;
	}

	private static SpacemusicunifyPlayer findPlayer(String[] columns) throws BusinessException {
		SpacemusicunifyPlayer player = new SpacemusicunifyPlayer((User) UtilityObjectRetriever.findObjectById(columns[0], usersFileName));
		player.setVolume(Double.parseDouble(columns[1]));
		player.setDuration(new Duration(Double.parseDouble(columns[2])));
		player.setMute(Boolean.parseBoolean(columns[3]));
		List<String> queueIDS = Utility.readArray(columns[4]);
		for (String songsID : queueIDS){
			player.getQueue().add((Song) UtilityObjectRetriever.findObjectById(songsID, songsFileName));
		}
		player.setCurrentSong(Integer.parseInt(columns[5]));
		if(queueIDS.size() > 0) player.setMediaPlayer(new MediaPlayer(new Media(Paths.get(ramFiles).toUri().toString())));
		return player;
	}


	private static Artist findArtist(String[] columns) throws BusinessException {
		Artist artist = new Artist();
		artist.setId(Integer.parseInt(columns[0]));
		artist.setName(columns[1]);
		artist.setYearsOfActivity(Integer.parseInt(columns[2]));
		artist.setBiography(columns[3]);
		currentArtist = artist;
		Set<Picture> pictures = new HashSet<>();
		List<String> picturesSource = Utility.readArray(columns[4]);
		for(String pictureId : picturesSource){
			pictures.add( (Picture) UtilityObjectRetriever.findObjectById(pictureId, picturesFileName));
		}
		artist.setPictures(pictures);
		artist.setNationality(Nationality.valueOf(columns[5]));
		Set<Artist> bandMembers = new HashSet<>();
		List<String> bandMembersIds = Utility.readArray(columns[6]);
		for(String artistId : bandMembersIds){
			bandMembers.add( (Artist) UtilityObjectRetriever.findObjectById(artistId, artistsFileName));
		}
		artist.setBandMembers(bandMembers);
		currentArtist = null;
		return artist;
	}

	private static Song findSong(String[] columns) throws BusinessException {
		Song song = new Song();
		song.setId(Integer.parseInt(columns[0]));
		song.setTitle(columns[1]);
		currentSong = song;
		song.setFileMp3((Audio)  UtilityObjectRetriever.findObjectById(columns[2], audiosFileName));
		song.setLyrics(columns[3]);
		if(currentAlbum == null) song.setAlbum((Album) UtilityObjectRetriever.findObjectById(columns[4], albumsFileName));
		else song.setAlbum(currentAlbum);
		song.setLength(columns[5]);
		song.setGenre(Genre.valueOf(columns[6]));
		currentSong = null;
		return song;
	}
	private static Album findAlbum(String[] columns) throws BusinessException {
		Album album = new Album();
		album.setId(Integer.parseInt(columns[0]));
		album.setTitle(columns[1]);
		album.setGenre(Genre.valueOf(columns[2]));
		currentAlbum = album;
		album.setCover(((Picture)  UtilityObjectRetriever.findObjectById(columns[3], picturesFileName)));
		Set<Song> songSet = new HashSet<>();
		for(String songs: Utility.readArray(columns[4])){
			songSet.add((Song) UtilityObjectRetriever.findObjectById(songs, songsFileName));
		}
		album.setSongs(songSet);
		album.setRelease(LocalDate.parse(columns[5]));
		currentAlbum = null;
		return album;
	}
	private static Multimedia findMultimedia(String[] columns) throws BusinessException {
		Multimedia multimedia;

		if(currentArtist == null){
			if(currentAlbum == null){
				multimedia = new Audio();
				multimedia.setId(Integer.parseInt(columns[0]));
				multimedia.setData(filesMp3Directory+columns[1]);
				multimedia.setOwnership(currentSong);
			} else {
				if(currentAlbum.getCover() == null){
					multimedia = new Picture();
					multimedia.setId(Integer.valueOf(columns[0]));
					multimedia.setData(picturesDirectory+columns[1]);
					((Picture) multimedia).setHeight(Integer.parseInt(columns[2]));
					((Picture) multimedia).setWidth(Integer.parseInt(columns[3]));
					multimedia.setOwnership(currentAlbum);
				}else{
					multimedia = new Audio();
					multimedia.setId(Integer.parseInt(columns[0]));
					multimedia.setData(filesMp3Directory+columns[1]);
					multimedia.setOwnership(currentSong);
				}
			}

		} else {
			multimedia = new Picture();
			multimedia.setId(Integer.valueOf(columns[0]));
			multimedia.setData(picturesDirectory+columns[1]);
			((Picture) multimedia).setHeight(Integer.parseInt(columns[2]));
			((Picture) multimedia).setWidth(Integer.parseInt(columns[3]));
			multimedia.setOwnership(currentArtist);
		}
		return multimedia;

	}

}

