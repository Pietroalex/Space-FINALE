package it.univaq.disim.oop.spacemusicunify.controller.usercontrollers;

import it.univaq.disim.oop.spacemusicunify.business.*;
import it.univaq.disim.oop.spacemusicunify.controller.DataInitializable;
import it.univaq.disim.oop.spacemusicunify.domain.Genre;
import it.univaq.disim.oop.spacemusicunify.domain.User;
import it.univaq.disim.oop.spacemusicunify.view.SpacemusicunifyPlayer;
import it.univaq.disim.oop.spacemusicunify.view.ViewDispatcher;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class LayoutController implements DataInitializable<User> {

	private ViewDispatcher dispatcher;
	@FXML
	private TextField searchField;
	@FXML
	private Button menu;

	private User user;
	private SpacemusicunifyPlayer spacemusicunifyPlayer;
	private static final String MyStyle = "views/controllerStyle.css";
	
	public LayoutController() {
		dispatcher = ViewDispatcher.getInstance();
		SpacemusicunifyBusinessFactory factory = SpacemusicunifyBusinessFactory.getInstance();
	}
	@Override
	public void initializeData(User user) {
		spacemusicunifyPlayer = RunTimeService.getPlayer();

		this.user = user;
		dispatcher.renderView("UserViews/LayoutView/playerPane", user);
		dispatcher.renderView("UserViews/LayoutView/playlistPane", user);

		
	}
	@FXML
	public void logout(MouseEvent event) {
		dispatcher.logout();

		spacemusicunifyPlayer.getQueue().removeListener(spacemusicunifyPlayer.getChangeListener());
		spacemusicunifyPlayer.setChangeListener(null);
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
	@FXML
	public void showGenreSelection(ActionEvent event) {
		Stage popupwindow = new Stage();
		popupwindow.initModality(Modality.APPLICATION_MODAL);
		VBox container = new VBox();
		VBox vBox = new VBox();
		for(Genre genre : Genre.values()) {
			if(genre != Genre.singles) {
				Button item = new Button(genre.toString());
				item.setId("item");
				item.setCursor(Cursor.HAND);
				item.setPrefWidth(115);
				item.setOnAction((ActionEvent event2) -> {
					searchField.setText(item.getText());
				});
				vBox.getChildren().add(item);
			}
		}
		ScrollPane scrollPane = new ScrollPane(vBox);
		scrollPane.setMaxHeight(200);//Adjust max height of the popup here
		scrollPane.setMaxWidth(130);//Adjust max width of the popup here
		Button closeButton = new Button("Close");
		closeButton.setId("b1");
		closeButton.setCursor(Cursor.HAND);
		closeButton.setOnAction(e -> {
			popupwindow.close();
		});
		container.getChildren().addAll(scrollPane, closeButton);
		container.setAlignment(Pos.CENTER);
		Scene scene1 = new Scene(container, 150, 200);
		
		scene1.getStylesheets().add(MyStyle);
		
		popupwindow.setScene(scene1);
		popupwindow.setResizable(false);
		popupwindow.setTitle("search by genre");
		popupwindow.showAndWait();
	}

}