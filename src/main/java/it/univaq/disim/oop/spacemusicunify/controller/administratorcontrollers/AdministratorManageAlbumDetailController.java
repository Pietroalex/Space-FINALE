package it.univaq.disim.oop.spacemusicunify.controller.administratorcontrollers;

import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.ResourceBundle;

import it.univaq.disim.oop.spacemusicunify.business.*;
import it.univaq.disim.oop.spacemusicunify.controller.DataInitializable;
import it.univaq.disim.oop.spacemusicunify.domain.*;
import it.univaq.disim.oop.spacemusicunify.view.ViewDispatcher;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
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

import javax.imageio.ImageIO;

public class AdministratorManageAlbumDetailController implements Initializable, DataInitializable<Album> {
	private final SPACEMusicUnifyService spaceMusicUnifyService;
	private final ViewDispatcher dispatcher;
	@FXML
	private AnchorPane masterPane;
	@FXML
	private AnchorPane modifyPane;
	@FXML
	private AnchorPane infoPane;
	@FXML
	private Button confirm;
	@FXML
	private Button modify;
	//tabella a sinistra
	@FXML
	private TableView<Canzone> modifyalbumsongs;
	@FXML
	private TableColumn<Canzone, String> idModify;

	@FXML
	private TableColumn<Canzone, String> titleModify;
	@FXML
	private TableColumn<Canzone, String> lengthModify;

	@FXML
	private TableColumn<Canzone, String> artistsModify;
	@FXML
	private TableColumn<Canzone, Button> detailSongModify;
	@FXML
	private TableColumn<Canzone, Button> managesong;
//tabella a destra
	@FXML
	private TableView<Canzone> albumsongs;
	@FXML
	private TableColumn<Canzone, String> id;

	@FXML
	private TableColumn<Canzone, String> title;
	@FXML
	private TableColumn<Canzone, String> length;

	@FXML
	private TableColumn<Canzone, String> artists;
	@FXML
	private TableColumn<Canzone, Button> detailSong;
	private Artista artista;

	@FXML
	private TextField titleField;
	@FXML
	private ComboBox<Genere> genreField;
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
	private Album album;

	private Amministratore admin;
	private String imageUrl;
	@FXML
	private Button newSong;
	@FXML
	private Button cancel1;

	@FXML
	private Button deletealbum;
	@FXML
	private Label existingLabel;

	public AdministratorManageAlbumDetailController(){
		dispatcher = ViewDispatcher.getInstance();

		SpacemusicunifyBusinessFactory factory = SpacemusicunifyBusinessFactory.getInstance();
		spaceMusicUnifyService = factory.getSPACEMusicUnifyService();

	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		confirm.disableProperty().bind(titleField.textProperty().isEmpty().or(existingLabel.visibleProperty()));
		titleField.textProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				existingLabel.setVisible(false);
			}
		});

		cancelBox.setVisible(false);

		genreField.getItems().addAll(Genere.values());
		genreField.getItems().remove(Genere.singoli);


		//tabella a sinistra
		idModify.setCellValueFactory(new PropertyValueFactory<>("id"));
		titleModify.setCellValueFactory(new PropertyValueFactory<>("title"));
		lengthModify.setCellValueFactory(new PropertyValueFactory<>("length"));

		artistsModify.setCellValueFactory((TableColumn.CellDataFeatures<Canzone, String> param) -> {


			return new SimpleStringProperty(album.getArtist().getStageName());
		});

		managesong.setStyle("-fx-alignment: CENTER;");
		managesong.setCellValueFactory((TableColumn.CellDataFeatures<Canzone, Button> param) -> {
			final Button deletesong = new Button("Delete");
			deletesong.setCursor(Cursor.HAND);
			if(album.getSongList().size() == 1){
				deletesong.setDisable(true);
			}
			deletesong.setOnAction((ActionEvent event) -> {
				try{
					if (param.getValue().getId() != null ) {
						spaceMusicUnifyService.delete(param.getValue());
					}else{
						System.out.println("Song not found");
					}
					spaceMusicUnifyService.setSituation(ViewSituations.modify);
					dispatcher.renderView("AdministratorViews/ManageArtistsView/ManageAlbumsView/album_detail", album);

				} catch (BusinessException e) {
					dispatcher.renderError(e);
				}
			});
			return new SimpleObjectProperty<Button>(deletesong);
		});
		detailSongModify.setStyle("-fx-alignment: CENTER;");
		detailSongModify.setCellValueFactory((TableColumn.CellDataFeatures<Canzone, Button> param) -> {
			final Button modify = new Button("Detail");
			modify.setCursor(Cursor.HAND);
			modify.setOnAction((ActionEvent event) -> {
				spaceMusicUnifyService.setSituation(ViewSituations.detail);
				dispatcher.renderView("AdministratorViews/ManageArtistsView/ManageAlbumsView/song_detail", param.getValue());
			});
			return new SimpleObjectProperty<Button>(modify);
		});

		//tabella a destra
		id.setCellValueFactory(new PropertyValueFactory<>("id"));
		title.setCellValueFactory(new PropertyValueFactory<>("title"));
		length.setCellValueFactory(new PropertyValueFactory<>("length"));

		artists.setCellValueFactory((TableColumn.CellDataFeatures<Canzone, String> param) -> {

			return new SimpleStringProperty(album.getArtist().getStageName() );
		});
		detailSong.setStyle("-fx-alignment: CENTER;");
		detailSong.setCellValueFactory((TableColumn.CellDataFeatures<Canzone, Button> param) -> {
			final Button modify = new Button("Detail");
			modify.setCursor(Cursor.HAND);
			modify.setOnAction((ActionEvent event) -> {
				if(spaceMusicUnifyService.getSituation() == ViewSituations.user){
					spaceMusicUnifyService.setSituation(ViewSituations.user);
				}else{
					spaceMusicUnifyService.setSituation(ViewSituations.detail);
				}

				dispatcher.renderView("AdministratorViews/ManageArtistsView/ManageAlbumsView/song_detail", param.getValue());
			});
			return new SimpleObjectProperty<Button>(modify);
		});

	}
	@Override
	public void initializeData(Album album) {
		this.album = album;
		this.artista = album.getArtist();

		if(this.album.getGenre() == Genere.singoli){
			deletealbum.setDisable(true);
			titleField.setDisable(true);
			releaseField.setDisable(true);
		}


		titleAlbum.setText(album.getTitle());
		genreAlbum.setText(String.valueOf(album.getGenre()));
		releaseAlbum.setText(String.valueOf(album.getRelease()));

		Image img = new Image(new ByteArrayInputStream(album.getCover().getPhoto()));

		ImageView imgview1 = new ImageView(img);
		imgview1.setFitHeight(150);
		imgview1.setFitWidth(150);

		coverAlbum.getChildren().add(imgview1);

		titleField.setText(album.getTitle());
		genreField.setValue(album.getGenre());
		releaseField.setValue(album.getRelease());


		ImageView imgview2 = new ImageView(img);
		imgview2.setFitHeight(150);
		imgview2.setFitWidth(150);
		imgview2.setCursor(Cursor.HAND);
		imgview2.setOnMouseClicked(event -> {
			this.focusImage(String.valueOf(album.getCover().getId()));
		});

		coverField.getChildren().add(imgview2);

		this.setView();

		List<Canzone> songs = album.getSongList();
		ObservableList<Canzone> songData = FXCollections.observableArrayList(songs);
		modifyalbumsongs.setItems(songData);

		albumsongs.setItems(songData);


		if(album.getGenre() == Genere.singoli){
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
	}
	@FXML
	public void confirmAlbum(ActionEvent event) {
		System.out.println("inizio modifica album");
		try {
			if (album.getId() == null) {
				album.setTitle(titleField.getText());
				album.setGenre(genreField.getValue());
				album.setRelease(releaseField.getValue());

				spaceMusicUnifyService.add(album);

			} else {
				spaceMusicUnifyService.modify(album.getId(), titleField.getText(), genreField.getValue(), releaseField.getValue(), album.getCover(), album.getSongList());
			}
			dispatcher.renderView("AdministratorViews/ManageArtistsView/ManageAlbumsView/manage_albums", artista.getDiscography());

		} catch (AlreadyTakenFieldException e){
			existingLabel.setText("This album title is already taken");
			existingLabel.setVisible(true);
			System.out.println("eccezione1");
		}catch (AlreadyExistingException e){
			existingLabel.setText("This album already exists");
			existingLabel.setVisible(true);
			System.out.println("eccezione2");
		} catch (BusinessException e) {
			dispatcher.renderError(e);
		}
	}
	@FXML
	public void cancelModify(ActionEvent event) {

		dispatcher.renderView("AdministratorViews/ManageArtistsView/ManageAlbumsView/manage_albums", artista.getDiscography());
	}
	@FXML
	public void backToTheArtist(ActionEvent event) {
		spaceMusicUnifyService.setSituation(ViewSituations.detail);
		dispatcher.renderView("AdministratorViews/ManageArtistsView/artist_detail", artista);
	}
	public void focusImage(String image){
		imageUrl = image;
		System.out.println(imageUrl);
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



				try {
				Picture picture = new Picture();
				ByteArrayOutputStream outStreamObj = new ByteArrayOutputStream();
				outStreamObj.writeBytes(Files.readAllBytes(Paths.get(path)));
				picture.setPhoto(outStreamObj.toByteArray());
				album.setCover(picture);

			} catch (IOException e) {
				throw new RuntimeException(e);
			}
				ImageView imgview2 = new ImageView(new Image(new ByteArrayInputStream(album.getCover().getPhoto())));
				imgview2.setFitHeight(150);
				imgview2.setFitWidth(150);
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
			imgAdd.setFitHeight(150);
			imgAdd.setFitWidth(150);
			imgAdd.setOnMouseClicked(event2 -> {
				this.focusAdd();
			});

			coverAlbum.getChildren().clear();
			coverField.getChildren().clear();

			coverField.getChildren().add(imgAdd);

			cancelBox.setVisible(false);


		}

	}
	@FXML
	public void createNewSong() {
		Canzone canzone = new Canzone();
		canzone.setAlbum(this.album);
		canzone.setTitle("Nuova canzone");
		canzone.setLyrics("add Lyric");
		canzone.setLength("00:00");
		if(album.getGenre() == Genere.singoli) {
			canzone.setGenre(Genere.pop);
		}else{
			canzone.setGenre(album.getGenre());
		}
		spaceMusicUnifyService.setSituation(ViewSituations.newobject);
        dispatcher.renderView("AdministratorViews/ManageArtistsView/ManageAlbumsView/song_detail", canzone);

	}
	@FXML
	public void deleteThisAlbum(ActionEvent event) {
		try{
			if (album.getId() != null) {
				spaceMusicUnifyService.delete(album);
			}else{
				System.out.println("Album not found");
			}
			dispatcher.renderView("AdministratorViews/ManageArtistsView/ManageAlbumsView/manage_albums", artista.getDiscography());

		} catch (BusinessException e) {
			dispatcher.renderError(e);
		}
	}

	public void setView(){
		switch (spaceMusicUnifyService.getSituation()){
			case detail:
				this.masterPane.getChildren().setAll(this.infoPane.getChildren());

				break;

			case modify:
				this.title.setText("Modify Album");
				this.confirm.setText("Modify");
				this.masterPane.getChildren().setAll(this.modifyPane.getChildren());

				break;

			case newobject:
				this.title.setText("New Album");
				this.confirm.setText("Create");
				this.masterPane.getChildren().setAll(this.modifyPane.getChildren());
				break;

			case user:
				this.deletealbum.setVisible(false);
				this.modify.setVisible(false);
				this.cancel1.setVisible(false);
				this.masterPane.getChildren().setAll(this.infoPane.getChildren()) ;
				break;
		}
	}
	
	@FXML
	public void showModify(ActionEvent event) {
		if(spaceMusicUnifyService.getSituation() == ViewSituations.detail){
			spaceMusicUnifyService.setSituation(ViewSituations.modify);

			this.setView();
		}else {
			spaceMusicUnifyService.setSituation(ViewSituations.detail);

			this.setView();
		}
	}
}
