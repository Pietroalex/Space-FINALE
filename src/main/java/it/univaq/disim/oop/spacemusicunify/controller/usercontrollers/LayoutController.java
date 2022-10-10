package it.univaq.disim.oop.spacemusicunify.controller.usercontrollers;

import it.univaq.disim.oop.spacemusicunify.business.*;
import it.univaq.disim.oop.spacemusicunify.controller.DataInitializable;
import it.univaq.disim.oop.spacemusicunify.domain.Genre;
import it.univaq.disim.oop.spacemusicunify.domain.User;
import it.univaq.disim.oop.spacemusicunify.view.SpacemusicunifyPlayer;
import it.univaq.disim.oop.spacemusicunify.view.ViewDispatcher;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.media.MediaPlayer;

public class LayoutController implements DataInitializable<User> {

	private final UserService userService;
	private ViewDispatcher dispatcher;
	@FXML
	private TextField searchField;
	@FXML
	private MenuButton menu;

	private User user;
	private PlayerService playerService;
	private SpacemusicunifyPlayer spacemusicunifyPlayer;

	public LayoutController() {
		dispatcher = ViewDispatcher.getInstance();
		SpacemusicunifyBusinessFactory factory = SpacemusicunifyBusinessFactory.getInstance();
		userService = factory.getUserService();
		playerService = factory.getPlayerService();
	}
	@Override
	public void initializeData(User user) {
		
		try {
			spacemusicunifyPlayer = playerService.getPlayer(user);
		} catch (BusinessException e) {
			e.printStackTrace();
		}
		for(Genre genre: Genre.values()){
			MenuItem menuItem = new MenuItem();
			menuItem.setText(genre.toString());
			menuItem.setOnAction( (ActionEvent event) -> { searchField.setText(menuItem.getText()); });
			menu.getItems().add(menuItem);
		}


		this.user = user;
		playerService.setPlayerState(PlayerState.started);
		dispatcher.renderView("UserViews/HomeView/playerPane", user);
		dispatcher.renderView("UserViews/HomeView/playlistPane", user);

		
	}
	@FXML
	public void logout(MouseEvent event) {
		dispatcher.logout();
		if(spacemusicunifyPlayer.getMediaPlayer() != null && spacemusicunifyPlayer.getMediaPlayer().getStatus() != MediaPlayer.Status.STOPPED){
			spacemusicunifyPlayer.getMediaPlayer().stop();
			spacemusicunifyPlayer.getMediaPlayer().dispose();
			spacemusicunifyPlayer.setPlay(false);
		}
	}
	@FXML
	public void searchAction(ActionEvent event) {
		RunTimeService.setSearch(searchField.getText());
		dispatcher.renderView("UserViews/SearchView/searchView", user);
	}

}