package it.univaq.disim.oop.spacemusicunify.controller.usercontrollers;

import it.univaq.disim.oop.spacemusicunify.business.BusinessException;
import it.univaq.disim.oop.spacemusicunify.business.UserService;
import it.univaq.disim.oop.spacemusicunify.business.SpacemusicunifyBusinessFactory;
import it.univaq.disim.oop.spacemusicunify.controller.DataInitializable;

import it.univaq.disim.oop.spacemusicunify.domain.Playlist;
import it.univaq.disim.oop.spacemusicunify.domain.User;
import it.univaq.disim.oop.spacemusicunify.view.ViewDispatcher;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Set;

public class PlaylistPaneController implements Initializable, DataInitializable<User> {
    private final ViewDispatcher dispatcher;
    private final UserService userService;

    @FXML
    private TableView<Playlist> playlistView;
    @FXML
    private TableColumn<Playlist, String> titleColumn;
    private User user;

    public PlaylistPaneController(){
        dispatcher = ViewDispatcher.getInstance();
        SpacemusicunifyBusinessFactory factory = SpacemusicunifyBusinessFactory.getInstance();
        userService = factory.getUserService();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // settaggio proprietà della tabella playlist
        titleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        playlistView.setCursor(Cursor.HAND);
        playlistView.setOnMouseClicked(event -> {
            // visualizzare la playlist
            Playlist selected = playlistView.getSelectionModel().getSelectedItem();

            if(selected != null) dispatcher.renderView("UserViews/PlaylistView/playlistView", selected);

        });
    }
    @Override
    public void initializeData(User user) {
        this.user = user;
        try {
            Set<Playlist> result = userService.getAllPlaylists(user);
            ObservableList<Playlist> playlistData = FXCollections.observableArrayList(result);
            playlistView.setItems(playlistData);

        } catch (BusinessException e) {
            dispatcher.renderError(e);
        }
    }

    public void addNewPlaylist(ActionEvent event) {
        Playlist nuova = new Playlist();
        createPlaylistPopup(nuova);
    }

    private void createPlaylistPopup(Playlist playlist) {
        Stage popupwindow = new Stage();
        popupwindow.initModality(Modality.APPLICATION_MODAL);
        // inserimento nome playlist
        TextField title = new TextField();
        title.setMaxWidth(250);
        title.setAlignment(Pos.CENTER);
        Label insert = new Label("Insert name");
        // operazione annulla
        Button closeButton = new Button("Cancel");
        closeButton.setCursor(Cursor.HAND);
        closeButton.setOnAction(e -> popupwindow.close());

        // operazione conferma
        Button createButton = new Button("Confirm");
        createButton.setCursor(Cursor.HAND);

        createButton.setOnAction(e -> {
            playlist.setTitle(title.getText());
            playlist.setUser(this.user);
            // aggiungere la playlist alla TableView
            try {
                userService.add(playlist);
            } catch (BusinessException e1) {
                dispatcher.renderError(e1);
            }
            dispatcher.renderView("UserViews/HomeView/playlistPane", this.user);
            //refresh table

            popupwindow.close();
        });
        createButton.disableProperty().bind(title.textProperty().isEmpty());

        VBox layout = new VBox(10);
        HBox align = new HBox(5);
        align.setAlignment(Pos.CENTER);
        align.getChildren().addAll(createButton, closeButton);
        layout.getChildren().addAll(insert, title, align);
        layout.setAlignment(Pos.CENTER);
        Scene scene1 = new Scene(layout, 300, 150);
        popupwindow.setScene(scene1);
        popupwindow.setResizable(false);
        popupwindow.setTitle("Create new playlist");
        popupwindow.showAndWait();
    }
}
