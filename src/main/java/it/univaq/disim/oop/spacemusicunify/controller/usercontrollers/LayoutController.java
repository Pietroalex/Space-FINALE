package it.univaq.disim.oop.spacemusicunify.controller.usercontrollers;

import it.univaq.disim.oop.spacemusicunify.business.*;
import it.univaq.disim.oop.spacemusicunify.controller.DataInitializable;
import it.univaq.disim.oop.spacemusicunify.domain.Genre;
import it.univaq.disim.oop.spacemusicunify.domain.User;
import it.univaq.disim.oop.spacemusicunify.view.ViewDispatcher;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.media.MediaPlayer;

public class LayoutController implements DataInitializable<User> {

	private final SPACEMusicUnifyService spaceMusicUnifyService;
	private ViewDispatcher dispatcher;
	@FXML
	private TextField searchField;
	@FXML
	private MenuButton menu;

	private User user;
	private MediaPlayerSettings mediaPlayerSettings;

	public LayoutController() {
		dispatcher = ViewDispatcher.getInstance();
		SpacemusicunifyBusinessFactory factory = SpacemusicunifyBusinessFactory.getInstance();
		spaceMusicUnifyService = factory.getSPACEMusicUnifyService();
		mediaPlayerSettings = MediaPlayerSettings.getInstance();
	}
	@Override
	public void initializeData(User utente) {
		for(Genre genre: Genre.values()){
			MenuItem menuItem = new MenuItem();
			menuItem.setText(genre.toString());
			menuItem.setOnAction( (ActionEvent event) -> { searchField.setText(menuItem.getText()); });
			menu.getItems().add(menuItem);
		}

		this.user = utente;
		mediaPlayerSettings.setPlayerState(PlayerState.started);
		dispatcher.renderView("UserViews/HomeView/playerPane", utente);
		dispatcher.renderView("UserViews/HomeView/playlistPane", utente);

		
	}
	@FXML
	public void logout(MouseEvent event) {
		dispatcher.logout();
		if(mediaPlayerSettings.getMediaPlayer() != null && mediaPlayerSettings.getMediaPlayer().getStatus() != MediaPlayer.Status.STOPPED){
			mediaPlayerSettings.getMediaPlayer().stop();
			mediaPlayerSettings.getMediaPlayer().dispose();
			mediaPlayerSettings.setPlayerOnPlay(false);
		}
	}
	@FXML
	public void searchAction(ActionEvent event) {
		spaceMusicUnifyService.setRicerca(searchField.getText());
		dispatcher.renderView("UserViews/RicercaView/ricercaView", user);
	}



}