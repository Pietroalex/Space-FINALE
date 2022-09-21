package it.univaq.disim.oop.spacemusicunify.controller.administratorcontrollers;

import it.univaq.disim.oop.spacemusicunify.controller.DataInitializable;
import it.univaq.disim.oop.spacemusicunify.domain.Administrator;
import it.univaq.disim.oop.spacemusicunify.view.MenuElement;
import it.univaq.disim.oop.spacemusicunify.view.ViewDispatcher;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Cursor;
import javafx.scene.control.Button;
import javafx.scene.control.Separator;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Paint;

import java.net.URL;
import java.util.ResourceBundle;


public class LayoutController implements Initializable, DataInitializable<Administrator> {
	
	private static final String Folder = "AdministratorViews/";
	
	private Administrator admin;

    private static final MenuElement MENU_HOME = new MenuElement("Home", Folder + "HomeView/home");
    private static final MenuElement[] MENU_ADMIN = {
            new MenuElement("Gestione Artisti", Folder + "ManageArtistsView/manage_artists"),//ManageArtistsView/manage_artist
            new MenuElement("Gestione Utenti", Folder + "ManageUsersView/manage_users"),//ManageUsersView/manage_user

    };
    @FXML
    private VBox menuBar;
    public void initialize(URL location, ResourceBundle resources) {
        menuBar.getChildren().add(new Separator());
        menuBar.getChildren().addAll(createButton(MENU_HOME));

        for (MenuElement menu : MENU_ADMIN) {
            menuBar.getChildren().add(new Separator());
            menuBar.getChildren().add(createButton(menu));
        }
        menuBar.getChildren().add(new Separator());
    }
    @Override
    public void initializeData(Administrator amministratore) {
		this.admin = amministratore;
	}


    @FXML
    public void logout(MouseEvent event) {
        ViewDispatcher dispatcher = ViewDispatcher.getInstance();
        dispatcher.logout();
    }
    private Button createButton(MenuElement viewItem) {
        Button button = new Button(viewItem.getNome());
        button.setStyle("-fx-background-color: transparent; -fx-font-size: 14;");
        button.setTextFill(Paint.valueOf("white"));
        button.setPrefHeight(10);
        button.setPrefWidth(180);
        button.setCursor(Cursor.HAND);
        button.setOnAction((ActionEvent event) -> {
            ViewDispatcher dispatcher = ViewDispatcher.getInstance();
            dispatcher.renderView(viewItem.getVista(), this.admin);
        });
        return button;
    }
}
