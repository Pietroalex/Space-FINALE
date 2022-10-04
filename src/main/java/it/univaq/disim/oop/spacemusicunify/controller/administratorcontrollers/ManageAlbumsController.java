package it.univaq.disim.oop.spacemusicunify.controller.administratorcontrollers;

import it.univaq.disim.oop.spacemusicunify.business.*;
import it.univaq.disim.oop.spacemusicunify.controller.DataInitializable;
import it.univaq.disim.oop.spacemusicunify.domain.*;
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
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.net.URL;
import java.time.LocalDate;
import java.util.*;

public class ManageAlbumsController implements Initializable, DataInitializable<Set<Production>> {
	
    private final ViewDispatcher dispatcher;
    private final ArtistService artistService;
    private Artist artist;
    
    @FXML
    private TableView<Album> albumList;
    @FXML
    private TableColumn<Album, ImageView> cover;
    @FXML
    private TableColumn<Album, String> title;
    @FXML
    private TableColumn<Album, String> songNumber;
    @FXML
    private TableColumn<Album, String> genre;
    @FXML
    private TableColumn<Album, LocalDate> release;
    @FXML
    private TableColumn<Album, Button> viewmodify;
    @FXML
    private Label stageName;
    @FXML
    private AnchorPane page;
    @FXML
    private Button newAlbumButton;
    @FXML
    private Label operation;
    private Set<Production> productionSet;

    public ManageAlbumsController(){
        dispatcher = ViewDispatcher.getInstance();
        SpacemusicunifyBusinessFactory factory = SpacemusicunifyBusinessFactory.getInstance();
        artistService = factory.getArtistService();
    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        title.setCellValueFactory(new PropertyValueFactory<>("title"));
        songNumber.setCellValueFactory((TableColumn.CellDataFeatures<Album, String> param) -> {

            int count = param.getValue().getSongs().size();

            String number = String.valueOf(count);
            return new SimpleStringProperty(number);
        });
        genre.setCellValueFactory(new PropertyValueFactory<>("genre"));
        cover.setCellValueFactory((TableColumn.CellDataFeatures<Album,ImageView> param) -> {
            ImageView image = new ImageView();
            if(param.getValue().getCover() != null){

                image = new ImageView(new Image(new ByteArrayInputStream(param.getValue().getCover().getData())));
                int height = param.getValue().getCover().getHeight()/4;
                int width = param.getValue().getCover().getWidth()/4;

                image.setFitHeight(height);
                image.setFitWidth(width);
            }
        	return new SimpleObjectProperty<ImageView>(image);
        });
        release.setCellValueFactory(new PropertyValueFactory<>("release"));
        viewmodify.setStyle("-fx-alignment: CENTER;");
        viewmodify.setCellValueFactory((TableColumn.CellDataFeatures<Album, Button> param) -> {
            final Button modify = new Button("Detail");
            modify.setCursor(Cursor.HAND);
            modify.setOnAction((ActionEvent event) -> {
                if (dispatcher.getSituation() == ViewSituations.user) {
                    dispatcher.setSituation(ViewSituations.user);
                } else {
                    dispatcher.setSituation(ViewSituations.detail);
                }
                findANDnav(param.getValue());

            });
            return new SimpleObjectProperty<Button>(modify);
        });

        if (dispatcher.getSituation() == ViewSituations.user){
            page.setPrefHeight(440);
            albumList.setPrefHeight(340);
            PlayerService playerService = SpacemusicunifyBusinessFactory.getInstance().getPlayerService();
            TableColumn<Album, Button> addAlbumToQueue = new TableColumn<Album, Button>();
            TableColumn<Album, Button> addAlbumToPlaylist = new TableColumn<Album, Button>();
            List<TableColumn<Album, Button>> columnsToAdd = new ArrayList<>();
            columnsToAdd.add(addAlbumToQueue);
            columnsToAdd.add(addAlbumToPlaylist);
            albumList.getColumns().addAll(columnsToAdd);
            
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
    					spacemusicunifyPlayer = playerService.getPlayer(RunTimeService.getCurrentUser());
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
    				dispatcher.renderView("UserViews/UserHomeView/playerPane", RunTimeService.getCurrentUser());
    			});
    			return new SimpleObjectProperty<Button>(addButton);
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
    }

	private void showPopupSelectPlaylist(Album selectedAlbum) {
		UserService userService = SpacemusicunifyBusinessFactory.getInstance().getUserService();
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
			ObservableList<Playlist> observableList = FXCollections.observableArrayList(userService.getAllPlaylists(RunTimeService.getCurrentUser()));
			tableView.setItems(observableList);
		} catch (BusinessException e1) {
			e1.printStackTrace();
		}

		// operazione annulla
		Button closeButton = new Button("Cancel");
		closeButton.setCursor(Cursor.HAND);
		closeButton.setOnAction(e -> {
			dispatcher.renderView("UserViews/HomeView/playlistPane", RunTimeService.getCurrentUser());
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
	
    private void findANDnav(Album value) {
        for(Production production : productionSet){
            if(production.getAlbum().getId().intValue() == value.getId().intValue()){
                dispatcher.renderView("AdministratorViews/ManageArtistsView/ManageAlbumsView/album_detail", production);
            }
        }
    }

    @Override
    public void initializeData(Set<Production> productions) {
        productionSet = productions;
        artist = productions.iterator().next().getArtist();

        stageName.setText(this.artist.getName());
        if(dispatcher.getSituation() == ViewSituations.user) {
        	newAlbumButton.setVisible(false);
        	operation.setText("View");
        }

        List<Album> albums = new ArrayList<>();
        for(Production production : productions){
            albums.add(production.getAlbum());
        }

        ObservableList<Album> albumsData = FXCollections.observableArrayList(albums);
        albumList.setItems(albumsData);
    }
    @FXML
    public void createNewAlbum(){
    	Album newAlbum = new Album();
    	newAlbum.setTitle("new album of "+artist.getName());
    	newAlbum.setGenre(Genre.rock);
    	newAlbum.setRelease(LocalDate.now());
        Set<Song> songs = new HashSet<>();
        newAlbum.setSongs(songs);
        Picture picture = new Picture();
        picture.setHeight(140);
        picture.setWidth(140);
        picture.setData("src"+ File.separator + "main" + File.separator + "resources" +File.separator+"dati"+ File.separator+"RAMfiles"+ File.separator+"cover.png");
        picture.setOwnership(newAlbum);
        newAlbum.setCover(picture);

        Production production = new Production();
        production.setArtist(artist);
        production.setAlbum(newAlbum);

        dispatcher.setSituation(ViewSituations.newobject);
        dispatcher.renderView("AdministratorViews/ManageArtistsView/ManageAlbumsView/album_modify", production);
    }
}