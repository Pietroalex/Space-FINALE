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
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.net.URL;
import java.time.LocalDate;
import java.util.*;

public class ManageAlbumsController implements Initializable, DataInitializable<Artist> {
	
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
            modify.setId("b3");
            modify.setCursor(Cursor.HAND);
            modify.setOnAction((ActionEvent event) -> {
                if (dispatcher.getSituation() == ViewSituations.user) {
                    dispatcher.setSituation(ViewSituations.user);
                } else {
                    dispatcher.setSituation(ViewSituations.detail);
                }
                dispatcher.renderView("AdministratorViews/ManageArtistsView/ManageAlbumsView/album_detail", param.getValue());


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
    			addButton.setId("b3");
    			addButton.setCursor(Cursor.HAND);
    			addButton.setOnAction((ActionEvent event) -> {
                    //aggiungere la canzone alla coda di riproduzione dell'utente
                    for(Song albumSong: param.getValue().getSongs()) {
                        if(!(checkForClones(RunTimeService.getPlayer(), albumSong))) {
                            try {
                                playerService.addSongToQueue(RunTimeService.getPlayer(), albumSong);
                            } catch (BusinessException e) {
                                dispatcher.renderError(e);
                            }
                        }
                    }
                });
    			return new SimpleObjectProperty<Button>(addButton);
    		});
    		
    		addAlbumToPlaylist.setStyle("-fx-alignment: CENTER;");
    		addAlbumToPlaylist.setCellValueFactory((TableColumn.CellDataFeatures<Album, Button> param) -> {
    			final Button addButton = new Button("Add to playlist");
    			addButton.setId("b3");
    			addButton.setCursor(Cursor.HAND);
    			addButton.setOnAction((ActionEvent event) -> {
    				addAlbumToPlaylist(param.getValue());
    			});
    			return new SimpleObjectProperty<Button>(addButton);
    		});
        }
    }
    @Override
    public void initializeData(Artist artist) {
        this.artist = artist;
        Set<Artist> artists = new HashSet<>();
        artists.add(artist);
        SpacemusicunifyBusinessFactory.getInstance().getAlbumService().setChosenArtists(artists);

        stageName.setText(artist.getName());
        if(dispatcher.getSituation() == ViewSituations.user) {
            newAlbumButton.setVisible(false);
            operation.setText("View");
        }

        Set<Album> albums = null;
        try {
            albums = artistService.findAllAlbums(artist);
        } catch (BusinessException e) {
            dispatcher.renderError(e);
        }

        ObservableList<Album> albumsData = FXCollections.observableArrayList(albums);
        albumList.setItems(albumsData);
    }
    @FXML
    public void createNewAlbum(ActionEvent event){
        Album newAlbum = new Album();
        newAlbum.setTitle("new album of "+artist.getName());
        newAlbum.setGenre(Genre.rock);
        newAlbum.setRelease(LocalDate.now());
        Set<Song> songs = new HashSet<>();
        newAlbum.setSongs(songs);
        Picture picture = new Picture();
        picture.setHeight(140);
        picture.setWidth(140);
        try {
            picture.setData("src"+ File.separator + "main" + File.separator + "resources" +File.separator+ "data" + File.separator+"RAMfiles"+ File.separator+"cover.png");
        } catch (BusinessException e) {
            dispatcher.renderError(e);
        }
        picture.setOwnership(newAlbum);
        newAlbum.setCover(picture);

        Set<Artist> artists = new HashSet<>();
        artists.add(artist);
        SpacemusicunifyBusinessFactory.getInstance().getAlbumService().setChosenArtists(artists);
        dispatcher.setSituation(ViewSituations.newobject);
        dispatcher.renderView("AdministratorViews/ManageArtistsView/ManageAlbumsView/album_modify", newAlbum);
    }
    private void addAlbumToPlaylist(Album selectedAlbum) {
        User user = RunTimeService.getCurrentUser();
        SpacemusicunifyBusinessFactory factory = SpacemusicunifyBusinessFactory.getInstance();
        UserService userService = factory.getUserService();

        Stage popupwindow = new Stage();
        popupwindow.initModality(Modality.APPLICATION_MODAL);
        Label title = new Label("Choose the playlist");
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
                for(Song albumSong: selectedAlbum.getSongs()) {
                    if(checkForClones(param.getValue(), albumSong)) songList.add(albumSong);
                }
                try {
                    userService.modify(songList, param.getValue());
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
            dispatcher.renderError(e1);
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
        popupwindow.setTitle("Add " + selectedAlbum.getTitle() + " to playlist?");
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

}