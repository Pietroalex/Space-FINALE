package it.univaq.disim.oop.spacemusicunify.controller.administratorcontrollers;

import it.univaq.disim.oop.spacemusicunify.business.*;
import it.univaq.disim.oop.spacemusicunify.controller.DataInitializable;
import it.univaq.disim.oop.spacemusicunify.domain.*;
import it.univaq.disim.oop.spacemusicunify.view.ViewDispatcher;
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
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.*;

public class AdministratorManageAlbumsController implements Initializable, DataInitializable<Set<Album>> {
	
    private final ViewDispatcher dispatcher;
    private final SPACEMusicUnifyService spaceMusicUnifyService;
    private Artista artist;


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

    public AdministratorManageAlbumsController(){
        dispatcher = ViewDispatcher.getInstance();
        SpacemusicunifyBusinessFactory factory = SpacemusicunifyBusinessFactory.getInstance();
        spaceMusicUnifyService = factory.getSPACEMusicUnifyService();
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

                image = new ImageView(new Image(new ByteArrayInputStream(param.getValue().getCover().getPhoto())));
                image.setFitHeight(40);
                image.setFitWidth(40);
            }
        	return new SimpleObjectProperty<ImageView>(image);
        });
        release.setCellValueFactory(new PropertyValueFactory<>("release"));
        viewmodify.setStyle("-fx-alignment: CENTER;");
        viewmodify.setCellValueFactory((TableColumn.CellDataFeatures<Album, Button> param) -> {
            final Button modify = new Button("Detail");
            modify.setCursor(Cursor.HAND);
            modify.setOnAction((ActionEvent event) -> {
                if (spaceMusicUnifyService.getSituation() == ViewSituations.user) {
                    spaceMusicUnifyService.setSituation(ViewSituations.user);
                } else {
                    spaceMusicUnifyService.setSituation(ViewSituations.detail);
                }

                dispatcher.renderView("AdministratorViews/ManageArtistsView/ManageAlbumsView/album_detail", param.getValue());
            });
            return new SimpleObjectProperty<Button>(modify);
        });

        if (spaceMusicUnifyService.getSituation() == ViewSituations.user){
            page.setMaxHeight(500);
        }
    }
    @Override
    public void initializeData(Set<Album> discography) {
        this.artist = discography.iterator().next().getArtist();

        stageName.setText(this.artist.getStageName());
        if(spaceMusicUnifyService.getSituation() == ViewSituations.user) {
        	newAlbumButton.setVisible(false);
        	operation.setText("View");
        }

        List<Album> albums = new ArrayList<>(discography);
        ObservableList<Album> albumsData = FXCollections.observableArrayList(albums);
        albumList.setItems(albumsData);
    }
    @FXML
    public void createNewAlbum(){
    	Album newAlbum = new Album();
    	newAlbum.setTitle("new album of "+artist.getStageName());
    	newAlbum.setGenre(Genere.rock);
    	newAlbum.setRelease(LocalDate.now());
	    newAlbum.setArtist(artist);
        Picture picture = new Picture();
        ByteArrayOutputStream outStreamObj = new ByteArrayOutputStream();
        try {
            outStreamObj.writeBytes(Files.readAllBytes(Paths.get("src"+ File.separator + "main" + File.separator + "resources" +File.separator+"dati"+ File.separator+"RAMfiles"+ File.separator+"cover.png")));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        picture.setPhoto(outStreamObj.toByteArray());
        newAlbum.setCover(picture);
        spaceMusicUnifyService.setSituation(ViewSituations.newobject);

        dispatcher.renderView("AdministratorViews/ManageArtistsView/ManageAlbumsView/album_detail", newAlbum);
    }
}