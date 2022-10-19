package it.univaq.disim.oop.spacemusicunify.controller.administratorcontrollers;

import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

import it.univaq.disim.oop.spacemusicunify.business.*;
import it.univaq.disim.oop.spacemusicunify.controller.DataInitializable;
import it.univaq.disim.oop.spacemusicunify.domain.*;
import it.univaq.disim.oop.spacemusicunify.view.SpacemusicunifyPlayer;
import it.univaq.disim.oop.spacemusicunify.view.ViewDispatcher;
import it.univaq.disim.oop.spacemusicunify.view.ViewSituations;
import javafx.beans.property.SimpleObjectProperty;

import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;
import javafx.scene.media.MediaPlayer;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class ManageAlbumDetailController implements Initializable, DataInitializable<Album> {
	private final ArtistService artistService;
	private final AlbumService albumService;
	private final ViewDispatcher dispatcher;

	@FXML
	private Button confirm;
	@FXML
	private Button modify;
	//tabella a sinistra
	@FXML
	private TableView<Song> modifyalbumsongs;
	@FXML
	private TableColumn<Song, String> idModify;

	@FXML
	private TableColumn<Song, String> titleModify;
	@FXML
	private TableColumn<Song, String> lengthModify;
	@FXML
	private TableColumn<Song, Genre> genreModify;
	@FXML
	private TableColumn<Song, Button> managesong;
//tabella a destra
	@FXML
	private TableView<Song> albumsongs;
	@FXML
	private TableColumn<Song, String> id;
	@FXML
	private TableColumn<Song, String> title;
	@FXML
	private TableColumn<Song, String> length;
	@FXML
	private TableColumn<Song, Genre> genretab;
	@FXML
	private TableColumn<Song, Button> detailSong;
	@FXML
	private TableColumn<Song, Button> addSongToQueue;
	@FXML
	private TextField titleField;
	@FXML
	private ComboBox<Genre> genreField;
	@FXML
	private DatePicker releaseField;
	@FXML
	private VBox coverField;
	@FXML
	private Label titleAlbum;
	@FXML
	private Label genreAlbum;
	@FXML
	private Label releaseAlbum;
	@FXML
	private VBox coverAlbum;
	@FXML
	private VBox cancelBox;
	@FXML
	private Button addToPlaylist;
	@FXML
	private Button newSong;
	@FXML
	private MenuButton artistS;
	@FXML
	private Button cancel;
	@FXML
	private Button addToQueue;
	@FXML
	private Button deletealbum;
	@FXML
	private Label existingLabel;
	
	private String imageUrl;
	private Artist artist;
	private Album album;
	private Picture tempPicture;

	private Set<Artist> artistSet = new HashSet<>();
	@FXML
	private Label titleView;
	@FXML
	private Label table_label;
	@FXML
	private TableView<Artist> artistTable;
	@FXML
	private TableColumn<Artist, Integer> artist_id;
	@FXML
	private TableColumn<Artist, String> artist_name;
	@FXML
	private TableColumn<Artist, Button> artist_add;

	public ManageAlbumDetailController(){
		dispatcher = ViewDispatcher.getInstance();
		SpacemusicunifyBusinessFactory factory = SpacemusicunifyBusinessFactory.getInstance();
		artistService = factory.getArtistService();
		albumService = factory.getAlbumService();
	}
	private void setView2(ObservableList<Song> songData) {
		switch (dispatcher.getSituation()){
			case detail:
				addToPlaylist.setVisible(false);
				addToQueue.setVisible(false);
				addSongToQueue.setVisible(false);
				id.setCellValueFactory(new PropertyValueFactory<>("id"));
				title.setCellValueFactory(new PropertyValueFactory<>("title"));
				length.setCellValueFactory(new PropertyValueFactory<>("length"));
				genretab.setCellValueFactory(new PropertyValueFactory<>("genre"));
				try {
					Set<Artist> artistsSet = albumService.findAllArtists(album);
					artistS.setVisible(true);
					for (Artist artistCtrl : artistsSet) {
						MenuItem menuItem = new MenuItem();
						menuItem.setText(artistCtrl.getName());
						menuItem.setOnAction((ActionEvent event) -> {
							artistS.hide();
							dispatcher.renderView("AdministratorViews/ManageArtistsView/artist_detail", artistCtrl);
						});
						artistS.getItems().add(menuItem);
					}
				} catch (BusinessException e) {
					dispatcher.renderError(e);
				}

				detailSong.setStyle("-fx-alignment: CENTER;");
				detailSong.setCellValueFactory((TableColumn.CellDataFeatures<Song, Button> param) -> {
					final Button modify = new Button("Detail");
					modify.setCursor(Cursor.HAND);
					modify.setOnAction((ActionEvent event) -> {
						dispatcher.renderView("AdministratorViews/ManageArtistsView/ManageAlbumsView/ManageSongsView/song_detail", param.getValue());
					});
					return new SimpleObjectProperty<Button>(modify);
				});

				titleAlbum.setText(album.getTitle());
				genreAlbum.setText(String.valueOf(album.getGenre()));
				releaseAlbum.setText(String.valueOf(album.getRelease()));

				Image img = new Image(new ByteArrayInputStream(album.getCover().getData()));

				ImageView imgview1 = new ImageView(img);
				imgview1.setFitHeight(album.getCover().getHeight());
				imgview1.setFitWidth(album.getCover().getWidth());

				coverAlbum.getChildren().add(imgview1);
				albumsongs.setItems(songData);
				break;

			case modify:
				confirm.disableProperty().bind(titleField.textProperty().isEmpty().or(existingLabel.visibleProperty()));
				titleField.textProperty().addListener((obs, oldText, newText)-> {
					if (existingLabel.isVisible()) {
						existingLabel.setVisible(false);
					}
				});
				cancelBox.setVisible(false);

				genreField.getItems().addAll(Genre.values());
				genreField.getItems().remove(Genre.singles);

				idModify.setCellValueFactory(new PropertyValueFactory<>("id"));
				titleModify.setCellValueFactory(new PropertyValueFactory<>("title"));
				lengthModify.setCellValueFactory(new PropertyValueFactory<>("length"));
				genreModify.setCellValueFactory(new PropertyValueFactory<>("genre"));

				managesong.setStyle("-fx-alignment: CENTER;");
				managesong.setCellValueFactory((TableColumn.CellDataFeatures<Song, Button> param) -> {
					final Button deletesong = new Button("Delete");
					deletesong.setCursor(Cursor.HAND);
					if(album.getSongs().size() == 1){
						deletesong.setDisable(true);
					}
					deletesong.setOnAction((ActionEvent event) -> {
						try{
							if (param.getValue().getId() != null ) {
								albumService.delete(param.getValue());
							}else{
								System.out.println("Song not found");
							}
							dispatcher.setSituation(ViewSituations.modify);
							dispatcher.renderView("AdministratorViews/ManageArtistsView/ManageAlbumsView/album_modify", album);

						} catch (BusinessException e) {
							dispatcher.renderError(e);
						}
					});
					return new SimpleObjectProperty<Button>(deletesong);
				});


				if(this.album.getGenre() == Genre.singles){
					genreField.setDisable(true);
					deletealbum.setDisable(true);
					titleField.setDisable(true);
					releaseField.setDisable(true);
				}



				titleField.setText(album.getTitle());
				genreField.setValue(album.getGenre());
				releaseField.setValue(album.getRelease());

				Image imgM = new Image(new ByteArrayInputStream(album.getCover().getData()));
				ImageView imgview2 = new ImageView(imgM);
				imgview2.setFitHeight(album.getCover().getHeight());
				imgview2.setFitWidth(album.getCover().getWidth());
				imgview2.setCursor(Cursor.HAND);
				imgview2.setOnMouseClicked(event -> {
					this.focusImage(String.valueOf(album.getCover().getId()));
				});

				coverField.getChildren().add(imgview2);

				confirm.setText("Modify Album");
				cancel.setText("Back");


				titleView.setText("Modify Album");
				modifyalbumsongs.setItems(songData);
				break;

			case newobject:
				confirm.disableProperty().bind(titleField.textProperty().isEmpty().or(existingLabel.visibleProperty()));
				titleField.textProperty().addListener((obs, oldText, newText)-> {
					if (existingLabel.isVisible()) {
						existingLabel.setVisible(false);
					}
				});
				modifyalbumsongs.setVisible(false);
				table_label.setVisible(false);
				cancelBox.setVisible(false);
				artistTable.setVisible(true);

				artist_id.setCellValueFactory(new PropertyValueFactory<>("id"));
				artist_name.setCellValueFactory(new PropertyValueFactory<>("name"));
				artist_add.setCellValueFactory((TableColumn.CellDataFeatures<Artist, Button> param) -> {
					final Button add = new Button("Add");
					add.setCursor(Cursor.HAND);
					add.setOnAction((ActionEvent event) -> {
						artistSet.add(param.getValue());
						add.setDisable(true);
					});
					return new SimpleObjectProperty<Button>(add);
				});

				try {
					Set<Artist> artists = artistService.getArtistList();
					artists.removeIf((Artist artist2) -> artist2.getId().intValue() == artist.getId().intValue());
					ObservableList<Artist> artistsData = FXCollections.observableArrayList(artists);
					artistTable.getItems().addAll(artistsData);
				} catch (BusinessException e) {
					dispatcher.renderError(e);
				}

				newSong.setVisible(false);
				deletealbum.setVisible(false);
				genreField.getItems().addAll(Genre.values());
				genreField.getItems().remove(Genre.singles);
				titleField.setText(album.getTitle());
				genreField.setValue(album.getGenre());
				releaseField.setValue(album.getRelease());
				Image imgs = new Image(new ByteArrayInputStream(album.getCover().getData()));
				ImageView imgsv = new ImageView(imgs);
				imgsv.setFitHeight(album.getCover().getHeight());
				imgsv.setFitWidth(album.getCover().getWidth());
				imgsv.setCursor(Cursor.HAND);
				imgsv.setOnMouseClicked(event -> {
					this.focusImage(String.valueOf(album.getCover().getId()));
				});
				coverField.getChildren().add(imgsv);

				titleView.setText("New Album");
				confirm.setText("Create");

				break;
				
			case user:
				addToPlaylist.setVisible(true);
				addToQueue.setVisible(true);
				addSongToQueue.setVisible(true);
				id.setCellValueFactory(new PropertyValueFactory<>("id"));
				title.setCellValueFactory(new PropertyValueFactory<>("title"));
				length.setCellValueFactory(new PropertyValueFactory<>("length"));
				genretab.setCellValueFactory(new PropertyValueFactory<>("genre"));
				try {
					Set<Artist> artistsSet = albumService.findAllArtists(album);
					artistS.setVisible(true);
					for (Artist artistCtrl : artistsSet) {
						MenuItem menuItem = new MenuItem();
						menuItem.setText(artistCtrl.getName());
						menuItem.setOnAction((ActionEvent event) -> {
							artistS.hide();
							/*dispatcher.setSituation(ViewSituations.user);*/
							dispatcher.renderView("AdministratorViews/ManageArtistsView/artist_detail", artistCtrl);
						});
						artistS.getItems().add(menuItem);
					}
				} catch (BusinessException e) {
					dispatcher.renderError(e);
				}

				detailSong.setStyle("-fx-alignment: CENTER;");
				detailSong.setCellValueFactory((TableColumn.CellDataFeatures<Song, Button> param) -> {
					final Button modify = new Button("Detail");
					modify.setCursor(Cursor.HAND);
					modify.setOnAction((ActionEvent event) -> {
						if(dispatcher.getSituation() == ViewSituations.user){
							dispatcher.setSituation(ViewSituations.user);
						}else{
							dispatcher.setSituation(ViewSituations.detail);
						}
						dispatcher.renderView("AdministratorViews/ManageArtistsView/ManageAlbumsView/ManageSongsView/song_detail", param.getValue());
					});
					return new SimpleObjectProperty<Button>(modify);
				});

				addSongToQueue.setStyle("-fx-alignment: CENTER;");
				addSongToQueue.setCellValueFactory((TableColumn.CellDataFeatures<Song, Button> param) -> {
					final Button addButton = new Button("Add to queue");
					addButton.setCursor(Cursor.HAND);
					addButton.setOnAction((ActionEvent event) -> {
						//aggiungere la canzone alla coda di riproduzione dell'utente
						try {
							if(!(checkForClones(RunTimeService.getPlayer(), param.getValue()))) SpacemusicunifyBusinessFactory.getInstance().getPlayerService().addSongToQueue(RunTimeService.getPlayer(), param.getValue());
							addButton.setDisable(true);
						} catch (BusinessException b) {
							dispatcher.renderError(b);
						}
					});

					return new SimpleObjectProperty<Button>(addButton);
				});
				
				titleAlbum.setText(album.getTitle());
				genreAlbum.setText(String.valueOf(album.getGenre()));
				releaseAlbum.setText(String.valueOf(album.getRelease()));

				Image img2 = new Image(new ByteArrayInputStream(album.getCover().getData()));

				ImageView imgview3 = new ImageView(img2);
				imgview3.setFitHeight(album.getCover().getHeight());
				imgview3.setFitWidth(album.getCover().getWidth());

				coverAlbum.getChildren().add(imgview3);
				albumsongs.setItems(songData);
				modify.setVisible(false);
				break;
			
			default:
				break;
		}
	}
	@Override
	public void initialize(URL location, ResourceBundle resources) {

	}
	@Override
	public void initializeData(Album album) {
		this.album = album;
		if(albumService.getChosenArtists() != null) {
			artist = albumService.getChosenArtists().iterator().next();
		} else {
			artist = null;
		}
		Set<Song> songs = album.getSongs();
		ObservableList<Song> songData = FXCollections.observableArrayList(songs);
		setView2(songData);

	}



	@FXML
	public void confirmAlbum(ActionEvent event) {

		try {
			if (album.getId() == null) {
				album.setTitle(titleField.getText());
				album.setGenre(genreField.getValue());
				album.setRelease(releaseField.getValue());
				if(tempPicture != null ) album.setCover(tempPicture);
				artistSet.add(artist);
				albumService.setChosenArtists(artistSet);
				albumService.add(album);

			} else {
				albumService.modify(album.getId(), titleField.getText(), genreField.getValue(), tempPicture, album.getSongs(), releaseField.getValue(), album);
			}
			dispatcher.renderView("AdministratorViews/ManageArtistsView/ManageAlbumsView/manage_albums", artist);

		}catch (AlreadyExistingException e){
			/*existingLabel.setText("This album already exists");*/
			existingLabel.setText(e.getMessage());
			existingLabel.setVisible(true);

		} catch (BusinessException e) {
			dispatcher.renderError(e);
		}
	}
	@FXML
	public void cancelModify(ActionEvent event) {
		switch (dispatcher.getSituation()){
			case newobject:
				dispatcher.renderView("AdministratorViews/ManageArtistsView/ManageAlbumsView/manage_albums", artist);
				break;
			default:
				dispatcher.setSituation(ViewSituations.detail);
				dispatcher.renderView("AdministratorViews/ManageArtistsView/ManageAlbumsView/album_detail", album);
		}

	}
	@FXML
	public void backToTheArtist(ActionEvent event) {
		//dispatcher.setSituation(ViewSituations.detail);
		dispatcher.renderView("AdministratorViews/ManageArtistsView/artist_detail", artist);
	}
	private void focusImage(String image){
		imageUrl = image;

		cancelBox.setVisible(true);
	}
	@FXML
	public void cancelDeleteSelection(ActionEvent event){
		this.imageUrl = "";
		cancelBox.setVisible(false);

	}
	private void focusAdd(){
		cancelBox.setVisible(false);
		FileChooser fileChoose = new FileChooser();
		File file =  fileChoose.showOpenDialog(null);

		if(file != null){
			String path = file.getPath();
			if(path.endsWith(".png") || path.endsWith(".jpg")){
				existingLabel.setVisible(false);

				tempPicture = new Picture();

				try {
					tempPicture.setData(path);
				} catch (BusinessException e) {
					dispatcher.renderError(e);
				}
				tempPicture.setHeight(140);
				tempPicture.setWidth(140);
				tempPicture.setOwnership(album);

				ImageView imgview2 = new ImageView(new Image(new ByteArrayInputStream(tempPicture.getData())));
				imgview2.setFitHeight(tempPicture.getHeight());
				imgview2.setFitWidth(tempPicture.getWidth());
				imgview2.setCursor(Cursor.HAND);
				imgview2.setOnMouseClicked(event -> {
					this.focusImage(path);
				});

				coverField.getChildren().clear();
				coverField.getChildren().add(imgview2);
				existingLabel.setVisible(false);
			}else{
				existingLabel.setText("Wrong image File type, Only .png or .jpg are allowed");
				existingLabel.setVisible(true);
			}


		}else {
			existingLabel.setText("Please choose an image to continue");
			existingLabel.setVisible(true);
		}
	}
	@FXML
	public void deleteCover(ActionEvent event){
		ImageView imgAdd = null;
		try {
			imgAdd = new ImageView(new Image(Files.newInputStream(Paths.get("src" + File.separator + "main" + File.separator + "resources" + File.separator + "data" + File.separator + "RAMfiles" + File.separator + "addp.png"))));
		} catch (IOException e) {
			dispatcher.renderError(e);
		}
		imgAdd.setFitHeight(140);
		imgAdd.setFitWidth(140);
		imgAdd.setOnMouseClicked(event2 -> {
			this.focusAdd();
		});

		coverField.getChildren().clear();

		coverField.getChildren().add(imgAdd);

		cancelBox.setVisible(false);

	}
	@FXML
	public void createNewSong() {
		Song song = new Song();
		song.setAlbum(this.album);
		song.setTitle("Nuova song");
		song.setLyrics("add Lyric");
		song.setLength("00:00");
		if(album.getGenre() == Genre.singles) {
			song.setGenre(Genre.pop);
		}else{
			song.setGenre(album.getGenre());
		}
		dispatcher.setSituation(ViewSituations.newobject);
        dispatcher.renderView("AdministratorViews/ManageArtistsView/ManageAlbumsView/ManageSongsView/song_modify", song);

	}
	@FXML
	public void deleteThisAlbum(ActionEvent event) {
		try{
			if (album.getId() != null) {
				albumService.delete(album);
			}else{
				System.out.println("Album not found");
			}
			dispatcher.renderView("AdministratorViews/ManageArtistsView/ManageAlbumsView/manage_albums", artistService.findAllProductions(artist));

		} catch (BusinessException e) {
			dispatcher.renderError(e);
		}
	}

	@FXML
	public void addAlbumToQueue(ActionEvent event) {
		//aggiungere la canzone alla coda di riproduzione dell'utente
		User user = RunTimeService.getCurrentUser();
		PlayerService playerService = SpacemusicunifyBusinessFactory.getInstance().getPlayerService();
		for(Song albumSong: album.getSongs()) {
			if(!(checkForClones(RunTimeService.getPlayer(), albumSong))) {
				try {
					playerService.addSongToQueue(RunTimeService.getPlayer(), albumSong);
				} catch (BusinessException e) {
					dispatcher.renderError(e);
				}
			}
		}


		SpacemusicunifyPlayer spacemusicunifyPlayer = RunTimeService.getPlayer();
			if(spacemusicunifyPlayer.getMediaPlayer() != null && spacemusicunifyPlayer.getMediaPlayer().getStatus() != MediaPlayer.Status.STOPPED){
				spacemusicunifyPlayer.getMediaPlayer().stop();
				spacemusicunifyPlayer.getMediaPlayer().dispose();
			}


		playerService.setPlayerState(PlayerState.searchSingleClick);
		dispatcher.renderView("UserViews/HomeView/playerPane", user);
	}
	@FXML
	public void addAlbumToPlaylist(ActionEvent event) {
		User user = RunTimeService.getCurrentUser();
		SpacemusicunifyBusinessFactory factory = SpacemusicunifyBusinessFactory.getInstance();
		UserService userService = factory.getUserService();
		
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
			addButton.setOnAction((actionEvent) -> {
				//aggiunto album alla playlist
				Set<Song> songList = param.getValue().getSongList();
				for(Song albumSong: album.getSongs()) {
					if(checkForClones(param.getValue(), albumSong)) songList.add(albumSong);
				}
				try {
					userService.modify(param.getValue().getId(), param.getValue().getTitle(),songList, param.getValue().getUser(), param.getValue());
					dispatcher.renderView("UserViews/HomeView/playlistPane", param.getValue().getUser());
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
			e1.printStackTrace();
		}

		// operazione annulla
		Button closeButton = new Button("Cancel");
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
		popupwindow.setTitle("Add " + album.getTitle() + " to playlist?");
		popupwindow.showAndWait();
	}
	private boolean checkForClones(Object object, Song value){

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
	@FXML
	public void showModify(ActionEvent event) {
		dispatcher.setSituation(ViewSituations.modify);
		dispatcher.renderView("AdministratorViews/ManageArtistsView/ManageAlbumsView/album_modify", album);
	}

}
