package it.univaq.disim.oop.spacemusicunify.business.impl.ram;

import it.univaq.disim.oop.spacemusicunify.business.*;
import it.univaq.disim.oop.spacemusicunify.domain.*;
import it.univaq.disim.oop.spacemusicunify.view.ViewDispatcher;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class RAMSpaceMusicUnifyServiceImpl implements SPACEMusicUnifyService {
	
	private static String path = "src"+ File.separator + "main" + File.separator + "resources" + File.separator + "data" + File.separator + "RAMfiles" + File.separator;

	@Override
	public void setAllDefaults() throws BusinessException {
		
		SpacemusicunifyBusinessFactory factory = SpacemusicunifyBusinessFactory.getInstance();
		ArtistService artistService = factory.getArtistService();
		UserService userService = factory.getUserService();

		// creo l'artista1
		Artist artist1 = new Artist();
		artist1.setName("Pasquale Arrosto");
		artist1.setBiography("Sono Pasquale Arrosto, suono la musica elettronica");
		artist1.setNationality(Nationality.italian);
		artist1.setYearsOfActivity(7);
		Set<Picture> artist1img = new HashSet<>();
	
		Picture picture1A = new Picture();

		picture1A.setData(path+"pasqualearrosto.jpg");

		picture1A.setHeight(120);
		picture1A.setWidth(120);
		picture1A.setOwnership(artist1);
		artist1img.add(picture1A);

		Picture picture2A = new Picture();
		picture2A.setData(path+"group4.jpg");
		picture2A.setHeight(120);
		picture2A.setWidth(120);
		picture2A.setOwnership(artist1);
		artist1img.add(picture2A);

		Set<Artist> artistSet = new HashSet<>();
		artist1.setBandMembers(artistSet);
		artist1.setPictures(artist1img);

		artistService.add(artist1);
		// creo l'artista2
		Artist artist2 = new Artist();
		artist2.setName("British Wonder");
		artist2.setBiography("We are 2 bassist, We are the new rock problems");
		artist2.setNationality(Nationality.british);
		artist2.setYearsOfActivity(10);
		Set<Picture> artist2img = new HashSet<>();

		Picture picture1B = new Picture();

		picture1B.setData(path+"2bassists.png");

		picture1B.setHeight(120);
		picture1B.setWidth(120);
		picture1B.setOwnership(artist2);
		artist2img.add(picture1B);

		Set<Artist> artistSetB = new HashSet<>();
		artist2.setBandMembers(artistSetB);

		artist2.setPictures(artist2img);

		artistService.add(artist2);

		User user = new User();
		user.setUsername("user");
		user.setPassword("123456");

		Playlist userPlaylist = new Playlist();
		userPlaylist.setUser(user);
		userPlaylist.setTitle("Default Playlist");

		userService.add(user);
		userService.add(userPlaylist);

	}

}
