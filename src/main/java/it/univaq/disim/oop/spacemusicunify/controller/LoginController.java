package it.univaq.disim.oop.spacemusicunify.controller;

import java.net.URL;
import java.util.ResourceBundle;

import it.univaq.disim.oop.spacemusicunify.business.*;
import it.univaq.disim.oop.spacemusicunify.domain.User;
import it.univaq.disim.oop.spacemusicunify.domain.GeneralUser;
import it.univaq.disim.oop.spacemusicunify.view.ViewDispatcher;
import it.univaq.disim.oop.spacemusicunify.view.ViewException;
import it.univaq.disim.oop.spacemusicunify.view.ViewSituations;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public class LoginController implements Initializable, DataInitializable<Object>{

	private final UserService userService;
	@FXML
	private TextField username;
	@FXML
	private TextField password;
	@FXML
	private Label errorLabel;
	@FXML
	private Button loginButton;
	@FXML
	private Button signupButton;
	
	private ViewDispatcher dispatcher;
	

	
	public LoginController() {
		dispatcher = ViewDispatcher.getInstance();
		SpacemusicunifyBusinessFactory factory = SpacemusicunifyBusinessFactory.getInstance();
		userService = factory.getUserService();
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		loginButton.disableProperty().bind(username.textProperty().isEmpty()
										   .or(password.textProperty().isEmpty()));
	}
	
	@FXML
	private void loginAction(ActionEvent event) throws ViewException {
		
		try {
			GeneralUser generalUser = userService.authenticate(username.getText(), password.getText());
			dispatcher.loggedIn(generalUser);
		} catch (UserNotFoundException e) {
			errorLabel.setText(e.getMessage());
			errorLabel.setVisible(true);
		} catch (BusinessException e) {
			dispatcher.renderError(e);
		} catch (ViewException e) {
			throw new ViewException();
		}
		
	}
	
	@FXML
	private void signupAction(ActionEvent event) {
		dispatcher.setSituation(ViewSituations.register);
		dispatcher.renderView("RegisterView/user_detail", new User());
	}

}
