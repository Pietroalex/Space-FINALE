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
			album.setCover(pictureAlbum);
			album.setRelease(LocalDate.now());

			//scrivo file artista
			try (PrintWriter writerArtista = new PrintWriter(new File(artistsFile))) {
				long contatore = fileDataArtisti.getContatore();
				writerArtista.println(contatore + 1);
				for (String[] righe : fileDataArtisti.getRighe()) {
					writerArtista.println(String.join(Utility.SEPARATORE_COLONNA, righe));
				}
				List<String> imageList = new ArrayList<>();
				for(Picture picture : artista.getPictures()){
					multimediaService.add(picture);
					imageList.add(String.valueOf(picture.getId()));
				}
				Set<Artist> band = getBandMembers();
				List<String> bandIds = new ArrayList<>();
				while(band.iterator().hasNext()){
					Artist artist = band.iterator().next();
					if(artist.getId() == null) {
						add(artist);
					}
					bandIds.add(String.valueOf(artist.getId()));
				}
				artista.setId(Integer.parseInt(String.valueOf(contatore)));
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
		}
	}

	@Override
	public void modify(Integer id, String name, String biography, int yearsOfActivity, Nationality nationality, Set<Picture> images) throws BusinessException {
		try {
			System.out.println("Modifico artista");
			FileData fileData = Utility.readAllRows(artistsFile);
			for(String[] colonne: fileData.getRighe()) {
				if(colonne[1].equals(name) && !colonne[0].equals(id.toString())) {
					throw new AlreadyTakenFieldException();
				}
			}
			int cont = 0;
			for (String[] righe : fileData.getRighe()) {
				if (righe[0].equals(id.toString())) {
					List<String> imagelist = new ArrayList<>();
					List<String> savedImages = Utility.leggiArray(righe[5]);
					List<String> removedImages = new ArrayList<>();
					List<String> toAddImages = new ArrayList<>();
					/*for(Picture newImage : images){
						System.out.println("current new image "+ newImage+ " current image "+savedImages);
						for(String string : savedImages) {
							if (newImage.contains(string)) {
								System.out.println("già c'è");
								imagelist.add(string);
							}
						}
					}*/

					for(String image : savedImages){
						System.out.println("current check image "+ image+ " current image "+savedImages);
						boolean checktoremove = true;
						for(String string : imagelist) {

							if (string.contains(image)) {
								System.out.println(string+" presente");
								checktoremove = false;
								break;
							}
						}
						if(checktoremove){
							removedImages.add(image);
						}
					}

					/*for(String image : images){
						System.out.println("current check image "+ image+ " current image "+savedImages);
						boolean checktoadd = true;
						for(String string : savedImages) {

							if (image.contains(string)) {
								System.out.println(string+" presente");
								checktoadd = false;
								break;
							}
						}
						if(checktoadd){
							toAddImages.add(image);
						}
					}
					for(String image : removedImages){
						images.removeIf(image::equals);
						Files.deleteIfExists(Paths.get(cartellaImmagini+image));
					}
					System.out.println("lista finale "+imagelist);
					System.out.println("lista finale delete "+removedImages);
					System.out.println("lista finale toadd "+toAddImages);
					for (String toAdd : toAddImages) {
						imagelist.add(saveANDstore(toAdd, stageName));
					}*/
					String[] row = new String[]{righe[0], name,  String.valueOf(yearsOfActivity), biography, String.valueOf(nationality), imagelist.toString(), righe[6]};
					fileData.getRighe().set(cont, row);
					System.out.println("row "+row[5]);

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
		System.out.println("artista da eliminare: "+artist.getId());
		try {
			FileData fileData = Utility.readAllRows(artistsFile);
			for(String[] righeCheck: fileData.getRighe()) {
				if(righeCheck[0].equals(artist.getId().toString())) {

					check = true;
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
					List<Album> albumList = new ArrayList<>();
					//aggiorno il file album.txt
					/*for (Album controllo : artista.getDiscography()) {
						Album album = new Album();
						album.setId(controllo.getId());
						album.setArtist(controllo.getArtist());
						album.setCover(controllo.getCover());
						album.setTitle(controllo.getTitle());
						album.setGenre(controllo.getGenre());
						album.setSongList(controllo.getSongList());
						album.setRelease(controllo.getRelease());
						albumList.add(album);
					}*/
					for(Album album : albumList){
//						delete(album);
					}
					/*for(String immagini : artista.getPictures()){
						Files.deleteIfExists(Paths.get(immagini));
					}*/
					break;
				}
			}
			if(!check)throw new BusinessException("artista inesistente");
		} catch (IOException e) {
		e.printStackTrace();
		}
	}
	

	@Override
	public List<Artist> getArtistaList() throws BusinessException {
		List<Artist> artistList = new ArrayList<>();
		try {
			FileData fileData = Utility.readAllRows(artistsFile);
			System.out.println("cerco artista getAll");
			for (String[] colonne : fileData.getRighe()) {
				artistList.add((Artist) UtilityObjectRetriever.findObjectById(colonne[0], artistsFile));
				System.out.println("aggiungo artista getall");
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
		return artistList;
	}
	@Override
	public List<Album> findAllAlbums(Artist artist) throws BusinessException {
		AlbumService albumService = SpacemusicunifyBusinessFactory.getInstance().getAlbumService();
		List<Album> albums = albumService.getAlbumList();
		List<Album> albumsFinal = new ArrayList<>();
		List<Production> productions = productionService.getAllProductions();
		for (Production production : productions){
			if (production.getArtist().getId().intValue() == artist.getId().intValue()){
				for (Album album : albums){
					if (album.getId().intValue() == production.getAlbum().getId().intValue()){
						albumsFinal.add(album);
					}
				}
			}
		}
		return albumsFinal;
	}

	public Set<Artist> getBandMembers() {
		Set<Artist> band = bandMembers;
		bandMembers = null;
		return band;
	}

	public void setBandMembers(Set<Artist> bandMembers) {
		this.bandMembers = bandMembers;
	}
}
