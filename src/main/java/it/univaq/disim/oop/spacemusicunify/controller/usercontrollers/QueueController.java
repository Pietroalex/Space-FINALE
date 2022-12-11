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
import it.univaq.disim.oop.spacemusicunify.domain.User;
import it.univaq.disim.oop.spacemusicunify.view.SpacemusicunifyPlayer;
import it.univaq.disim.oop.spacemusicunify.view.ViewDispatcher;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Cursor;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;

public class QueueController implements Initializable, DataInitializable<User> {

	private PlayerService playerService;
	private AlbumService albumService;
	private SpacemusicunifyPlayer spacemusicunifyPlayer;
	@FXML
	private TableView<Song> queueTable;
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
	
	private ViewDispatcher dispatcher;
	
	public QueueController(){
		SpacemusicunifyBusinessFactory factory = SpacemusicunifyBusinessFactory.getInstance();
		playerService = factory.getPlayerService();
		albumService = factory.getAlbumService();
		this.dispatcher = ViewDispatcher.getInstance();
		this.spacemusicunifyPlayer = RunTimeService.getPlayer();

	}
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		songName.setCellValueFactory(new PropertyValueFactory<>("title"));
		artistName.setCellValueFactory((TableColumn.CellDataFeatures<Song, String> param) -> {
			String artists = "";
			try {
				Set<Artist> artistsSet = albumService.findAllArtists(param.getValue().getAlbum());
				for (Artist artistCtrl : artistsSet) {
					artists = artists + artistCtrl.getName() + ", ";
				}
				artists = artists.substring(0, artists.length()-2);
			} catch (BusinessException e) {
				ViewDispatcher.getInstance().renderError(e);
			}
			return new SimpleStringProperty(artists);
		});
		albumName.setCellValueFactory((TableColumn.CellDataFeatures<Song, String> param) -> new SimpleStringProperty(param.getValue().getAlbum().getTitle()));
		duration.setCellValueFactory(new PropertyValueFactory<>("length"));
		delete.setStyle("-fx-alignment: CENTER;");
		
		queueTable.setRowFactory( tablerow -> {
			TableRow<Song> songTableRow = new TableRow<>();
			songTableRow.setOnMouseClicked(( event) -> {
				if(songTableRow.getItem() != null){
					if(event.getClickCount() == 2) {
						PlayerService playerService = SpacemusicunifyBusinessFactory.getInstance().getPlayerService();
						if(spacemusicunifyPlayer.getQueue().size() > 0) {
							if(spacemusicunifyPlayer.getQueue().get(spacemusicunifyPlayer.getCurrentSong()).getId().intValue() != songTableRow.getItem().getId().intValue()) {
								try {
							    	spacemusicunifyPlayer.getQueue().removeListener(spacemusicunifyPlayer.getChangeListener());
									spacemusicunifyPlayer.setChangeListener(null);
									
									if(spacemusicunifyPlayer.getMediaPlayer() != null && spacemusicunifyPlayer.getMediaPlayer().getStatus() != MediaPlayer.Status.STOPPED){
										spacemusicunifyPlayer.getMediaPlayer().stop();
										spacemusicunifyPlayer.getMediaPlayer().dispose();
									}
									
									playerService.updateCurrentSong(spacemusicunifyPlayer, songTableRow.getIndex());
									playerService.updateDuration(spacemusicunifyPlayer, Duration.ZERO);
				
									dispatcher.renderView("UserViews/HomeView/playerPane", RunTimeService.getCurrentUser());
									
								} catch (BusinessException e) {
									dispatcher.renderError(e);
								}
							}
						}
					}
				}
			});
			return songTableRow;
		});
	}
	@Override
	public void initializeData(User user) {
		
		delete.setCellValueFactory((TableColumn.CellDataFeatures<Song, Button> param) -> {
			final Button deleteButton = new Button("Delete");
			deleteButton.setCursor(Cursor.HAND);
			deleteButton.setOnAction((event) -> {
				try {
					playerService.deleteSongFromQueue(spacemusicunifyPlayer, param.getValue());
				} catch (BusinessException e) {
					dispatcher.renderError(e);
				}
			});
			return new SimpleObjectProperty<>(deleteButton);
		});
		queueTable.setItems(spacemusicunifyPlayer.getQueue());
	}
}