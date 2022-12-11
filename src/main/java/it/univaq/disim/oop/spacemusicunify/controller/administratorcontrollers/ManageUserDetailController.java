package it.univaq.disim.oop.spacemusicunify.controller.administratorcontrollers;

import it.univaq.disim.oop.spacemusicunify.business.*;
import it.univaq.disim.oop.spacemusicunify.controller.DataInitializable;
import it.univaq.disim.oop.spacemusicunify.domain.Administrator;
import it.univaq.disim.oop.spacemusicunify.domain.User;
import it.univaq.disim.oop.spacemusicunify.view.ViewDispatcher;
import it.univaq.disim.oop.spacemusicunify.view.ViewSituations;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.net.URL;
import java.util.ResourceBundle;

public class ManageUserDetailController implements Initializable, DataInitializable<User> {
    private final UserService userService;
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
    private User user;
    @FXML
    private Button delete;

    public ManageUserDetailController(){
        dispatcher = ViewDispatcher.getInstance();
        SpacemusicunifyBusinessFactory factory = SpacemusicunifyBusinessFactory.getInstance();
        userService = factory.getUserService();

    }
    private void setView(){
        switch (dispatcher.getSituation()){
            case detail:
                this.username.setText(user.getUsername());
                this.password.setText(user.getPassword());
                break;

            case modify:
                title.setText("Modify User");
                confirm.setText("Modify");
                this.usernameField.setText(user.getUsername());
                this.passwordField.setText(user.getPassword());
                confirm.disableProperty().bind(usernameField.textProperty().isEmpty().or(passwordField.textProperty().isEmpty()));
                break;

            case newobject:
                this.usernameField.setText(user.getUsername());
                this.passwordField.setText(user.getPassword());
                confirm.disableProperty().bind(usernameField.textProperty().isEmpty().or(passwordField.textProperty().isEmpty()));
                delete.setVisible(false);
                title.setText("New User");
                confirm.setText("Create");

                break;
            case register:
                confirm.disableProperty().bind(usernameField.textProperty().isEmpty().or(passwordField.textProperty().isEmpty()));
                title.setText("Register");
                confirm.setText("Create");
                break;
                
             default:
            	 break;
        }
    }
    @Override
    public void initializeData(User user) {
        this.user = user;
        this.setView();
    }
    @Override
    public void initialize(URL location, ResourceBundle resources) {}
    @FXML
    public void confirmUser(ActionEvent event) {
        try {

            if (user.getId() == null) {
                user.setUsername(usernameField.getText());
                user.setPassword(passwordField.getText());
                userService.add(user);
                if (dispatcher.getSituation() == ViewSituations.register) {
                    dispatcher.logout();
                } else {
                    dispatcher.renderView("AdministratorViews/ManageUsersView/manage_users", this.admin);
                }

            } else {
                userService.modify(user.getId(), usernameField.getText(), passwordField.getText());
                dispatcher.renderView("AdministratorViews/ManageUsersView/manage_users", this.admin);
            }

        }catch (AlreadyExistingException e){

            existingLabel.setText(e.getMessage());
            existingLabel.setVisible(true);

        } catch (BusinessException e) {
            dispatcher.renderError(e);
        }
    }
    @FXML
    public void cancelModify(ActionEvent event) {
        switch (dispatcher.getSituation()){
            case register:
                dispatcher.logout();
                break;
            case newobject:
                dispatcher.renderView("AdministratorViews/ManageUsersView/manage_users", admin);
                break;
            default:
                dispatcher.setSituation(ViewSituations.detail);
                dispatcher.renderView("AdministratorViews/ManageUsersView/user_detail", user);
        }
    }
    @FXML
    public void deleteUser(ActionEvent event){
        try{
            if (user.getId() != null) {
                userService.delete(user);
            }
            dispatcher.renderView("AdministratorViews/ManageUsersView/manage_users", this.admin);

        } catch (BusinessException e) {
            dispatcher.renderError(e);
        }
    }
    @FXML
    public void showModify(ActionEvent event) {
        dispatcher.setSituation(ViewSituations.modify);
        dispatcher.renderView("AdministratorViews/ManageUsersView/user_modify", user);
    }

}
