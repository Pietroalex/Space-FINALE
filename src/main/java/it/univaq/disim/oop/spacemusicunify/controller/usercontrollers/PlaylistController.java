package it.univaq.disim.oop.spacemusicunify.controller.usercontrollers;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.Set;

import it.univaq.disim.oop.spacemusicunify.business.*;
import it.univaq.disim.oop.spacemusicunify.controller.DataInitializable;
import it.univaq.disim.oop.spacemusicunify.domain.Song;
import it.univaq.disim.oop.spacemusicunify.domain.Playlist;
import it.univaq.disim.oop.spacemusicunify.domain.User;
import it.univaq.disim.oop.spacemusicunify.view.SpacemusicunifyPlayer;
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
import javafx.scene.media.MediaPlayer;

public class PlaylistController implements Initializable, DataInitializable<Playlist>{

	private final UserService userService;
	@FXML
	private TableView<Song> playlistTable;
	@FXML
	private TableColumn<Song, String> songName;
	@FXML
	private TableColumn<Song, String> artistName;
	@FXML
	private TableColumn<Song, String> albumName;
	@FXML
	private TableColumn<Song, String> duration;
	@FXML
	private TableColumn<Song, Button> delete;
	@FXML
	private Label playlistName;
	@FXML
	private Button play;
	@FXML
	private Button deleteButton;
	
	private Playlist playlist;
	
	private ViewDispatcher dispatcher;
	
	private PlayerService playerService;
	
	public PlaylistController() {
		dispatcher = ViewDispatcher.getInstance();
		SpacemusicunifyBusinessFactory factory = SpacemusicunifyBusinessFactory.getInstance();
		userService = factory.getUserService();
		playerService = factory.getPlayerService();
	}
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		songName.setCellValueFactory(new PropertyValueFactory<>("title"));
		artistName.setCellValueFactory((TableColumn.CellDataFeatures<Song, String> param) -> {

			return new SimpleStringProperty(/*param.getValue().getAlbum().getArtist().getStageName()*/);
		});
		albumName.setCellValueFactory((TableColumn.CellDataFeatures<Song, String> param) -> {
			String albumName = "";
			if(playlist.getSongList() != null) {
				albumName = param.getValue().getAlbum().getTitle();
			}
			return new SimpleStringProperty(albumName);
		});
		duration.setCellValueFactory(new PropertyValueFactory<>("length"));
		delete.setStyle("-fx-alignment: CENTER;");
	}

	@Override
	public void initializeData(Playlist playlist) {
		this.playlist = playlist;
		delete.setCellValueFactory((TableColumn.CellDataFeatures<Song, Button> param) -> {
			final Button deleteButton = new Button("Delete");
			deleteButton.setCursor(Cursor.HAND);
			deleteButton.setOnAction((ActionEvent event) -> {
				
				playlist.getSongList().remove(param.getValue());
				try {
					userService.modify(playlist.getId(),playlist.getTitle(),playlist.getSongList(),playlist.getUser(), playlist);
				} catch (BusinessException e) {
					e.printStackTrace();
				}
				
				initializeTable();
			});
			return new SimpleObjectProperty<>(deleteButton);
		});
		
		playlistName.setText(playlist.getTitle());
		initializeTable();
	}

	private void initializeTable() {
		Set<Song> songList = playlist.getSongList();
		ObservableList<Song> playlistData = FXCollections.observableArrayList(songList);
		playlistTable.setItems(playlistData);
	}
	
	@FXML
	public void addPlaylistToQueue() {
		User user = playlist.getUser();
		Set<Song> lista = playlist.getSongList();
		for(Song canzonePlaylist: lista) {
			Boolean alreadyAdded = false;
			/*for(Song canzone: utente.getSongQueue()) {
				if(canzonePlaylist.getId().intValue() == canzone.getId().intValue()) {
					alreadyAdded = true;
					break;
				}
			}
			if(!alreadyAdded) {
				utente.getSongQueue().add(canzonePlaylist);
			}*/
		}
		
		SpacemusicunifyPlayer spacemusicunifyPlayer = RunTimeService.getPlayer();
			if(spacemusicunifyPlayer.getMediaPlayer() != null && spacemusicunifyPlayer.getMediaPlayer().getStatus() != MediaPlayer.Status.STOPPED){
				spacemusicunifyPlayer.getMediaPlayer().stop();
				spacemusicunifyPlayer.getMediaPlayer().dispose();
			}


		
		playerService.setPlayerState(PlayerState.searchSingleClick);
		dispatcher.renderView("UserViews/HomeView/playerPane", user);
	}
	
	@FXML
	public void deletePlaylist(ActionEvent event) {
		try {
			userService.delete(playlist);
		} catch (BusinessException e) {
			dispatcher.renderError(e);
		}
		dispatcher.renderView("UserViews/HomeView/home", RunTimeService.getCurrentUser());
		dispatcher.renderView("UserViews/HomeView/playlistPane", RunTimeService.getCurrentUser());
	}
}
