package it.univaq.disim.oop.spacemusicunify.business.impl.ram;

import it.univaq.disim.oop.spacemusicunify.business.*;
import it.univaq.disim.oop.spacemusicunify.domain.*;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.*;

public class RAMSPACEMusicUnifyServiceImpl implements SPACEMusicUnifyService {

	private static List<Artista> storedArtists = new ArrayList<>();
	private static List<Album> storedAlbums = new ArrayList<>();
	private static List<Canzone> storedSongs = new ArrayList<>();
	private static List<Utente> storedUsers = new ArrayList<>();
	private static int idUser = 1;
	private static ViewSituations situation;

	private List<Playlist> storedPlaylists = new ArrayList<>();
	private String ricerca;
	private static int id = 1;

	private static int idArtists = 1;
	private static int idAlbums = 1;
	private static int idSongs = 1;

	private static String path = "src"+ File.separator + "main" + File.separator + "resources" + File.separator + "dati" + File.separator + "RAMfiles" + File.separator;
	private static String pathmp3 = "src"+ File.separator + "main" + File.separator + "resources" + File.separator + "dati" + File.separator + "RAMfiles" + File.separator;


	@Override
	public void add(Utente utente) throws BusinessException {
		createNewUser(utente);
	}

	@Override
	public void modify(Integer id, String username, String password) throws AlreadyTakenFieldException {
		List<Utente> utenti = getAllUsers();

		for (Utente user : utenti) {
			if (user.getUsername().equals(username) && user.getId().intValue() != id.intValue()) {
				System.out.println("controllo");
				throw new AlreadyTakenFieldException();
			}
		}
		for (Utente user : utenti) {
			if (user.getId().intValue() == id.intValue()) {
				user.setUsername(username);
				user.setPassword(password);
			}
		}

	}

	@Override
	public void delete(Utente utente) {

		List<Utente> utenti = getAllUsers();
		List<Playlist> playlists = null;
		try {
			playlists = getAllPlaylists(utente);
			System.out.println(playlists);
		} catch (BusinessException e) {
			throw new RuntimeException(e);
		}

		for (Utente user : utenti) {
			if (user.getId().intValue() == utente.getId().intValue()) {
				utenti.remove(user);
				break;
			}
		}
		List<Playlist> playlistList = playlists;
		for (Playlist playlist : playlistList) {
			if (playlist.getUser().equals(utente)) {
				try {
					deletePlaylist(playlist);
				} catch (BusinessException e) {
					e.printStackTrace();
				}
			}
		}

		System.out.println(playlists);
	}

	@Override
	public void add(Picture picture) throws BusinessException {

	}

	@Override
	public void modify(Integer id, byte[] photo, int height, int width) throws BusinessException {

	}

	@Override
	public void delete(Picture picture) throws BusinessException {

	}

	@Override
	public void add(Artista artista) throws AlreadyExistingException {
		for (Artista artist : storedArtists) {
			if (artist.getStageName().equals(artista.getStageName())) {
				System.out.println("controllo");
				throw new AlreadyExistingException();
			}
		}



		// creo l'album dell'artista
		Album album = new Album();
		album.setId(idAlbums++);
		album.setTitle("Inediti");
		album.setGenre(Genere.singoli);
		album.setRelease(LocalDate.now());
		/*album.setCover(path+"cover.png");*/
		album.setArtist(artista);

		// creo la canzone e la lego all'album dell'artista1
		Canzone canzone = new Canzone();
		canzone.setId(idSongs++);
		canzone.setAlbum(album);
		canzone.setTitle("Our Sympathy");
		canzone.setLyrics("ElDlive");
		canzone.setLength("04:02");
		canzone.setGenre(Genere.pop);


			canzone.setFileMp3(pathmp3+"our_sympathy.mp3");

		// aggiungo la canzone all'album dell'artista1
		List<Canzone> canzoneList = album.getSongList();
		canzoneList.add(canzone);
		album.setSongList(canzoneList);

		// creo la produzione tra l'album e l'artista1
		Set<Album> artista1albums = new HashSet<>();

		artista1albums.add(album);

		artista.setDiscography(artista1albums);

		storedAlbums.add(album);
		storedSongs.add(canzone);

		artista.setId(idArtists++);
		storedArtists.add(artista);

	}

	@Override
	public void modify(Integer id, String stageName, String biography, int yearsOfActivity, Nazionalità nationality, Set<Picture> images)
			throws AlreadyTakenFieldException {
		for (Artista artista : storedArtists) {
			if (artista.getStageName().equals(stageName) && artista.getId().intValue() != id.intValue()) {
				System.out.println("controllo");
				throw new AlreadyTakenFieldException();
			}
		}
		for (Artista artist : storedArtists) {

			if (artist.getId().intValue() == id.intValue()) {
				artist.setStageName(stageName);
				artist.setBiography(biography);
				artist.setYearsOfActivity(yearsOfActivity);
				artist.setNationality(nationality);
				artist.setPictures(images);
			}
		}

	}

	@Override
	public void delete(Artista artista) {
		for (Album album : artista.getDiscography()) {
			try {
				this.delete(album);
			} catch (BusinessException e) {
				e.printStackTrace();
			}
		}
			storedArtists.removeIf((Artista artistacheck) -> artista.getId().intValue() == artistacheck.getId().intValue());
	}

	@Override
	public void add(Album album) throws AlreadyExistingException {
		for (Album album1 : storedAlbums) {
			if (album1.getTitle().equals(album.getTitle())) {
				System.out.println("controllo");
				throw new AlreadyExistingException();
			}
		}

		album.setId(idAlbums++);

			Canzone canzone = new Canzone();
			canzone.setAlbum(album);
			canzone.setTitle("new song of '"+ album.getTitle()+ "'");
			canzone.setLyrics("In the dark 背を向けた未来\n" +
				"僕は あの場所からずっと\n" +
				"動けないで いたけれど\n" +
				"君と 出会えたことで 少し変われたよ\n" +
				"何も出来ないなんて事はないんだ ah\n" +
				"Cause fighting you somewhere\n" +
				"信じつづけるから\n" +
				"I will fight in this place\n" +
				"そう 世界を救うイメージ\n" +
				"願い かけて今 彼方へ\n" +
				"悲しいあすにしたくない\n" +
				"君が 君が ほら解き放つシンパシー\n" +
				"辿りつけるなら 飛べるさ\n" +
				"懐かしいあの光景まで\n" +
				"恐れない\n" +
				"立ち向かう 爆ぜる心を掴まえて\n" +
				"その声が聞こえるなら\n" +
				"深い闇を\n" +
				"光の刃で crash crash crash\n" +
				"Start！ Transfer ahh\n" +
				"In the dark 重い鎖を\n" +
				"過去に縛られないでと その言葉が\n" +
				"引きちぎった\n" +
				"君は 視線そらさずに 前を見てるから\n" +
				"揺るがないその瞳 守りたいんだ ah\n" +
				"It's found always someday\n" +
				"探しつづけるなら\n" +
				"Thank you, for being my friend\n" +
				"僕だけに残したメッセージ\n" +
				"狙いさだめ今 貫け\n" +
				"誰も不幸にしたくない\n" +
				"君と 君と 進化を遂げるシンパシー\n" +
				"やってみなくちゃわからないさ\n" +
				"まだ遠いあの流星まで\n" +
				"届けたい\n" +
				"強くなる だからもう1度...\n" +
				"果てしないこの宇宙(ほし)を\n" +
				"孤独を選ぶ君を\n" +
				"思惟の繋がりを\n" +
				"感じたい ah\n" +
				"Cause fighting you somewhere\n" +
				"だから今\n" +
				"I will fight in this place\n" +
				"光射す方へ\n" +
				"Thank you, for being my friend\n" +
				"迫り来る 次のステージ\n" +
				"願い かけて今 彼方へ\n" +
				"悲しい未来(あす)にしたくない\n" +
				"君が 君が ほら解き放つシンパシー\n" +
				"辿りつけるなら 飛べるさ\n" +
				"懐かしいあの光景まで\n" +
				"忘れない\n" +
				"どこまでも 行けるこの想いを馳せて\n" +
				"叶えたい 結末なら\n" +
				"深い愛を 光の刃で flash flash flash\n" +
				"Start！ Transfer ahh");
			canzone.setLength("04:02");
			canzone.setGenre(album.getGenre());

			canzone.setFileMp3("src" + File.separator + "main" + File.separator + "resources" + File.separator + "viste" + File.separator + "RAMfiles" + File.separator +"our_sympathy.mp3");
			try {
				this.add(canzone);
			} catch (BusinessException e) {
				throw new RuntimeException(e);
			}


		Set<Album> set = album.getArtist().getDiscography();

		set.add(album);

		album.getArtist().setDiscography(set);

		storedAlbums.add(album);

	}

	@Override
	public void modify(Integer id, String title, Genere genre, LocalDate release, Picture cover, List<Canzone> songlist) throws AlreadyTakenFieldException {
		for (Album album : storedAlbums) {
			if (album.getTitle().equals(title) && album.getId().intValue() != id.intValue()) {
				System.out.println("controllo");
				throw new AlreadyTakenFieldException();
			}
		}
		for (Album albumcheck : storedAlbums) {
			if (albumcheck.getId().equals(id)) {
				albumcheck.setRelease(release);
				albumcheck.setTitle(title);
				if (albumcheck.getGenre() != genre && genre != Genere.singoli) {
					List<Canzone> toChangeGenreSongs = albumcheck.getSongList();
					for (Canzone canzone : toChangeGenreSongs) {
						canzone.setGenre(genre);
					}
					albumcheck.setSongList(null);
					albumcheck.setSongList(toChangeGenreSongs);
					albumcheck.setGenre(genre);

				}
				break;
			}
		}
	}

	@Override
	public void delete(Album album) throws BusinessException {
		List<Canzone> songs = album.getSongList();
		boolean controllo = false;
		for (Album albums : storedAlbums) {
			if (albums.getId().intValue() == album.getId().intValue()) {
				controllo = true;
				break;
			}
		}

		if (controllo) {
				Set<Album> artistaalbums = album.getArtist().getDiscography();
				Set<Album> albumset = new HashSet<>(artistaalbums);
			for (Album albumcheck : artistaalbums) {
				if (albumcheck == album) {
					albumset.remove(album);
				}

				album.getArtist().setDiscography(albumset);
			}

			storedSongs.removeIf(songs::contains);
			storedAlbums.removeIf((Album albumcheck) -> albumcheck.getId().intValue() == album.getId().intValue());

		} else {
			throw new BusinessException();
		}
	}

	@Override
	public void add(Canzone canzone) throws AlreadyExistingException {
		Album album = canzone.getAlbum();
		for (Canzone canzoni : storedSongs) {
			if (canzoni.getTitle().equals(canzone.getTitle())) {
				System.out.println("controllo");
				throw new AlreadyExistingException();
			}
		}

		canzone.setId(idSongs++);
		List<Canzone> canzoniAlbum = album.getSongList();

		canzoniAlbum.add(canzone);
		album.setSongList(canzoniAlbum);

		storedSongs.add(canzone);

	}

	@Override
	public void modify(Integer id, String title, String length, Genere genre, String mp3, String lyrics, Album album)
			throws AlreadyTakenFieldException {
		for (Canzone canzone : storedSongs) {
			if (canzone.getTitle().equals(title) && canzone.getId().intValue() != id.intValue()) {
				System.out.println("controllo");
				throw new AlreadyTakenFieldException();
			}
		}
		for (Canzone canzone : storedSongs) {
			if (canzone.getId().intValue() == id.intValue()) {
				canzone.setLyrics(lyrics);
				canzone.setTitle(title);
				canzone.setFileMp3((mp3));
				canzone.setLength(length);
				canzone.setGenre(genre);

			}
		}

	}

	@Override
	public void delete(Canzone canzone) throws BusinessException {
		boolean controllo = false;
		for (Canzone song : storedSongs) {
			if (song.getId().intValue() == canzone.getId().intValue()) {
				controllo = true;
				break;
			}
		}
		if (controllo) {
			storedSongs.removeIf((Canzone canzonecheck) -> canzonecheck == canzone);

			storedAlbums.forEach(
					(Album albums) -> canzone.getAlbum().getSongList().removeIf((Canzone canzonecheck) -> canzonecheck == canzone));

			for(Utente utente : getAllUsers()) {
				getAllPlaylists(utente).forEach(
						(Playlist playlist) -> playlist.getSongList().removeIf((Canzone canzonecheck) -> canzonecheck == canzone));
			}
		} else {
			throw new BusinessException();
		}
	}

	@Override
	public void setAllDefaults() {

		List<Artista> artisti = new ArrayList<>();
		List<Album> albums = new ArrayList<>();
		List<Canzone> canzoni = new ArrayList<>();

		// creo l'artista1
		Artista artista1 = new Artista();
		artista1.setId(idArtists++);
		artista1.setStageName("Pasquale Arrosto");
		artista1.setBiography("Sono Pasquale Arrosto, suono la musica elettronica");
		artista1.setNationality(Nazionalità.italian);
		artista1.setYearsOfActivity(6);
		Set<Picture> artista1img = new HashSet<>();
		try {
			Picture picture = new Picture();
			ByteArrayOutputStream outStreamObj = new ByteArrayOutputStream();
			outStreamObj.writeBytes(Files.readAllBytes(Paths.get(path+"pasqualearrosto.jpg")));
			picture.setPhoto(outStreamObj.toByteArray());
			artista1img.add(picture);

			outStreamObj.writeBytes(Files.readAllBytes(Paths.get(path+"group4.jpg")));
			picture.setPhoto(outStreamObj.toByteArray());
			artista1img.add(picture);

			outStreamObj.writeBytes(Files.readAllBytes(Paths.get(path+"group4.jpg")));
			picture.setPhoto(outStreamObj.toByteArray());
			artista1img.add(picture);

			outStreamObj.writeBytes(Files.readAllBytes(Paths.get(path+"group4.jpg")));
			picture.setPhoto(outStreamObj.toByteArray());
			artista1img.add(picture);

		} catch (IOException e) {
			throw new RuntimeException(e);
		}

		artista1.setPictures(artista1img);

		// creo l'album dell'artista1
		Album artista1album1 = new Album();
		artista1album1.setId(idAlbums++);
		artista1album1.setTitle("Inediti");
		artista1album1.setGenre(Genere.singoli);
		artista1album1.setRelease(LocalDate.of(2012, 10, 20));
		/*artista1album1.setCover(path+"cover.png");*/
		artista1album1.setArtist(artista1);

		// creo la canzone e la lego all'album dell'artista1
		Canzone artista1album1canzone1 = new Canzone();
		artista1album1canzone1.setId(idSongs++);
		artista1album1canzone1.setAlbum(artista1album1);
		artista1album1canzone1.setTitle("Our Sympathy");
		artista1album1canzone1.setLyrics("In the dark 背を向けた未来\n" +
				"僕は あの場所からずっと\n" +
				"動けないで いたけれど\n" +
				"君と 出会えたことで 少し変われたよ\n" +
				"何も出来ないなんて事はないんだ ah\n" +
				"Cause fighting you somewhere\n" +
				"信じつづけるから\n" +
				"I will fight in this place\n" +
				"そう 世界を救うイメージ\n" +
				"願い かけて今 彼方へ\n" +
				"悲しいあすにしたくない\n" +
				"君が 君が ほら解き放つシンパシー\n" +
				"辿りつけるなら 飛べるさ\n" +
				"懐かしいあの光景まで\n" +
				"恐れない\n" +
				"立ち向かう 爆ぜる心を掴まえて\n" +
				"その声が聞こえるなら\n" +
				"深い闇を\n" +
				"光の刃で crash crash crash\n" +
				"Start！ Transfer ahh\n" +
				"In the dark 重い鎖を\n" +
				"過去に縛られないでと その言葉が\n" +
				"引きちぎった\n" +
				"君は 視線そらさずに 前を見てるから\n" +
				"揺るがないその瞳 守りたいんだ ah\n" +
				"It's found always someday\n" +
				"探しつづけるなら\n" +
				"Thank you, for being my friend\n" +
				"僕だけに残したメッセージ\n" +
				"狙いさだめ今 貫け\n" +
				"誰も不幸にしたくない\n" +
				"君と 君と 進化を遂げるシンパシー\n" +
				"やってみなくちゃわからないさ\n" +
				"まだ遠いあの流星まで\n" +
				"届けたい\n" +
				"強くなる だからもう1度...\n" +
				"果てしないこの宇宙(ほし)を\n" +
				"孤独を選ぶ君を\n" +
				"思惟の繋がりを\n" +
				"感じたい ah\n" +
				"Cause fighting you somewhere\n" +
				"だから今\n" +
				"I will fight in this place\n" +
				"光射す方へ\n" +
				"Thank you, for being my friend\n" +
				"迫り来る 次のステージ\n" +
				"願い かけて今 彼方へ\n" +
				"悲しい未来(あす)にしたくない\n" +
				"君が 君が ほら解き放つシンパシー\n" +
				"辿りつけるなら 飛べるさ\n" +
				"懐かしいあの光景まで\n" +
				"忘れない\n" +
				"どこまでも 行けるこの想いを馳せて\n" +
				"叶えたい 結末なら\n" +
				"深い愛を 光の刃で flash flash flash\n" +
				"Start！ Transfer ahh");
		artista1album1canzone1.setLength("04:02");
		artista1album1canzone1.setGenre(Genere.j_pop);

		artista1album1canzone1.setFileMp3(pathmp3+"our_sympathy.mp3");

		canzoni.add(artista1album1canzone1);

		// aggiungo la canzone all'album dell'artista1
		List<Canzone> canzone = artista1album1.getSongList();
		canzone.add(artista1album1canzone1);
		artista1album1.setSongList(canzone);

		Canzone artista1album1canzone2 = new Canzone();
		artista1album1canzone2.setId(idSongs++);
		artista1album1canzone2.setAlbum(artista1album1);
		artista1album1canzone2.setTitle("Unravel");
		artista1album1canzone2.setLyrics("Dimmi dimmi\n" +
				"教えて 教えてよ\n" +
				"\n" +
				"come funziona\n" +
				"その仕組みを\n" +
				"\n" +
				"chi è in me\n" +
				"僕の中に誰がいるの？\n" +
				"rotto rotto\n" +
				"壊れた 壊れたよ\n" +
				"\n" +
				"In questo mondo\n" +
				"この世界で\n" +
				"\n" +
				"ridi\n" +
				"君が笑う\n" +
				"\n" +
				"senza vedere nulla\n" +
				"何も見えずに\n" +
				"Che mi spezzato\n" +
				"壊れた僕なんてさ\n" +
				"\n" +
				"trattieni il fiato\n" +
				"息を止めて\n" +
				"\n" +
				"Non posso slegarlo, non posso più slegarlo\n" +
				"ほどけない もう ほどけないよ\n" +
				"\n" +
				"Anche la verità si blocca\n" +
				"真実さえ freeze\n" +
				"\n" +
				"infrangibile infrangibile\n" +
				"壊せる 壊せない\n" +
				"\n" +
				"impazzire non impazzire\n" +
				"狂える 狂えない\n" +
				"\n" +
				"Ti ho trovato e ho tremato\n" +
				"あなたを見つけて 揺れた\n" +
				"A poco a poco mi ritrovo in un mondo distorto\n" +
				"歪んだ世界にだんだん僕は\n" +
				"\n" +
				"Non riesco più a vedere attraverso\n" +
				"透き通って見えなくなって\n" +
				"\n" +
				"non trovarmi\n" +
				"見つけないで 僕のことを\n" +
				"\n" +
				"non fissare\n" +
				"見つめないで\n" +
				"\n" +
				"Nel mondo che qualcuno ha disegnato\n" +
				"誰かが描いた世界の中で\n" +
				"\n" +
				"non voglio farti del male\n" +
				"あなたを傷つけたくはないよ\n" +
				"\n" +
				"Ricordati di me\n" +
				"覚えていて 僕のことを\n" +
				"\n" +
				"rimani vivido\n" +
				"鮮やかなまま\n" +
				"La solitudine che si diffonde all'infinito è intrecciata\n" +
				"無限に広がる孤独が絡まる\n" +
				"\n" +
				"Il ricordo di una risata innocente punge\n" +
				"無邪気に笑った記憶が刺さって\n" +
				"\n" +
				"Non posso muovermi non posso muovermi\n" +
				"動けない 動けない\n" +
				"\n" +
				"Non posso muovermi non posso muovermi\n" +
				"動けない 動けない\n" +
				"\n" +
				"Non posso muovermi non posso muovermi\n" +
				"動けない 動けないよ\n" +
				"\n" +
				"Svelare il mondo\n" +
				"Unravelling the world\n" +
				"Ho cambiato non potevo cambiare\n" +
				"変わってしまった 変えられなかった\n" +
				"\n" +
				"due intrecciati due muoiono\n" +
				"2つが絡まる 2人が滅びる\n" +
				"\n" +
				"infrangibile infrangibile\n" +
				"壊せる 壊せない\n" +
				"\n" +
				"impazzire non impazzire\n" +
				"狂える 狂えない\n" +
				"\n" +
				"Non posso macchiarti, ho tremato\n" +
				"あなたを汚せないよ 揺れた\n" +
				"A poco a poco mi ritrovo in un mondo distorto\n" +
				"歪んだ世界にだんだん僕は\n" +
				"\n" +
				"Non riesco più a vedere attraverso\n" +
				"透き通って見えなくなって\n" +
				"\n" +
				"non trovarmi\n" +
				"見つけないで 僕のことを\n" +
				"\n" +
				"non fissare\n" +
				"見つめないで\n" +
				"\n" +
				"In una trappola solitaria che qualcuno ha teso\n" +
				"誰かが仕組んだ孤独な罠に\n" +
				"\n" +
				"Prima che il futuro si dispari\n" +
				"未来がほどけてしまう前に\n" +
				"\n" +
				"Ricordati di me\n" +
				"思い出して 僕のことを\n" +
				"\n" +
				"rimani vivido\n" +
				"鮮やかなまま\n" +
				"non dimenticare non dimenticare\n" +
				"忘れないで 忘れないで\n" +
				"\n" +
				"non dimenticare non dimenticare\n" +
				"忘れないで 忘れないで\n" +
				"Paralizza le cose che sono cambiate\n" +
				"変わってしまったことに paralyze\n" +
				"\n" +
				"Un paradiso pieno di cose che non possono essere cambiate\n" +
				"変えられないことだらけの paradise\n" +
				"\n" +
				"Ricordati di me\n" +
				"覚えていて 僕のことを\n" +
				"dimmi\n" +
				"教えて\n" +
				"\n" +
				"dimmi\n" +
				"教えて\n" +
				"\n" +
				"chi è in me\n" +
				"僕の中に誰がいるの？");
		artista1album1canzone2.setLength("04:00");
		artista1album1canzone2.setGenre(Genere.j_rock);


			artista1album1canzone2.setFileMp3(pathmp3+"unravel.mp3");

		canzoni.add(artista1album1canzone2);

		canzone = artista1album1.getSongList();
		canzone.add(artista1album1canzone2);
		artista1album1.setSongList(canzone);

		// creo il set di albums per l'artista1
		Set<Album> artista1Albums = new HashSet<>();

		artista1Albums.add(artista1album1);
		artista1.setDiscography(artista1Albums);


		artisti.add(artista1);
		albums.add(artista1album1);

		// creo l'artista2
		Artista artista2 = new Artista();
		artista2.setId(idArtists++);
		artista2.setStageName("British Wonder");
		artista2.setBiography("We are 2 bassist, We are the new rock problems");
		artista2.setNationality(Nazionalità.british);
		artista2.setYearsOfActivity(10);
		Set<Picture> artista2img = new HashSet<>();
		try {
			Picture picture = new Picture();
			ByteArrayOutputStream outStreamObj = new ByteArrayOutputStream();
			outStreamObj.writeBytes(Files.readAllBytes(Paths.get(path+"2bassists.png")));
			picture.setPhoto(outStreamObj.toByteArray());
			artista2img.add(picture);

		} catch (IOException e) {
			throw new RuntimeException(e);
		}

		artista2.setPictures(artista2img);

		// creo l'album dell'artista2
		Album artista2album1 = new Album();
		artista2album1.setId(idAlbums++);
		artista2album1.setTitle("Inediti");
		artista2album1.setGenre(Genere.singoli);
		artista2album1.setRelease(LocalDate.of(2012, 10, 20));
		/*artista2album1.setCover(path+"cover.png");*/
		artista2album1.setArtist(artista2);

		// creo la canzone e la lego all'album dell'artista2
		Canzone artista2album1canzone1 = new Canzone();
		artista2album1canzone1.setId(idSongs++);
		artista2album1canzone1.setAlbum(artista2album1);
		artista2album1canzone1.setTitle("AOT-Opening 1");
		artista2album1canzone1.setLyrics("Seid ihr das Essen?\n" +
				"Seid ihr das Essen?\n" +
				"\n" +
				"Nein, wir sind der Jäger!\n" +
				"Nein, wir sind der Jäger!\n" +
				"Feuerroter Pfeil und Bogen\n" +
				"Feuerroter Pfeil und Bogen\n" +
				"Senza conoscere i nomi dei fiori calpestati\n" +
				"踏まれた花の 名前も知らずに\n" +
				"\n" +
				"Un uccello caduto a terra attende con impazienza il vento\n" +
				"地に墜ちた鳥は 風を待ち侘びる\n" +
				"\n" +
				"Anche se prego, nulla cambierà\n" +
				"祈ったところで 何も変わらない\n" +
				"\n" +
				"L'unico modo per cambiare la riluttanza della situazione attuale è essere preparati a combattere\n" +
				"不本意な現状(いま)を変えるのは 戦う覚悟だ\n" +
				"Un maiale che ride della volontà di scavalcare un cadavere\n" +
				"屍踏み越えて 進む意志を 嗤う豚よ\n" +
				"\n" +
				"La pace del bestiame, la prosperità della menzogna e la \"libertà\" dei lupi morti affamati!\n" +
				"家畜の安寧 虚偽の繁栄 死せる餓狼の「自由」を!\n" +
				"L'umiliazione catturata è l'inizio del contrattacco\n" +
				"囚われた屈辱は 反撃の嚆矢だ\n" +
				"\n" +
				"L'altro lato delle mura del castello Il cacciatore che massacra la sua preda (Jäger)\n" +
				"城壁の其の彼方 獲物を屠る 狩人(Jäger)\n" +
				"\n" +
				"Mentre il mio corpo brucia con l'intenso intento omicida\n" +
				"迸る 殺意(しょうどう)に 其の身を灼きながら\n" +
				"\n" +
				"Un arco e una freccia cremisi che trafiggono lo scarlatto nel crepuscolo\n" +
				"黄昏に緋を穿つ 紅蓮の弓矢\n" +
				"Non posso lasciare che il bersaglio (tizio) scappi quando corro dietro alla freccia\n" +
				"矢を番え追い駈ける 標的(やつ)は逃がさない\n" +
				"\n" +
				"Spara una freccia per metterlo all'angolo, non farlo mai scappare\n" +
				"矢を放ち追い詰める 決して逃がさない\n" +
				"\n" +
				"Tirando al limite, una corda che sembra scoppiare\n" +
				"限界まで引き絞る はち切れそうな弦\n" +
				"\n" +
				"Spara tutte le volte che vuoi finché il bersaglio (uomo) non muore\n" +
				"標的(やつ)が息絶えるまで 何度でも放つ\n" +
				"per uccidere la preda\n" +
				"獲物を殺すのは\n" +
				"\n" +
				"Non è né un'arma né una tecnica\n" +
				"凶器(どうぐ)でも 技術でもない\n" +
				"\n" +
				"È il tuo stesso intento omicida che è stato acuito.\n" +
				"研き澄まされた お前自身の殺意だ\n" +
				"Wir sind der Jäger Caldo come una fiamma!\n" +
				"Wir sind der Jäger 焔のように熱く!\n" +
				"\n" +
				"Wir sind der Jäger Sii freddo come il ghiaccio!\n" +
				"Wir sind der Jäger 氷のように冷ややかに!\n" +
				"\n" +
				"Wir sind der Jäger Mettiti nella freccia!\n" +
				"Wir sind der Jäger 己を矢に込めて!\n" +
				"\n" +
				"Wir sind der Jäger Conquista attraverso tutto!\n" +
				"Wir sind der Jäger 全てを貫いて征け!\n" +
				"Angriff auf die Titanen\n" +
				"Angriff auf die Titanen\n" +
				"\n" +
				"Der Junge von einst wird calvo zum Schwert greifen\n" +
				"Der Junge von einst wird bald zum Schwert greifen\n" +
				"\n" +
				"Wer nur seine Machtlosigkeit beklagt, kann nichts verändern\n" +
				"Wer nur seine Machtlosigkeit beklagt, kann nichts verändern\n" +
				"\n" +
				"Der Junge von einst wird calvo das schwarze Schwert ergreifen\n" +
				"Der Junge von einst wird bald das schwarze Schwert ergreifen\n" +
				"\n" +
				"Hass und Zorn sind eine zweischneidige Klinge\n" +
				"Hass und Zorn sind eine zweischneidige Klinge\n" +
				"\n" +
				"Bald eines Tages wird er dem Schicksal die Zähne zeigen\n" +
				"Bald eines Tages wird er dem Schicksal die Zähne zeigen\n" +
				"può cambiare qualcosa\n" +
				"何かを変える事が出来るのは\n" +
				"\n" +
				"qualcosa che può essere buttato via\n" +
				"何かを捨てる事が出来るもの\n" +
				"\n" +
				"Qualcosa si avvererà senza assumersi alcun pericolo (rischi)\n" +
				"何ひとつ 危険性(リスク)等 背負わないままで 何かが叶う等\n" +
				"Oscure ipotesi, mere illusioni, persino coraggio sconsiderato adesso\n" +
				"暗愚の想定 唯の幻影 今は無謀な勇気も\n" +
				"\n" +
				"Avanguardia della \"libertà\" Scommesse offensive\n" +
				"「自由」の尖兵 賭けの攻勢\n" +
				"\n" +
				"Vittoria agli schiavi erranti!\n" +
				"奔る奴隷に勝利を!\n" +
				"L'assurdità sospesa è l'inizio dell'attacco\n" +
				"架せられた不条理は 進撃の嚆だ\n" +
				"\n" +
				"Il ragazzo di quel giorno (Ellen) che anela alla 'libertà' dell'orizzonte rubato\n" +
				"奪われた其の地平「自由(せかい)」を望む あの日の少年(エレン)\n" +
				"\n" +
				"Portare la morte nell'oscurità mentre viene attaccato da un inarrestabile intento omicida\n" +
				"止めどなき殺意(しょうどう)に 其の身を侵されながら 宵闇に死を運ぶ\n" +
				"arco e freccia dell'inferno\n" +
				"冥府の弓矢");
		artista2album1canzone1.setLength("03:13");
		artista2album1canzone1.setGenre(Genere.j_metal);


			artista2album1canzone1.setFileMp3(path+"attack.mp3");

		canzoni.add(artista2album1canzone1);

		// aggiungo la canzone all'album dell'artista2
		canzone = artista2album1.getSongList();
		canzone.add(artista2album1canzone1);
		artista2album1.setSongList(canzone);

		Set<Album> artista2albums = new HashSet<>();

		artista2albums.add(artista2album1);
		artista2.setDiscography(artista2albums);


		artisti.add(artista2);
		albums.add(artista2album1);

		Utente utente = new Utente();
		utente.setUsername("utente");
		utente.setPassword("123456");


		try {
			this.add(utente);
		} catch (BusinessException e) {
			throw new RuntimeException(e);
		}

		storedSongs.addAll(canzoni);
		storedAlbums.addAll(albums);
		storedArtists.addAll(artisti);
	}

	@Override
	public List<Artista> getAllArtists() {
		return storedArtists;
	}


	@Override
	public List<Album> getAllAlbums() {
		return storedAlbums;
	}

	@Override
	public List<Canzone> getAllSongs() {
		return storedSongs;
	}

	@Override
	public void setSituation(ViewSituations sit) {
		situation = sit;
	}

	@Override
	public ViewSituations getSituation() {
		return situation;
	}

	@Override
	public void createNewUser(Utente utente) throws BusinessException, AlreadyExistingException {
		// controllo se l'utente già è presente
		for (Utente user : storedUsers) {
			if (user.getUsername().equals(utente.getUsername())) {
				throw new AlreadyExistingException();
			}
		}
		// utente nuovo

		utente.setId(idUser++);
		storedUsers.add(utente);
	}

	@Override
	public UtenteGenerico authenticate(String username, String password) throws BusinessException {
		if ("admin".equalsIgnoreCase(username)) {
			UtenteGenerico admin = new Amministratore();
			admin.setUsername(username);
			admin.setPassword(password);
			return admin;
		} else {
			for (Utente user : storedUsers) {
				if (user.getUsername().equals(username) && user.getPassword().equals(password)) {
					return user;
				}
			}
		}
		throw new UtenteGenericoNotFoundException();
	}

	@Override
	public List<Utente> getAllUsers() {
		return storedUsers;
	}

	@Override
	public void addNewPlaylist(Playlist playlist) throws BusinessException {
		playlist.setId(id++);
		if(!storedPlaylists.add(playlist)) throw new BusinessException();
	}
	@Override
	public void modify(Integer id, String title, List<Canzone> songlist, Utente user)
			throws AlreadyTakenFieldException {

		for (Playlist playlist : storedPlaylists) {

			if (playlist.getId().intValue() == id.intValue()) {
				playlist.setSongList(songlist);

			}
		}

	}
	@Override
	public void deletePlaylist(Playlist playlist) throws BusinessException {
		boolean controllo = false;
		for(Playlist play : storedPlaylists) {
			if(play.getId().intValue() == playlist.getId().intValue()) {
				controllo = true;
				break;
			}
		}
		if(controllo) {
			storedPlaylists.remove(playlist);
		} else {
			throw new BusinessException();
		}
	}
	@Override
	public List<Playlist> getAllPlaylists(Utente utente) throws BusinessException {

		boolean controllo = false;
		for(Utente user: getAllUsers()) {
			if(user.getId().intValue() == utente.getId().intValue()) {
				controllo = true;
				break;
			}
		}
		if(controllo) {
			List<Playlist> userPlaylists = new ArrayList<>();
			//prendere solo le playlist dell'utente passato

			for(Playlist playList: storedPlaylists) {
				if(playList.getUser() == utente) {
					userPlaylists.add(playList);
				}
			}
			return userPlaylists;
		}
		throw new BusinessException();
	}

	@Override
	public String getRicerca() {
		return ricerca;
	}
	@Override
	public void setRicerca(String ricerca) {
		this.ricerca = ricerca;
	}
	@Override
	public void addSongToQueue(Utente utente, Canzone canzone) {
		utente.getSongQueue().add(canzone);
	}
	@Override
	public void deleteSongFromQueue(Utente utente, Canzone canzone) {
		utente.getSongQueue().remove(canzone);
	}
	@Override
	public void updateCurrentSong(Utente utente, int position) {
		utente.setcurrentPosition(position);
	}
	@Override
	public void replaceCurrentSong(Utente utente, Canzone canzone) {
		utente.getSongQueue().set(utente.getcurrentPosition(), canzone);
	}
}
