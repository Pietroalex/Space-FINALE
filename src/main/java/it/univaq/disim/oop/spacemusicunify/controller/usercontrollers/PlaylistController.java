package it.univaq.disim.oop.spacemusicunify.controller.usercontrollers;

import java.net.URL;
import java.util.List;
import java.util.Queue;
import java.util.ResourceBundle;
import java.util.Set;

import it.univaq.disim.oop.spacemusicunify.business.BusinessException;
import it.univaq.disim.oop.spacemusicunify.business.MediaPlayerSettings;
import it.univaq.disim.oop.spacemusicunify.business.PlayerState;
import it.univaq.disim.oop.spacemusicunify.business.SpacemusicunifyBusinessFactory;
import it.univaq.disim.oop.spacemusicunify.business.UtenteService;
import it.univaq.disim.oop.spacemusicunify.controller.DataInitializable;
import it.univaq.disim.oop.spacemusicunify.domain.Canzone;
import it.univaq.disim.oop.spacemusicunify.domain.Playlist;
import it.univaq.disim.oop.spacemusicunify.domain.Utente;
import it.univaq.disim.oop.spacemusicunify.view.ViewDispatcher;
import it.univaq.disim.oop.spacemusicunify.view.ViewException;
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

	@FXML
	private TableView<Canzone> playlistTable;
	@FXML
	private TableColumn<Canzone, String> songName;
	@FXML
	private TableColumn<Canzone, String> artistName;
	@FXML
	private TableColumn<Canzone, String> albumName;
	@FXML
	private TableColumn<Canzone, String> duration;
	@FXML
	private TableColumn<Canzone, Button> delete;
	@FXML
	private Label playlistName;
	@FXML
	private Button play;
	@FXML
	private Button deleteButton;
	
	private Playlist playlist;
	
	private ViewDispatcher dispatcher;
	
	private UtenteService utenteService;
	
	private MediaPlayerSettings mediaPlayerSettings;
	
	public PlaylistController() {
		dispatcher = ViewDispatcher.getInstance();
		utenteService = SpacemusicunifyBusinessFactory.getInstance().getUtenteService();
		mediaPlayerSettings = MediaPlayerSettings.getInstance();
	}
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		songName.setCellValueFactory(new PropertyValueFactory<>("title"));
		artistName.setCellValueFactory((TableColumn.CellDataFeatures<Canzone, String> param) -> {

			return new SimpleStringProperty(param.getValue().getAlbum().getArtist().getStageName());
		});
		albumName.setCellValueFactory((TableColumn.CellDataFeatures<Canzone, String> param) -> {
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
		delete.setCellValueFactory((TableColumn.CellDataFeatures<Canzone, Button> param) -> {
			final Button deleteButton = new Button("Delete");
			deleteButton.setCursor(Cursor.HAND);
			deleteButton.setOnAction((ActionEvent event) -> {
				
				playlist.getSongList().remove(param.getValue());
				try {
					utenteService.modify(playlist.getId(),playlist.getTitle(),playlist.getSongList(),playlist.getUser());
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
		List<Canzone> songList = playlist.getSongList();
		ObservableList<Canzone> playlistData = FXCollections.observableArrayList(songList);
		playlistTable.setItems(playlistData);
	}
	
	@FXML
	public void addPlaylistToQueue() {
		Utente utente = playlist.getUser();
		List<Canzone> lista = playlist.getSongList();
		for(Canzone canzonePlaylist: lista) {
			Boolean alreadyAdded = false;
			for(Canzone canzone: utente.getSongQueue()) {
				if(canzonePlaylist.getId().intValue() == canzone.getId().intValue()) {
					alreadyAdded = true;
					break;
				}
			}
			if(!alreadyAdded) {
				utente.getSongQueue().add(canzonePlaylist);
			}
		}

		if(mediaPlayerSettings.getMediaPlayer() != null && mediaPlayerSettings.getMediaPlayer().getStatus() != MediaPlayer.Status.STOPPED){
			mediaPlayerSettings.getMediaPlayer().stop();
			mediaPlayerSettings.getMediaPlayer().dispose();
		}
		mediaPlayerSettings.setPlayerState(PlayerState.searchSingleClick);
		dispatcher.renderPlayer("UserViews/UserHomeView/playerPane", utente);
	}
	
	@FXML
	public void deletePlaylist(ActionEvent event) {
		UtenteService utenteService = SpacemusicunifyBusinessFactory.getInstance().getUtenteService();
		try {
			utenteService.deletePlaylist(playlist);
		} catch (BusinessException e) {
			e.printStackTrace();
		}
		dispatcher.renderView("UserViews/UserHomeView/home", playlist.getUser());
		dispatcher.renderPlaylists("UserViews/UserHomeView/playlistPane", playlist.getUser());
	}
}
