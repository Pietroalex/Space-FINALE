package it.univaq.disim.oop.spacemusicunify.controller.usercontrollers;

import it.univaq.disim.oop.spacemusicunify.business.MediaPlayerSettings;
import it.univaq.disim.oop.spacemusicunify.business.SpacemusicunifyBusinessFactory;
import it.univaq.disim.oop.spacemusicunify.business.UtenteService;
import it.univaq.disim.oop.spacemusicunify.business.PlayerState;
import it.univaq.disim.oop.spacemusicunify.controller.DataInitializable;
import it.univaq.disim.oop.spacemusicunify.domain.Utente;
import it.univaq.disim.oop.spacemusicunify.view.ViewDispatcher;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;

public class UserLayoutController implements DataInitializable<Utente> {

	private ViewDispatcher dispatcher;
	@FXML
	private TextField searchField;
	private UtenteService utenteService;
	private Utente user;
	private MediaPlayerSettings mediaPlayerSettings;

	public UserLayoutController() {
		dispatcher = ViewDispatcher.getInstance();
		SpacemusicunifyBusinessFactory factory = SpacemusicunifyBusinessFactory.getInstance();
		utenteService = factory.getUtenteService();
		mediaPlayerSettings = MediaPlayerSettings.getInstance();
	}
	@Override
	public void initializeData(Utente utente) {
		this.user = utente;
		mediaPlayerSettings.setPlayerState(PlayerState.started);
		dispatcher.renderPlayer("UserViews/UserHomeView/playerPane", utente);
		dispatcher.renderPlaylists("UserViews/UserHomeView/playlistPane", utente);

		
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
		utenteService.setRicerca(searchField.getText());
		dispatcher.renderView("UserViews/RicercaView/ricercaView", user);
	}



}