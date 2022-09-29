package it.univaq.disim.oop.spacemusicunify.business;

import java.util.Set;

import it.univaq.disim.oop.spacemusicunify.domain.Album;
import it.univaq.disim.oop.spacemusicunify.domain.Artist;
import it.univaq.disim.oop.spacemusicunify.domain.Nationality;
import it.univaq.disim.oop.spacemusicunify.domain.Picture;
import it.univaq.disim.oop.spacemusicunify.domain.Production;

public interface ArtistService {
	
	public void add(Artist artist) throws BusinessException;
	
	void modify(Integer id, String name, String biography, int yearsOfActivity, Nationality nationality, Set<Picture> images, Set<Artist> addMembers, Artist artist) throws BusinessException;

	public void delete(Artist artist) throws BusinessException;

    Set<Artist> getArtistList() throws BusinessException;

    Set<Album> findAllAlbums(Artist artist) throws BusinessException;

    Set<Production> findAllProductions(Artist artist) throws BusinessException;

    Set<Artist> getModifiedMembers();

    void setModifieMembers(Set<Artist> bandMembers);
}
