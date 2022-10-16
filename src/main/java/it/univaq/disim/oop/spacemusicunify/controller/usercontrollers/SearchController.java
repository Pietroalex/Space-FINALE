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
	private String search;
	private User user;
	private PlayerService playerService;
	private ArtistService artistService;
	private AlbumService albumService;
	private ProductionService productionService;
	private SpacemusicunifyPlayer spacemusicunifyPlayer;
	
	public SearchController() {
		dispatcher = ViewDispatcher.getInstance();
		SpacemusicunifyBusinessFactory factory = SpacemusicunifyBusinessFactory.getInstance();
		userService = factory.getUserService();
		playerService = factory.getPlayerService();
		artistService = factory.getArtistService();
		albumService = factory.getAlbumService();
	}
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		this.search = RunTimeService.getSearch();
		//songTable
		songTitle.setCellValueFactory(new PropertyValueFactory<>("title"));
		songArtist.setCellValueFactory((TableColumn.CellDataFeatures<Song, String> param) -> {
			String artists = "";
			try {
				Set<Artist> artistsSet = albumService.findAllArtists(param.getValue().getAlbum());
				for (Artist artistCtrl : artistsSet) {
					artists = artists + artistCtrl.getName() + ", ";
				}
				artists = artists.substring(0, artists.length()-2);
			} catch (BusinessException e) {
				dispatcher.renderError(e);
			}
			return new SimpleStringProperty(artists);
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
				dispatcher.renderView("AdministratorViews/ManageArtistsView/ManageAlbumsView/ManageSongsView/song_detail", param.getValue());
			});
			return new SimpleObjectProperty<Button>(info);
		});
		addSongToQueue.setStyle("-fx-alignment: CENTER;");
		addSongToQueue.setCellValueFactory((TableColumn.CellDataFeatures<Song, Button> param) -> {

			final Button addButton = new Button("Add to queue");
			addButton.setCursor(Cursor.HAND);
			addButton.setOnAction((ActionEvent event) -> {
				//aggiungere la canzone alla coda di riproduzione dell'utente
				try {
					if(!(checkForClones(spacemusicunifyPlayer, param.getValue()))) playerService.addSongToQueue(spacemusicunifyPlayer, param.getValue());
					addButton.setDisable(true);
				} catch (BusinessException b) {
					dispatcher.renderError(b);
				}
			});
			if(checkForClones(spacemusicunifyPlayer, param.getValue())) addButton.setDisable(true);


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
			String artists = "";
			try {
				Set<Artist> artistsSet = albumService.findAllArtists(param.getValue());
				for (Artist artistCtrl : artistsSet) {
					artists = artists + artistCtrl.getName() + ", ";
				}
				artists = artists.substring(0, artists.length()-2);
			} catch (BusinessException e) {
				dispatcher.renderError(e);
			}
			return new SimpleStringProperty(artists);
		});
		albumGenre.setCellValueFactory(new PropertyValueFactory<>("genre"));
		albumRelease.setCellValueFactory(new PropertyValueFactory<>("release"));
		addAlbumToQueue.setStyle("-fx-alignment: CENTER;");
		addAlbumToQueue.setCellValueFactory((TableColumn.CellDataFeatures<Album, Button> param) -> {

			final Button addButton = new Button("Add to queue");
			addButton.setCursor(Cursor.HAND);
			addButton.setOnAction((ActionEvent event) -> {
				//aggiungere la canzone alla coda di riproduzione dell'utente
				try {
					Set<Song> songs = param.getValue().getSongs();
					for(Song albumSong: songs) {
						if(!(checkForClones(spacemusicunifyPlayer, albumSong))) playerService.addSongToQueue(spacemusicunifyPlayer, albumSong);
					}
					addButton.setDisable(true);
					/*
					 * if(spacemusicunifyPlayer.getMediaPlayer() != null &&
					 * spacemusicunifyPlayer.getMediaPlayer().getStatus() !=
					 * MediaPlayer.Status.STOPPED){ spacemusicunifyPlayer.getMediaPlayer().stop();
					 * spacemusicunifyPlayer.getMediaPlayer().dispose(); }
					 */
				} catch (BusinessException b) {
					dispatcher.renderError(b);
				}
				
				/*
				 * playerService.setPlayerState(PlayerState.searchSingleClick);
				 * dispatcher.renderView("UserViews/HomeView/playerPane", user);
				 */
			});
			return new SimpleObjectProperty<Button>(addButton);
		});
		albumInfo.setStyle("-fx-alignment: CENTER;");
		albumInfo.setCellValueFactory((TableColumn.CellDataFeatures<Album, Button> param) -> {


			final Button info = new Button("info");
			info.setCursor(Cursor.HAND);
			info.setOnAction((ActionEvent event) -> {
				dispatcher.setSituation(ViewSituations.user);
				dispatcher.renderView("AdministratorViews/ManageArtistsView/ManageAlbumsView/album_detail", param.getValue());
			});
			return new SimpleObjectProperty<Button>(info);
		});
		addAlbumToPlaylist.setStyle("-fx-alignment: CENTER;");
		addAlbumToPlaylist.setCellValueFactory((TableColumn.CellDataFeatures<Album, Button> param) -> {
			final Button addButton = new Button("Add to playlist");
			addButton.setCursor(Cursor.HAND);
			addButton.setOnAction((ActionEvent event) -> {
				showPopupSelectPlaylist(param.getValue());
				dispatcher.renderView("UserViews/HomeView/playlistPane", user);
			});
			return new SimpleObjectProperty<Button>(addButton);
		});
	}

	@Override
	public void initializeData(User user) {
		this.search = RunTimeService.getSearch();
		this.user = user;

		this.spacemusicunifyPlayer = RunTimeService.getPlayer();

		try {
			List<Artist> artistList = new ArrayList<>();
			for(Artist artista: artistService.getArtistList()) {
				if(artista.getName().contains(search.strip())) {
					artistList.add(artista);
				}
			}
			ObservableList<Artist> artistData = FXCollections.observableArrayList(artistList);
			artist.setItems(artistData);


			List<Song> songList = new ArrayList<>();

			for(Song song: albumService.getSongList()) {
				if(song.getTitle().contains(search.strip()) || song.getGenre().toString().equals(search)) {
					songList.add(song);
				}
			}

			ObservableList<Song> songData = FXCollections.observableArrayList(songList);
			song.setItems(songData);


			List<Album> albumList = new ArrayList<>();
			for(Album album: albumService.getAlbumList()) {
				if(album.getTitle().contains(search.strip()) || album.getGenre().toString().equals(search)) {
					albumList.add(album);
				}
			}
			ObservableList<Album> albumData = FXCollections.observableArrayList(albumList);
			album.setItems(albumData);

		} catch (BusinessException e) {
			dispatcher.renderError(e);
		}

		song.setRowFactory( tablerow -> {
			TableRow<Song> canzone = new TableRow<>();
			canzone.setOnMouseClicked((MouseEvent event) -> {
				if(canzone.getItem() != null){
					if(event.getClickCount() == 2) {
						PlayerService playerService = SpacemusicunifyBusinessFactory.getInstance().getPlayerService();
						if(spacemusicunifyPlayer.getQueue().size() != 0) {

							if(spacemusicunifyPlayer.getQueue().get(spacemusicunifyPlayer.getCurrentSong()).getId().intValue() != canzone.getItem().getId().intValue()) {
								try {
									playerService.deleteSongFromQueue(spacemusicunifyPlayer, canzone.getItem());	//rimuovo la canzone se già presente in coda
									playerService.replaceCurrentSong(spacemusicunifyPlayer, canzone.getItem());
								} catch (BusinessException e) {
									dispatcher.renderError(e);
								}

								song.refresh();
							} else {
								System.out.println("la canzone è già in riproduzione al momento");
							}
						} else {
							try {
								playerService.addSongToQueue(spacemusicunifyPlayer, canzone.getItem());
							} catch (BusinessException e) {
								dispatcher.renderError(e);
							}

						}

					}
				}
			});
			return canzone;
		});

	}

	public boolean checkForClones(Object object, Song value){

		if(object instanceof SpacemusicunifyPlayer) {
			for (Song songs : ((SpacemusicunifyPlayer) object).getQueue()) {
				if (songs.getId().intValue() ==  value.getId().intValue()) return true;
			}
			return false;
		} else {
			for (Song songs : ((Playlist) object).getSongList()) {
				if (songs.getId().intValue() == value.getId().intValue()) return false;
			}
			return true;
		}
	}

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
			final Button addButton = new Button("ADD");

			addButton.setOnAction((ActionEvent event) -> {
				//aggiunto album alla playlist
				Set<Song> songList = param.getValue().getSongList();
				for(Song albumSong: selectedAlbum.getSongs()) {
					if(checkForClones(param.getValue(), albumSong)) songList.add(albumSong);
				}
				try {
					userService.modify(param.getValue().getId(), param.getValue().getTitle(),songList, param.getValue().getUser(), param.getValue());
				} catch (BusinessException e) {
					 dispatcher.renderError(e);
				}

				addButton.setDisable(true);
			});
			return new SimpleObjectProperty<Button>(addButton);
		});
		
		tableView.getColumns().add(name);
		tableView.getColumns().add(add);

		try {
			ObservableList<Playlist> observableList = FXCollections.observableArrayList(userService.getAllPlaylists(user));
			tableView.setItems(observableList);
		} catch (BusinessException e1) {
			dispatcher.renderError(e1);
		}

		// operazione annulla
		Button closeButton = new Button("Close");
		closeButton.setCursor(Cursor.HAND);
		closeButton.setOnAction(e -> {
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
