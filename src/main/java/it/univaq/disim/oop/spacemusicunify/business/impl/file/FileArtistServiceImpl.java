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
	private Set<Artist> bandMembers = new HashSet<>();
	
	public FileArtistServiceImpl(String artistsFile, ProductionService productionService, MultimediaService multimediaService) {
		this.artistsFile = artistsFile;
		this.productionService = productionService;
		this.multimediaService = multimediaService;
	}
	
	@Override
	public void add(Artist artista) throws BusinessException {
		
		try {
			FileData fileDataArtisti = Utility.readAllRows(artistsFile);
			for(String[] colonne: fileDataArtisti.getRighe()) {
				if(colonne[1].equals(artista.getName())) {
					throw new AlreadyExistingException();
				}
			}

			//creo l'album Inediti
			Album album = new Album();

			album.setGenre(Genre.singoli);
			Picture pictureAlbum = new Picture();
			pictureAlbum.setData("src" + File.separator + "main" + File.separator + "resources" + File.separator + "dati" + File.separator + "RAMfiles" + File.separator + "cover.png");
			pictureAlbum.setOwnership(album);
			pictureAlbum.setHeight(140);
			pictureAlbum.setWidth(140);
			album.setCover(pictureAlbum);
			album.setRelease(LocalDate.now());

			//scrivo file artista
			try (PrintWriter writerArtista = new PrintWriter(new File(artistsFile))) {
				long contatore = fileDataArtisti.getContatore();
				writerArtista.println(contatore + 1);
				for (String[] righe : fileDataArtisti.getRighe()) {
					writerArtista.println(String.join(Utility.SEPARATORE_COLONNA, righe));
				}
				artista.setId(Integer.parseInt(String.valueOf(contatore)));
				List<String> imageList = new ArrayList<>();
				for(Picture picture : artista.getPictures()){
					multimediaService.add(picture);
					imageList.add(String.valueOf(picture.getId()));
				}

				List<String> bandIds = new ArrayList<>();
				for(Artist artist : getModifiedMembers()){
					bandIds.add(artist.getId().toString());
				}

				album.setTitle("Inediti"+(artista.getId()));
				StringBuilder rowArtista = new StringBuilder();
				rowArtista.append(contatore);
				rowArtista.append(Utility.SEPARATORE_COLONNA);
				rowArtista.append(artista.getName());
				rowArtista.append(Utility.SEPARATORE_COLONNA);
				rowArtista.append(artista.getYearsOfActivity());
				rowArtista.append(Utility.SEPARATORE_COLONNA);
				rowArtista.append(artista.getBiography());
				rowArtista.append(Utility.SEPARATORE_COLONNA);
				rowArtista.append(imageList);
				rowArtista.append(Utility.SEPARATORE_COLONNA);
				rowArtista.append(artista.getNationality());
				rowArtista.append(Utility.SEPARATORE_COLONNA);
				rowArtista.append(bandIds);
				writerArtista.println(rowArtista.toString());
			}

			//scrivo file album
			SpacemusicunifyBusinessFactory.getInstance().getAlbumService().add(album);
			//creo la produzione
			Production production = new Production();
			production.setArtist(artista);
			production.setAlbum(album);
			productionService.add(production);

		} catch (IOException e) {
			e.printStackTrace();
			throw new BusinessException(e);
		}
	}

	@Override
	public void modify(Integer id, String name, String biography, int yearsOfActivity, Nationality nationality, Set<Picture> images, Set<Artist> addMembers, Artist artist) throws BusinessException {
		try {

			FileData fileData = Utility.readAllRows(artistsFile);
			for(String[] colonne: fileData.getRighe()) {
				if(colonne[1].equals(name) && !colonne[0].equals(id.toString())) {
					throw new AlreadyTakenFieldException();
				}
			}
			int cont = 0;
			for (String[] righe : fileData.getRighe()) {
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
						imagesIds.addAll(Utility.leggiArray(righe[4]));
					}
					List<String> membersIds = new ArrayList<>();
					if(addMembers != null){
						for(Artist artistCtrl : addMembers){
							membersIds.add(artistCtrl.getId().toString());
						}
					}else{
						membersIds.addAll(Utility.leggiArray(righe[6]));
					}
					String[] row = new String[]{righe[0], name,  String.valueOf(yearsOfActivity), biography, imagesIds.toString(),String.valueOf(nationality),  membersIds.toString()};
					fileData.getRighe().set(cont, row);

					break;
				}
				cont++;
			}
			try (PrintWriter writer = new PrintWriter(new File(artistsFile))) {
				writer.println(fileData.getContatore());
				for (String[] righe : fileData.getRighe()) {
					writer.println(String.join(Utility.SEPARATORE_COLONNA, righe));
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
			throw new BusinessException();
		}
	}

	@Override
	public void delete(Artist artist) throws BusinessException {
		boolean check = false;
		try {
			FileData fileData = Utility.readAllRows(artistsFile);
			for(String[] righeCheck: fileData.getRighe()) {
				if(righeCheck[0].equals(artist.getId().toString())) {

					check = true;

					Set<Album> albumList = findAllAlbums(artist);
					//aggiorno il file album.txt
					for (Album albumCtrl : albumList) {
						if(SpacemusicunifyBusinessFactory.getInstance().getAlbumService().findAllProductions(albumCtrl).size() <= 1){
							SpacemusicunifyBusinessFactory.getInstance().getAlbumService().delete(albumCtrl);System.out.println("1");
						}
					}
					for(Production production : findAllProductions(artist)){
						productionService.delete(production);
					}

					//aggiorno il file artisti.txt
					try (PrintWriter writer = new PrintWriter(new File(artistsFile))) {
						writer.println(fileData.getContatore());
						for (String[] righe : fileData.getRighe()) {
							if (righe[0].equals(artist.getId().toString())) {
								//jump line
								continue;
							} else {
								writer.println(String.join("§", righe));
							}
						}
					}



					System.out.println("artista id "+ artist.getId());

					for(Picture picture : artist.getPictures()){
						multimediaService.delete(picture);
					}
					break;
				}
			}
			if(!check)throw new BusinessException("artista inesistente");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}



	@Override
	public Set<Artist> getArtistList() throws BusinessException {
		Set<Artist> artistList = new HashSet<>();
		try {
			FileData fileData = Utility.readAllRows(artistsFile);
			for (String[] colonne : fileData.getRighe()) {
				artistList.add((Artist) UtilityObjectRetriever.findObjectById(colonne[0], artistsFile));
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
		return artistList;
	}
	@Override
	public Set<Album> findAllAlbums(Artist artist) throws BusinessException {

		Set<Album> albumsFinal = new HashSet<>();
		Set<Production> productions = findAllProductions(artist);
		for (Production production : productions){

				albumsFinal.add(production.getAlbum());


		}
		return albumsFinal;
	}
	@Override
	public Set<Production> findAllProductions(Artist artist) throws BusinessException {
		Set<Production> productions = productionService.getAllProductions();
		Set<Production> productionList = new HashSet<>();
		for (Production production : productions){
			if (production.getArtist().getId().intValue() == artist.getId().intValue()){
				productionList.add(production);
			}
		}

		return productionList;
	}
	@Override
	public Set<Artist> getModifiedMembers() {
		Set<Artist> band = bandMembers;
		bandMembers = null;
		return band;
	}
	@Override
	public void setModifieMembers(Set<Artist> bandMembers) {
		this.bandMembers = bandMembers;
	}
}
