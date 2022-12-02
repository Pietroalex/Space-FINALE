package it.univaq.disim.oop.spacemusicunify.controller.usercontrollers;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Set;

import it.univaq.disim.oop.spacemusicunify.business.*;
import it.univaq.disim.oop.spacemusicunify.controller.DataInitializable;
import it.univaq.disim.oop.spacemusicunify.domain.Artist;
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
import javafx.scene.control.*;
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
	private TableColumn<Song, Button> addSongToQueue;
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
	private SpacemusicunifyPlayer spacemusicunifyPlayer;
	
	public PlaylistController() {
		dispatcher = ViewDispatcher.getInstance();
		SpacemusicunifyBusinessFactory factory = SpacemusicunifyBusinessFactory.getInstance();
		userService = factory.getUserService();
		playerService = factory.getPlayerService();
	}
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {

		playlistTable.setRowFactory( tablerow -> {
			TableRow<Song> songTableRow = new TableRow<>();
			songTableRow.setOnMouseClicked(( event) -> {
				if(songTableRow.getItem() != null){
					if(event.getClickCount() == 2) {
						PlayerService playerService = SpacemusicunifyBusinessFactory.getInstance().getPlayerService();
						if(spacemusicunifyPlayer.getQueue().size() != 0) {

							if(spacemusicunifyPlayer.getQueue().get(spacemusicunifyPlayer.getCurrentSong()).getId().intValue() != songTableRow.getItem().getId().intValue()) {
								try {
									List<Song> songList = new ArrayList<>(spacemusicunifyPlayer.getQueue());
									for(Song songCheck : songList){
										if(songCheck.getId().intValue() == songTableRow.getItem().getId().intValue()) playerService.deleteSongFromQueue(spacemusicunifyPlayer, songTableRow.getItem());	//rimuovo la songTableRow se già presente in coda
									}

									playerService.replaceCurrentSong(spacemusicunifyPlayer, songTableRow.getItem());
								} catch (BusinessException e) {
									dispatcher.renderError(e);
								}

								playlistTable.refresh();
							}
						} else {
							try {
								playerService.addSongToQueue(spacemusicunifyPlayer, songTableRow.getItem());
							} catch (BusinessException e) {
								dispatcher.renderError(e);
							}

						}

					}
				}
			});
			return songTableRow;
		});
		songName.setCellValueFactory(new PropertyValueFactory<>("title"));
		artistName.setCellValueFactory((TableColumn.CellDataFeatures<Song, String> param) -> {
			String artists = "";
			try {
				Set<Artist> artistsSet = SpacemusicunifyBusinessFactory.getInstance().getAlbumService().findAllArtists(param.getValue().getAlbum());
				for (Artist artistCtrl : artistsSet) {
					artists = artists + artistCtrl.getName() + ", ";
				}
				artists = artists.substring(0, artists.length()-2);
			} catch (BusinessException e) {
				dispatcher.renderError(e);
			}
			return new SimpleStringProperty(artists);
		});
		addSongToQueue.setStyle("-fx-alignment: CENTER;");
		addSongToQueue.setCellValueFactory((TableColumn.CellDataFeatures<Song, Button> param) -> {

			final Button addButton = new Button("Add to queue");
			addButton.setId("button");
			addButton.setCursor(Cursor.HAND);
			addButton.setOnAction((ActionEvent event) -> {
				//aggiungere la canzone alla coda di riproduzione dell'utente
				try {
					if(!(checkForClones(spacemusicunifyPlayer, param.getValue()))) playerService.addSongToQueue(spacemusicunifyPlayer, param.getValue());
					addButton.setDisable(true);
				} catch (BusinessException b) {
					dispatcher.renderError(b);
				}
			});
			if(checkForClones(spacemusicunifyPlayer, param.getValue())) addButton.setDisable(true);
			return new SimpleObjectProperty<Button>(addButton);
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
		delete.setCellValueFactory((TableColumn.CellDataFeatures<Song, Button> param) -> {
			final Button deleteButton = new Button("Delete");
			deleteButton.setCursor(Cursor.HAND);
			deleteButton.setOnAction((ActionEvent event) -> {

				playlist.getSongList().remove(param.getValue());
				try {
					userService.modify(playlist.getSongList(), playlist);
				} catch (BusinessException e) {
					dispatcher.renderError(e);
				}

				initializeTable();
			});
			return new SimpleObjectProperty<>(deleteButton);
		});
	}

	@Override
	public void initializeData(Playlist playlist) {
		this.playlist = playlist;
		this.spacemusicunifyPlayer = RunTimeService.getPlayer();
		playlistName.setText(playlist.getTitle());
		initializeTable();
	}

	private void initializeTable() {
		Set<Song> songList = playlist.getSongList();
		ObservableList<Song> playlistData = FXCollections.observableArrayList(songList);
		playlistTable.setItems(playlistData);
	}
	public boolean checkForClones(SpacemusicunifyPlayer player, Song value){
		for (Song songs : player.getQueue()) {
			if (songs.getId().intValue() ==  value.getId().intValue()) return true;
		}
		return false;
	}

	@FXML
	public void addPlaylistToQueue() {
		for(Song playlistSong: playlist.getSongList()) {
			Boolean alreadyAdded = false;
			for(Song song: spacemusicunifyPlayer.getQueue()) {
				if(playlistSong.getId().intValue() == song.getId().intValue()) {
					alreadyAdded = true;
					break;
				}
			}
			if(!alreadyAdded) {
				try {
					playerService.addSongToQueue(spacemusicunifyPlayer, playlistSong);
				} catch (BusinessException e) {
					dispatcher.renderError(e);
				}
			}
		}
		playlistTable.refresh();
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
