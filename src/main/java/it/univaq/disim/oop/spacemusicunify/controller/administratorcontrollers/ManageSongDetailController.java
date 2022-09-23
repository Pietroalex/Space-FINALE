package it.univaq.disim.oop.spacemusicunify.controller.administratorcontrollers;

import it.univaq.disim.oop.spacemusicunify.business.*;
import it.univaq.disim.oop.spacemusicunify.controller.DataInitializable;
import it.univaq.disim.oop.spacemusicunify.domain.Album;
import it.univaq.disim.oop.spacemusicunify.domain.Song;
import it.univaq.disim.oop.spacemusicunify.domain.Genre;
import it.univaq.disim.oop.spacemusicunify.view.ViewDispatcher;
import it.univaq.disim.oop.spacemusicunify.view.ViewSituations;
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

public class ManageSongDetailController implements Initializable, DataInitializable<Song> {

	private final AlbumService albumService;
    private final ViewDispatcher dispatcher;
    private final UserService userService;
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
    private ComboBox<Genre> genreField;
    
    private Album album;
    @FXML
    private Label genrelabel;
    @FXML
    private Label genretext;
    @FXML Button back;
    @FXML
    private Label existingLabel;
    private Song song;
    @FXML
    private Button mp3button;


    public ManageSongDetailController(){
        dispatcher = ViewDispatcher.getInstance();
        SpacemusicunifyBusinessFactory factory = SpacemusicunifyBusinessFactory.getInstance();
        userService = factory.getUserService();
        albumService = factory.getAlbumService();
    }
    public void setView2(){
        switch (dispatcher.getSituation()){
            case detail:
                title.setText(song.getTitle());
                length.setText(song.getLength());
                lyrics.setText(song.getLyrics());
                lyrics.setEditable(false);
                genretext.setText(String.valueOf(song.getGenre()));
                break;

            case modify:
                genreField.getItems().addAll(Genre.values());
                genreField.getItems().remove(Genre.singoli);
                titleView.setText("Modify Song");
                confirm.setText("Modify");
                mp3button.setText("Change mp3 file");
                titleField.setText(song.getTitle());
                lengthField.setText(song.getLength());
                lyricsField.setText(song.getLyrics());
                if(song.getId() != null) {
                    //songField.setText(canzone.getFileMp3());
                }
                if(album.getGenre() == Genre.singoli) {
                    genreField.setDisable(false);
                }else{
                    genreField.setDisable(true);
                }
                genreField.setValue(song.getGenre());

                if(album.getSongList().size() == 1){
                    deletesong.setDisable(true);
                }
                break;

            case newobject:
                genreField.getItems().addAll(Genre.values());
                genreField.getItems().remove(Genre.singoli);
                titleField.setText(song.getTitle());
                lengthField.setText(song.getLength());
                lyricsField.setText(song.getLyrics());
                if(song.getId() != null) {
                    //songField.setText(canzone.getFileMp3());
                }
                if(album.getGenre() == Genre.singoli) {
                    genreField.setDisable(false);
                }else{
                    genreField.setDisable(true);
                }
                genreField.setValue(song.getGenre());
                this.album = song.getAlbum();
                if(album.getSongList().size() == 1){
                    deletesong.setDisable(true);
                }
                confirm.disableProperty().bind(lyricsField.textProperty().isEmpty().or(titleField.textProperty().isEmpty()).or(lengthField.textProperty().isEmpty()).or(existingLabel.visibleProperty()));
                mp3button.setText("Pick Up an mp3");
                titleView.setText("New Song");
                confirm.setText("Create");

                break;
                
            default:
            	break;
        }
    }
    @Override
    public void initialize(URL location, ResourceBundle resources) {
       /* confirm.disableProperty().bind(lyricsField.textProperty().isEmpty().or(titleField.textProperty().isEmpty()).or(lengthField.textProperty().isEmpty()).or(existingLabel.visibleProperty()));
        titleField.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                existingLabel.setVisible(false);
            }
        });*/


    }
    @Override
    public void initializeData(Song song) {
        this.song = song;
        this.album = song.getAlbum();
        setView2();
    }
    @FXML
    public void confirmSong(ActionEvent event) {
    	try {

            if (song.getId() == null) {

                song.setTitle(titleField.getText());
                song.setLength(lengthField.getText());
                if (album.getGenre() == Genre.singoli) {
                    song.setGenre(genreField.getValue());
                } else {
                    song.setGenre(album.getGenre());
                }
                if (!(songField.getText().isEmpty())) {

                   // canzone.setFileMp3(songField.getText());

                } else {
                    this.changeSong();
                  //  canzone.setFileMp3(songField.getText());
                }

                song.setLyrics(lyricsField.getText());

                albumService.add(song);
            } else {
                System.out.println("eseguo modify");
             //   spaceMusicUnifyService.modify(canzone.getId(), titleField.getText(), lengthField.getText(), genreField.getValue(), songField.getText(), lyricsField.getText(), album);
            }
            dispatcher.setSituation(ViewSituations.detail);
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
        switch (dispatcher.getSituation()){
            case newobject:
                dispatcher.setSituation(ViewSituations.modify);
                dispatcher.renderView("AdministratorViews/ManageArtistsView/ManageAlbumsView/album_modify", album);
                break;
            default:
                dispatcher.setSituation(ViewSituations.detail);
                dispatcher.renderView("AdministratorViews/ManageArtistsView/ManageAlbumsView/ManageSongsView/song_detail", song);
        }
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
            if (song.getId() != null) {
            	albumService.delete(song);
            }else{
                System.out.println("Song not found");
            }
            dispatcher.renderView("AdministratorViews/ManageArtistsView/ManageAlbumsView/album_detail", album);

        } catch (BusinessException e) {
            dispatcher.renderError(e);
        }
    }

    @FXML
    public void showModify() {
        dispatcher.setSituation(ViewSituations.modify);
        dispatcher.renderView("AdministratorViews/ManageArtistsView/ManageAlbumsView/ManageSongsView/song_modify", song);
    }
}
