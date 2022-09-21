package it.univaq.disim.oop.spacemusicunify.controller.administratorcontrollers;

import it.univaq.disim.oop.spacemusicunify.business.*;
import it.univaq.disim.oop.spacemusicunify.controller.DataInitializable;
import it.univaq.disim.oop.spacemusicunify.domain.Administrator;
import it.univaq.disim.oop.spacemusicunify.domain.User;
import it.univaq.disim.oop.spacemusicunify.view.ViewDispatcher;
import it.univaq.disim.oop.spacemusicunify.view.ViewSituations;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import java.net.URL;
import java.util.ResourceBundle;

public class ManageUserDetailController implements Initializable, DataInitializable<User> {
    private final SPACEMusicUnifyService spaceMusicUnifyService;
    private final ViewDispatcher dispatcher;
    @FXML
    private Button confirm;
    @FXML
    private TextField usernameField;
    @FXML
    private TextField passwordField;
    @FXML
    private Label title;
    @FXML
    private Label username;
    @FXML
    private Label password;
    @FXML
    private Label existingLabel;
    private Administrator admin;
    private User utente;
    @FXML
    private Button delete;

    public ManageUserDetailController(){
        dispatcher = ViewDispatcher.getInstance();
        SpacemusicunifyBusinessFactory factory = SpacemusicunifyBusinessFactory.getInstance();
        spaceMusicUnifyService = factory.getSPACEMusicUnifyService();

    }
    public void setView2(){
        switch (dispatcher.getSituation()){
            case detail:
                this.username.setText(utente.getUsername());
                this.password.setText(utente.getPassword());
                break;

            case modify:
                title.setText("Modify User");
                confirm.setText("Modify");
                this.usernameField.setText(utente.getUsername());
                this.passwordField.setText(utente.getPassword());
                confirm.disableProperty().bind(usernameField.textProperty().isEmpty().or(passwordField.textProperty().isEmpty()));
                break;

            case newobject:
                this.usernameField.setText(utente.getUsername());
                this.passwordField.setText(utente.getPassword());
                confirm.disableProperty().bind(usernameField.textProperty().isEmpty().or(passwordField.textProperty().isEmpty()));
                delete.setVisible(false);
                title.setText("New User");
                confirm.setText("Create");

                break;
            case register:
                this.usernameField.setText(utente.getUsername());
                this.passwordField.setText(utente.getPassword());
                confirm.disableProperty().bind(usernameField.textProperty().isEmpty().or(passwordField.textProperty().isEmpty()));
                title.setText("Register");
                confirm.setText("Create");

                break;
        }
    }
    @Override
    public void initializeData(User utente) {
        this.utente = utente;
        this.setView2();


    }
    @Override
    public void initialize(URL location, ResourceBundle resources) {


    }
    @FXML
    public void confirmUser() {
        try {

            if (utente.getId() == null) {
                utente.setUsername(usernameField.getText());
                utente.setPassword(passwordField.getText());
                spaceMusicUnifyService.add(utente);
                if (dispatcher.getSituation() == ViewSituations.register) {
                    dispatcher.logout();
                } else {
                    dispatcher.renderView("AdministratorViews/ManageUsersView/manage_users", this.admin);
                }

            } else {

                spaceMusicUnifyService.modify(utente.getId(), usernameField.getText(), passwordField.getText());
            }
            dispatcher.renderView("AdministratorViews/ManageUsersView/manage_users", this.admin);
        } catch (AlreadyTakenFieldException e){
            System.out.println(e.getMessage());
            existingLabel.setVisible(true);

        }catch (AlreadyExistingException e){
            System.out.println(e.getMessage());
            existingLabel.setText("The user already exist");
            existingLabel.setVisible(true);

        } catch (BusinessException e) {
            dispatcher.renderError(e);
        }
    }
    @FXML
    public void cancelModify() {
        switch (dispatcher.getSituation()){
            case register:
                dispatcher.logout();
                break;
            case newobject:
                dispatcher.renderView("AdministratorViews/ManageUsersView/manage_users", admin);
                break;
            default:
                dispatcher.setSituation(ViewSituations.detail);
                dispatcher.renderView("AdministratorViews/ManageUsersView/user_detail", utente);
        }
    }
    @FXML
    public void deleteUser(){
        try{
            if (utente.getId() != null) {
                spaceMusicUnifyService.delete(utente);
            }else{
                System.out.println("Utente not found");
            }
            dispatcher.renderView("AdministratorViews/ManageUsersView/manage_users", this.admin);

        } catch (BusinessException e) {
            dispatcher.renderError(e);
        }
    }
    @FXML
    public void showModify() {
        dispatcher.setSituation(ViewSituations.modify);
        dispatcher.renderView("AdministratorViews/ManageUsersView/user_modify", utente);
    }

}
