package it.univaq.disim.oop.spacemusicunify.controller.administratorcontrollers;

import java.io.File;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import it.univaq.disim.oop.spacemusicunify.business.AlbumService;
import it.univaq.disim.oop.spacemusicunify.business.AlreadyExistingException;
import it.univaq.disim.oop.spacemusicunify.business.AlreadyTakenFieldException;
import it.univaq.disim.oop.spacemusicunify.business.BusinessException;
import it.univaq.disim.oop.spacemusicunify.business.SpacemusicunifyBusinessFactory;
import it.univaq.disim.oop.spacemusicunify.controller.DataInitializable;
import it.univaq.disim.oop.spacemusicunify.domain.Album;
import it.univaq.disim.oop.spacemusicunify.domain.Audio;
import it.univaq.disim.oop.spacemusicunify.domain.Genre;
import it.univaq.disim.oop.spacemusicunify.domain.Production;
import it.univaq.disim.oop.spacemusicunify.domain.Song;
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

public class ManageSongDetailController implements Initializable, DataInitializable<Song> {

	private final AlbumService albumService;
    private final ViewDispatcher dispatcher;

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

    private Audio tempAudio;


    public ManageSongDetailController(){
        dispatcher = ViewDispatcher.getInstance();
        SpacemusicunifyBusinessFactory factory = SpacemusicunifyBusinessFactory.getInstance();
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
                genreField.getItems().remove(Genre.singles);
                titleView.setText("Modify Song");
                confirm.setText("Modify");
                mp3button.setText("Change mp3 file");
                titleField.setText(song.getTitle());
                lengthField.setText(song.getLength());
                lyricsField.setText(song.getLyrics());

                songField.setText("MP3 Audio File loaded");

                if(album.getGenre() == Genre.singles) {
                    genreField.setDisable(false);
                }else{
                    genreField.setDisable(true);
                }
                genreField.setValue(song.getGenre());

                if(album.getSongs().size() == 1){
                    deletesong.setDisable(true);
                }
                break;

            case newobject:
                genreField.getItems().addAll(Genre.values());
                genreField.getItems().remove(Genre.singles);
                titleField.setText(song.getTitle());
                lengthField.setText(song.getLength());
                lyricsField.setText(song.getLyrics());
                deletesong.setVisible(false);




                songField.setText("No MP3 File selected");


                if(album.getGenre() == Genre.singles) {
                    genreField.setDisable(false);
                }else{
                    genreField.setDisable(true);
                }
                genreField.setValue(song.getGenre());
                if(album.getSongs().size() == 1){
                    deletesong.setDisable(true);
                }
                confirm.disableProperty().bind(lyricsField.textProperty().isEmpty().or(titleField.textProperty().isEmpty()).or(lengthField.textProperty().isEmpty()).or(existingLabel.visibleProperty()));
                mp3button.setText("Pick Up an mp3");
                titleView.setText("New Song");
                confirm.setText("Create");

                break;
                
            case user:
                title.setText(song.getTitle());
                length.setText(song.getLength());
                lyrics.setText(song.getLyrics());
                lyrics.setEditable(false);
                genretext.setText(String.valueOf(song.getGenre()));
                modify.setVisible(false);
                break;
                
            default:
            	break;
        }
    }
    @Override
    public void initialize(URL location, ResourceBundle resources) {

/*        "No MP3 File selected".equals(songField.getText());
        Object ChangeListener;
        confirm.disableProperty().bind(  );*/
       /* confirm.disableProperty().bind(lyricsField.textProperty().isEmpty().or(titleField.textProperty().isEmpty()).or(lengthField.textProperty().isEmpty()).or(existingLabel.visibleProperty()));
        titleField.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                existingLabel.setVisible(false);
            }
        });*/
    	
    	if(dispatcher.getSituation() == ViewSituations.modify)
    		confirm.disableProperty().bind(songField.textProperty().isEqualTo("No MP3 File selected")); //OK
    		//confirm.disableProperty().bind(songField.textProperty().isEqualTo("MP3 Audio File loaded")); -> per prova
    }
    @Override
    public void initializeData(Song song) {
        this.song = song;
        album = song.getAlbum();
        setView2();
    }
    @FXML
    public void confirmSong(ActionEvent event) {
    	try {

            if (song.getId() == null) {

                song.setTitle(titleField.getText());
                song.setLength(lengthField.getText());
                song.setLyrics(lyricsField.getText());
                if (album.getGenre() == Genre.singles) {
                    song.setGenre(genreField.getValue());
                } else {
                        song.setGenre(album.getGenre());
                }

                if (tempAudio != null) song.setFileMp3(tempAudio);


                albumService.add(song);
            } else {
                System.out.println("eseguo modify");
              albumService.modify(song.getId(), titleField.getText(),  tempAudio, lyricsField.getText(), album, lengthField.getText(), genreField.getValue(), song);
            }
            dispatcher.setSituation(ViewSituations.modify);
            dispatcher.renderView("AdministratorViews/ManageArtistsView/ManageAlbumsView/album_modify", album);

        } catch (AlreadyTakenFieldException e) {
            existingLabel.setText("This song's title is already taken");
            existingLabel.setVisible(true);

        } catch (AlreadyExistingException e) {
            if("audio".equals(e.getMessage())){
                existingLabel.setText("This song's file already exists");
            } else {
                existingLabel.setText("This song already exists");
            }
            existingLabel.setVisible(true);

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
    public void pickMP3(){

            FileChooser fileChoose = new FileChooser();
            File file =  fileChoose.showOpenDialog(null);

            if(file != null){
                String path = file.getPath();

                if(path.endsWith(".mp3")){
                    tempAudio = new Audio();
                    try {
                        tempAudio.setData(path);
                    } catch (BusinessException e) {
                        dispatcher.renderError(e);
                    }
                    tempAudio.setOwnership(song);
                    songField.setText("MP3 Audio File loaded");
                    existingLabel.setVisible(false);

                }else{
                    existingLabel.setText("Wrong File type, Only .mp3 are allowed");
                    existingLabel.setVisible(true);

                }

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
            dispatcher.setSituation(ViewSituations.detail);
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
