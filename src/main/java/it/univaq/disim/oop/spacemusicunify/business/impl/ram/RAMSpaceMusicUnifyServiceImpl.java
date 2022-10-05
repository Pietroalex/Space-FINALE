package it.univaq.disim.oop.spacemusicunify.business.impl.ram;

import it.univaq.disim.oop.spacemusicunify.business.*;
import it.univaq.disim.oop.spacemusicunify.domain.*;
import it.univaq.disim.oop.spacemusicunify.view.SpacemusicunifyPlayer;
import it.univaq.disim.oop.spacemusicunify.view.ViewDispatcher;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class RAMSpaceMusicUnifyServiceImpl implements SPACEMusicUnifyService {
	
	private static String path = "src"+ File.separator + "main" + File.separator + "resources" + File.separator + "dati" + File.separator + "RAMfiles" + File.separator;
	private static String pathmp3 = "src"+ File.separator + "main" + File.separator + "resources" + File.separator + "dati" + File.separator + "RAMfiles" + File.separator;

	@Override
	public void setAllDefaults() {
		
		SpacemusicunifyBusinessFactory factory = SpacemusicunifyBusinessFactory.getInstance();
		ArtistService artistService = factory.getArtistService();
		AlbumService albumService = factory.getAlbumService();
		UserService userService = factory.getUserService();

		List<Artist> artists = new ArrayList<>();
		List<Album> albums = new ArrayList<>();
		List<Song> songs = new ArrayList<>();


		// creo l'artista1
		Artist artist1 = new Artist();
		artist1.setName("Pasquale Arrosto");
		artist1.setBiography("Sono Pasquale Arrosto, suono la musica elettronica");
		artist1.setNationality(Nationality.italian);
		artist1.setYearsOfActivity(6);
		Set<Picture> artista1img = new HashSet<>();

		Picture picture1 = new Picture();
		picture1.setData(path+"pasqualearrosto.jpg");
		picture1.setHeight(120);
		picture1.setWidth(120);
		artista1img.add(picture1);

		Picture picture2 = new Picture();
		picture2.setData(path+"group4.jpg");
		picture2.setHeight(120);
		picture2.setWidth(120);
		artista1img.add(picture2);

		Picture picture3 = new Picture();
		picture3.setData(path+"group4.jpg");
		picture3.setHeight(120);
		picture3.setWidth(120);
		artista1img.add(picture3);

		Picture picture4 = new Picture();
		picture4.setData(path+"group4.jpg");
		picture4.setHeight(120);
		picture4.setWidth(120);
		artista1img.add(picture4);


		artist1.setPictures(artista1img);

		try {
			artistService.add(artist1);
		} catch (BusinessException e1) {
			ViewDispatcher.getInstance().renderError(e1);
		}
		
		
		
		
		
		
/*		

		//artista1album1canzone1.setFileMp3(pathmp3+"our_sympathy.mp3");

		canzoni.add(artista1album1canzone1);

		// aggiungo la canzone all'album dell'artista1
		Set<Song> canzone = artista1album1.getSongs();
		canzone.add(artista1album1canzone1);
		artista1album1.setSongs(canzone);

		Song artista1album1canzone2 = new Song();
		//artista1album1canzone2.setId(idSongs++);
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
		artista1album1canzone2.setGenre(Genre.j_rock);


		//artista1album1canzone2.setFileMp3(pathmp3+"unravel.mp3");

		canzoni.add(artista1album1canzone2);

		canzone = artista1album1.getSongs();
		canzone.add(artista1album1canzone2);
		artista1album1.setSongs(canzone);

		// creo il set di albums per l'artista1
		Set<Album> artista1Albums = new HashSet<>();

		artista1Albums.add(artista1album1);
		/*artista1.setDiscography(artista1Albums);


		artisti.add(artista1);
		albums.add(artista1album1);

		// creo l'artista2
		Artist artista2 = new Artist();
		artista2.setId(idArtists++);
		artista2.setStageName("British Wonder");
		artista2.setBiography("We are 2 bassist, We are the new rock problems");
		artista2.setNationality(Nationality.british);
		artista2.setYearsOfActivity(10);*
		Set<Picture> artista2img = new HashSet<>();

			Picture picture2 = new Picture();
			picture2.setData(path+"2bassists.png");
			artista2img.add(picture2);



		/*artista2.setPictures(artista2img);*

		// creo l'album dell'artista2
		Album artista2album1 = new Album();
		//artista2album1.setId(idAlbums++);
		artista2album1.setTitle("Inediti");
		artista2album1.setGenre(Genre.singoli);
		artista2album1.setRelease(LocalDate.of(2012, 10, 20));
		/*artista2album1.setCover(path+"cover.png");*
		/*artista2album1.setArtist(artista2);*

		// creo la canzone e la lego all'album dell'artista2
		Song artista2album1canzone1 = new Song();
		//artista2album1canzone1.setId(idSongs++);
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
		artista2album1canzone1.setGenre(Genre.j_metal);


		//artista2album1canzone1.setFileMp3(path+"attack.mp3");

		canzoni.add(artista2album1canzone1);

		// aggiungo la canzone all'album dell'artista2
		canzone = artista2album1.getSongs();
		canzone.add(artista2album1canzone1);
		artista2album1.setSongs(canzone);

		Set<Album> artista2albums = new HashSet<>();

		artista2albums.add(artista2album1);
		/*artista2.setDiscography(artista2albums);*


		/*artisti.add(artista2);*
		albums.add(artista2album1);
*/
		User user = new User();
		user.setUsername("utente");
		user.setPassword("123456");
		try {
			userService.add(user);
		} catch (BusinessException e) {
			throw new RuntimeException(e);
		}

//		storedSongs.addAll(canzoni);
//		storedAlbums.addAll(albums);
//		storedArtists.addAll(artisti);
	}


}
