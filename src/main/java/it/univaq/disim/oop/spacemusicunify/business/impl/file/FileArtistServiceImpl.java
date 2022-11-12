package it.univaq.disim.oop.spacemusicunify.business.impl.file;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import it.univaq.disim.oop.spacemusicunify.business.*;
import it.univaq.disim.oop.spacemusicunify.domain.*;

public class FileArtistServiceImpl implements ArtistService {
	
	private String artistsFile;
	private ProductionService productionService;
	private MultimediaService multimediaService;
	
	public FileArtistServiceImpl(String artistsFile, ProductionService productionService, MultimediaService multimediaService) {
		this.artistsFile = artistsFile;
		this.productionService = productionService;
		this.multimediaService = multimediaService;
	}
	
	@Override
	public void add(Artist artist) throws BusinessException {
		
		try {
			FileData fileData = Utility.readAllRows(artistsFile);
			for(String[] column: fileData.getRows()) {
				if(column[1].equals(artist.getName())) {
					throw new AlreadyExistingException("New Artist, Already Existing artist with this name");
				}
			}
			if(artist.getBandMembers().size() == 1)	throw new AlreadyExistingException("New Artist, The Band Must be composed at least of 2 artists");

			artist.setId(Integer.parseInt(String.valueOf(fileData.getCounter())));
			List<String> imageList = new ArrayList<>();
			for(Picture picture : artist.getPictures()){
				multimediaService.add(picture);
				imageList.add(String.valueOf(picture.getId()));
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

			//scrivo file artista
			try (PrintWriter writer = new PrintWriter(new File(artistsFile))) {
				long counter = fileData.getCounter();
				writer.println(counter + 1);
				for (String[] rows : fileData.getRows()) {
					writer.println(String.join(Utility.COLUMN_SEPARATOR, rows));
				}


				List<String> bandIds = new ArrayList<>();
				if(!(artist.getBandMembers().isEmpty())) {
					for (Artist artists : artist.getBandMembers()) {
						bandIds.add(artists.getId().toString());
					}
				}

				StringBuilder row = new StringBuilder();
				row.append(counter);
				row.append(Utility.COLUMN_SEPARATOR);
				row.append(artist.getName());
				row.append(Utility.COLUMN_SEPARATOR);
				row.append(artist.getYearsOfActivity());
				row.append(Utility.COLUMN_SEPARATOR);
				row.append(artist.getBiography());
				row.append(Utility.COLUMN_SEPARATOR);
				row.append(imageList);
				row.append(Utility.COLUMN_SEPARATOR);
				row.append(artist.getNationality());
				row.append(Utility.COLUMN_SEPARATOR);
				row.append(bandIds);
				writer.println(row.toString());
			}

			AlbumService albumService = SpacemusicunifyBusinessFactory.getInstance().getAlbumService();
			Set<Artist> artistSet = new HashSet<>();
			artistSet.add(artist);
			albumService.setChosenArtists(artistSet);
			//scrivo file album
			albumService.add(album);


		} catch (IOException e) {
			throw new BusinessException(e);
		}
	}

	@Override
	public void modify(Integer id, String name, String biography, int yearsOfActivity, Nationality nationality, Set<Picture> images, Set<Artist> addMembers, Artist artist) throws BusinessException {
		try {

			FileData fileData = Utility.readAllRows(artistsFile);
			for(String[] column: fileData.getRows()) {
				if(column[1].equals(artist.getName())) {
					throw new AlreadyExistingException("Modify Artist, Already Existing artist with this name");
				}
			}
			int cont = 0;
			for (String[] righe : fileData.getRows()) {
				if (righe[0].equals(id.toString())) {
					List<String> imagesIds = new ArrayList<>();
					if (images != null) {
						Set<Picture> toAddPictures = new HashSet<>();
						Set<Picture> toRemovePictures = new HashSet<>();
						Set<Picture> toCheckPictures = new HashSet<>();
						for (Picture newPic : images) {
							if (newPic.getId() == null) {
								toAddPictures.add(newPic);
							}else{
								toCheckPictures.add(newPic);
							}
						}
						for(Picture image : artist.getPictures()) {

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
						for (Picture picture : toRemovePictures){
							multimediaService.delete(picture);
						}
						for (Picture picture : toAddPictures){
							multimediaService.add(picture);
						}
						for(Picture picture : images){
							imagesIds.add(picture.getId().toString());
						}
					}else{
						imagesIds.addAll(Utility.readArray(righe[4]));
					}
					List<String> membersIds = new ArrayList<>();
					if(addMembers != null){
						for(Artist artistCtrl : addMembers){
							membersIds.add(artistCtrl.getId().toString());
						}
					}else{
						membersIds.addAll(Utility.readArray(righe[6]));
					}
					String[] row = new String[]{righe[0], name,  String.valueOf(yearsOfActivity), biography, imagesIds.toString(),String.valueOf(nationality),  membersIds.toString()};
					fileData.getRows().set(cont, row);

					break;
				}
				cont++;
			}
			try (PrintWriter writer = new PrintWriter(new File(artistsFile))) {
				writer.println(fileData.getCounter());
				for (String[] righe : fileData.getRows()) {
					writer.println(String.join(Utility.COLUMN_SEPARATOR, righe));
				}
			}
		} catch (IOException e) {
			throw new BusinessException(e);
		}
	}

	@Override
	public void delete(Artist artist) throws BusinessException {
		boolean check = false;
		try {
			FileData fileData = Utility.readAllRows(artistsFile);
			for(String[] righeCheck: fileData.getRows()) {
				if(righeCheck[0].equals(artist.getId().toString())) {

					check = true;

					Set<Album> albumList = findAllAlbums(artist);
					//aggiorno il file album.txt
					for (Album albumCtrl : albumList) {
						Set<Production> productions = SpacemusicunifyBusinessFactory.getInstance().getAlbumService().findAllProductions(albumCtrl);
						if(productions.size() > 1){
							for(Production prod : productions){
								if(prod.getArtist().getId().intValue() == artist.getId().intValue()) productionService.delete(prod);
							}
						}else{
							SpacemusicunifyBusinessFactory.getInstance().getAlbumService().delete(albumCtrl);
						}
					}

					//aggiorno il file artisti.txt
					try (PrintWriter writer = new PrintWriter(new File(artistsFile))) {
						writer.println(fileData.getCounter());
						for (String[] righe : fileData.getRows()) {
							if (righe[0].equals(artist.getId().toString())) {
								//jump line
								continue;
							} else {
								writer.println(String.join("§", righe));
							}
						}
					}





					for(Picture picture : artist.getPictures()){
						multimediaService.delete(picture);
					}
					break;
				}
			}

		} catch (IOException e) {
			throw new BusinessException(e);
		}
		if(!check)throw new ObjectNotFoundException("This artist doesn't exist");
	}



	@Override
	public Set<Artist> getArtistList() throws BusinessException {
		Set<Artist> artistList = new HashSet<>();
		try {
			FileData fileData = Utility.readAllRows(artistsFile);
			for (String[] columns : fileData.getRows()) {
				artistList.add((Artist) UtilityObjectRetriever.findObjectById(columns[0], artistsFile));
			}

		} catch (IOException e) {
			throw new BusinessException(e);
		}
		return artistList;
	}
	@Override
	public Set<Album> findAllAlbums(Artist artist) throws BusinessException {

		Set<Album> albums = new HashSet<>();
		Set<Production> productions = findAllProductions(artist);
		for (Production production : productions){
			albums.add(production.getAlbum());
		}
		if(albums.isEmpty()) throw new ObjectNotFoundException("There is no album for this artist");
		return albums;
	}
	@Override
	public Set<Production> findAllProductions(Artist artist) throws BusinessException {
		Set<Production> productionList = new HashSet<>();
		for (Production production : productionService.getAllProductions()){
			if (production.getArtist().getId().intValue() == artist.getId().intValue()){
				productionList.add(production);
			}
		}
		if(productionList.isEmpty()) throw new ObjectNotFoundException("There is no production for this artist");
		return productionList;
	}

}
