package it.univaq.disim.oop.spacemusicunify.controller.administratorcontrollers;

import it.univaq.disim.oop.spacemusicunify.business.*;
import it.univaq.disim.oop.spacemusicunify.controller.DataInitializable;
import it.univaq.disim.oop.spacemusicunify.domain.Administrator;
import it.univaq.disim.oop.spacemusicunify.domain.Artist;
import it.univaq.disim.oop.spacemusicunify.domain.Nationality;
import it.univaq.disim.oop.spacemusicunify.domain.Picture;
import it.univaq.disim.oop.spacemusicunify.view.ViewDispatcher;
import it.univaq.disim.oop.spacemusicunify.view.ViewSituations;
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

import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ResourceBundle;
import java.util.Set;

public class ManageArtistDetailController implements Initializable, DataInitializable<Artist>{

    private final ViewDispatcher dispatcher;
    private Artist artist;


    @FXML
    private TextField stageNameField;
    @FXML
    private ComboBox<Nationality> nationalityField;
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

    private Administrator admin;
    private SPACEMusicUnifyService spaceMusicUnifyService;

    private static String imgUrl;
    public ManageArtistDetailController() {
        dispatcher = ViewDispatcher.getInstance();
        SpacemusicunifyBusinessFactory factory = SpacemusicunifyBusinessFactory.getInstance();
        spaceMusicUnifyService = factory.getSPACEMusicUnifyService();

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
        nationalityField.getItems().addAll(Nationality.values());
    }
    @Override
    public void initializeData(Artist artist) {
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
        if (!(this.artist.getPictures().isEmpty())) {
            for( Picture img: this.artist.getPictures()) {
                ImageView imgview;

                    imgview = new ImageView(new Image(new ByteArrayInputStream(img.getPhoto())));

                imgview.setFitHeight(120);
                imgview.setFitWidth(120);
                this.images.getChildren().add(imgview);
            }
        }
    }
    public void loadModifyImages() {
        if (!(this.artist.getPictures().isEmpty()) ) {
            for (Picture img : this.artist.getPictures()) {

                ImageView imgs;

                    imgs = new ImageView(new Image(new ByteArrayInputStream(img.getPhoto())));
                    imgs.setFitHeight(120);
                    imgs.setFitWidth(120);
                    imgs.setCursor(Cursor.HAND);

                    imgs.setOnMouseClicked(event -> {

                        this.focusImage(String.valueOf(img.getId()));
                    });



                modifyImages.getChildren().add(imgs);
            }
            ImageView imgAdd;
            try {
                imgAdd = new ImageView(new Image(new FileInputStream("src" + File.separator + "main" + File.separator + "resources" + File.separator + "dati" + File.separator + "RAMfiles" + File.separator + "addp.png")));
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
                imgAdd = new ImageView(new Image(new FileInputStream("src" + File.separator + "main" + File.separator + "resources" + File.separator + "dati" + File.separator + "RAMfiles" + File.separator + "addp.png")));
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

                spaceMusicUnifyService.add(artist);
            } else {
                spaceMusicUnifyService.modify(artist.getId(), stageNameField.getText(), biographyField.getText(), yearsOfActivityField.getValue(), nationalityField.getValue(), artist.getPictures());
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
        if(dispatcher.getSituation() == ViewSituations.user){
            dispatcher.setSituation(ViewSituations.user);
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
                Set<Picture> tempimg = this.artist.getPictures();
                try {
                    Picture picture = new Picture();
                    ByteArrayOutputStream outStreamObj = new ByteArrayOutputStream();
                    outStreamObj.writeBytes(Files.readAllBytes(Paths.get(path)));
                    picture.setPhoto(outStreamObj.toByteArray());
                    tempimg.add(picture);

                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
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
        Set<Picture> tempimg = this.artist.getPictures();

        for(Picture img: tempimg){
            if(imgUrl.equals(img.getId().toString())){
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
        imgUrl = String.valueOf(0);
        cancelbox.setVisible(false);

    }
    @FXML
    public void deleteThisArtist(){
        try{
            if (artist.getId() != null) {
                spaceMusicUnifyService.delete(artist);
            }else{
                System.out.println("Artist not found");
            }
            dispatcher.renderView("AdministratorViews/ManageArtistsView/manage_artists", this.admin);

        } catch (BusinessException e) {
            dispatcher.renderError(e);
        }
    }
    public void setView(){
        switch (dispatcher.getSituation()){
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
                dispatcher.setSituation(ViewSituations.user);
                this.masterPane.getChildren().setAll(this.infoPane.getChildren());
                break;
        }
    }
    @FXML
    public void showModify(ActionEvent event) {
        if(dispatcher.getSituation() == ViewSituations.detail){
            dispatcher.setSituation(ViewSituations.modify);
            this.setView();
        }else {
            dispatcher.setSituation(ViewSituations.detail);
            this.setView();
        }
    }


}
