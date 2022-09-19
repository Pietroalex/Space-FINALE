package it.univaq.disim.oop.spacemusicunify.controller.administratorcontrollers;

import it.univaq.disim.oop.spacemusicunify.business.*;
import it.univaq.disim.oop.spacemusicunify.controller.DataInitializable;
import it.univaq.disim.oop.spacemusicunify.domain.Administrator;
import it.univaq.disim.oop.spacemusicunify.domain.User;
import it.univaq.disim.oop.spacemusicunify.view.ViewDispatcher;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Cursor;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class AdministratorManageUsersController implements Initializable, DataInitializable<Administrator>{
    private final SPACEMusicUnifyService spaceMusicUnifyService;
    private final ViewDispatcher dispatcher;
    @FXML
    private TableView<User> usersList;
    @FXML
    private TableColumn<User, String> id;

    @FXML
    private TableColumn<User, String> username;
    @FXML
    private TableColumn<User, String> password;
    @FXML
    private TableColumn<User, Button> viewmodify;
    public AdministratorManageUsersController(){
        dispatcher = ViewDispatcher.getInstance();

        SpacemusicunifyBusinessFactory factory = SpacemusicunifyBusinessFactory.getInstance();
        spaceMusicUnifyService = factory.getSPACEMusicUnifyService();
    }
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        id.setCellValueFactory(new PropertyValueFactory<>("id"));
        username.setCellValueFactory(new PropertyValueFactory<>("username"));
        password.setCellValueFactory(new PropertyValueFactory<>("password"));


        viewmodify.setStyle("-fx-alignment: CENTER;");
        viewmodify.setCellValueFactory((TableColumn.CellDataFeatures<User, Button> param) -> {
            final Button modify = new Button("Detail");
            modify.setCursor(Cursor.HAND);
            modify.setOnAction((ActionEvent event) -> {

                spaceMusicUnifyService.setSituation(ViewSituations.detail);
                dispatcher.renderView("AdministratorViews/ManageUsersView/detailuser", param.getValue());
            });
            return new SimpleObjectProperty<Button>(modify);
        });
    }
    @Override
    public void initializeData(Administrator amministratore) {

            List<User> utenti = spaceMusicUnifyService.getAllUsers();
            ObservableList<User> utenteData = FXCollections.observableArrayList(utenti);
            usersList.setItems(utenteData);

    }
    @FXML
    public void addNewUser(){
        User utente = new User();
        utente.setUsername("utente");
        utente.setPassword("123456");

        spaceMusicUnifyService.setSituation(ViewSituations.newobject);
        dispatcher.renderView("AdministratorViews/ManageUsersView/user_detail", utente);

    }



}
