package it.univaq.disim.oop.spacemusicunify.controller.usercontrollers;

import it.univaq.disim.oop.spacemusicunify.business.*;
import it.univaq.disim.oop.spacemusicunify.controller.DataInitializable;
import it.univaq.disim.oop.spacemusicunify.domain.Song;
import it.univaq.disim.oop.spacemusicunify.domain.Playlist;
import it.univaq.disim.oop.spacemusicunify.domain.User;
import it.univaq.disim.oop.spacemusicunify.view.ViewDispatcher;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.ByteArrayInputStream;
import java.io.File;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class PlayerPaneController implements Initializable, DataInitializable<User>{
    protected static final Duration Scarto = Duration.millis(60);
	private final ViewDispatcher dispatcher;
    private final SPACEMusicUnifyService spaceMusicUnifyService;
    @FXML
    private Button playButton, nextButton, previousButton, pauseButton, volumeButton, muteButton, queueButton, addToPlaylistButton;
    @FXML
    private Slider progressSlider, volumeSlider;
    @FXML
    private Label songTitle, songAlbum, songArtist, currentTime, totalTime;

    @FXML
    private ImageView songImage, volumeImg;

    private MediaPlayer mediaPlayer;
    private User user;
    private MediaPlayerSettings mediaPlayerSettings;
    private String path = "src" + File.separator + "main" + File.separator + "resources" + File.separator + "viste" + File.separator + "UserViews" + File.separator + "UserHomeView" + File.separator + "icon" + File.separator;


    public PlayerPaneController() {
        dispatcher = ViewDispatcher.getInstance();
        SpacemusicunifyBusinessFactory factory = SpacemusicunifyBusinessFactory.getInstance();
        spaceMusicUnifyService = factory.getSPACEMusicUnifyService();
        mediaPlayerSettings = MediaPlayerSettings.getInstance();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            songImage.setImage(new Image(Files.newInputStream(Paths.get(path+"music.png"))));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    @Override
    public void initializeData(User utente) {
        this.user = utente;

        if(mediaPlayerSettings.getPlayerOnPlay() == null) {
        	mediaPlayerSettings.setPlayerOnPlay(false);
        }
        addToPlaylistButton.setDisable(true);

        playButton.setDisable(true);
        playButton.setVisible(true);
        pauseButton.setDisable(true);
        pauseButton.setVisible(false);
        nextButton.setDisable(true);
        previousButton.setDisable(true);

        if(mediaPlayerSettings.getMediaPlayer() == null) {
        	mediaPlayerSettings.setPlayerVolume(volumeSlider.getValue());
        } else {
        	volumeSlider.setValue(mediaPlayerSettings.getPlayerVolume());
        }
        
        currentTime.setText("00:00");
        totalTime.setText("00:00");



            switch (mediaPlayerSettings.getPlayerState()){
                case searchDoubleClick:
                    System.out.println("cambio effettuato");
                    this.loadSong();
                break;

                case searchSingleClick:
                    System.out.println("aggiunta");
                    if (  user.getSongQueue().size() <= 1 || mediaPlayerSettings.getLastSong() == null) {
                            System.out.println("aggiunta start");
                            this.startPlayer();
                    }else{
                        System.out.println("aggiunta resume");
                        mediaPlayerSettings.setLastSong(user.getcurrentSong());
                        this.resume();
                    }
                    break;

                case queueControllerLoad:
                    System.out.println("canzone corrente cancellata");
                    mediaPlayerSettings.setLastDuration(Duration.ZERO);
                    System.out.println(mediaPlayerSettings.getLastDuration());
                    this.loadSong();
                    System.out.println(mediaPlayerSettings.getLastDuration());
                    break;

                case queueControllerResume:
                    System.out.println("canzone non corrente cancellata");
                    this.resume();
                    break;
                case started:
                    System.out.println("default");
                    if (user.getcurrentSong() == null || user.getSongQueue().size() == 1) {
                        System.out.println("player first check passed");

                        this.startPlayer();

                    } else {
                        System.out.println("resume");

                        this.resume();
                    }
                    break;
            }


    }
    public void startPlayer(){
            if(this.loadFirstSong()) {
                System.out.println("carico prima canzone");
                Image img = null;
                try {
                    img = new Image(Files.newInputStream(Paths.get(path + "volume-up.png")));
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                volumeImg.setImage(img);
                volumeSlider.setDisable(false);
                progressSlider.setDisable(false);
                mediaPlayer.setMute(false);

            } else {
                Image img = null;
                try {
                    img = new Image(Files.newInputStream(Paths.get(path + "mute.png")));
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                volumeImg.setImage(img);
                volumeSlider.setDisable(true);
                progressSlider.setDisable(true);
                muteButton.setDisable(true);
                volumeButton.setDisable(true);
            }
        }

    @FXML
    public void showQueue(ActionEvent event) {
        dispatcher.renderView("UserViews/QueueView/queueView", user);
    }

    public void mediaPlayerModulesInitializer(){

        volumeSlider.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                if (mediaPlayer != null) {
                    mediaPlayer.setVolume(volumeSlider.getValue() * 0.01);
                    mediaPlayerSettings.setPlayerVolume(volumeSlider.getValue());

                }
            }
        });
        mediaPlayer.currentTimeProperty().addListener(new ChangeListener<Duration>() {
            @Override
            public void changed(ObservableValue<? extends Duration> observable, Duration oldValue, Duration current) {

                progressSlider.setValue(current.toSeconds());
                int minTime = (int) current.toMinutes();
                int secTime = (int) current.toSeconds();
                currentTime.setText(String.valueOf(minTime % 60)+":"+String.valueOf(secTime%60));

                if(user.getcurrentSong() == mediaPlayerSettings.getLastSong()) {
	                if(current.toSeconds() != 0){
	                	mediaPlayerSettings.setLastDuration(current);
	                }else{
	                    mediaPlayerSettings.setLastDuration(oldValue);
	                }
                } else {
                	mediaPlayerSettings.setLastDuration(current);
                	mediaPlayerSettings.setLastSong(user.getcurrentSong());
                }
                
                if(mediaPlayerSettings.getLastDuration().greaterThanOrEqualTo(Duration.millis(mediaPlayer.getTotalDuration().toMillis() - Scarto.toMillis()))) {
                	if(user.getcurrentPosition() < user.getSongQueue().size() - 1) {
                		mediaPlayerSettings.setLastSong(user.getcurrentSong());
                        spaceMusicUnifyService.updateCurrentSong(user, user.getcurrentPosition() + 1);
                    	mediaPlayerSettings.setPlayerOnPlay(true);

                        mediaPlayer.stop();
                        mediaPlayer.dispose();
                        loadSong();
                        System.out.println("prossima");
                        previousButton.setDisable(false);
                	}
                }
            }
        });

        progressSlider.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                mediaPlayer.seek(Duration.seconds(progressSlider.getValue()));
            }
        });

        progressSlider.setOnMouseDragged(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                mediaPlayer.seek(Duration.seconds(progressSlider.getValue()));
            }
        });

        mediaPlayer.setOnReady(new Runnable() {
            @Override
            public void run() {

                progressSlider.setMax(mediaPlayer.getTotalDuration().toSeconds());

                if(mediaPlayerSettings.getLastDuration() != null) {
                    Duration duration = mediaPlayerSettings.getLastDuration();
                    int minTime = (int) duration.toMinutes();
                    int secTime = (int) duration.toSeconds();
                    currentTime.setText(String.valueOf(minTime % 60)+":"+String.valueOf(secTime % 60));
                    
                    mediaPlayer.seek(Duration.seconds(duration.toSeconds()));
                    
                   progressSlider.setValue(duration.toSeconds());
                    
                }
            }
        });
        
        if(mediaPlayerSettings.getPlayerOnPlay()) {
            mediaPlayer.play();
        }

        mediaPlayer.setVolume(volumeSlider.getValue() * 0.01);
    }
    
    private boolean loadFirstSong() {
        Song song;
        song = user.getcurrentSong();
        System.out.println("first song "+user.getcurrentSong());
        if(song != null) {
            songTitle.setText(song.getTitle());

            songArtist.setText(song.getAlbum().getArtist().getStageName());
            songAlbum.setText(song.getAlbum().getTitle());
            songImage.setImage(new Image(new ByteArrayInputStream(song.getAlbum().getCover().getPhoto())));
            totalTime.setText(song.getLength());

            mediaPlayerSettings.setLastDuration(Duration.ZERO);
            mediaPlayerSettings.startMediaPlayer(new Media(Paths.get(user.getcurrentSong().getFileMp3()).toUri().toString()));
            //Files.newInputStream(Paths.get(user.getcurrentSong().getFileMp3()))
            mediaPlayer = mediaPlayerSettings.getMediaPlayer();

            this.mediaPlayerModulesInitializer();

            Image img = null;
            try {
                img = new Image(Files.newInputStream(Paths.get(path + "volume-up.png")));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            volumeImg.setImage(img);
            volumeButton.setDisable(false);
            addToPlaylistButton.setDisable(false);

            playButton.setDisable(false);
            playButton.setVisible(true);
            pauseButton.setDisable(false);
            pauseButton.setVisible(false);
            mediaPlayer.setVolume(volumeSlider.getValue() * 0.01);
            mediaPlayerSettings.setMute(false);

            if(user.getSongQueue().size() > user.getcurrentPosition() + 1) {
                nextButton.setDisable(false);
            }else{
                nextButton.setDisable(true);
            }
            if(user.getcurrentPosition() > 0) {
                previousButton.setDisable(false);
            }else {
                previousButton.setDisable(true);
            }

            System.out.println("last song: " + mediaPlayerSettings.getLastSong());
        	System.out.println("current: " + user.getcurrentSong());
            mediaPlayerSettings.setLastSong(song);
            System.out.println("update last: " + mediaPlayerSettings.getLastSong());
            return true;
        }

        return false;

    }
    public void loadSong() {
        Song song;
        song = user.getcurrentSong();

        if(song != null) {
            songTitle.setText(song.getTitle());

            songArtist.setText(song.getAlbum().getArtist().getStageName());
            songAlbum.setText(song.getAlbum().getTitle());
            songImage.setImage(new Image(new ByteArrayInputStream(song.getAlbum().getCover().getPhoto())));

            mediaPlayerSettings.setLastDuration(Duration.ZERO);
            mediaPlayerSettings.startMediaPlayer(new Media(Paths.get(user.getcurrentSong().getFileMp3()).toUri().toString()));
            mediaPlayer = mediaPlayerSettings.getMediaPlayer();



            this.mediaPlayerModulesInitializer();

            totalTime.setText(song.getLength());

            if(mediaPlayerSettings.isMute()) {
                Image img = null;
                try {
                    img = new Image(Files.newInputStream(Paths.get(path+"mute.png")));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                volumeImg.setImage(img);
                volumeSlider.setDisable(true);
                mediaPlayer.setMute(true);
            } else {
                Image img = null;
                try {
                    img = new Image(Files.newInputStream(Paths.get(path+"volume-up.png")));
                } catch (FileNotFoundException e) {
                    throw new RuntimeException(e);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                volumeImg.setImage(img);
                volumeSlider.setDisable(false);
                mediaPlayer.setMute(false);
            }

            addToPlaylistButton.setDisable(false);

            playButton.setDisable(false);
            pauseButton.setDisable(false);

            if(user.getSongQueue().size() > user.getcurrentPosition() + 1) {
                nextButton.setDisable(false);
            }else{
                nextButton.setDisable(true);
            }
            if(user.getcurrentPosition() > 0) {
                previousButton.setDisable(false);
            }else {
                previousButton.setDisable(true);
            }

            if(mediaPlayerSettings.getPlayerOnPlay()) {
                pauseButton.setVisible(true);
                playButton.setVisible(false);
            }else{
                pauseButton.setVisible(false);
                playButton.setVisible(true);
            }
        }
    }
    public void resume() {
        Song song = user.getcurrentSong();
        songTitle.setText(song.getTitle());

        songArtist.setText(song.getAlbum().getArtist().getStageName());
        songAlbum.setText(song.getAlbum().getTitle());
        songImage.setImage(new Image(new ByteArrayInputStream(song.getAlbum().getCover().getPhoto())));

        if(mediaPlayerSettings.getPlayerOnPlay()) {
            pauseButton.setVisible(true);
            playButton.setVisible(false);
        }else{
            pauseButton.setVisible(false);
            playButton.setVisible(true);
        }

        mediaPlayerSettings.startMediaPlayer(new Media(Paths.get(user.getcurrentSong().getFileMp3()).toUri().toString()));
        mediaPlayer = mediaPlayerSettings.getMediaPlayer();


        
	        if(mediaPlayerSettings.isMute()) {
	            Image img = null;
	            try {
	                img = new Image(Files.newInputStream(Paths.get(path+"mute.png")));
	            } catch (IOException e) {
	                throw new RuntimeException(e);
	            }
                volumeImg.setImage(img);
	            volumeSlider.setDisable(true);
	            mediaPlayer.setMute(true);
	        } else {
	            Image img = null;
	            try {
	                img = new Image(Files.newInputStream(Paths.get(path + "volume-up.png")));
	            } catch (IOException e) {
	                throw new RuntimeException(e);
	            }
                volumeImg.setImage(img);
	            volumeSlider.setDisable(false);
	            mediaPlayer.setMute(false);
	        }
        
        
        this.mediaPlayerModulesInitializer();

        addToPlaylistButton.setDisable(false);


        if(user.getSongQueue().size() > user.getcurrentPosition() + 1) {
            nextButton.setDisable(false);
        }else{
            nextButton.setDisable(true);
        }
        if(user.getcurrentPosition() > 0) {
            previousButton.setDisable(false);
        }else {
            previousButton.setDisable(true);
        }
        playButton.setDisable(false);
        pauseButton.setDisable(false);
        volumeSlider.setValue(mediaPlayerSettings.getPlayerVolume());
        totalTime.setText(song.getLength());
        
    }
    
    public void muteSong(ActionEvent event) {
        if(mediaPlayer.isMute()){
            Image img = null;
            try {
                img = new Image(Files.newInputStream(Paths.get(path+"volume-up.png")));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            volumeImg.setImage(img);
            volumeSlider.setDisable(false);
            mediaPlayer.setMute(false);
            mediaPlayerSettings.setMute(false);
        }else{
            Image img = null;
            try {
                img = new Image(Files.newInputStream(Paths.get(path+"mute.png")));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            volumeImg.setImage(img);
            volumeSlider.setDisable(true);
            mediaPlayer.setMute(true);
            mediaPlayerSettings.setMute(true);
        }

    }

    public void pauseSong(ActionEvent event) {

    	mediaPlayerSettings.setPlayerOnPlay(false);
        mediaPlayer.pause();
        playButton.setVisible(true);
        pauseButton.setVisible(false);
    }

    public void previousSong(ActionEvent event) {
        mediaPlayerSettings.setLastSong(user.getcurrentSong());
        spaceMusicUnifyService.updateCurrentSong(user, user.getcurrentPosition() - 1);
    	mediaPlayerSettings.setPlayerOnPlay(true);

        mediaPlayer.stop();
        mediaPlayer.dispose();
        this.loadSong();
        nextButton.setDisable(false);
        System.out.println("precedente");

    }

    public void nextSong(ActionEvent event) {

        mediaPlayerSettings.setLastSong(user.getcurrentSong());
        spaceMusicUnifyService.updateCurrentSong(user, user.getcurrentPosition() + 1);
    	mediaPlayerSettings.setPlayerOnPlay(true);

        mediaPlayer.stop();
        mediaPlayer.dispose();
        this.loadSong();
        System.out.println("prossima");
        previousButton.setDisable(false);

    }

    public void playSong(ActionEvent event) {
    	mediaPlayerSettings.setPlayerOnPlay(true);
        mediaPlayer.play();
        playButton.setVisible(false);
        pauseButton.setVisible(true);
    }

    public void addThisSongToPlaylist(ActionEvent event) {
    	Stage popupwindow = new Stage();
		popupwindow.initModality(Modality.APPLICATION_MODAL);
		Label title = new Label("Add " + user.getcurrentSong().getTitle() + " to which playlist?");
		title.setAlignment(Pos.CENTER);
		//selezione multipla playlist
		TableView<Playlist> tableView = new TableView<>();
		TableColumn<Playlist, String> name = new TableColumn<>("Title");
		TableColumn<Playlist, Button> add = new TableColumn<>();
		name.setCellValueFactory(new PropertyValueFactory<>("title"));
		add.setCellValueFactory((TableColumn.CellDataFeatures<Playlist,Button> param) -> {

			final Button addButton = new Button("Add here");

            if(this.checkForClones(param.getValue())){
                addButton.setDisable(true);
            }

			addButton.setOnAction((ActionEvent evento) -> {
				//aggiunto album alla playlist
				List<Song> lista = param.getValue().getSongList();
				Boolean alreadyAdded = false;
				for(Song canzonePlaylist: lista) {
					if(user.getcurrentSong().getId().intValue() == canzonePlaylist.getId().intValue()) {
						alreadyAdded = true;
						break;
					}
				}
				if(!alreadyAdded) {
					lista.add(user.getcurrentSong());
				}

				try {
                    spaceMusicUnifyService.modify(param.getValue().getId(), param.getValue().getTitle(),lista, param.getValue().getUser());
				} catch (BusinessException e) {
					 e.printStackTrace();
				}
				addButton.setDisable(true);
			});
			return new SimpleObjectProperty<>(addButton);
		});
        tableView.getColumns().add(name);
        tableView.getColumns().add(add);
		
		try {
			ObservableList<Playlist> observableList = FXCollections.observableArrayList(spaceMusicUnifyService.getAllPlaylists(user));
			tableView.setItems(observableList);
		} catch (BusinessException e1) {
			e1.printStackTrace();
		}
		
		// operazione annulla
		Button closeButton = new Button("Close");
		closeButton.setCursor(Cursor.HAND);
		closeButton.setOnAction(e -> {
			dispatcher.renderPlaylists("UserViews/UserHomeView/playlistPane", user);
			popupwindow.close();
		});

		VBox layout = new VBox(10);
		layout.getChildren().addAll(title, tableView, closeButton);
		layout.setAlignment(Pos.CENTER);
		Scene scene1 = new Scene(layout, 300, 150);
		popupwindow.setScene(scene1);
		popupwindow.setResizable(false);
		popupwindow.setTitle("Add to playlist");
		popupwindow.showAndWait();
    }

    public void showSongInfo(MouseEvent mouseEvent) {
        if (user.getcurrentSong() != null) {
            spaceMusicUnifyService.setSituation(ViewSituations.user);
            dispatcher.renderView("AdministratorViews/ManageArtistsView/ManageAlbumsView/song_detail", user.getcurrentSong());
        }
    }
    public boolean checkForClones(Playlist playlist){
        for(Song canzone : playlist.getSongList()){
            if(canzone.getId().equals( user.getcurrentSong().getId())){
                return true;
            }
        }

        return false;
    }
}
