package it.univaq.disim.oop.spacemusicunify.controller.administratorcontrollers;

import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

import it.univaq.disim.oop.spacemusicunify.business.*;
import it.univaq.disim.oop.spacemusicunify.controller.DataInitializable;
import it.univaq.disim.oop.spacemusicunify.domain.*;
import it.univaq.disim.oop.spacemusicunify.view.ViewDispatcher;
import it.univaq.disim.oop.spacemusicunify.view.ViewSituations;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Cursor;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;

import javax.swing.*;

public class ManageAlbumDetailController implements Initializable, DataInitializable<Production> {
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
	private TableColumn<Song, String> artistsModify;
	@FXML
	private TableColumn<Song, Button> detailSongModify;
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
	private TableColumn<Song, String> artists;
	@FXML
	private TableColumn<Song, Button> detailSong;
	private Artist artista;

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

	private String imageUrl;
	@FXML
	private Button newSong;
	@FXML
	private Button cancel1;

	@FXML
	private Button deletealbum;
	@FXML
	private Label existingLabel;
	private Album album;
	private Picture tempPicture;
	private Administrator admin;
	private Production albumProduction;

	List<Object> objects = new ArrayList<>();
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
				id.setCellValueFactory(new PropertyValueFactory<>("id"));
				title.setCellValueFactory(new PropertyValueFactory<>("title"));
				length.setCellValueFactory(new PropertyValueFactory<>("length"));

				artists.setCellValueFactory((TableColumn.CellDataFeatures<Song, String> param) -> new SimpleStringProperty(/*album.getArtist().getStageName()*/ ));
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
						objects.add(param.getValue());
						dispatcher.renderView("AdministratorViews/ManageArtistsView/ManageAlbumsView/ManageSongsView/song_detail", objects);
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
				cancelBox.setVisible(false);

				genreField.getItems().addAll(Genre.values());
				genreField.getItems().remove(Genre.singoli);

				idModify.setCellValueFactory(new PropertyValueFactory<>("id"));
				titleModify.setCellValueFactory(new PropertyValueFactory<>("title"));
				lengthModify.setCellValueFactory(new PropertyValueFactory<>("length"));

				artistsModify.setCellValueFactory((TableColumn.CellDataFeatures<Song, String> param) -> new SimpleStringProperty(/*album.getArtist().getStageName()*/));

				managesong.setStyle("-fx-alignment: CENTER;");
				managesong.setCellValueFactory((TableColumn.CellDataFeatures<Song, Button> param) -> {
					final Button deletesong = new Button("Delete");
					deletesong.setCursor(Cursor.HAND);
					if(album.getSongList().size() == 1){
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
							dispatcher.renderView("AdministratorViews/ManageArtistsView/ManageAlbumsView/album_modify", albumProduction);

						} catch (BusinessException e) {
							dispatcher.renderError(e);
						}
					});
					return new SimpleObjectProperty<Button>(deletesong);
				});
				detailSongModify.setStyle("-fx-alignment: CENTER;");
				detailSongModify.setCellValueFactory((TableColumn.CellDataFeatures<Song, Button> param) -> {
					final Button modify = new Button("Detail");
					modify.setCursor(Cursor.HAND);
					modify.setOnAction((ActionEvent event) -> {
						dispatcher.setSituation(ViewSituations.detail);
						objects.add(param.getValue());
						dispatcher.renderView("AdministratorViews/ManageArtistsView/ManageAlbumsView/ManageSongsView/song_detail", objects);
					});
					return new SimpleObjectProperty<Button>(modify);
				});

				if(this.album.getGenre() == Genre.singoli){
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
				if(album.getGenre() == Genre.singoli){
					genreField.setDisable(true);

				}else {
					if (album.getId() == null) {
						newSong.setVisible(false);

						confirm.setText("Create Album");
						modifyalbumsongs.setVisible(false);
					} else {

						confirm.setText("Modify Album");
					}
				}
				titleView.setText("Modify Album");
				modifyalbumsongs.setItems(songData);
				break;

			case newobject:
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
					artists.removeIf((Artist artist) -> artist.getId().intValue() == artista.getId().intValue());
					ObservableList<Artist> artistsData = FXCollections.observableArrayList(artists);
					artistTable.getItems().addAll(artistsData);
				} catch (BusinessException e) {
					throw new RuntimeException(e);
				}

				newSong.setVisible(false);
				deletealbum.setVisible(false);
				genreField.getItems().addAll(Genre.values());
				genreField.getItems().remove(Genre.singoli);
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
				artistSet.add(artista);
				coverField.getChildren().add(imgsv);

				titleView.setText("New Album");
				confirm.setText("Create");

				break;
			
			default:
				break;
		}
	}
	@Override
	public void initialize(URL location, ResourceBundle resources) {
/*		confirm.disableProperty().bind(titleField.textProperty().isEmpty().or(existingLabel.visibleProperty()));
		titleField.textProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				existingLabel.setVisible(false);
			}
		});*/


	}
	@Override
	public void initializeData(Production production) {
		objects.add(production);
		albumProduction = production;
		album = production.getAlbum();
		artista = production.getArtist();


		Set<Song> songs = album.getSongList();
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
				albumService.setChoosenArtists(artistSet);
				albumService.add(album);

			} else {
				albumService.modify(album.getId(), titleField.getText(), genreField.getValue(), releaseField.getValue(), tempPicture, album.getSongList());
			}
			dispatcher.renderView("AdministratorViews/ManageArtistsView/ManageAlbumsView/manage_albums", artistService.findAllProductions(artista));

		} catch (AlreadyTakenFieldException e){
			existingLabel.setText("This album title is already taken");
			existingLabel.setVisible(true);

		}catch (AlreadyExistingException e){
			existingLabel.setText("This album already exists");
			existingLabel.setVisible(true);

		} catch (BusinessException e) {
			dispatcher.renderError(e);
		}
	}
	@FXML
	public void cancelModify(ActionEvent event) {
		switch (dispatcher.getSituation()){
			case newobject:
				try {
					dispatcher.renderView("AdministratorViews/ManageArtistsView/ManageAlbumsView/manage_albums", artistService.findAllProductions(artista));
				} catch (BusinessException e) {
					throw new RuntimeException(e);
				}
				break;
			default:
				dispatcher.setSituation(ViewSituations.detail);
				dispatcher.renderView("AdministratorViews/ManageArtistsView/ManageAlbumsView/album_detail", albumProduction);
		}

	}
	@FXML
	public void backToTheArtist(ActionEvent event) {
		dispatcher.setSituation(ViewSituations.detail);
		dispatcher.renderView("AdministratorViews/ManageArtistsView/artist_detail", artista);
	}
	public void focusImage(String image){
		imageUrl = image;

		cancelBox.setVisible(true);
	}
	@FXML
	public void cancelDeleteSelection(ActionEvent event){
		this.imageUrl = "";
		cancelBox.setVisible(false);

	}
	public void focusAdd(){
		cancelBox.setVisible(false);
		FileChooser fileChoose = new FileChooser();
		File file =  fileChoose.showOpenDialog(null);

		if(file != null){
			String path = file.getPath();
			if(path.endsWith(".png") || path.endsWith(".jpg")){
				existingLabel.setVisible(false);

				tempPicture = new Picture();

				tempPicture.setData(path);
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
		String tempimg = String.valueOf(album.getCover().getId());


		if(this.imageUrl.equals(tempimg)){
			ImageView imgAdd = null;
			try {
				imgAdd = new ImageView(new Image(Files.newInputStream(Paths.get("src" + File.separator + "main" + File.separator + "resources" + File.separator + "dati" + File.separator + "RAMfiles" + File.separator + "addp.png"))));
			} catch (IOException e) {
				throw new RuntimeException(e);
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

	}
	@FXML
	public void createNewSong() {
		Song canzone = new Song();
		canzone.setAlbum(this.album);
		canzone.setTitle("Nuova canzone");
		canzone.setLyrics("add Lyric");
		canzone.setLength("00:00");
		if(album.getGenre() == Genre.singoli) {
			canzone.setGenre(Genre.pop);
		}else{
			canzone.setGenre(album.getGenre());
		}
		dispatcher.setSituation(ViewSituations.newobject);
		objects.add(canzone);
        dispatcher.renderView("AdministratorViews/ManageArtistsView/ManageAlbumsView/ManageSongsView/song_modify", objects);

	}
	@FXML
	public void deleteThisAlbum(ActionEvent event) {
		try{
			if (album.getId() != null) {
				albumService.delete(album);
			}else{
				System.out.println("Album not found");
			}
			dispatcher.renderView("AdministratorViews/ManageArtistsView/ManageAlbumsView/manage_albums", artistService.findAllProductions(artista));

		} catch (BusinessException e) {
			dispatcher.renderError(e);
		}
	}


	
	@FXML
	public void showModify(ActionEvent event) {
		dispatcher.setSituation(ViewSituations.modify);
		dispatcher.renderView("AdministratorViews/ManageArtistsView/ManageAlbumsView/album_modify", albumProduction);
	}

}
