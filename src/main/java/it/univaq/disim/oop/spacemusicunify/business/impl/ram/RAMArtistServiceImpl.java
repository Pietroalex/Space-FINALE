package it.univaq.disim.oop.spacemusicunify.business.impl.ram;

import java.io.File;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import it.univaq.disim.oop.spacemusicunify.business.AlbumService;
import it.univaq.disim.oop.spacemusicunify.business.AlreadyExistingException;
import it.univaq.disim.oop.spacemusicunify.business.ArtistService;
import it.univaq.disim.oop.spacemusicunify.business.BusinessException;
import it.univaq.disim.oop.spacemusicunify.business.MultimediaService;
import it.univaq.disim.oop.spacemusicunify.business.ProductionService;
import it.univaq.disim.oop.spacemusicunify.business.SpacemusicunifyBusinessFactory;
import it.univaq.disim.oop.spacemusicunify.domain.Album;
import it.univaq.disim.oop.spacemusicunify.domain.Artist;
import it.univaq.disim.oop.spacemusicunify.domain.Genre;
import it.univaq.disim.oop.spacemusicunify.domain.Nationality;
import it.univaq.disim.oop.spacemusicunify.domain.Picture;
import it.univaq.disim.oop.spacemusicunify.domain.Production;

public class RAMArtistServiceImpl implements ArtistService {

	private static Set<Artist> storedArtists = new HashSet<>();
	private static int idArtists = 1;
	private MultimediaService multimediaService;
	private ProductionService productionService;
	
	public RAMArtistServiceImpl(MultimediaService multimediaService, ProductionService productionService) {
		this.multimediaService = multimediaService;
		this.productionService = productionService;
	}
	
	@Override
	public void add(Artist artist) throws BusinessException {
		
		SpacemusicunifyBusinessFactory factory = SpacemusicunifyBusinessFactory.getInstance();
		AlbumService albumService = factory.getAlbumService();
		
		for (Artist storedArtist : storedArtists) {
			if (artist.getName().equals(storedArtist.getName())) {
				System.out.println("controllo");
				throw new AlreadyExistingException();
			}
		}
		
		Production production = new Production();
		production.setArtist(artist);
		
		// creo l'album dell'artista
		Album album = new Album();
		album.setTitle("Inediti");
		album.setGenre(Genre.singoli);
		album.setRelease(LocalDate.now());
		
		Picture picture = new Picture();
		picture.setData("src"+ File.separator + "main" + File.separator + "resources" + File.separator + "dati" + File.separator + "RAMfiles" + File.separator + "cover.png");
		picture.setHeight(140);
		picture.setWidth(140);
		picture.setOwnership(album);
		
		multimediaService.add(picture);
		
		album.setCover(picture);
		
		albumService.add(album);
		
		production.setAlbum(album);
		
		productionService.add(production);
		
		artist.setId(idArtists++);
		storedArtists.add(artist);

	}

	@Override
	public void modify(Integer id, String stageName, String biography, int yearsOfActivity, Nationality nationality, Set<Picture> images, Set<Artist> addMembers, Artist artist)
			throws BusinessException {
		/*for (Artist artista : storedArtists) {
			if (artista.getStageName().equals(stageName) && artista.getId().intValue() != id.intValue()) {
				System.out.println("controllo");
				throw new AlreadyTakenFieldException();
			}
		}
		for (Artist artist : storedArtists) {

			if (artist.getId().intValue() == id.intValue()) {
				artist.setStageName(stageName);
				artist.setBiography(biography);
				artist.setYearsOfActivity(yearsOfActivity);
				artist.setNationality(nationality);
				artist.setPictures(images);
			}
		}*/

	}

	@Override
	public void delete(Artist artist) {
		/*for (Album album : artist.getDiscography()) {
			try {
				this.delete(album);
			} catch (BusinessException e) {
				e.printStackTrace();
			}
		}*/
			storedArtists.removeIf((Artist artistacheck) -> artist.getId().intValue() == artistacheck.getId().intValue());
	}

	@Override
	public Set<Artist> getArtistList() throws BusinessException {
		return storedArtists;
	}

	@Override
	public Set<Album> findAllAlbums(Artist artist) throws BusinessException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<Production> findAllProductions(Artist artist) throws BusinessException {
		return null;
	}

	@Override
	public Set<Artist> getModifiedMembers() {
		return null;
	}

	@Override
	public void setModifieMembers(Set<Artist> bandMembers) {

	}

}
