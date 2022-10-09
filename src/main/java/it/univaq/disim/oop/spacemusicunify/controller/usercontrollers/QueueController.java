package it.univaq.disim.oop.spacemusicunify.controller.usercontrollers;

import java.net.URL;
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
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

public class QueueController implements Initializable, DataInitializable<User> {

	private PlayerService playerService;
	private UserService userService;
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
		userService = factory.getUserService();
		albumService = factory.getAlbumService();
		this.dispatcher = ViewDispatcher.getInstance();
		try {
			this.spacemusicunifyPlayer = playerService.getPlayer(RunTimeService.getCurrentUser());
		} catch (ObjectNotFoundException e) {
			System.out.println(e.getMessage());
		} catch (BusinessException e) {
			dispatcher.renderError(e);
		}
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
		albumName.setCellValueFactory((TableColumn.CellDataFeatures<Song, String> param) -> {
			return new SimpleStringProperty(param.getValue().getAlbum().getTitle());
		});
		duration.setCellValueFactory(new PropertyValueFactory<>("length"));
		delete.setStyle("-fx-alignment: CENTER;");
	}
	@Override
	public void initializeData(User user) {
		
		delete.setCellValueFactory((TableColumn.CellDataFeatures<Song, Button> param) -> {
			final Button deleteButton = new Button("Delete");
			deleteButton.setCursor(Cursor.HAND);
			deleteButton.setOnAction((event) -> {

				if(param.getValue().getId().intValue() == spacemusicunifyPlayer.getQueue().get(spacemusicunifyPlayer.getCurrentSong()).getId().intValue()){	//canzone in corso uguale a quella selezionata

					if(spacemusicunifyPlayer.getQueue().size() > 1 ){				//più canzoni in riproduzione

						if(spacemusicunifyPlayer.getCurrentSong() + 1 == spacemusicunifyPlayer.getQueue().size()){		//ultima canzone in coda uguale a canzone in corso
							try {
								if(spacemusicunifyPlayer.isPlay()) {spacemusicunifyPlayer.setPlay(false);}
								playerService.updateCurrentSong(spacemusicunifyPlayer, spacemusicunifyPlayer.getCurrentSong() - 1);
							} catch (BusinessException e) {
								dispatcher.renderError(e);
							}
							playerService.setPlayerState(PlayerState.queueControllerLoad);
						}else {																			//canzone corrente tra prima posizione e penultima
							if(spacemusicunifyPlayer.getCurrentSong() != 0){
								playerService.setPlayerState(PlayerState.queueControllerLoad);//canzone corrente non in prima posizione
							//	playerService.setLastSong(spacemusicunifyPlayer.getQueue().get(spacemusicunifyPlayer.getCurrentSong() -1));
							}else{												//canzone corrente in prima posizione
								playerService.setPlayerState(PlayerState.queueControllerLoad);
							//	playerService.setLastSong(null);
							}
						}

					}else{									//una sola canzone in riproduzione
					//	playerService.setLastSong(null);
						if(spacemusicunifyPlayer.isPlay()) { spacemusicunifyPlayer.setPlay(false);}
						playerService.setPlayerState(PlayerState.started);
					}
				}else{																				//canzone in corso diversa da quella selezionata

					for(int i = 0; i < spacemusicunifyPlayer.getQueue().size(); i++ ){
						if(spacemusicunifyPlayer.getQueue().get(i).equals(param.getValue())){
							if (spacemusicunifyPlayer.getCurrentSong() > i ){
							//	playerService.setPlayerState(PlayerState.queueControllerResume);
								try {
									playerService.updateCurrentSong(spacemusicunifyPlayer, spacemusicunifyPlayer.getCurrentSong() - 1);
								} catch (BusinessException e) {
									dispatcher.renderError(e);
								}
								break;
							}
						}
					}
					// playerService.setPlayerState(PlayerState.queueControllerResume);
				}

				spacemusicunifyPlayer.getMediaPlayer().stop();
				spacemusicunifyPlayer.getMediaPlayer().dispose();
				queueTable.getItems().remove(param.getValue());
				try {
					playerService.deleteSongFromQueue(spacemusicunifyPlayer, param.getValue());
				} catch (ObjectNotFoundException e) {
					//text
				} catch (BusinessException e) {
					dispatcher.renderError(e);
				}
				queueTable.refresh();
			});
			return new SimpleObjectProperty<Button>(deleteButton);
		});
		
	//	queueTable.getItems().addAll(spacemusicunifyPlayer.getQueue());
		
		List<Song> songsList = spacemusicunifyPlayer.getQueue();
		ObservableList<Song> songData = FXCollections.observableArrayList(songsList);
		queueTable.setItems(songData);
		
	}
}