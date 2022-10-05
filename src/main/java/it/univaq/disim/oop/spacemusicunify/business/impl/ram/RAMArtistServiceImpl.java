package it.univaq.disim.oop.spacemusicunify.business.impl.ram;

import java.io.File;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

import it.univaq.disim.oop.spacemusicunify.business.AlbumService;
import it.univaq.disim.oop.spacemusicunify.business.AlreadyExistingException;
import it.univaq.disim.oop.spacemusicunify.business.ArtistService;
import it.univaq.disim.oop.spacemusicunify.business.BusinessException;
import it.univaq.disim.oop.spacemusicunify.business.MultimediaService;
import it.univaq.disim.oop.spacemusicunify.business.ObjectNotFoundException;
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

		for (Artist storedArtist : storedArtists) {
			if (artist.getName().equals(storedArtist.getName()) || artist.getName().contains("Singles")) {
				throw new AlreadyExistingException();
			}
		}

		artist.setId(idArtists++);

		for(Picture picture : artist.getPictures()){
			multimediaService.add(picture);
		}

		//creo l'album Inediti
		Album album = new Album();

		album.setTitle("Singles"+(artist.getId()));
		album.setGenre(Genre.singles);
		Picture pictureAlbum = new Picture();
		pictureAlbum.setData("src" + File.separator + "main" + File.separator + "resources" + File.separator + "dati" + File.separator + "RAMfiles" + File.separator + "cover.png");
		pictureAlbum.setOwnership(album);
		pictureAlbum.setHeight(140);
		pictureAlbum.setWidth(140);
		album.setCover(pictureAlbum);
		album.setRelease(LocalDate.now());
		album.setSongs(null);


		AlbumService albumService = SpacemusicunifyBusinessFactory.getInstance().getAlbumService();
		Set<Artist> artistSet = new HashSet<>();
		artistSet.add(artist);
		albumService.setChosenArtists(artistSet);

		albumService.add(album);


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
		if(storedArtists == null) throw new ObjectNotFoundException();
		Set<Artist> artists = new HashSet<>(storedArtists);
		return artists;
	}

	@Override
	public Set<Album> findAllAlbums(Artist artist) throws BusinessException {
		Set<Album> albums = new HashSet<>();
		for(Production production : findAllProductions(artist)) {
			albums.add(production.getAlbum());
		}
		if(albums.isEmpty()) throw new ObjectNotFoundException("no albums for this artist");
		return albums;
	}

	@Override
	public Set<Production> findAllProductions(Artist artist) throws BusinessException {
		Set<Production> productions = new HashSet<>();
		for(Production production : productionService.getAllProductions()) {
			if(production.getArtist().getId().intValue() == artist.getId().intValue()) {
				productions.add(production);
			}
		}
		if(productions.isEmpty()) throw new ObjectNotFoundException("no productions for this artist");
		return productions;
	}
}
