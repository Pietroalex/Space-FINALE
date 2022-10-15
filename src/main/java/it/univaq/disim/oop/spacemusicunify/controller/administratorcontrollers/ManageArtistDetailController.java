package it.univaq.disim.oop.spacemusicunify.controller.administratorcontrollers;

import it.univaq.disim.oop.spacemusicunify.business.*;
import it.univaq.disim.oop.spacemusicunify.controller.DataInitializable;
import it.univaq.disim.oop.spacemusicunify.domain.*;
import it.univaq.disim.oop.spacemusicunify.view.ViewDispatcher;
import it.univaq.disim.oop.spacemusicunify.view.ViewSituations;
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
import java.lang.reflect.Array;
import java.net.URL;
import java.util.Arrays;
import java.util.HashSet;
import java.util.ResourceBundle;
import java.util.Set;

public class ManageArtistDetailController implements Initializable, DataInitializable<Artist>{

    private final ViewDispatcher dispatcher;
    private final ArtistService artistService;
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
    @FXML
    private MenuButton members;

    private Administrator admin;
    private UserService userService;

    private static Picture imgUrl;
    @FXML
    private MenuButton membersModify;
    @FXML
    private Set<Artist> addMembers ;
    private Set<Picture> tempPictures;


    public ManageArtistDetailController() {
        dispatcher = ViewDispatcher.getInstance();
        SpacemusicunifyBusinessFactory factory = SpacemusicunifyBusinessFactory.getInstance();

        artistService = factory.getArtistService();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        /*confirm.disableProperty().bind(stageNameField.textProperty().isEmpty().or(existingLabel.visibleProperty()).or(biographyField.textProperty().isEmpty()));
        stageNameField.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                existingLabel.setVisible(false);
            }
        });*/
        

    }
    
    public void setView2(){
        switch (dispatcher.getSituation()){
            case detail:
                loadImages(artist.getPictures());
                if(!(artist.getBandMembers().isEmpty())) {
                    members.setVisible(true);
                    for (Artist artistCtrl : artist.getBandMembers()) {
                        MenuItem menuItem = new MenuItem();
                        menuItem.setText(artistCtrl.getName());
                        menuItem.setOnAction((ActionEvent event) -> {
                            members.hide();
                            dispatcher.setSituation(ViewSituations.detail);
                            dispatcher.renderView("AdministratorViews/ManageArtistsView/artist_detail", artistCtrl);
                            System.out.println("andato");

                        });
                        members.getItems().add(menuItem);
                    }
                }
                stageName.setText(artist.getName());
                yearsOfActivity.setText(String.valueOf(artist.getYearsOfActivity()));
                biography.setText(artist.getBiography());
                nationality.setText(String.valueOf(artist.getNationality()));
                biography.setEditable(false);
                break;

            case modify:
                for(int i=1; i<101; i++) {
                    yearsOfActivityField.getItems().add(i);
                }
                if(!(artist.getBandMembers().isEmpty())) {
                    membersModify.setVisible(true);
                    addMembers = new HashSet<>(artist.getBandMembers());
                    for (Artist artistCtrl : artist.getBandMembers()) {
                        MenuItem menuItem = new MenuItem();
                        Button delete = new Button("Delete");
                        delete.setCursor(Cursor.HAND);
                        delete.setOnAction((ActionEvent event) -> {
                            addMembers.remove(artistCtrl);
                            membersModify.getItems().remove(menuItem);
                        });
                        Label name = new Label(artistCtrl.getName());
                        HBox hBox = new HBox();
                        hBox.getChildren().add(name);
                        hBox.getChildren().add(delete);

                        menuItem.setGraphic(hBox);
                        menuItem.setOnAction((ActionEvent event) -> {
                            dispatcher.setSituation(ViewSituations.detail);
                            dispatcher.renderView("AdministratorViews/ManageArtistsView/artist_detail", artistCtrl);
                            System.out.println("andato");

                        });
                        membersModify.getItems().add(menuItem);
                    }
                }
                nationalityField.getItems().addAll(Nationality.values());
                scroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);
                scroll.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
                cancelbox.setVisible(false);
                loadModifyImages(artist.getPictures());

                stageNameField.setText(artist.getName());
                yearsOfActivityField.setValue(artist.getYearsOfActivity());
                biographyField.setText(artist.getBiography());
                nationalityField.setValue(artist.getNationality());


                title.setText("Modify Artist");
                confirm.setText("Modify");

                break;

            case newobject:
                for(int i=1; i<101; i++) {
                    yearsOfActivityField.getItems().add(i);
                }

                membersModify.setVisible(true);
                try {
                    Set<Artist> artists = artistService.getArtistList();
                    addMembers = new HashSet<>();
                    Set<Artist> singleArtist = new HashSet<>(artists);
                    Set<Artist> band = new HashSet<>();
                    for(Artist artistCheck : artists){
                        if(!(artistCheck.getBandMembers().isEmpty())){
                            singleArtist.remove(artistCheck);
                            band.add(artistCheck);
                        }
                    }
                    for(Artist bandctr : band){
                        for(Artist art : bandctr.getBandMembers()){
                            for(Artist single2 : singleArtist) {

                                if (art.getId().intValue() == single2.getId().intValue()) {
                                    artists.remove(single2);
                                }

                            }
                        }
                    }
                    for(Artist removeBand : band){
                        artists.remove(removeBand);
                    }

                    for (Artist artistCtrl : artists) {
                        MenuItem menuItem = new MenuItem();
                        Button add = new Button("Add");
                        add.setCursor(Cursor.HAND);
                        add.setOnAction((ActionEvent event) -> {
                            addMembers.add(artistCtrl);
                            add.setDisable(true);
                        });
                        Label name = new Label(artistCtrl.getName());
                        HBox hBox = new HBox();
                        hBox.getChildren().add(name);
                        hBox.getChildren().add(add);

                        menuItem.setGraphic(hBox);

                        membersModify.getItems().add(menuItem);
                    }
                } catch (BusinessException e) {
                    throw new RuntimeException(e);
                }

                nationalityField.getItems().addAll(Nationality.values());
                scroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);
                scroll.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
                cancelbox.setVisible(false);
                loadModifyImages(artist.getPictures());

                stageNameField.setText(artist.getName());
                yearsOfActivityField.setValue(artist.getYearsOfActivity());
                biographyField.setText(artist.getBiography());
                nationalityField.setValue(artist.getNationality());
                deleteartist.setVisible(false);

                title.setText("New Artist");
                confirm.setText("Create");

                break;
                
            case user:
                loadImages(artist.getPictures());
                if(!(artist.getBandMembers().isEmpty())) {
                    members.setVisible(true);
                    for (Artist artistCtrl : artist.getBandMembers()) {
                        MenuItem menuItem = new MenuItem();
                        menuItem.setText(artistCtrl.getName());
                        menuItem.setOnAction((ActionEvent event) -> {
                            members.hide();
                            dispatcher.setSituation(ViewSituations.detail);
                            dispatcher.renderView("AdministratorViews/ManageArtistsView/artist_detail", artistCtrl);

                        });
                        members.getItems().add(menuItem);
                    }
                }
                stageName.setText(artist.getName());
                yearsOfActivity.setText(String.valueOf(artist.getYearsOfActivity()));
                biography.setText(artist.getBiography());
                nationality.setText(String.valueOf(artist.getNationality()));
                biography.setEditable(false);
                modify.setVisible(false);
                break;
                
            default:
            	break;
        }
    }
    @Override
    public void initializeData(Artist artist) {
        this.artist = artist;
	    setView2();
    }
    
    public void loadImages(Set<Picture> pictures){
        if (!(pictures.isEmpty())) {
            for( Picture img: pictures) {
                ImageView imgview;

                    imgview = new ImageView(new Image(new ByteArrayInputStream(img.getData())));

                imgview.setFitHeight(img.getHeight());
                imgview.setFitWidth(img.getWidth());
                images.getChildren().add(imgview);
            }
        }
    }
    
    public void loadModifyImages(Set<Picture> pictures) {
        if (!(pictures.isEmpty()) ) {
            for (Picture img : pictures) {

                ImageView imgs;

                    imgs = new ImageView(new Image(new ByteArrayInputStream(img.getData())));
                    imgs.setFitHeight(img.getHeight());
                    imgs.setFitWidth(img.getWidth());
                    imgs.setCursor(Cursor.HAND);

                    imgs.setOnMouseClicked( (mouseEvent) -> {
                        this.focusImage(img);
                    });



                modifyImages.getChildren().add(imgs);
            }
            ImageView imgAdd;
            try {
                imgAdd = new ImageView(new Image(new FileInputStream("src" + File.separator + "main" + File.separator + "resources" + File.separator + "data" + File.separator + "RAMfiles" + File.separator + "addp.png")));
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            }
            imgAdd.setFitHeight(120);
            imgAdd.setFitWidth(120);
            imgAdd.setOnMouseClicked(event -> {
                this.focusAdd();
            });
            modifyImages.getChildren().add(imgAdd);
        }else{
            ImageView imgAdd;
            try {
                imgAdd = new ImageView(new Image(new FileInputStream("src" + File.separator + "main" + File.separator + "resources" + File.separator + "data" + File.separator + "RAMfiles" + File.separator + "addp.png")));
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
                artist.setName(stageNameField.getText());
                artist.setBiography(biographyField.getText());
                artist.setYearsOfActivity(yearsOfActivityField.getValue());
                artist.setNationality(nationalityField.getValue());
                artist.setBandMembers(addMembers);
                if(tempPictures != null ) artist.setPictures(tempPictures);
                artistService.add(artist);
            } else {
            	artistService.modify(artist.getId(), stageNameField.getText(), biographyField.getText(), yearsOfActivityField.getValue(), nationalityField.getValue(), tempPictures, addMembers, artist);
            }

            dispatcher.renderView("AdministratorViews/ManageArtistsView/manage_artists", this.admin);
        } catch (AlreadyTakenFieldException e){
            existingLabel.setText("This Artist stage name is already taken");
            existingLabel.setVisible(true);
            System.out.println("eccezzione1");
        }catch (AlreadyExistingException e){

            existingLabel.setText("This artist already exists");
            existingLabel.setVisible(true);
            System.out.println("eccezione "+e.getMessage());
        } catch (BusinessException e) {
            dispatcher.renderError(e);
        }
    }
    @FXML
    public void cancelModify(ActionEvent event) {
        switch (dispatcher.getSituation()){
            case newobject:
                dispatcher.renderView("AdministratorViews/ManageArtistsView/manage_artists", admin);
                break;
            default:
                dispatcher.setSituation(ViewSituations.detail);
                dispatcher.renderView("AdministratorViews/ManageArtistsView/artist_detail", artist);
        }
    }
    @FXML
    public void manageAlbums(ActionEvent event) {
        dispatcher.renderView("AdministratorViews/ManageArtistsView/ManageAlbumsView/manage_albums", artist);
    }
    
    public void focusImage(Picture image){
        imgUrl = image;
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
                if(tempPictures == null) tempPictures = new HashSet<>(artist.getPictures());

                Picture picture = new Picture();
                picture.setOwnership(artist);
                picture.setHeight(120);
                picture.setWidth(120);
                try {
                    picture.setData(path);
                } catch (BusinessException e) {
                    dispatcher.renderError(e);
                }
                boolean check = false;
                for (Picture pictureCheck : artist.getPictures()){
                    if(Arrays.equals(pictureCheck.getData(), picture.getData())){
                        check = true;
                        existingLabel.setText("Already present picture, chose another or leave it as it is");
                        existingLabel.setVisible(true);
                        break;
                    }
                }
                for (Picture pictureCheck : tempPictures){
                    if(Arrays.equals(pictureCheck.getData(), picture.getData())){
                        check = true;
                        existingLabel.setText("Already present picture, chose another or leave it as it is");
                        existingLabel.setVisible(true);
                        break;
                    }
                }
                if(!check) {
                    tempPictures.add(picture);
                    modifyImages.getChildren().clear();
                    /*artist.setPictures(tempPictures);*/
                    loadModifyImages(tempPictures);
                }
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
        if(tempPictures == null) tempPictures = new HashSet<>(artist.getPictures());

        tempPictures.removeIf((Picture picture) -> picture.equals(imgUrl));
        modifyImages.getChildren().clear();
        loadModifyImages(tempPictures);
        cancelbox.setVisible(false);
/*        for(Picture img: tempPictures){
            if(imgUrl.getId().intValue() == img.getId().intValue()){
                tempPictures.remove(img);
                modifyImages.getChildren().clear();
                *//*artist.setPictures(tempPictures);*//*
                loadModifyImages(tempPictures);
                cancelbox.setVisible(false);

                break;
            }
        }*/
    }
    @FXML
    public void cancelDeleteSelection(ActionEvent event){
        imgUrl = null;
        cancelbox.setVisible(false);

    }
    @FXML
    public void deleteThisArtist(){
        try{
            if (artist.getId() != null) {
            	artistService.delete(artist);
            }else{
                System.out.println("Artist not found");
            }
            dispatcher.renderView("AdministratorViews/ManageArtistsView/manage_artists", this.admin);

        } catch (BusinessException e) {
            dispatcher.renderError(e);
        }
    }
    @FXML
    public void showModify(ActionEvent event) {
        dispatcher.setSituation(ViewSituations.modify);
        dispatcher.renderView("AdministratorViews/ManageArtistsView/artist_modify", artist);
    }
    
}
