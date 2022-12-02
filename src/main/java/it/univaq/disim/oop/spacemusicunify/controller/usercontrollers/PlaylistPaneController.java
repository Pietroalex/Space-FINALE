package it.univaq.disim.oop.spacemusicunify.controller.usercontrollers;

import it.univaq.disim.oop.spacemusicunify.business.*;
import it.univaq.disim.oop.spacemusicunify.controller.DataInitializable;

import it.univaq.disim.oop.spacemusicunify.domain.Playlist;
import it.univaq.disim.oop.spacemusicunify.domain.Song;
import it.univaq.disim.oop.spacemusicunify.domain.User;
import it.univaq.disim.oop.spacemusicunify.view.ViewDispatcher;
import javafx.beans.InvalidationListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Paint;
import javafx.scene.text.TextAlignment;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.net.URL;
import java.util.*;

public class PlaylistPaneController implements Initializable, DataInitializable<User> {
    private final ViewDispatcher dispatcher;
    private final UserService userService;
	private static final String MyStyle = "views/controllerStyle.css";


    @FXML
    private ListView<Playlist> myPlaylists;
    private User user;

    public PlaylistPaneController(){
        dispatcher = ViewDispatcher.getInstance();
        SpacemusicunifyBusinessFactory factory = SpacemusicunifyBusinessFactory.getInstance();
        userService = factory.getUserService();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        myPlaylists.setCellFactory(param -> new ListCell<Playlist>() {
            @Override
            protected void updateItem(Playlist item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || item == null || item.getTitle() == null) {
                    setText(null);
                } else {
                    setText(item.getTitle());
                }
            }

        });

        myPlaylists.setOnMouseClicked( mouseEvent ->  {
            if(myPlaylists.getSelectionModel().getSelectedItem() != null) dispatcher.renderView("UserViews/PlaylistView/playlistView", myPlaylists.getSelectionModel().getSelectedItem());
        });


    }
    @Override
    public void initializeData(User user) {
        this.user = user;
        try {
            ObservableList<Playlist> playlistData = FXCollections.observableArrayList(userService.getAllPlaylists(user));
            myPlaylists.setItems(playlistData);

        } catch (BusinessException e) {
            dispatcher.renderError(e);
        }
    }

    public void addNewPlaylist(ActionEvent event) {
        Playlist newPlaylist = new Playlist();
        newPlaylist.setUser(user);
        createPlaylistPopup(newPlaylist);
    }

    private void createPlaylistPopup(Playlist playlist) {
        Stage popupwindow = new Stage();
        popupwindow.initModality(Modality.APPLICATION_MODAL);
        // inserimento nome playlist
        TextField title = new TextField();
        title.setId("title");
        title.setMaxWidth(250);
        title.setAlignment(Pos.CENTER);
        Label error = new Label("This playlist already exists");
        error.setTextFill(Paint.valueOf("red"));
        error.setVisible(false);
        Label insert = new Label("Playlist's title:");
        // operazione annulla
        Button closeButton = new Button("Cancel");
        closeButton.setCursor(Cursor.HAND);
        closeButton.setId("b1");
        closeButton.setOnAction(ae -> popupwindow.close());

        // operazione conferma
        Button createButton = new Button("Confirm");
        createButton.setCursor(Cursor.HAND);
        createButton.setId("b1");

        createButton.setOnAction(ae -> {
            playlist.setTitle(title.getText());
            // aggiungere la playlist alla TableView
            try {
                userService.add(playlist);
            } catch (AlreadyExistingException e) {
            	error.setVisible(true);
            } catch (BusinessException e) {
                dispatcher.renderError(e);
            }
            dispatcher.renderView("UserViews/HomeView/playlistPane", this.user);
            
            if(!error.isVisible()) popupwindow.close();
        });
        
        createButton.disableProperty().bind(title.textProperty().isEmpty().or(error.visibleProperty()));
        title.textProperty().addListener((obs, oldText, newText) -> {
            if (error.isVisible()) {
                error.setVisible(false);
            }
        });
        
        VBox layout = new VBox(10);
        HBox align = new HBox(5);
        align.setAlignment(Pos.CENTER);
        align.getChildren().addAll(createButton, closeButton);
        layout.getChildren().addAll(error, insert, title, align);
        layout.setAlignment(Pos.CENTER);
        Scene scene1 = new Scene(layout, 300, 150);
        scene1.getStylesheets().add(MyStyle);
        popupwindow.setScene(scene1);
        popupwindow.setResizable(false);
        popupwindow.setTitle("Create new playlist");
        popupwindow.showAndWait();
    }
}
