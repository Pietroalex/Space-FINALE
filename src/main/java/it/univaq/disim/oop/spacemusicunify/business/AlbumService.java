package it.univaq.disim.oop.spacemusicunify.business;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import it.univaq.disim.oop.spacemusicunify.domain.Album;
import it.univaq.disim.oop.spacemusicunify.domain.Artist;
import it.univaq.disim.oop.spacemusicunify.domain.Genre;
import it.univaq.disim.oop.spacemusicunify.domain.Picture;
import it.univaq.disim.oop.spacemusicunify.domain.Song;

public interface AlbumService {
	
	void add(Album album) throws BusinessException;
	
	void modify(Integer id, String title, Genre genre, LocalDate release, Picture cover, Set<Song> songlist) throws BusinessException;

	void delete(Album album) throws BusinessException;

	void add(Song song)throws BusinessException;

	void modify(Integer id, String title, String duration, Genre genre, byte[] mp3, String lyrics, Album album) throws BusinessException;

	void delete(Song song) throws BusinessException;
	
	List<Artist> findAllArtists(Album album) throws BusinessException;
	
}
