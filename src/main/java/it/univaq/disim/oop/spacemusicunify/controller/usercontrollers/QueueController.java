package it.univaq.disim.oop.spacemusicunify.controller.usercontrollers;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import it.univaq.disim.oop.spacemusicunify.business.*;
import it.univaq.disim.oop.spacemusicunify.controller.DataInitializable;
import it.univaq.disim.oop.spacemusicunify.domain.Artist;
import it.univaq.disim.oop.spacemusicunify.domain.Song;
import it.univaq.disim.oop.spacemusicunify.domain.User;
import it.univaq.disim.oop.spacemusicunify.view.SpacemusicunifyPlayer;
import it.univaq.disim.oop.spacemusicunify.view.ViewDispatcher;
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
	
	public QueueController(){
		SpacemusicunifyBusinessFactory factory = SpacemusicunifyBusinessFactory.getInstance();
		playerService = factory.getPlayerService();
		userService = factory.getUserService();
	}
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		songName.setCellValueFactory(new PropertyValueFactory<>("title"));
		artistName.setCellValueFactory((TableColumn.CellDataFeatures<Song, String> param) -> {

			return new SimpleStringProperty(/*param.getValue().getAlbum().getArtist().getStageName()*/);
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
			/*	deleteButton.setOnAction((ActionEvent event) -> {

			if(param.getValue().getId().intValue() == utente.getcurrentSong().getId().intValue()){	//canzone in corso uguale a quella selezionata

					if(utente.getSongQueue().size() > 1 ){				//più canzoni in riproduzione

						if(utente.getcurrentPosition() + 1 == utente.getSongQueue().size()){		//ultima canzone in coda uguale a canzone in corso
							spaceMusicUnifyService.updateCurrentSong(utente, utente.getcurrentPosition() - 1);
							mediaPlayerSettings.setPlayerState(PlayerState.queueControllerLoad);
						}else {																			//canzone corrente tra prima posizione e penultima
							if(utente.getcurrentPosition() != 0){
								mediaPlayerSettings.setPlayerState(PlayerState.queueControllerLoad);//canzone corrente non in prima posizione
								MediaPlayerSettings.getInstance().setLastSong(utente.getSongQueue().get(utente.getcurrentPosition() -1));
							}else{												//canzone corrente in prima posizione
								mediaPlayerSettings.setPlayerState(PlayerState.queueControllerLoad);
								MediaPlayerSettings.getInstance().setLastSong(null);
							}
						}

					}else{									//una sola canzone in riproduzione
						MediaPlayerSettings.getInstance().setLastSong(null);
						mediaPlayerSettings.setPlayerState(PlayerState.started);
					}
				}else{																				//canzone in corso diversa da quella selezionata

					System.out.println("canzone prima di quella selezionata");
					for(int i = 0; i < utente.getSongQueue().size(); i++ ){
						if(utente.getSongQueue().get(i).equals(param.getValue())){
							if (utente.getcurrentPosition() > i ){
								System.out.println("canzone prima trovata "+param.getValue());

								System.out.println("last song "+MediaPlayerSettings.getInstance().getLastSong());
								System.out.println("current song "+utente.getcurrentSong());
								if(utente.getSongQueue().get(i).equals( MediaPlayerSettings.getInstance().getLastSong())){
									System.out.println("canzone da eliminare uguale a canzone precedente");
								}
								mediaPlayerSettings.setPlayerState(PlayerState.queueControllerResume);
								spaceMusicUnifyService.updateCurrentSong(utente, utente.getcurrentPosition() - 1);
								break;
							}
						}
					}
					mediaPlayerSettings.setPlayerState(PlayerState.queueControllerResume);
				}
				System.out.println("current position "+utente.getcurrentPosition());

				MediaPlayerSettings.getInstance().getMediaPlayer().stop();
				MediaPlayerSettings.getInstance().getMediaPlayer().dispose();
				queueTable.getItems().remove(param.getValue());
				spaceMusicUnifyService.deleteSongFromQueue(utente, param.getValue());
				queueTable.refresh();

				dispatcher.renderPlayer("UserViews/UserHomeView/playerPane", utente);

			});
			return new SimpleObjectProperty<Button>(deleteButton);
		});
		System.out.println("songqueue: " + utente.getSongQueue());
		queueTable.getItems().addAll(utente.getSongQueue());
	*/
		return null;});
		SpacemusicunifyPlayer spacemusicunifyPlayer;
		try {
			spacemusicunifyPlayer = playerService.getPlayer(user);
			List<Song> songsList = spacemusicunifyPlayer.getQueue();
			ObservableList<Song> songData = FXCollections.observableArrayList(songsList);
			queueTable.setItems(songData);
		} catch (BusinessException e) {
			e.printStackTrace();
		}
		
	}
}