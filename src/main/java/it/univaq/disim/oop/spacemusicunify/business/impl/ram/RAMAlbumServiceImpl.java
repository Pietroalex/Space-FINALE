package it.univaq.disim.oop.spacemusicunify.business.impl.ram;

import java.io.File;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

import it.univaq.disim.oop.spacemusicunify.business.*;
import it.univaq.disim.oop.spacemusicunify.controller.usercontrollers.PlayerPaneController;
import it.univaq.disim.oop.spacemusicunify.domain.*;

public class RAMAlbumServiceImpl implements AlbumService {
	
	private static Set<Album> storedAlbums = new HashSet<>();
	private static Set<Song> storedSongs = new HashSet<>();
	private static Integer idAlbum = 1;
	private static Integer idSong = 1;
	private MultimediaService multimediaService;
	private ProductionService productionService;
	private Set<Artist> chosenArtists;

	public RAMAlbumServiceImpl(MultimediaService multimediaService, ProductionService productionService) {
		this.multimediaService = multimediaService;
		this.productionService = productionService;
	}

	@Override
	public void add(Album album) throws BusinessException {
		
		if (album.getTitle().contains("Singles") && album.getGenre() != Genre.singles)
				throw new AlreadyExistingException("New Album, The title must not contain 'Singles'");
		
		for (Album albums : storedAlbums) {
			if (albums.getTitle().equals(album.getTitle())) throw new AlreadyExistingException("New Album, Already Existing album with this title");
		}
		album.setId(idAlbum++);
		multimediaService.add(album.getCover());

		//creo la canzone
		Song song = new Song();
		song.setAlbum(album);
		if(album.getSongs() == null)song.setTitle("Default"+album.getTitle());
		else song.setTitle("Default "+album.getTitle());
		song.setLyrics("In the dark 背を向けた未来\n" +
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
		song.setLength("04:02");
		if(album.getGenre() == Genre.singles){
			song.setGenre(Genre.alternative_rock);
		} else {
			song.setGenre(album.getGenre());
		}

		Audio audio = new Audio();
		audio.setData("src" + File.separator + "main" + File.separator + "resources" + File.separator + "data" + File.separator + "RAMfiles" + File.separator + "our_sympathy.mp3");
		audio.setOwnership(song);
		song.setFileMp3(audio);
		
		add(song);

		Set<Artist> artists = getChosenArtists();
		if(artists != null) {
			for (Artist artist : artists) {
				Production production = new Production();
				production.setArtist(artist);
				production.setAlbum(album);
				productionService.add(production);
			}
		}

		storedAlbums.add(album);

	}

	@Override
	public void modify(Integer id, String title, Genre genre, Picture tempPicture, Set<Song> songlist, LocalDate release, Album album) throws BusinessException {
		
		if (album.getTitle().contains("Singles") && album.getGenre() != Genre.singles)
			throw new AlreadyExistingException("Modify Album, The title must not contain 'Singles'");
		
		for (Album albums : storedAlbums) {
			if (albums.getTitle().equals(title) && albums.getId().intValue() != id.intValue())
				throw new AlreadyExistingException("Modify Album, Already Existing album with this title");
		}
		for (Album albumCheck : storedAlbums) {
			if (albumCheck.getId().equals(id)) {
				albumCheck.setTitle(title);

				if (albumCheck.getGenre() != genre && genre != Genre.singles) {
					albumCheck.setGenre(genre);
					Set<Song> toChangeGenreSongs = albumCheck.getSongs();
					for (Song song : toChangeGenreSongs) {
						modify(song.getId(), song.getTitle(), null, song.getLyrics(), album, song.getLength(), genre, song);
					}
					Picture savePicture;
					if(tempPicture != null){
						savePicture = tempPicture;

						multimediaService.add(savePicture);
						multimediaService.delete(album.getCover());
					}else{
						savePicture = album.getCover();
					}
					albumCheck.setCover(savePicture);
					albumCheck.setSongs(toChangeGenreSongs);

					albumCheck.setRelease(release);
				}
				break;
			}
		}
	}



	@Override
	public void delete(Album album) throws BusinessException {
		boolean check = false;

		for(Album albumCheck : getAlbumList()) {

			if(albumCheck.getId().intValue() ==  album.getId().intValue()) {
				check = true;

				for(Production production : productionService.getAllProductions()){
					if(production.getAlbum().getId().intValue() == album.getId().intValue()) productionService.delete(production);
				}
				Set<Song> songs = new HashSet<>(album.getSongs());
				for(Song song : songs){
					delete(song);
				}
				multimediaService.delete(album.getCover());
				storedAlbums.removeIf((Album albumCheck2) -> albumCheck2.getId().intValue() == album.getId().intValue());
				break;
			}
		}
		if(!check)throw new BusinessException("Object not found, This album doesn't exist");
	}

	@Override
	public void add(Song song) throws BusinessException {
		
		if((song.getTitle().contains("DefaultSingles") && (song.getAlbum().getSongs() != null && song.getAlbum().getSongs().size() > 1)) || (song.getTitle().contains("DefaultSingles") && song.getAlbum().getGenre() != Genre.singles))
			throw new AlreadyExistingException("New Song, The title must not contain 'DefaultSingles'");
		
		for (Song songs : storedSongs) {
			if(songs.getTitle().equals(song.getTitle())) throw new AlreadyExistingException("Already Existing song with this title");
		}

		song.setId(idSong++);
		multimediaService.add(song.getFileMp3());
		Album album = song.getAlbum();

		Set<Song> songList;
		if(album.getSongs() == null) songList = new HashSet<>();
		else songList = album.getSongs();
		songList.add(song);
		album.setSongs(songList);

		storedSongs.add(song);

	}

	@Override
	public void modify(Integer id, String title, Audio tempAudio, String lyrics, Album album, String length, Genre genre, Song oldSong) throws BusinessException {
		
		if((title.contains("DefaultSingles") && album.getSongs().size() > 1) || (title.contains("DefaultSingles") && album.getGenre() != Genre.singles))
			throw new AlreadyExistingException("Modify Song, The title must not contain 'DefaultSingles'");
			
		for (Song songs : storedSongs) {
			if(songs.getTitle().equals(title) && songs.getId().intValue() != id.intValue())
				throw new AlreadyExistingException("Modify Song, Already Existing song with this title");
		}
		
		for (Song song : storedSongs) {
			if (song.getId().intValue() == id.intValue()) {
				Audio saveAudio;


				if(tempAudio != null){
					saveAudio = tempAudio;

					multimediaService.add(tempAudio);
					multimediaService.delete(song.getFileMp3());
				}else{
					saveAudio = song.getFileMp3();
				}

				song.setTitle(title);
				song.setFileMp3(saveAudio);
				song.setLyrics(lyrics);
				song.setLength(length);
				song.setGenre(genre);

			}
		}
	}


	@Override
	public void delete(Song song) throws BusinessException {
		boolean check = false;
		Album album = song.getAlbum();
		for (Song songs : getSongList()) {
			if(songs.getId().intValue() == song.getId().intValue()) {
				check = true;

				UserService userService = SpacemusicunifyBusinessFactory.getInstance().getUserService();
				for(User user : userService.getAllUsers()){
					for(Playlist playlist : userService.getAllPlaylists(user)){
						Set<Song> playlistSongs = playlist.getSongList();
						playlistSongs.removeIf((Song songCheck) -> songCheck.getId().intValue() == song.getId().intValue());
						userService.modify(playlistSongs, playlist);
					}
					PlayerService playerService = SpacemusicunifyBusinessFactory.getInstance().getPlayerService();
					for(Song songCheck : playerService.getPlayer(user).getQueue()) {
						if(songCheck.getId().intValue() == song.getId().intValue()){
							playerService.deleteSongFromQueue(playerService.getPlayer(user), song);
							break;
						}
					}
				}
				multimediaService.delete(song.getFileMp3());

				storedSongs.removeIf((Song songCheck) -> songCheck.getId().intValue() == song.getId().intValue());
				Set<Song> songList = album.getSongs();
				songList.removeIf((Song songCheck) -> songCheck.getId().intValue() == song.getId().intValue());
				album.setSongs(songList);


				break;
			}
		}

		if(!check)throw new BusinessException("Object not found, This song doesn't exist");

	}

	@Override
	public Set<Album> getAlbumList() throws BusinessException {
		if(storedAlbums == null) throw new BusinessException("Error In albums storage");
		return new HashSet<>(storedAlbums);
	}

	@Override
	public Set<Song> getSongList() throws BusinessException {
		if(storedSongs == null) throw new BusinessException("Error In songs storage");
		return new HashSet<>(storedSongs);
	}

	@Override
	public Set<Artist> findAllArtists(Album album) throws BusinessException {
		Set<Artist> artists = new HashSet<>();
		for(Production production : findAllProductions(album)) {
			artists.add(production.getArtist());
		}
		if(artists.isEmpty()) throw new BusinessException("Object not found, There is no artist for this album");
		return artists;
	}

	@Override
	public Set<Production> findAllProductions(Album album) throws BusinessException {
		Set<Production> productions = new HashSet<>();
		for(Production production : productionService.getAllProductions()) {
			if(production.getAlbum().getId().intValue() == album.getId().intValue()) {
				productions.add(production);
			}
		}
		if(productions.isEmpty()) throw new BusinessException("Object not found, There is no production for this album");
		return productions;
	}

	@Override
	public Set<Artist> getChosenArtists() {
		return chosenArtists;
	}
	@Override
	public void setChosenArtists(Set<Artist> chosenArtists) {
		this.chosenArtists = chosenArtists;
	}
}
