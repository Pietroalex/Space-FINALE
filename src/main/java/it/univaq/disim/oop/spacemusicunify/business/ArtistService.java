package it.univaq.disim.oop.spacemusicunify.business;

import java.util.List;
import java.util.Set;

import it.univaq.disim.oop.spacemusicunify.domain.Album;
import it.univaq.disim.oop.spacemusicunify.domain.Artist;
import it.univaq.disim.oop.spacemusicunify.domain.Nationality;
import it.univaq.disim.oop.spacemusicunify.domain.Picture;

public interface ArtistService {
	
	public void add(Artist artist) throws BusinessException;
	
	void modify(Integer id, String name, String biography, int yearsOfActivity, Nationality nationality, Set<Picture> images) throws BusinessException;

	public void delete(Artist artist) throws BusinessException;
	
	List<Album> findAllAlbums(Artist artist) throws BusinessException;
	
}
