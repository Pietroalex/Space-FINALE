package it.univaq.disim.oop.spacemusicunify.controller.administratorcontrollers;

import it.univaq.disim.oop.spacemusicunify.business.*;
import it.univaq.disim.oop.spacemusicunify.controller.DataInitializable;
import it.univaq.disim.oop.spacemusicunify.domain.Amministratore;
import it.univaq.disim.oop.spacemusicunify.domain.Artista;
import it.univaq.disim.oop.spacemusicunify.domain.Nazionalità;
import it.univaq.disim.oop.spacemusicunify.view.ViewDispatcher;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Cursor;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.ResourceBundle;

public class AdministratorManageArtistDetailController implements Initializable, DataInitializable<Artista>{

    private final UtenteGenericoService utenteGenerico;
    private ViewDispatcher dispatcher;
    private Artista artist;


    @FXML
    private TextField stageNameField;
    @FXML
    private ComboBox<Nazionalità> nationalityField;
    @FXML
    private ComboBox<Integer> yearsOfActivityField;

    @FXML
    private TextArea biographyField;

    @FXML
    private Label stageName;
    @FXML
    private Label nationality;
    @FXML
    private Label yearsOfActivity;
    @FXML
    private TextArea biography;
    @FXML
    private Button confirm;
    @FXML
    private Button deleteartist;
    @FXML
    private Button cancel;
    @FXML
    private Button albums;

    @FXML
    private Button modify;
    @FXML
    private AnchorPane infoPane;
    @FXML
    private AnchorPane modifyPane;
    @FXML
    private AnchorPane masterPane;
    @FXML
    private HBox images;
    @FXML
    private HBox modifyImages;
    @FXML
    private ScrollPane scroll;
    @FXML
    private Label title;
    @FXML
    private VBox cancelbox;
    @FXML
    private Label existingLabel;

    private Amministratore admin;
    private SPACEMusicUnifyService SPACEMusicUnifyService;

    private static String imgUrl;
    public AdministratorManageArtistDetailController() {
        dispatcher = ViewDispatcher.getInstance();
        SpacemusicunifyBusinessFactory factory = SpacemusicunifyBusinessFactory.getInstance();
        SPACEMusicUnifyService = factory.getAmministratoreService();
        utenteGenerico = factory.getUtenteGenerico();

    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        confirm.disableProperty().bind(stageNameField.textProperty().isEmpty().or(existingLabel.visibleProperty()).or(biographyField.textProperty().isEmpty()));
        stageNameField.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                existingLabel.setVisible(false);
            }
        });
        
        for(int i=1; i<101; i++) {
        	yearsOfActivityField.getItems().add(i);
        }
        nationalityField.getItems().addAll(Nazionalità.values());
    }
    @Override
    public void initializeData(Artista artist) {
	    scroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);
	    scroll.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
	
	    cancelbox.setVisible(false);
	
	
	    this.setView();
	    this.artist = artist;
	
	    this.loadImages();
	
	    this.stageName.setText(artist.getStageName());
	    this.yearsOfActivity.setText(String.valueOf(artist.getYearsOfActivity()));
	    this.biography.setText(artist.getBiography());
	    this.nationality.setText(String.valueOf(artist.getNationality()));
	
	
	    this.loadModifyImages();
	
	    this.stageNameField.setText(artist.getStageName());
	    this.yearsOfActivityField.setValue(artist.getYearsOfActivity());
	    this.biographyField.setText(artist.getBiography());
	    this.nationalityField.setValue(artist.getNationality());
	    this.biography.setEditable(false);
    }
    public void loadImages(){
        if (!(this.artist.getPictures().isEmpty()) && (this.artist.getPictures().get(0).endsWith(".png") || this.artist.getPictures().get(0).endsWith(".jpg"))) {
            for( String img: this.artist.getPictures()) {
                ImageView imgview;
                try {
                    imgview = new ImageView(new Image(Files.newInputStream(Paths.get(img))));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                imgview.setFitHeight(120);
                imgview.setFitWidth(120);
                this.images.getChildren().add(imgview);
            }
        }
    }
    public void loadModifyImages() {
        if (!(this.artist.getPictures().isEmpty()) && (this.artist.getPictures().get(0).endsWith(".png") || this.artist.getPictures().get(0).endsWith(".jpg"))) {
            for (String img : this.artist.getPictures()) {

                ImageView imgs;
                try {
                    imgs = new ImageView(new Image(Files.newInputStream(Paths.get(img))));
                    imgs.setFitHeight(120);
                    imgs.setFitWidth(120);
                    imgs.setCursor(Cursor.HAND);

                    imgs.setOnMouseClicked(event -> {

                        this.focusImage(img);
                    });
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }


                modifyImages.getChildren().add(imgs);
            }
            ImageView imgAdd;
            try {
                imgAdd = new ImageView(new Image(new FileInputStream("src" + File.separator + "main" + File.separator + "resources" + File.separator + "viste" + File.separator + "RAMfiles" + File.separator + "addp.png")));
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            }
            imgAdd.setFitHeight(120);
            imgAdd.setFitWidth(120);
            imgAdd.setOnMouseClicked(event -> {
                this.focusAdd();
            });
            this.modifyImages.getChildren().add(imgAdd);
        }else{
            ImageView imgAdd;
            try {
                imgAdd = new ImageView(new Image(new FileInputStream("src" + File.separator + "main" + File.separator + "resources" + File.separator + "viste" + File.separator + "RAMfiles" + File.separator + "addp.png")));
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            }
            imgAdd.setFitHeight(120);
            imgAdd.setFitWidth(120);
            imgAdd.setOnMouseClicked(event -> {
                this.focusAdd();
            });
            this.modifyImages.getChildren().add(imgAdd);
        }
    }
    @FXML
    public void confirmArtist(ActionEvent event) {

        try{

            if (artist.getId() == null) {
                artist.setStageName(stageNameField.getText());
                artist.setBiography(biographyField.getText());
                artist.setYearsOfActivity(yearsOfActivityField.getValue());
                artist.setNationality(nationalityField.getValue());

                SPACEMusicUnifyService.add(artist);
            } else {
                SPACEMusicUnifyService.modify(artist.getId(), stageNameField.getText(), biographyField.getText(), yearsOfActivityField.getValue(), nationalityField.getValue(), artist.getPictures());
            }

            dispatcher.renderView("AdministratorViews/ManageArtistsView/manage_artists", this.admin);
        } catch (AlreadyTakenFieldException e){
            existingLabel.setText("This Artist stage name is already taken");
            existingLabel.setVisible(true);
            System.out.println("eccezzione1");
        }catch (AlreadyExistingException e){
            existingLabel.setText("This artist already exists");
            existingLabel.setVisible(true);
            System.out.println("eccezione");
        } catch (BusinessException e) {
            dispatcher.renderError(e);
        }
    }
    @FXML
    public void cancelModify(ActionEvent event) {

        dispatcher.renderView("AdministratorViews/ManageArtistsView/manage_artists", this.admin);

    }
    @FXML
    public void manageAlbums(){
        if(this.utenteGenerico.getSituation() == ViewSituations.user){
            this.utenteGenerico.setSituation(ViewSituations.user);
        }
        ViewDispatcher dispatcher = ViewDispatcher.getInstance();
        dispatcher.renderView("AdministratorViews/ManageArtistsView/ManageAlbumsView/manage_albums", this.artist.getDiscography());
    }
    public void focusImage(String image){
        imgUrl = image;
        System.out.println(imgUrl);
        cancelbox.setVisible(true);
    }
    public void focusAdd(){
        cancelbox.setVisible(false);
        FileChooser fileChoose = new FileChooser();
        File file =  fileChoose.showOpenDialog(null);

        if(file != null){
            String path = file.getPath();
            if(path.endsWith(".png") || path.endsWith(".jpg")){
                existingLabel.setVisible(false);
                List<String> tempimg = this.artist.getPictures();
                tempimg.add(path);
                modifyImages.getChildren().clear();
                this.artist.setPictures(tempimg);
                this.loadModifyImages();
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
    public void deleteThisImage(ActionEvent event){
        List<String> tempimg = this.artist.getPictures();

        for(String img: tempimg){
            if(this.imgUrl.equals(img)){
                tempimg.remove(img);
                modifyImages.getChildren().clear();
                this.artist.setPictures(tempimg);
                this.loadModifyImages();
                cancelbox.setVisible(false);

                break;
            }
        }
    }
    @FXML
    public void cancelDeleteSelection(ActionEvent event){
        this.imgUrl = "";
        cancelbox.setVisible(false);

    }
    @FXML
    public void deleteThisArtist(){
        try{
            if (artist.getId() != null) {
                SPACEMusicUnifyService.delete(artist);
            }else{
                System.out.println("Artist not found");
            }
            dispatcher.renderView("AdministratorViews/ManageArtistsView/manage_artists", this.admin);

        } catch (BusinessException e) {
            dispatcher.renderError(e);
        }
    }
    public void setView(){
        switch (this.utenteGenerico.getSituation()){
            case detail:
                masterPane.getChildren().setAll(infoPane.getChildren()) ;
                break;

            case modify:
                title.setText("Modify Artist");
                confirm.setText("Modify");
                masterPane.getChildren().setAll(modifyPane.getChildren()) ;
                break;

            case newobject:
                title.setText("New Artist");
                confirm.setText("Create");
                masterPane.getChildren().setAll(modifyPane.getChildren()) ;
                break;

            case user:
                this.deleteartist.setVisible(false);
                this.modify.setVisible(false);
                this.albums.setText("View Artist albums");
                this.utenteGenerico.setSituation(ViewSituations.user);
                this.masterPane.getChildren().setAll(this.infoPane.getChildren());
                break;
        }
    }
    @FXML
    public void showModify(ActionEvent event) {
        if(this.utenteGenerico.getSituation() == ViewSituations.detail){
            this.utenteGenerico.setSituation(ViewSituations.modify);
            this.setView();
        }else {
            this.utenteGenerico.setSituation(ViewSituations.detail);
            this.setView();
        }
    }


}
