package it.univaq.disim.oop.spacemusicunify.business.impl.file;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import it.univaq.disim.oop.spacemusicunify.business.AlbumService;
import it.univaq.disim.oop.spacemusicunify.business.AlreadyExistingException;
import it.univaq.disim.oop.spacemusicunify.business.AlreadyTakenFieldException;
import it.univaq.disim.oop.spacemusicunify.business.ArtistService;
import it.univaq.disim.oop.spacemusicunify.business.BusinessException;
import it.univaq.disim.oop.spacemusicunify.business.ProductionService;
import it.univaq.disim.oop.spacemusicunify.business.SpacemusicunifyBusinessFactory;
import it.univaq.disim.oop.spacemusicunify.domain.Album;
import it.univaq.disim.oop.spacemusicunify.domain.Artist;
import it.univaq.disim.oop.spacemusicunify.domain.Genre;
import it.univaq.disim.oop.spacemusicunify.domain.Nationality;
import it.univaq.disim.oop.spacemusicunify.domain.Picture;
import it.univaq.disim.oop.spacemusicunify.domain.Song;

public class FileArtistServiceImpl implements ArtistService {
	
	private String artistsFile;
	private AlbumService albumService;
	private ProductionService productionService;
	
	public FileArtistServiceImpl(String artistsFile) {
		this.artistsFile = artistsFile;
		SpacemusicunifyBusinessFactory factory = SpacemusicunifyBusinessFactory.getInstance();
		albumService = factory.getAlbumService();
		productionService = factory.getProductionService();
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
			
			
			FileData fileDataCanzoni = Utility.readAllRows(songsFile);
			
			FileData fileDataAlbum = Utility.readAllRows(albumsFile);
			
			List<String> albums = new ArrayList<>();
			albums.add(String.valueOf(fileDataAlbum.getContatore()));
			
			List<String> canzoneList = new ArrayList<>();
			canzoneList.add(String.valueOf(fileDataCanzoni.getContatore()));
			
			//creo l'album Inediti
			Album album = new Album();
			album.setTitle("Inediti");
			album.setGenre(Genre.singoli);
			/*album.setCover(saveANDstore(("src" + File.separator + "main" + File.separator + "resources" + File.separator + "dati" + File.separator + "RAMfiles" + File.separator + "cover.png"), "cover"));
			*/
			Picture picture = new Picture();
			picture.setId(5);
			album.setCover(picture);
			album.setRelease(LocalDate.now());
			
			//creo la canzone di default
			Song canzone = new Song();
			canzone.setTitle("Our Sympathy of "+artista.getName());
			canzone.setLyrics("ElDlive");
			canzone.setLength("04:02");
			canzone.setGenre(Genre.pop);
			/*canzone.setFileMp3(saveANDstore(("src" + File.separator + "main" + File.separator + "resources" + File.separator + "dati" + File.separator + "RAMfiles" + File.separator + "our_sympathy.mp3"), "audio"));
			*/
			//scrivo file artista
			try (PrintWriter writerArtista = new PrintWriter(new File(artistsFile))) {
				long contatore = fileDataArtisti.getContatore();
				writerArtista.println(contatore + 1);
				for (String[] righe : fileDataArtisti.getRighe()) {
					writerArtista.println(String.join(Utility.SEPARATORE_COLONNA, righe));
				}
				List<String> imageList = new ArrayList<>();
/*				for(String image : artista.getPictures()){
					imageList.add(saveANDstore(image, artista.getStageName()));
				}*/
				StringBuilder rowArtista = new StringBuilder();
				rowArtista.append(contatore);
				rowArtista.append(Utility.SEPARATORE_COLONNA);
				rowArtista.append(artista.getName());
				rowArtista.append(Utility.SEPARATORE_COLONNA);
				rowArtista.append(artista.getYearsOfActivity());
				rowArtista.append(Utility.SEPARATORE_COLONNA);
				rowArtista.append(artista.getBiography());
				rowArtista.append(Utility.SEPARATORE_COLONNA);
				/*rowArtista.append(artista.getNationality());*/
				rowArtista.append(Utility.SEPARATORE_COLONNA);
				rowArtista.append(imageList);
				rowArtista.append(Utility.SEPARATORE_COLONNA);
				rowArtista.append(albums);
				writerArtista.println(rowArtista.toString());
			}
			//scrivo file canzone
			try (PrintWriter writerCanzone = new PrintWriter(new File(songsFile))) {
				long contatore = fileDataCanzoni.getContatore();
				writerCanzone.println(contatore + 1);
				for (String[] righe : fileDataCanzoni.getRighe()) {
					writerCanzone.println(String.join(Utility.SEPARATORE_COLONNA, righe));
				}
				StringBuilder row = new StringBuilder();
				row.append(contatore);
				row.append(Utility.SEPARATORE_COLONNA);
				row.append(canzone.getTitle());
				row.append(Utility.SEPARATORE_COLONNA);
				row.append(canzone.getFileMp3());
				row.append(Utility.SEPARATORE_COLONNA);
				row.append(canzone.getLyrics());
				row.append(Utility.SEPARATORE_COLONNA);
				row.append(fileDataAlbum.getContatore());
				row.append(Utility.SEPARATORE_COLONNA);
				row.append(canzone.getLength());
				row.append(Utility.SEPARATORE_COLONNA);
				row.append(canzone.getGenre());
				writerCanzone.println(row.toString());
			}
			//scrivo file album
			try (PrintWriter writerAlbum = new PrintWriter(new File(albumsFile))) {
				long contatore = fileDataAlbum.getContatore();
				writerAlbum.println(contatore + 1);
				for (String[] righe : fileDataAlbum.getRighe()) {
					writerAlbum.println(String.join(Utility.SEPARATORE_COLONNA, righe));
				}
				StringBuilder row = new StringBuilder();
				row.append(contatore);
				row.append(Utility.SEPARATORE_COLONNA);
				row.append(album.getTitle());
				row.append(Utility.SEPARATORE_COLONNA);
				row.append(album.getGenre());
				row.append(Utility.SEPARATORE_COLONNA);
				row.append(album.getCover());
				row.append(Utility.SEPARATORE_COLONNA);
				row.append(album.getRelease());
				row.append(Utility.SEPARATORE_COLONNA);
				row.append(fileDataArtisti.getContatore());
				row.append(Utility.SEPARATORE_COLONNA);
				row.append(canzoneList);
				writerAlbum.println(row.toString());
			}
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
	
	public void add(Picture picture) throws BusinessException {
		try {
			FileData fileData = Utility.readAllRows(picturesFile);
/*			for (String[] colonne : fileData.getRighe()) {
				if (colonne[0].equals(picture.getId().toString())) {
					throw new AlreadyExistingException("Already existing picture");
				}
			}*/
			try (PrintWriter writer = new PrintWriter(new File(picturesFile))) {
				long contatore = fileData.getContatore();
				writer.println((contatore));
				for (String[] righe : fileData.getRighe()) {
					writer.println(String.join(Utility.SEPARATORE_COLONNA, righe));
				}
				StringBuilder row = new StringBuilder();
				row.append(contatore);
				picture.setId((int) contatore);
				row.append(Utility.SEPARATORE_COLONNA);
				File file = new File(String.valueOf(new ByteArrayInputStream(picture.getPhoto())));
				row.append(saveANDstore(file.getAbsolutePath(), "cover"));
				row.append(Utility.SEPARATORE_COLONNA);
				row.append(picture.getHeight());
				row.append(Utility.SEPARATORE_COLONNA);
				row.append(picture.getWidth());
				writer.println(row.toString());

			}
		} catch (IOException e) {
			e.printStackTrace();
			throw new BusinessException(e);
		}
	}
	
	public void delete(Picture picture) throws BusinessException {
		
	}

	@Override
	public List<Album> findAllAlbums(Artist artist) throws BusinessException {
		// TODO Auto-generated method stub
		return null;
	}

}
