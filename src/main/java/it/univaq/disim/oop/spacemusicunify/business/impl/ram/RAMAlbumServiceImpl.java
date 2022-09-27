package it.univaq.disim.oop.spacemusicunify.business.impl.ram;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import it.univaq.disim.oop.spacemusicunify.business.AlbumService;
import it.univaq.disim.oop.spacemusicunify.business.AlreadyExistingException;
import it.univaq.disim.oop.spacemusicunify.business.AlreadyTakenFieldException;
import it.univaq.disim.oop.spacemusicunify.business.BusinessException;
import it.univaq.disim.oop.spacemusicunify.business.MultimediaService;
import it.univaq.disim.oop.spacemusicunify.business.ProductionService;
import it.univaq.disim.oop.spacemusicunify.domain.*;

public class RAMAlbumServiceImpl implements AlbumService {
	
	private static Set<Album> storedAlbums = new HashSet<>();
	private static Set<Song> storedSongs = new HashSet<>();
	private static Integer idAlbum = 1;
	private static Integer idSong = 1;
	private MultimediaService multimediaService;
	private ProductionService productionService;
	
	public RAMAlbumServiceImpl(MultimediaService multimediaService, ProductionService productionService) {
		this.multimediaService = multimediaService;
		this.productionService = productionService;
	}

	@Override
	public void add(Album album) throws BusinessException {
		for (Album album1 : storedAlbums) {
			if (album1.getTitle().equals(album.getTitle())) {
				System.out.println("controllo");
				throw new AlreadyExistingException();
			}
		}

		album.setId(idAlbum++);

			Song canzone = new Song();
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

			//canzone.setFileMp3("src" + File.separator + "main" + File.separator + "resources" + File.separator + "viste" + File.separator + "RAMfiles" + File.separator +"our_sympathy.mp3");
			try {
				this.add(canzone);
			} catch (BusinessException e) {
				throw new RuntimeException(e);
			}


		/*Set<Album> set = album.getArtist().getDiscography();

		set.add(album);

		album.getArtist().setDiscography(set);*/

		storedAlbums.add(album);

	}

	@Override
	public void modify(Integer id, String title, Genre genre, Picture tempPicture, Set<Song> songlist, LocalDate release, Album album) throws BusinessException {
		for (Album albums : storedAlbums) {
			if (albums.getTitle().equals(title) && album.getId().intValue() != id.intValue()) {
				System.out.println("controllo");
				throw new AlreadyTakenFieldException();
			}
		}
		for (Album albumcheck : storedAlbums) {
			if (albumcheck.getId().equals(id)) {
				albumcheck.setRelease(release);
				albumcheck.setTitle(title);
				if (albumcheck.getGenre() != genre && genre != Genre.singoli) {
					Set<Song> toChangeGenreSongs = albumcheck.getSongs();
					for (Song canzone : toChangeGenreSongs) {
						canzone.setGenre(genre);
					}
					albumcheck.setSongs(null);
					albumcheck.setSongs(toChangeGenreSongs);
					albumcheck.setGenre(genre);

				}
				break;
			}
		}
	}



	@Override
	public void delete(Album album) throws BusinessException {
		Set<Song> songs = album.getSongs();
		boolean controllo = false;
		for (Album albums : storedAlbums) {
			if (albums.getId().intValue() == album.getId().intValue()) {
				controllo = true;
				break;
			}
		}

/*		if (controllo) {
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
		}*/
	}

	@Override
	public void add(Song canzone) throws AlreadyExistingException {
		Album album = canzone.getAlbum();
		for (Song canzoni : storedSongs) {
			if (canzoni.getTitle().equals(canzone.getTitle())) {
				System.out.println("controllo");
				throw new AlreadyExistingException();
			}
		}

		canzone.setId(idSong++);
		Set<Song> canzoniAlbum = album.getSongs();

		canzoniAlbum.add(canzone);
		album.setSongs(canzoniAlbum);

		storedSongs.add(canzone);

	}

	@Override
	public void modify(Integer id, String title, Audio mp3, String lyrics, Album album, String length, Genre genre, Song song) throws BusinessException {
		for (Song canzone : storedSongs) {
			if (canzone.getTitle().equals(title) && canzone.getId().intValue() != id.intValue()) {
				System.out.println("controllo");
				throw new AlreadyTakenFieldException();
			}
		}
		for (Song canzone : storedSongs) {
			if (canzone.getId().intValue() == id.intValue()) {
				canzone.setLyrics(lyrics);
				canzone.setTitle(title);
				//canzone.setFileMp3((mp3));
				canzone.setLength(length);
				canzone.setGenre(genre);

			}
		}
	}


	@Override
	public void delete(Song canzone) throws BusinessException {
		boolean controllo = false;
		for (Song song : storedSongs) {
			if (song.getId().intValue() == canzone.getId().intValue()) {
				controllo = true;
				break;
			}
		}
		if (controllo) {
			storedSongs.removeIf((Song canzonecheck) -> canzonecheck == canzone);

			storedAlbums.forEach(
					(Album albums) -> canzone.getAlbum().getSongs().removeIf((Song canzonecheck) -> canzonecheck == canzone));

//			for(User utente : getAllUsers()) {
//				getAllPlaylists(utente).forEach(
//						(Playlist playlist) -> playlist.getSongList().removeIf((Song canzonecheck) -> canzonecheck == canzone));
//			}
		} else {
			throw new BusinessException();
		}
	}

	@Override
	public Set<Album> getAlbumList() throws BusinessException {
		return storedAlbums;
	}

	@Override
	public Set<Song> getSongList() throws BusinessException {
		return storedSongs;
	}

	@Override
	public Set<Artist> findAllArtists(Album album) throws BusinessException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<Production> findAllProductions(Album album) throws BusinessException {
		return null;
	}

	@Override
	public Set<Artist> getChoosenArtists() {
		return null;
	}

	@Override
	public void setChoosenArtists(Set<Artist> choosenArtists) {

	}

}
