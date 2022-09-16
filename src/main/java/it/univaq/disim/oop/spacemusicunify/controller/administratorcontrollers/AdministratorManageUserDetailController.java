package it.univaq.disim.oop.spacemusicunify.controller.administratorcontrollers;

import it.univaq.disim.oop.spacemusicunify.business.*;
import it.univaq.disim.oop.spacemusicunify.controller.DataInitializable;
import it.univaq.disim.oop.spacemusicunify.domain.Amministratore;
import it.univaq.disim.oop.spacemusicunify.domain.Utente;
import it.univaq.disim.oop.spacemusicunify.view.ViewDispatcher;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import java.net.URL;
import java.util.ResourceBundle;

public class AdministratorManageUserDetailController implements Initializable, DataInitializable<Utente> {
    private final SPACEMusicUnifyService spaceMusicUnifyService;
    private final ViewDispatcher dispatcher;
    @FXML
    private AnchorPane masterPane;
    @FXML
    private AnchorPane infoPane;
    @FXML
    private AnchorPane modifyPane;
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
    private Amministratore admin;
    private Utente utente;

    public AdministratorManageUserDetailController(){
        dispatcher = ViewDispatcher.getInstance();
        SpacemusicunifyBusinessFactory factory = SpacemusicunifyBusinessFactory.getInstance();
        spaceMusicUnifyService = factory.getSPACEMusicUnifyService();

    }
    @Override
    public void initializeData(Utente utente) {
        this.utente = utente;
        this.setView();
        if(spaceMusicUnifyService.getSituation() != ViewSituations.register) {
	        this.username.setText(utente.getUsername());
	        this.password.setText(utente.getPassword());
        }
        this.usernameField.setText(utente.getUsername());
        this.passwordField.setText(utente.getPassword());
    }
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        confirm.disableProperty().bind(usernameField.textProperty().isEmpty().or(passwordField.textProperty().isEmpty()));

    }
    @FXML
    public void confirmUser() {
        try {

            if (utente.getId() == null) {
                utente.setUsername(usernameField.getText());
                utente.setPassword(passwordField.getText());
                spaceMusicUnifyService.add(utente);
                if (spaceMusicUnifyService.getSituation() == ViewSituations.register) {
                    dispatcher.logout();
                } else {
                    dispatcher.renderView("AdministratorViews/ManageUsersView/manage_users", this.admin);
                }

            } else {

                spaceMusicUnifyService.modify(utente.getId(), usernameField.getText(), passwordField.getText());

                dispatcher.renderView("AdministratorViews/ManageUsersView/manage_users", this.admin);
            }
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

        if(spaceMusicUnifyService.getSituation() == ViewSituations.register){
            dispatcher.logout();
        }else {
            dispatcher.renderView("AdministratorViews/ManageUsersView/manage_users", this.admin);
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
    public void setView(){
        switch (spaceMusicUnifyService.getSituation()){
            case detail:
                masterPane.getChildren().setAll(infoPane.getChildren()) ;
                break;

            case modify:
                title.setText("Modify User");
                confirm.setText("Modify");
                masterPane.getChildren().setAll(modifyPane.getChildren()) ;
                break;

            case newobject:
                title.setText("New User");
                confirm.setText("Create");
                masterPane.getChildren().setAll(modifyPane.getChildren()) ;
                break;
            case register:
                title.setText("Register");
                confirm.setText("Create");
                masterPane.getChildren().setAll(modifyPane.getChildren());
                masterPane.setPrefHeight(600);
                masterPane.setPrefWidth(1070);
                break;
        }
    }
    @FXML
    public void showModify() {
        if(spaceMusicUnifyService.getSituation() == ViewSituations.detail){
            spaceMusicUnifyService.setSituation(ViewSituations.modify);
            this.setView();
        }else {
            spaceMusicUnifyService.setSituation(ViewSituations.detail);
            this.setView();
        }
    }

}