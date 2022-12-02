package it.univaq.disim.oop.spacemusicunify.controller.usercontrollers;

import it.univaq.disim.oop.spacemusicunify.business.*;
import it.univaq.disim.oop.spacemusicunify.controller.DataInitializable;
import it.univaq.disim.oop.spacemusicunify.domain.*;
import it.univaq.disim.oop.spacemusicunify.view.SpacemusicunifyPlayer;
import it.univaq.disim.oop.spacemusicunify.view.ViewDispatcher;
import it.univaq.disim.oop.spacemusicunify.view.ViewSituations;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ListChangeListener.Change;
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
import javafx.scene.media.MediaPlayer.Status;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.ByteArrayInputStream;
import java.io.File;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class PlayerPaneController implements Initializable, DataInitializable<User>{
    protected static final Duration Scarto = Duration.millis(60);
	private static final String path = "src" + File.separator + "main" + File.separator + "resources" + File.separator + "views" + File.separator + "UserViews" + File.separator + "HomeView" + File.separator + "icon" + File.separator;
	private static final String volumeEnabled = path + "volume-up.png";
	private static final String volumeDisabled = path + "mute.png";
	private final ViewDispatcher dispatcher;
    private final UserService userService;
	private PlayerService playerService;
	private static final String MyStyle = "views/controllerStyle.css";	
	
    @FXML
    private Button playButton, nextButton, previousButton, pauseButton, volumeButton, queueButton, addToPlaylistButton;
    @FXML
    private Slider progressSlider, volumeSlider;
    @FXML
    private Label songTitle, songAlbum, songArtist, currentTime, totalTime;
    @FXML
    private ImageView songImage, volumeImg;

    private SpacemusicunifyPlayer spacemusicunifyPlayer;
    private User user;

    public PlayerPaneController() {
        dispatcher = ViewDispatcher.getInstance();
        SpacemusicunifyBusinessFactory factory = SpacemusicunifyBusinessFactory.getInstance();
        userService = factory.getUserService();
        playerService = factory.getPlayerService();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            songImage.setImage(new Image(Files.newInputStream(Paths.get(path+"music.png"))));
        } catch (IOException e) {
            dispatcher.renderError(e);
        }
    }
    @Override
    public void initializeData(User user) {
    	currentTime.setText("00:00");
        totalTime.setText("00:00");
        
        this.user = user;

		this.spacemusicunifyPlayer = RunTimeService.getPlayer();
		
		if(spacemusicunifyPlayer.getChangeListener() == null) {
			spacemusicunifyPlayer.setChangeListener((ListChangeListener.Change<? extends Song> c) -> {
				if(spacemusicunifyPlayer.getQueue().size() > 0) {
					//viene riabilitato il player
					addToPlaylistButton.setDisable(false);
					playButton.setDisable(false);
					pauseButton.setDisable(false);
					progressSlider.setDisable(false);
					volumeButton.setDisable(false);
					volumeSlider.setDisable(false);
					if(spacemusicunifyPlayer.getMediaPlayer() == null) {
						loadSong();
						//dispatcher.renderView("UserViews/HomeView/playerPane", user); //per garantire una grafica funzionante
					} else if(c.next() == c.wasReplaced()) {
						loadSong();
					}
				} else {
					/**
					 * player initial state/ player reset state
					 */
					addToPlaylistButton.setDisable(true);
					playButton.setDisable(true);
					pauseButton.setDisable(true);
					nextButton.setDisable(true);
					previousButton.setDisable(true);
					progressSlider.setDisable(true);
					volumeButton.setDisable(true);
					volumeSlider.setDisable(true);
					spacemusicunifyPlayer.setMediaPlayer(null);
					playButton.setVisible(true);
					pauseButton.setVisible(false);
					songTitle.setText("songName");
					songAlbum.setText("albumName");
					songArtist.setText("artistsName");
					try {
			            songImage.setImage(new Image(Files.newInputStream(Paths.get(path+"music.png"))));
			        } catch (IOException e) {
			            dispatcher.renderError(e);
			        }
				}
				if(spacemusicunifyPlayer.getCurrentSong() >= spacemusicunifyPlayer.getQueue().size() - 1) {
					nextButton.setDisable(true);
				} else {
					nextButton.setDisable(false);
				}
				if(spacemusicunifyPlayer.getCurrentSong() == 0) previousButton.setDisable(true);
				else previousButton.setDisable(false);
	
				if(c.next() == c.wasAdded() && (spacemusicunifyPlayer.getMediaPlayer().getStatus() == Status.DISPOSED || spacemusicunifyPlayer.getMediaPlayer() == null)) { //riabilitazioni successive del player
					loadSong();
				}
				
				Image img;
				try {
					if(spacemusicunifyPlayer.isMute()) {
						img = new Image(Files.newInputStream(Paths.get(path + "mute.png")));
						volumeImg.setImage(img);
						if(spacemusicunifyPlayer.getMediaPlayer() != null) {
							volumeButton.setDisable(false);
							volumeSlider.setDisable(true);
						}
					}
					
				} catch (IOException e) {
					dispatcher.renderError(e);
				}
				//dispatcher.renderView("UserViews/HomeView/playerPane", user); //per garantire una grafica funzionante
			});
			
			spacemusicunifyPlayer.getQueue().addListener(spacemusicunifyPlayer.getChangeListener());
		}

		/*
		 * if(playerService.getPlayerOnPlay() == null) {
		 * playerService.setPlayerOnPlay(false); }
		 */
        if(spacemusicunifyPlayer.getMediaPlayer() == null && spacemusicunifyPlayer.getQueue().size() == 0) {
	        addToPlaylistButton.setDisable(true);
	        playButton.setDisable(true);
	        playButton.setVisible(true);
	        pauseButton.setDisable(true);
	        pauseButton.setVisible(false);
	        nextButton.setDisable(true);
	        previousButton.setDisable(true);
	        progressSlider.setDisable(true);
        	volumeButton.setDisable(true);
        	volumeSlider.setDisable(true);
        } else {
        	Duration lastDuration = spacemusicunifyPlayer.getDuration();
        	double lastVolume = spacemusicunifyPlayer.getVolume() * 100;
        	boolean isPaused = !spacemusicunifyPlayer.isPlay();
        	addToPlaylistButton.setDisable(false);
	        playButton.setDisable(false);
	        pauseButton.setDisable(false);
	        if(spacemusicunifyPlayer.getCurrentSong() > 0) previousButton.setDisable(false); else previousButton.setDisable(true);
	        if(spacemusicunifyPlayer.getCurrentSong() < spacemusicunifyPlayer.getQueue().size() - 1) nextButton.setDisable(false); else nextButton.setDisable(true);
	        progressSlider.setDisable(false);
        	loadSong();
        	if(isPaused) {
        		spacemusicunifyPlayer.setPlay(false);
        		playButton.setVisible(true);
        		pauseButton.setVisible(false);
        	}
			try {
				playerService.updateDuration(spacemusicunifyPlayer, lastDuration);
			} catch (BusinessException e) {
				dispatcher.renderError(e);
			}
        	/*spacemusicunifyPlayer.setDuration(lastDuration);*/
        	volumeSlider.setValue(lastVolume);
        }

    }

    @FXML
    public void showQueue(ActionEvent event) {
        dispatcher.renderView("UserViews/QueueView/queueView", user);
    }

    public void mediaPlayerModulesInitializer(){

    	spacemusicunifyPlayer.getMediaPlayer().setVolume(volumeSlider.getValue() * 0.01);

        volumeSlider.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
               // if (spacemusicunifyPlayer.getMediaPlayer() != null) {

				try {
					playerService.updateVolume(spacemusicunifyPlayer, volumeSlider.getValue() * 0.01);
				} catch (BusinessException e) {
					dispatcher.renderError(e);
				}
				/*spacemusicunifyPlayer.setVolume(volumeSlider.getValue() * 0.01);*/
               // }
            }
        });
        spacemusicunifyPlayer.getMediaPlayer().currentTimeProperty().addListener(new ChangeListener<Duration>() {
        	
            @Override
            public void changed(ObservableValue<? extends Duration> observable, Duration oldValue, Duration current) {
                progressSlider.setValue(current.toSeconds());
                int minTime = (int) current.toMinutes();
                int secTime = (int) current.toSeconds();
                String minutes = String.valueOf(minTime % 60);
                String seconds = String.valueOf(secTime%60);
                if(minTime % 60 < 10) minutes = "0" + minutes;
                if(secTime % 60 < 10) seconds = "0" + seconds;
                currentTime.setText(minutes + ":" + seconds);
                if(current != Duration.ZERO) {
					try {
						playerService.updateDuration(spacemusicunifyPlayer, current);
					} catch (BusinessException e) {
						dispatcher.renderError(e);
					}
					/*spacemusicunifyPlayer.setDuration(current);*/
				}
				
				if(spacemusicunifyPlayer.getMediaPlayer() != null && current.greaterThanOrEqualTo(Duration.millis(spacemusicunifyPlayer.getMediaPlayer().getTotalDuration().toMillis() - Scarto.toMillis()))) {
                	if(spacemusicunifyPlayer.getCurrentSong() < spacemusicunifyPlayer.getQueue().size() - 1) {
                        try {
							playerService.updateCurrentSong(spacemusicunifyPlayer, spacemusicunifyPlayer.getCurrentSong() + 1);
							loadSong();
							previousButton.setDisable(false);
							if(spacemusicunifyPlayer.getCurrentSong() == spacemusicunifyPlayer.getQueue().size() - 1) nextButton.setDisable(true);
                        } catch (BusinessException e) {
							dispatcher.renderError(e);
						}
                	}
                }
                
            }
        });

        progressSlider.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
            	spacemusicunifyPlayer.getMediaPlayer().seek(Duration.seconds(progressSlider.getValue()));
            }
        });

        progressSlider.setOnMouseDragged(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
            	spacemusicunifyPlayer.getMediaPlayer().seek(Duration.seconds(progressSlider.getValue()));
            }
        });

        spacemusicunifyPlayer.getMediaPlayer().setOnReady(new Runnable() {
            @Override
            public void run() {

                progressSlider.setMax(spacemusicunifyPlayer.getMediaPlayer().getTotalDuration().toSeconds());

                if(spacemusicunifyPlayer.getDuration() != null) {
                    Duration duration = spacemusicunifyPlayer.getDuration();
                    int minTime = (int) duration.toMinutes();
                    int secTime = (int) duration.toSeconds();
                    currentTime.setText(String.valueOf(minTime % 60)+":"+String.valueOf(secTime % 60));
                    
                    spacemusicunifyPlayer.getMediaPlayer().seek(Duration.seconds(duration.toSeconds()));
                    
                   progressSlider.setValue(duration.toSeconds());
                    
                }
            }
        });
        
    }

    public void loadSong() {
		if (spacemusicunifyPlayer.getMediaPlayer() != null && spacemusicunifyPlayer.getMediaPlayer().getStatus() != MediaPlayer.Status.STOPPED){
			spacemusicunifyPlayer.getMediaPlayer().stop();
			spacemusicunifyPlayer.getMediaPlayer().dispose();
		}
        Song song = spacemusicunifyPlayer.getQueue().get(spacemusicunifyPlayer.getCurrentSong());
        if(song != null) {
            songTitle.setText(song.getTitle());
            String artists = "";
            try {
				for(Artist artist : SpacemusicunifyBusinessFactory.getInstance().getAlbumService().findAllArtists(song.getAlbum())){
					artists = artists + artist.getName() + ", ";

				}
			} catch (BusinessException e) {
				dispatcher.renderError(e);
			}
            songArtist.setText(artists.substring(0, artists.length() - 2));
            songAlbum.setText(song.getAlbum().getTitle());
            songImage.setImage(new Image(new ByteArrayInputStream(song.getAlbum().getCover().getData())));
            spacemusicunifyPlayer.setMediaPlayer(new MediaPlayer(getMediaFromBytes(song)));

            this.mediaPlayerModulesInitializer();

            totalTime.setText(song.getLength());
            
			try {
				playerService.updateDuration(spacemusicunifyPlayer, Duration.ZERO);

				/*spacemusicunifyPlayer.setDuration(Duration.ZERO);*/
				spacemusicunifyPlayer.setPlay(true);
				pauseButton.setVisible(true);
				playButton.setVisible(false);
				if(spacemusicunifyPlayer.isMute()) {
					playerService.updateMute(spacemusicunifyPlayer, true);
					volumeImg.setImage(new Image(Files.newInputStream(Paths.get(volumeDisabled))));
					volumeSlider.setDisable(true);
				}
			} catch (IOException | BusinessException e) {
				dispatcher.renderError(e);
			}

			if(dispatcher.getLayout().getCenter() != null && "song_detail".equals(dispatcher.getLayout().getCenter().getId() )) dispatcher.renderView("AdministratorViews/ManageArtistsView/ManageAlbumsView/ManageSongsView/song_detail", song);

		}
    }

    public void muteSong() {
		try {
			if(spacemusicunifyPlayer.isMute()){
				Image img;

					img = new Image(Files.newInputStream(Paths.get(volumeEnabled)));

				volumeImg.setImage(img);
				volumeSlider.setDisable(false);
				playerService.updateMute(spacemusicunifyPlayer, false);

			}else{
				Image img;

				img = new Image(Files.newInputStream(Paths.get(volumeDisabled)));

				volumeImg.setImage(img);
				volumeSlider.setDisable(true);
				playerService.updateMute(spacemusicunifyPlayer, true);
			}
			
		} catch (IOException | BusinessException e) {
			dispatcher.renderError(e);
		}
    }

    public void pauseSong(ActionEvent event) {
    	spacemusicunifyPlayer.setPlay(false);
        playButton.setVisible(true);
        pauseButton.setVisible(false);
    }

    public void previousSong(ActionEvent event) {
    	spacemusicunifyPlayer.getMediaPlayer().stop();
    	spacemusicunifyPlayer.getMediaPlayer().dispose();
		try {
			playerService.updateCurrentSong(spacemusicunifyPlayer, spacemusicunifyPlayer.getCurrentSong() - 1);
		} catch (BusinessException e) {
			dispatcher.renderError(e);
		}
		
        loadSong();

		spacemusicunifyPlayer.setPlay(true);
        nextButton.setDisable(false);
        if(spacemusicunifyPlayer.getCurrentSong() == 0) previousButton.setDisable(true);
    }

    public void nextSong(ActionEvent event) {

    	spacemusicunifyPlayer.getMediaPlayer().stop();
    	spacemusicunifyPlayer.getMediaPlayer().dispose();
		try {
			playerService.updateCurrentSong(spacemusicunifyPlayer, spacemusicunifyPlayer.getCurrentSong() + 1);
		} catch (BusinessException e) {
			dispatcher.renderError(e);
		}
		
        loadSong();

        spacemusicunifyPlayer.setPlay(true);
        previousButton.setDisable(false);
        if(spacemusicunifyPlayer.getCurrentSong() == spacemusicunifyPlayer.getQueue().size() - 1) nextButton.setDisable(true);
    }

    public void playSong(ActionEvent event) {
    	spacemusicunifyPlayer.setPlay(true);
        playButton.setVisible(false);
        pauseButton.setVisible(true);
    }

    @FXML
    public void addThisSongToPlaylist(ActionEvent event) {
    	Song songToAdd = spacemusicunifyPlayer.getQueue().get(spacemusicunifyPlayer.getCurrentSong());
    	Stage popupwindow = new Stage();
		popupwindow.initModality(Modality.APPLICATION_MODAL);
		Label title = new Label("Add " + songToAdd.getTitle() + " to which playlist?");
		title.setAlignment(Pos.CENTER);
		//selezione multipla playlist
		TableView<Playlist> tableView = new TableView<>();
		tableView.setId("table");
		TableColumn<Playlist, String> name = new TableColumn<>("Title");
		TableColumn<Playlist, Button> add = new TableColumn<>();
		name.setCellValueFactory(new PropertyValueFactory<>("title"));
		add.setCellValueFactory((TableColumn.CellDataFeatures<Playlist,Button> param) -> {

			final Button addButton = new Button("Add here");
			addButton.setId("b2");
			if(checkForClones(param.getValue(), songToAdd))addButton.setDisable(true);
			addButton.setOnAction((ActionEvent evento) -> {

				//aggiunta canzone alla playlist
				Set<Song> list = param.getValue().getSongList();
				list.add(songToAdd);
				
				try {
					userService.modify(list, param.getValue());
					if("playlist".equals(dispatcher.getLayout().getCenter().getId() )) dispatcher.renderView("UserViews/PlaylistView/playlistView", param.getValue());
					dispatcher.renderView("UserViews/HomeView/playlistPane", param.getValue().getUser());

				} catch (BusinessException e) {
					dispatcher.renderError(e);
				}
				addButton.setDisable(true);
			});
			return new SimpleObjectProperty<>(addButton);
		});
		
        tableView.getColumns().add(name);
        tableView.getColumns().add(add);
		
		try {
			ObservableList<Playlist> observableList = FXCollections.observableArrayList(userService.getAllPlaylists(user));
			tableView.setItems(observableList);
		} catch (BusinessException e) {
			dispatcher.renderError(e);
		}
		
		// operazione annulla
		Button closeButton = new Button("Close");
		closeButton.setId("b1");
		closeButton.setCursor(Cursor.HAND);
		closeButton.setOnAction(e -> {
			popupwindow.close();
		});

		VBox layout = new VBox(10);
		layout.getChildren().addAll(title, tableView, closeButton);
		layout.setAlignment(Pos.CENTER);
		Scene scene1 = new Scene(layout, 300, 150);
		scene1.getStylesheets().add(MyStyle);
		popupwindow.setScene(scene1);
		popupwindow.setResizable(false);
		popupwindow.setTitle("Add to playlist");
		popupwindow.showAndWait();
    }

    public void showSongInfo() {
		if((!spacemusicunifyPlayer.getQueue().isEmpty())) {
			Song song = spacemusicunifyPlayer.getQueue().get(spacemusicunifyPlayer.getCurrentSong());
			if (song != null) {
				dispatcher.setSituation(ViewSituations.user);
				dispatcher.renderView("AdministratorViews/ManageArtistsView/ManageAlbumsView/ManageSongsView/song_detail", song);
			}
		}
    }
	public boolean checkForClones(Playlist playlist,Song value){

		for(Song songs : playlist.getSongList()){
			if(songs.getId().intValue() == value.getId().intValue()) return true;
		}
		return false;
	}
    private Media getMediaFromBytes(Song song) {
    	try {
			File tempMp3 = File.createTempFile("tempmp3", ".mp3", null);
			tempMp3.deleteOnExit();
			FileOutputStream fos = new FileOutputStream(tempMp3);
			fos.write(song.getFileMp3().getData());
			fos.close();
			return new Media(tempMp3.toURI().toURL().toString());
		} catch (IOException e) {
			dispatcher.renderError(e);
		}
    	return null;
    }
    
}
