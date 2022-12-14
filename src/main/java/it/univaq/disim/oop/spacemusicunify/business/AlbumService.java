package it.univaq.disim.oop.spacemusicunify.business;

import java.time.LocalDate;
import java.util.Set;

import it.univaq.disim.oop.spacemusicunify.domain.Album;
import it.univaq.disim.oop.spacemusicunify.domain.Artist;
import it.univaq.disim.oop.spacemusicunify.domain.Audio;
import it.univaq.disim.oop.spacemusicunify.domain.Genre;
import it.univaq.disim.oop.spacemusicunify.domain.Picture;
import it.univaq.disim.oop.spacemusicunify.domain.Production;
import it.univaq.disim.oop.spacemusicunify.domain.Song;

public interface AlbumService {
	
	void add(Album album) throws BusinessException;

	void modify(Integer id, String title, Genre genre, Picture tempPicture,  Set<Song> songlist, LocalDate release, Album album) throws BusinessException;

	void delete(Album album) throws BusinessException;

	void add(Song song)throws BusinessException;

	void modify(Integer id, String title, Audio tempAudio, String lyrics,Album album,String duration, Genre genre, Song song) throws BusinessException;

	void delete(Song song) throws BusinessException;

    Set<Album> getAlbumList() throws BusinessException;

	Set<Song> getSongList() throws BusinessException;

	Set<Artist> findAllArtists(Album album) throws BusinessException;

    Set<Production> findAllProductions(Album album) throws BusinessException;

    Set<Artist> getChosenArtists();

	void setChosenArtists(Set<Artist> chosenArtists);
}
