package it.univaq.disim.oop.spacemusicunify.business.impl.ram;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import it.univaq.disim.oop.spacemusicunify.business.*;
import it.univaq.disim.oop.spacemusicunify.business.impl.file.FileData;
import it.univaq.disim.oop.spacemusicunify.business.impl.file.Utility;
import it.univaq.disim.oop.spacemusicunify.domain.*;

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

		for (Artist artists : storedArtists) {
			if (artists.getName().equals(artist.getName())) {
				throw new AlreadyExistingException("existing_artist");
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
		pictureAlbum.setData("src" + File.separator + "main" + File.separator + "resources" + File.separator + "data" + File.separator + "RAMfiles" + File.separator + "cover.png");
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
	public void modify(Integer id, String name, String biography, int yearsOfActivity, Nationality nationality, Set<Picture> images, Set<Artist> addMembers, Artist artist) throws BusinessException {
		for(Artist artists : storedArtists) {
			if(artists.getName().equals(name) && artists.getId().intValue() != id.intValue()) {
				throw new AlreadyTakenFieldException("taken_artist_name");
			}
		}
		for(Artist artistCheck : storedArtists){
			if(artistCheck.getId().intValue() == id.intValue()) {
				Set<Picture> artistPictures;
				if (images != null) {
					Set<Picture> toAddPictures = new HashSet<>();
					Set<Picture> toRemovePictures = new HashSet<>();
					Set<Picture> toCheckPictures = new HashSet<>();

					for (Picture newPic : images) {
						if (newPic.getId() == null) {
							toAddPictures.add(newPic);
						} else {
							toCheckPictures.add(newPic);
						}
					}
					for (Picture image : artist.getPictures()) {
						boolean checktoremove = true;

						for (Picture img : toCheckPictures) {

							if (image.getId().intValue() == img.getId().intValue()) {
								checktoremove = false;
								break;
							}
						}
						if (checktoremove) {
							toRemovePictures.add(image);
						}
					}
					for (Picture picture : toRemovePictures) {
						multimediaService.delete(picture);
					}
					for (Picture picture : toAddPictures) {
						multimediaService.add(picture);
					}

					artistPictures = images;

				} else {
					artistPictures = artist.getPictures();
				}

				Set<Artist> members;
				if (addMembers != null) {
					members = addMembers;
				} else {
					members = artist.getBandMembers();
				}
				artistCheck.setName(name);
				artistCheck.setBiography(biography);
				artistCheck.setYearsOfActivity(yearsOfActivity);
				artistCheck.setNationality(nationality);
				artistCheck.setPictures(artistPictures);
				artistCheck.setBandMembers(members);
				break;
			}
		}


	}

	@Override
	public void delete(Artist artist) throws BusinessException {
		boolean check = false;

		for(Artist artistCheck : getArtistList()) {

			if(artistCheck.getId().intValue() ==  artist.getId().intValue()) {
				check = true;

				AlbumService albumService = SpacemusicunifyBusinessFactory.getInstance().getAlbumService();

				for(Album album : findAllAlbums(artist)){
					 if(albumService.findAllProductions(album).size() <= 1) {
						 albumService.delete(album);
					 }
				}
				for(Production production : productionService.getAllProductions()){
					if(production.getArtist().getId().intValue() == artist.getId().intValue()) {
						productionService.delete(production);
					}
				}
				if(!(artist.getPictures().isEmpty())) {
					for (Picture picture : artist.getPictures()) {
						multimediaService.delete(picture);
					}
				}

				storedArtists.removeIf((Artist artistCheck2) -> artistCheck2.getId().intValue() == artist.getId().intValue());
				break;
			}
		}
		if(!check)throw new ObjectNotFoundException("album not exist");
	}

	@Override
	public Set<Artist> getArtistList() throws BusinessException {
		if(storedArtists == null) throw new ObjectNotFoundException();
		return new HashSet<>(storedArtists);
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
