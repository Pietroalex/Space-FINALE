package it.univaq.disim.oop.spacemusicunify.controller.administratorcontrollers;

import it.univaq.disim.oop.spacemusicunify.business.*;
import it.univaq.disim.oop.spacemusicunify.controller.DataInitializable;
import it.univaq.disim.oop.spacemusicunify.domain.*;
import it.univaq.disim.oop.spacemusicunify.view.ViewDispatcher;
import it.univaq.disim.oop.spacemusicunify.view.ViewSituations;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.*;
import java.net.URL;
import java.util.Arrays;
import java.util.HashSet;
import java.util.ResourceBundle;
import java.util.Set;

public class ManageArtistDetailController implements Initializable, DataInitializable<Artist>{

    private final ViewDispatcher dispatcher;
    private final ArtistService artistService;
    private Artist artist;
    private static final String MyStyle = "views/controllerStyle.css";
    
    @FXML
    private TextField nameField;
    @FXML
    private ComboBox<Nationality> nationalityField;
    @FXML
    private ComboBox<Integer> yearsOfActivityField;

    @FXML
    private TextArea biographyField;

    @FXML
    private Label name;
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
    private ListView<HBox> artistsModifyListView;
    @FXML
    private ListView<Artist> artistsDetailListView;
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
    @FXML Button addArtists;

    private Administrator admin;
    private UserService userService;

    private static Picture imgUrl;
    @FXML
    private AnchorPane artistsPane;
    @FXML
    private AnchorPane artistsModifyPane;
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
                    artistsPane.setVisible(true);
                    artistsDetailListView.setCellFactory(param -> new ListCell<Artist>() {
                        @Override
                        protected void updateItem(Artist item, boolean empty) {
                            super.updateItem(item, empty);

                            if (empty || item == null || item.getName() == null) {
                                setText(null);
                            } else {
                                setText(item.getName());
                            }
                        }

                    });

                    artistsDetailListView.setOnMouseClicked( mouseEvent ->  {
                        if(artistsDetailListView.getSelectionModel().getSelectedItem() != null) dispatcher.renderView("AdministratorViews/ManageArtistsView/artist_detail", artistsDetailListView.getSelectionModel().getSelectedItem());
                    });
                    ObservableList<Artist> bandMembersData = FXCollections.observableArrayList(artist.getBandMembers());
                    artistsDetailListView.setItems(bandMembersData);
                }
                name.setText(artist.getName());
                yearsOfActivity.setText(String.valueOf(artist.getYearsOfActivity()));
                biography.setText(artist.getBiography());
                nationality.setText(String.valueOf(artist.getNationality()));
                biography.setEditable(false);
                break;

            case modify:
                confirm.disableProperty().bind(nameField.textProperty().isEmpty().or(existingLabel.visibleProperty()));
                nameField.textProperty().addListener((obs, oldText, newText)-> {
                    if (existingLabel.isVisible()) {
                        existingLabel.setVisible(false);
                    }
                });
                for(int i=1; i<101; i++) {
                    yearsOfActivityField.getItems().add(i);
                }

                if(!(artist.getBandMembers().isEmpty())) {
                    artistsModifyPane.setVisible(true);
                    addMembers = new HashSet<>(artist.getBandMembers());
                    for (Artist artistCtrl : artist.getBandMembers()) {

                        HBox hBox = new HBox();
                        Label aName = new Label(artistCtrl.getName());
                        Button del = new Button("Delete");
                        del.setOnAction((action) -> {
                            addMembers.remove(artistCtrl);
                            artistsModifyListView.getItems().remove(hBox);
                        });
                        hBox.getChildren().add(aName);
                        hBox.getChildren().add(del);
                        artistsModifyListView.getItems().addListener((ListChangeListener<? super HBox>) a -> del.setDisable(a.getList().size() <= 2));
                        artistsModifyListView.getItems().add(hBox);
                    }
                }
                nationalityField.getItems().addAll(Nationality.values());
                scroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);
                scroll.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
                cancelbox.setVisible(false);
                loadModifyImages(artist.getPictures());

                nameField.setText(artist.getName());
                yearsOfActivityField.setValue(artist.getYearsOfActivity());
                biographyField.setText(artist.getBiography());
                nationalityField.setValue(artist.getNationality());


                title.setText("Modify Artist");
                confirm.setText("Modify");

                break;

            case newobject:
                confirm.disableProperty().bind(nameField.textProperty().isEmpty().or(existingLabel.visibleProperty()));
                nameField.textProperty().addListener((obs, oldText, newText)-> {
                    if (existingLabel.isVisible()) {
                        existingLabel.setVisible(false);
                    }
                });
                for(int i=1; i<101; i++) {
                    yearsOfActivityField.getItems().add(i);
                }
                artistsModifyPane.setVisible(true);
                addMembers = new HashSet<>();

                nationalityField.getItems().addAll(Nationality.values());
                scroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);
                scroll.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
                cancelbox.setVisible(false);
                loadModifyImages(artist.getPictures());

                nameField.setText(artist.getName());
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
                    artistsPane.setVisible(true);
                    artistsDetailListView.setCellFactory(param -> new ListCell<Artist>() {
                        @Override
                        protected void updateItem(Artist item, boolean empty) {
                            super.updateItem(item, empty);

                            if (empty || item == null || item.getName() == null) {
                                setText(null);
                            } else {
                                setText(item.getName());
                            }
                        }

                    });

                    artistsDetailListView.setOnMouseClicked( mouseEvent ->  {
                        if(artistsDetailListView.getSelectionModel().getSelectedItem() != null) dispatcher.renderView("AdministratorViews/ManageArtistsView/artist_detail", artistsDetailListView.getSelectionModel().getSelectedItem());
                    });
                    ObservableList<Artist> bamdMembersData = FXCollections.observableArrayList(artist.getBandMembers());
                    artistsDetailListView.setItems(bamdMembersData);
                }
                name.setText(artist.getName());
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
    @FXML
    public void showArtistsSelection() {
        Stage popupwindow = new Stage();
        popupwindow.initModality(Modality.APPLICATION_MODAL);
        VBox container = new VBox();
        VBox vBox = new VBox();
        try {
            Set<Artist> artists = artistService.getArtistList();

            Set<Artist> availableArtists = new HashSet<>(artists);
            Set<Artist> band = new HashSet<>();

            //divisione singoli artisti e band da tutti gli artisti salvati
            for(Artist artistCheck : artists){
                if(!(artistCheck.getBandMembers().isEmpty())){
                    availableArtists.remove(artistCheck);
                    band.add(artistCheck);
                }
            }
            //scrematura di artisti singoli rimasti non presenti in band e rimozione artisti presenti in band
            for(Artist bandctr : band){
                for(Artist art : bandctr.getBandMembers()){
                    for(Artist single2 : availableArtists) {

                        if (art.getId().intValue() == single2.getId().intValue()) {
                            artists.remove(single2);
                        }

                    }
                }
            }
            //rimozione band da set principale
            for(Artist removeBand : band){
                artists.remove(removeBand);
            }

            for(Artist addBack : artist.getBandMembers()){
                boolean checkArtist = false;
                for(Artist check : addMembers) {
                    if (addBack.getId().intValue() == check.getId().intValue()){
                        checkArtist = true;
                        break;
                    }
                }
                if(!checkArtist) artists.add(addBack);
            }

            for (Artist artistCtrl : artists) {

                Button add = new Button("Add");
                add.setId("b2");
                add.setCursor(Cursor.HAND);
                add.setOnAction((ActionEvent event) -> {
                    addMembers.add(artistCtrl);
                    HBox hBox = new HBox();
                    Label aName = new Label(artistCtrl.getName());
                    Button del = new Button("Delete");
                    del.setOnAction((action) -> {
                        addMembers.remove(artistCtrl);
                        artistsModifyListView.getItems().remove(hBox);
                        add.setDisable(false);
                    });
                    hBox.getChildren().add(aName);
                    hBox.getChildren().add(del);
                    if(dispatcher.getSituation() == ViewSituations.modify) artistsModifyListView.getItems().addListener((ListChangeListener<? super HBox>) a -> del.setDisable(a.getList().size() <= 2));
                    artistsModifyListView.getItems().add(hBox);
                    add.setDisable(true);
                });

                Label name = new Label(artistCtrl.getName());
                HBox hBox = new HBox();
                hBox.getChildren().add(name);
                hBox.getChildren().add(new Label("    "));
                hBox.getChildren().add(add);
                
                

                for(Artist ctrl : addMembers){
                    if(artistCtrl.getId().intValue() == ctrl.getId().intValue()){
                        add.setDisable(true);
                        break;
                    }
                }
                vBox.getChildren().add(hBox);
                Label space = new Label();
                space.setStyle("-fx-font-size:1px;");
                space.setPrefHeight(4);
                vBox.getChildren().add(space);
            }
        } catch (BusinessException e) {
            throw new RuntimeException(e);
        }

        ScrollPane scrollPane = new ScrollPane(vBox);
        scrollPane.setPrefHeight(500);//Adjust max height of the popup here
        scrollPane.setPrefWidth(130);//Adjust max width of the popup here
        Button closeButton = new Button("Close");
        closeButton.setId("b1");
        closeButton.setCursor(Cursor.HAND);
        closeButton.setOnAction(e -> {
            popupwindow.close();
        });
        container.getChildren().addAll(scrollPane, closeButton);
        container.setAlignment(Pos.CENTER);
        Scene scene1 = new Scene(container, 150, 200);

        scene1.getStylesheets().add(MyStyle); /*scene1.getStylesheets().add(MyStyle);*/

        popupwindow.setScene(scene1);
        popupwindow.setResizable(false);
        popupwindow.setTitle("Add artists");
        popupwindow.showAndWait();
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
                artist.setName(nameField.getText());
                artist.setBiography(biographyField.getText());
                artist.setYearsOfActivity(yearsOfActivityField.getValue());
                artist.setNationality(nationalityField.getValue());
                artist.setBandMembers(addMembers);
                if(tempPictures != null ) artist.setPictures(tempPictures);
                artistService.add(artist);
            } else {
            	artistService.modify(artist.getId(), nameField.getText(), biographyField.getText(), yearsOfActivityField.getValue(), nationalityField.getValue(), tempPictures, addMembers, artist);
            }

            dispatcher.renderView("AdministratorViews/ManageArtistsView/manage_artists", this.admin);

        }catch (AlreadyExistingException e){
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
