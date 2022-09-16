package it.univaq.disim.oop.spacemusicunify.controller.administratorcontrollers;

import it.univaq.disim.oop.spacemusicunify.business.*;
import it.univaq.disim.oop.spacemusicunify.controller.DataInitializable;
import it.univaq.disim.oop.spacemusicunify.domain.Album;
import it.univaq.disim.oop.spacemusicunify.domain.Canzone;
import it.univaq.disim.oop.spacemusicunify.domain.Genere;
import it.univaq.disim.oop.spacemusicunify.view.ViewDispatcher;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

public class AdministratorManageSongDetailController implements Initializable, DataInitializable<Canzone> {


    private final ViewDispatcher dispatcher;
    private final SPACEMusicUnifyService spaceMusicUnifyService;
    @FXML
    private AnchorPane masterPane;
    @FXML
    private AnchorPane modifyPane;
    @FXML
    private AnchorPane infoPane;
    @FXML
    private TextField titleField;
    @FXML
    private TextField lengthField;
    @FXML
    private TextField songField;
    @FXML
    private Button confirm;
    @FXML
    private Button cancel;
    @FXML
    private Button modify;
    @FXML
    private TextArea lyricsField;
    @FXML
    private Label titleView;
    @FXML
    private Label title;
    @FXML
    private Label length;
    @FXML
    private TextArea lyrics;
    @FXML
    private Button deletesong;
    @FXML
    private ComboBox<Genere> genreField;
    
    private Album album;
    @FXML
    private Label genrelabel;
    @FXML
    private Label genretext;
    @FXML Button back;
    @FXML
    private Label existingLabel;
    private Canzone canzone;


    public AdministratorManageSongDetailController(){
        dispatcher = ViewDispatcher.getInstance();

        SpacemusicunifyBusinessFactory factory = SpacemusicunifyBusinessFactory.getInstance();
        spaceMusicUnifyService = factory.getSPACEMusicUnifyService();
    }
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        confirm.disableProperty().bind(lyricsField.textProperty().isEmpty().or(titleField.textProperty().isEmpty()).or(lengthField.textProperty().isEmpty()).or(existingLabel.visibleProperty()));
        titleField.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                existingLabel.setVisible(false);
            }
        });
        genreField.getItems().addAll(Genere.values());
        genreField.getItems().remove(Genere.singoli);

    }
    @Override
    public void initializeData(Canzone canzone) {
        this.canzone = canzone;

        genreField.setValue(canzone.getGenre());
        this.album = canzone.getAlbum();
        if(album.getSongList().size() == 1){
            deletesong.setDisable(true);
        }

        this.setView();

        title.setText(canzone.getTitle());
        length.setText(canzone.getLength());
        lyrics.setText(canzone.getLyrics());
        lyrics.setEditable(false);
        genretext.setText(String.valueOf(canzone.getGenre()));

        titleField.setText(canzone.getTitle());
        lengthField.setText(canzone.getLength());
        lyricsField.setText(canzone.getLyrics());
        if(canzone.getId() != null) {
            songField.setText(canzone.getFileMp3());
        }
        if(album.getGenre() == Genere.singoli) {
            genreField.setDisable(false);
        }else{
            genreField.setDisable(true);
        }

    }
    @FXML
    public void confirmSong(ActionEvent event) {
    	try {

            if (canzone.getId() == null) {

                canzone.setTitle(titleField.getText());
                canzone.setLength(lengthField.getText());
                if (album.getGenre() == Genere.singoli) {
                    canzone.setGenre(genreField.getValue());
                } else {
                    canzone.setGenre(album.getGenre());
                }
                if (!(songField.getText().isEmpty())) {

                    canzone.setFileMp3(songField.getText());

                } else {
                    this.changeSong();
                    canzone.setFileMp3(songField.getText());
                }

                canzone.setLyrics(lyricsField.getText());

                spaceMusicUnifyService.add(canzone);
            } else {
                System.out.println("eseguo modify");
                spaceMusicUnifyService.modify(canzone.getId(), titleField.getText(), lengthField.getText(), genreField.getValue(), songField.getText(), lyricsField.getText(), album);
            }
            spaceMusicUnifyService.setSituation(ViewSituations.detail);
            dispatcher.renderView("AdministratorViews/ManageArtistsView/ManageAlbumsView/album_detail", album);

        } catch (AlreadyTakenFieldException e) {
            existingLabel.setText("This song title is already taken");
            existingLabel.setVisible(true);
        }catch (AlreadyExistingException e){
            existingLabel.setText("This song already exists");
            existingLabel.setVisible(true);
            System.out.println("eccezione");
		} catch (BusinessException e) {
			dispatcher.renderError(e);
		}
    }

    public void cancelModify(ActionEvent event) {
        dispatcher.renderView("AdministratorViews/ManageArtistsView/ManageAlbumsView/album_detail", album);
    }
    @FXML
    public void backToTheAlbum(){
        dispatcher.renderView("AdministratorViews/ManageArtistsView/ManageAlbumsView/album_detail", album);
    }

    @FXML
    public void changeSong(){

            FileChooser fileChoose = new FileChooser();
            File file =  fileChoose.showOpenDialog(null);

            if(file != null){
                String path = file.getPath();

                if(path.endsWith(".mp3")){
                    songField.setText(path);
                    existingLabel.setVisible(false);
                }else{
                    existingLabel.setText("Wrong File type, Only .mp3 are allowed");
                    existingLabel.setVisible(true);
                }

            }else{
                existingLabel.setText("Please choose an .mp3 File to continue");
                existingLabel.setVisible(true);
            }


    }
    @FXML
    public void deleteThisSong(ActionEvent event) {
        try{
            if (canzone.getId() != null) {
                spaceMusicUnifyService.delete(canzone);
            }else{
                System.out.println("Song not found");
            }
            dispatcher.renderView("AdministratorViews/ManageArtistsView/ManageAlbumsView/album_detail", album);

        } catch (BusinessException e) {
            dispatcher.renderError(e);
        }
    }
    public void setView(){
        switch (spaceMusicUnifyService.getSituation()){
            case detail:
                this.back.setVisible(true);
                this.masterPane.getChildren().setAll(this.infoPane.getChildren()) ;
                break;

            case modify:
                this.title.setText("Modify Artist");
                this.confirm.setText("Modify");
                this.masterPane.getChildren().setAll(this.modifyPane.getChildren()) ;
                break;

            case newobject:
                this.title.setText("New Artist");
                this.confirm.setText("Create");
                this.masterPane.getChildren().setAll(this.modifyPane.getChildren()) ;
                break;

            case user:
                this.deletesong.setVisible(false);
                this.modify.setVisible(false);
                this.back.setVisible(false);
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
