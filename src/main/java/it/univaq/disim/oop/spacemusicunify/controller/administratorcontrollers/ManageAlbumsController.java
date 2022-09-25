package it.univaq.disim.oop.spacemusicunify.controller.administratorcontrollers;

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
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;

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

            int count = param.getValue().getSongList().size();

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
            page.setMaxHeight(500);
        }
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