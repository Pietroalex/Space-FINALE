package it.univaq.disim.oop.spacemusicunify.controller.usercontrollers;

import java.io.ByteArrayInputStream;
import java.net.URL;
import java.time.LocalDate;
import java.util.*;

import it.univaq.disim.oop.spacemusicunify.business.*;
import it.univaq.disim.oop.spacemusicunify.controller.DataInitializable;
import it.univaq.disim.oop.spacemusicunify.domain.Album;
import it.univaq.disim.oop.spacemusicunify.domain.Artist;
import it.univaq.disim.oop.spacemusicunify.domain.Song;
import it.univaq.disim.oop.spacemusicunify.domain.Genre;
import it.univaq.disim.oop.spacemusicunify.domain.Nationality;
import it.univaq.disim.oop.spacemusicunify.domain.Playlist;
import it.univaq.disim.oop.spacemusicunify.domain.Production;
import it.univaq.disim.oop.spacemusicunify.domain.User;
import it.univaq.disim.oop.spacemusicunify.view.SpacemusicunifyPlayer;
import it.univaq.disim.oop.spacemusicunify.view.ViewDispatcher;
import it.univaq.disim.oop.spacemusicunify.view.ViewSituations;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class SearchController implements Initializable, DataInitializable<User>{

	private UserService userService;
	@FXML
	private TableView<Song> song;
	@FXML
	private TableColumn<Song, String> songTitle;
	@FXML
	private TableColumn<Song, String> songArtist;
	@FXML
	private TableColumn<Song, String> songAlbum;
	@FXML
	private TableColumn<Song, String> songTime;
	@FXML
	private TableColumn<Song, Genre> songGenre;
	@FXML
	private TableColumn<Song, Button> addSongToQueue;
	@FXML
	private TableColumn<Song, Button> songInfo;
	
	@FXML
	private TableView<Artist> artist;
	@FXML
	private TableColumn<Artist,String> artistName;
	@FXML
	private TableColumn<Artist,Nationality> artistNationality;
	@FXML
	private TableColumn<Artist,Integer> artistYears;
	@FXML
	private TableColumn<Artist, Button> artistInfo;
	
	@FXML
	private TableView<Album> album;
	@FXML
	private TableColumn<Album, ImageView> albumCover;
	@FXML
	private TableColumn<Album, String> albumName;
	@FXML
	private TableColumn<Album, String> albumArtists;
	@FXML
	private TableColumn<Album, Genre> albumGenre;
	@FXML
	private TableColumn<Album, LocalDate> albumRelease;
	@FXML
	private TableColumn<Album, Button> addAlbumToQueue;
	@FXML
	private TableColumn<Album, Button> addAlbumToPlaylist;
	@FXML
	private TableColumn<Album, Button> albumInfo;
	private ViewDispatcher dispatcher;
	private String ricerca;
	private User utente;
	private PlayerService playerService;
	private ArtistService artistService;
	private AlbumService albumService;
	private ProductionService productionService;
	
	public SearchController() {
		dispatcher = ViewDispatcher.getInstance();
		SpacemusicunifyBusinessFactory factory = SpacemusicunifyBusinessFactory.getInstance();
		userService = factory.getUserService();
		playerService = factory.getPlayerService();
		artistService = factory.getArtistService();
		albumService = factory.getAlbumService();
		productionService = factory.getProductionService();
	}
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		this.ricerca = RunTimeService.getSearch();
		//songTable
		songTitle.setCellValueFactory(new PropertyValueFactory<>("title"));
		songArtist.setCellValueFactory((TableColumn.CellDataFeatures<Song, String> param) -> {

			return new SimpleStringProperty(/*param.getValue().getAlbum().getArtist().getStageName()*/);
		});
		songAlbum.setCellValueFactory((TableColumn.CellDataFeatures<Song, String> param) -> {
			return new SimpleStringProperty(param.getValue().getAlbum().getTitle());
		});
		songGenre.setCellValueFactory(new PropertyValueFactory<>("genre"));
		songTime.setCellValueFactory(new PropertyValueFactory<>("length"));
		songInfo.setStyle("-fx-alignment: CENTER;");
		songInfo.setCellValueFactory((TableColumn.CellDataFeatures<Song, Button> param) -> {



			final Button info = new Button("info");
			info.setCursor(Cursor.HAND);
			info.setOnAction((ActionEvent event) -> {
				dispatcher.setSituation(ViewSituations.user);
				List<Object> fakeList = new ArrayList<>();
				try {
					for(Production prod : productionService.getAllProductions()) {
						if(prod.getAlbum().equals(param.getValue().getAlbum())) {
							fakeList.add(prod);
							break;
						}
					}
				} catch (BusinessException e) {
					e.printStackTrace();
				}
				fakeList.add(param.getValue());
				dispatcher.renderView("AdministratorViews/ManageArtistsView/ManageAlbumsView/ManageSongsView/song_detail", fakeList);
			});
			return new SimpleObjectProperty<Button>(info);
		});
		addSongToQueue.setStyle("-fx-alignment: CENTER;");
		addSongToQueue.setCellValueFactory((TableColumn.CellDataFeatures<Song, Button> param) -> {

			final Button addButton = new Button("Add to queue");
			addButton.setCursor(Cursor.HAND);

			/*if(this.checkForClones(param.getValue())){
				addButton.setDisable(true);
			}

			addButton.setOnAction((ActionEvent event) -> {
				spaceMusicUnifyService.addSongToQueue(utente, param.getValue());
				if(mediaPlayerSettings.getMediaPlayer() != null && mediaPlayerSettings.getMediaPlayer().getStatus() != MediaPlayer.Status.STOPPED){
					mediaPlayerSettings.getMediaPlayer().stop();
					mediaPlayerSettings.getMediaPlayer().dispose();
				}
				song.refresh();
				mediaPlayerSettings.setPlayerState(PlayerState.searchSingleClick);
				dispatcher.renderPlayer("UserViews/UserHomeView/playerPane", utente);
			});
*/
			return new SimpleObjectProperty<Button>(addButton);
		});

		//artistaTable
		artistName.setCellValueFactory(new PropertyValueFactory<>("name"));
		artistNationality.setCellValueFactory(new PropertyValueFactory<>("nationality"));
		artistYears.setCellValueFactory(new PropertyValueFactory<>("yearsOfActivity"));
		artistInfo.setStyle("-fx-alignment: CENTER;");
		artistInfo.setCellValueFactory((TableColumn.CellDataFeatures<Artist, Button> param) -> {
			final Button info = new Button("info");
			info.setCursor(Cursor.HAND);
			info.setOnAction((ActionEvent event) -> {
				dispatcher.setSituation(ViewSituations.user);
				dispatcher.renderView("AdministratorViews/ManageArtistsView/artist_detail", param.getValue());
			});
			return new SimpleObjectProperty<Button>(info);
		});
		
		//albumTable
		albumCover.setCellValueFactory((TableColumn.CellDataFeatures<Album,ImageView> param) -> {

			ImageView image;
			image = new ImageView(new Image(new ByteArrayInputStream(param.getValue().getCover().getData())));
			image.setFitHeight(40);
        	image.setFitWidth(40);
        	return new SimpleObjectProperty<ImageView>(image);
        });
		albumName.setCellValueFactory(new PropertyValueFactory<>("title"));
		albumArtists.setCellValueFactory((TableColumn.CellDataFeatures<Album, String> param) -> {

			return new SimpleStringProperty(/*param.getValue().getArtist().getStageName()*/);
		});
		albumGenre.setCellValueFactory(new PropertyValueFactory<>("genre"));
		albumRelease.setCellValueFactory(new PropertyValueFactory<>("release"));
		addAlbumToQueue.setStyle("-fx-alignment: CENTER;");
		addAlbumToQueue.setCellValueFactory((TableColumn.CellDataFeatures<Album, Button> param) -> {

			final Button addButton = new Button("Add to queue");
			addButton.setCursor(Cursor.HAND);
			addButton.setOnAction((ActionEvent event) -> {
				//aggiungere la canzone alla coda di riproduzione dell'utente
				Set<Song> lista = param.getValue().getSongs();
				for(Song canzoneAlbum: lista) {
					Boolean alreadyAdded = false;
					/*for(Song canzone: utente.getSongQueue()) {
						if(canzoneAlbum.getId().intValue() == canzone.getId().intValue()) {
							alreadyAdded = true;
							break;
						}
					}
					if(!alreadyAdded) {
						spaceMusicUnifyService.addSongToQueue(utente, canzoneAlbum);
					}*/
				}
				SpacemusicunifyPlayer spacemusicunifyPlayer;
				try {
					spacemusicunifyPlayer = playerService.getPlayer(utente);
					if(spacemusicunifyPlayer.getMediaPlayer() != null && spacemusicunifyPlayer.getMediaPlayer().getStatus() != MediaPlayer.Status.STOPPED){
						spacemusicunifyPlayer.getMediaPlayer().stop();
						spacemusicunifyPlayer.getMediaPlayer().dispose();
					}
				} catch (ObjectNotFoundException o) {
					//show label
				} catch (BusinessException e) {
					dispatcher.renderError(e);
				}

				playerService.setPlayerState(PlayerState.searchSingleClick);
				dispatcher.renderView("UserViews/UserHomeView/playerPane", utente);
			});
			return new SimpleObjectProperty<Button>(addButton);
		});
		albumInfo.setStyle("-fx-alignment: CENTER;");
		albumInfo.setCellValueFactory((TableColumn.CellDataFeatures<Album, Button> param) -> {


			final Button info = new Button("info");
			info.setCursor(Cursor.HAND);
			info.setOnAction((ActionEvent event) -> {
				Production production = new Production();
				try {
					for(Production prod : productionService.getAllProductions()) {
						if(prod.getAlbum() == param.getValue()) production = prod;
						break;
					}
				} catch (BusinessException e) {
					dispatcher.renderError(e);
				}
				dispatcher.setSituation(ViewSituations.user);
				dispatcher.renderView("AdministratorViews/ManageArtistsView/ManageAlbumsView/album_detail", production);
			});
			return new SimpleObjectProperty<Button>(info);
		});
		addAlbumToPlaylist.setStyle("-fx-alignment: CENTER;");
		addAlbumToPlaylist.setCellValueFactory((TableColumn.CellDataFeatures<Album, Button> param) -> {
			final Button addButton = new Button("Add to playlist");
			addButton.setCursor(Cursor.HAND);
			addButton.setOnAction((ActionEvent event) -> {
				showPopupSelectPlaylist(param.getValue());
			});
			return new SimpleObjectProperty<Button>(addButton);
		});
	}

	@Override
	public void initializeData(User utente) {
		this.ricerca = RunTimeService.getSearch();
		this.utente = utente;

		try {
			List<Artist> artistList = new ArrayList<>();
			for(Artist artista: artistService.getArtistList()) {
				if(artista.getName().contains(ricerca.strip())) {
					artistList.add(artista);
				}
			}
			ObservableList<Artist> artistData = FXCollections.observableArrayList(artistList);
			artist.setItems(artistData);


			List<Song> songList = new ArrayList<>();

			for(Song song: albumService.getSongList()) {
				if(song.getTitle().contains(ricerca.strip()) || song.getGenre().toString().equals(ricerca)) {
					songList.add(song);
				}
			}

		ObservableList<Song> songData = FXCollections.observableArrayList(songList);
			song.setItems(songData);


			List<Album> albumList = new ArrayList<>();
			for(Album album: albumService.getAlbumList()) {
				if(album.getTitle().contains(ricerca.strip()) || album.getGenre().toString().equals(ricerca)) {
					albumList.add(album);
				}
			}
			ObservableList<Album> albumData = FXCollections.observableArrayList(albumList);
			album.setItems(albumData);

		} catch (BusinessException e) {
			throw new RuntimeException(e);
		}

		song.setRowFactory( tablerow -> {
			TableRow<Song> canzone = new TableRow<>();
			canzone.setOnMouseClicked((MouseEvent event) -> {
				if(event.getClickCount() == 2) {

					/*if(this.utente.getcurrentSong() != null) {
						mediaPlayerSettings.setPlayerState(PlayerState.searchDoubleClick);
						System.out.println("this.utente.getcurrentSong() != null");
						if(this.utente.getcurrentSong().getId().intValue() != canzone.getItem().getId().intValue()) {

							spaceMusicUnifyService.deleteSongFromQueue(utente, canzone.getItem());	//rimuovo la canzone se già presente in coda
							
							//carica la canzone nel player
							if(utente.getSongQueue().size() == this.utente.getcurrentPosition()) {
								spaceMusicUnifyService.addSongToQueue(utente, canzone.getItem());
								System.out.println("utente.getSongQueue().size() <= this.utente.getcurrentPosition()");
								dispatcher.renderPlayer("UserViews/UserHomeView/playerPane", utente);
							} else {
								mediaPlayerSettings.getMediaPlayer().stop();
								mediaPlayerSettings.getMediaPlayer().dispose();
								spaceMusicUnifyService.replaceCurrentSong(utente, canzone.getItem());
								System.out.println("utente.getSongQueue().size() > this.utente.getcurrentPosition()");
								dispatcher.renderPlayer("UserViews/UserHomeView/playerPane", utente);
							}
							song.refresh();
						} else {
							System.out.println("la canzone è già in riproduzione al momento");
						}
					} else {
						spaceMusicUnifyService.addSongToQueue(utente, canzone.getItem());

						dispatcher.renderPlayer("UserViews/UserHomeView/playerPane", utente);
					}
*/
				}
			});
			return canzone;
		});
	}

/*	public boolean checkForClones(Song value){

		for(Song canzone : utente.getSongQueue()){
			if(canzone.getId().equals(value.getId())) return true;
		}
		return false;
	}*/

	private void showPopupSelectPlaylist(Album selectedAlbum) {
		Stage popupwindow = new Stage();
		popupwindow.initModality(Modality.APPLICATION_MODAL);
		Label title = new Label("Seleziona la playlist");
		title.setAlignment(Pos.CENTER);
		//selezione multipla playlist
		TableView<Playlist> tableView = new TableView<>();
		TableColumn<Playlist, String> name = new TableColumn<Playlist,String>("Title");
		TableColumn<Playlist, Button> add = new TableColumn<Playlist, Button>();
		name.setCellValueFactory(new PropertyValueFactory<>("title"));
		add.setCellValueFactory((TableColumn.CellDataFeatures<Playlist,Button> param) -> {
			final Button addButton = new Button("add");
			addButton.setOnAction((ActionEvent event) -> {
				//aggiunto album alla playlist
				Set<Song> lista = param.getValue().getSongList();
				for(Song canzoneAlbum: selectedAlbum.getSongs()) {
					Boolean alreadyAdded = false;
					for(Song canzonePlaylist: lista) {
						if(canzoneAlbum.getId().intValue() == canzonePlaylist.getId().intValue()) {
							alreadyAdded = true;
							break;
						}
					}
					if(!alreadyAdded) {
						lista.add(canzoneAlbum);
					}
				}
				try {
					userService.modify(param.getValue().getId(), param.getValue().getTitle(),lista, param.getValue().getUser());
				} catch (BusinessException e) {
					 e.printStackTrace();
				}

				addButton.setDisable(true);
			});
			return new SimpleObjectProperty<Button>(addButton);
		});
		
		tableView.getColumns().add(name);
		tableView.getColumns().add(add);

		try {
			ObservableList<Playlist> observableList = FXCollections.observableArrayList(userService.getAllPlaylists(utente));
			tableView.setItems(observableList);
		} catch (BusinessException e1) {
			e1.printStackTrace();
		}

		// operazione annulla
		Button closeButton = new Button("Cancel");
		closeButton.setCursor(Cursor.HAND);
		closeButton.setOnAction(e -> {
			dispatcher.renderView("UserViews/UserHomeView/playlistPane", utente);
			popupwindow.close();
		});

		VBox layout = new VBox(10);
		layout.getChildren().addAll(title, tableView, closeButton);
		layout.setAlignment(Pos.CENTER);
		Scene scene1 = new Scene(layout, 300, 150);
		popupwindow.setScene(scene1);
		popupwindow.setResizable(false);
		popupwindow.setTitle("Add " + selectedAlbum.getTitle() + " to playlist?");
		popupwindow.showAndWait();
	}


	@FXML
	public void artistView(ActionEvent event) {
		song.setVisible(false);
		album.setVisible(false);
		artist.setVisible(true);
	}
	
	@FXML
	public void songView(ActionEvent event) {
		song.setVisible(true);
		artist.setVisible(false);
		album.setVisible(false);
	}
	
	@FXML
	public void albumView(ActionEvent event) {
		song.setVisible(false);
		artist.setVisible(false);
		album.setVisible(true);
	}
}
