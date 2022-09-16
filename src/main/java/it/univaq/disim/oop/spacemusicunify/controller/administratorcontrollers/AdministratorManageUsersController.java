package it.univaq.disim.oop.spacemusicunify.controller.administratorcontrollers;

import it.univaq.disim.oop.spacemusicunify.business.*;
import it.univaq.disim.oop.spacemusicunify.controller.DataInitializable;
import it.univaq.disim.oop.spacemusicunify.domain.Amministratore;
import it.univaq.disim.oop.spacemusicunify.domain.Utente;
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

public class AdministratorManageUsersController implements Initializable, DataInitializable<Amministratore>{
    private final SPACEMusicUnifyService SPACEMusicUnifyService;
    private final UtenteGenericoService utenteService;
    private final ViewDispatcher dispatcher;
    @FXML
    private TableView<Utente> usersList;
    @FXML
    private TableColumn<Utente, String> id;

    @FXML
    private TableColumn<Utente, String> username;
    @FXML
    private TableColumn<Utente, String> password;
    @FXML
    private TableColumn<Utente, Button> viewmodify;
    public AdministratorManageUsersController(){
        dispatcher = ViewDispatcher.getInstance();

        SpacemusicunifyBusinessFactory factory = SpacemusicunifyBusinessFactory.getInstance();
        SPACEMusicUnifyService = factory.getAmministratoreService();
        utenteService = factory.getUtenteGenerico();
    }
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        id.setCellValueFactory(new PropertyValueFactory<>("id"));
        username.setCellValueFactory(new PropertyValueFactory<>("username"));
        password.setCellValueFactory(new PropertyValueFactory<>("password"));


        viewmodify.setStyle("-fx-alignment: CENTER;");
        viewmodify.setCellValueFactory((TableColumn.CellDataFeatures<Utente, Button> param) -> {
            final Button modify = new Button("Detail");
            modify.setCursor(Cursor.HAND);
            modify.setOnAction((ActionEvent event) -> {

                this.utenteService.setSituation(ViewSituations.detail);
                dispatcher.renderView("AdministratorViews/ManageUsersView/user_detail", param.getValue());
            });
            return new SimpleObjectProperty<Button>(modify);
        });
    }
    @Override
    public void initializeData(Amministratore amministratore) {

            List<Utente> utenti = utenteService.getAllUsers();
            ObservableList<Utente> utenteData = FXCollections.observableArrayList(utenti);
            usersList.setItems(utenteData);

    }
    @FXML
    public void addNewUser(){
        Utente utente = new Utente();
        utente.setUsername("utente");
        utente.setPassword("123456");

        this.utenteService.setSituation(ViewSituations.newobject);
        dispatcher.renderView("AdministratorViews/ManageUsersView/user_detail", utente);

    }



}
