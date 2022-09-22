package it.univaq.disim.oop.spacemusicunify.business.impl.ram;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import it.univaq.disim.oop.spacemusicunify.SpaceMusicUniFyApplication;
import it.univaq.disim.oop.spacemusicunify.business.AlbumService;
import it.univaq.disim.oop.spacemusicunify.business.AlreadyExistingException;
import it.univaq.disim.oop.spacemusicunify.business.ArtistService;
import it.univaq.disim.oop.spacemusicunify.business.BusinessException;
import it.univaq.disim.oop.spacemusicunify.business.SpacemusicunifyBusinessFactory;
import it.univaq.disim.oop.spacemusicunify.domain.Album;
import it.univaq.disim.oop.spacemusicunify.domain.Artist;
import it.univaq.disim.oop.spacemusicunify.domain.Genre;
import it.univaq.disim.oop.spacemusicunify.domain.Nationality;
import it.univaq.disim.oop.spacemusicunify.domain.Picture;
import it.univaq.disim.oop.spacemusicunify.domain.Song;

public class RAMArtistServiceImpl implements ArtistService {

	private static List<Artist> storedArtists = new ArrayList<>();
	private static int idArtists = 1;
	private AlbumService albumService;
	
	public RAMArtistServiceImpl() {
		
		this.albumService = SpacemusicunifyBusinessFactory.getInstance().getAlbumService();
		
	}
	
	@Override
	public void add(Artist artist) throws AlreadyExistingException {
		for (Artist storedArtist : storedArtists) {
			/*if (artist.getStageName().equals(artista.getStageName())) {
				System.out.println("controllo");
				throw new AlreadyExistingException();
			}*/
		}



		// creo l'album dell'artista
		Album album = new Album();
//		album.setId(idAlbums++);
		album.setTitle("Inediti");
		album.setGenre(Genre.singoli);
		album.setRelease(LocalDate.now());
		/*album.setCover(path+"cover.png");*/
		/*album.setArtist(artista);*/

		// creo la canzone e la lego all'album dell'artista1
		Song canzone = new Song();
//		canzone.setId(idSongs++);
		canzone.setAlbum(album);
		canzone.setTitle("Our Sympathy");
		canzone.setLyrics("ElDlive");
		canzone.setLength("04:02");
		canzone.setGenre(Genre.pop);

		//canzone.setFileMp3(pathmp3+"our_sympathy.mp3");

		// aggiungo la canzone all'album dell'artista1
		Set<Song> canzoneList = album.getSongList();
		canzoneList.add(canzone);
		album.setSongList(canzoneList);

		// creo la produzione tra l'album e l'artista1
		Set<Album> artista1albums = new HashSet<>();

		artista1albums.add(album);

		/*artista.setDiscography(artista1albums);*/

//		storedAlbums.add(album);
//		storedSongs.add(canzone);
//
		artist.setId(idArtists++);
		storedArtists.add(artist);

	}

	@Override
	public void modify(Integer id, String stageName, String biography, int yearsOfActivity, Nationality nationality, Set<Picture> images)
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
	public List<Album> findAllAlbums(Artist artist) throws BusinessException {
		// TODO Auto-generated method stub
		return null;
	}

}