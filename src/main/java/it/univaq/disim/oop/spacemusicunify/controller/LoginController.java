package it.univaq.disim.oop.spacemusicunify.controller;

import java.net.URL;
import java.util.ResourceBundle;

import it.univaq.disim.oop.spacemusicunify.business.*;
import it.univaq.disim.oop.spacemusicunify.domain.Utente;
import it.univaq.disim.oop.spacemusicunify.domain.UtenteGenerico;
import it.univaq.disim.oop.spacemusicunify.view.ViewDispatcher;
import it.univaq.disim.oop.spacemusicunify.view.ViewException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public class LoginController implements Initializable, DataInitializable<Object>{
	
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
	
	private UtenteGenericoService utenteGenericoService;
	private SPACEMusicUnifyService SPACEMusicUnifyService;
	
	public LoginController() {
		dispatcher = ViewDispatcher.getInstance();
		SpacemusicunifyBusinessFactory factory = SpacemusicunifyBusinessFactory.getInstance();
		utenteGenericoService = factory.getUtenteGenerico();
		SPACEMusicUnifyService = factory.getAmministratoreService();
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		loginButton.disableProperty().bind(username.textProperty().isEmpty()
										   .or(password.textProperty().isEmpty()));
		this.errorLabel.setVisible(false);
	}
	
	@FXML
	private void loginAction(ActionEvent event) throws ViewException {
		
		try {
			UtenteGenerico utenteGenerico = utenteGenericoService.authenticate(username.getText(), password.getText());
			dispatcher.loggedIn(utenteGenerico);
		} catch (UtenteGenericoNotFoundException e) {
			errorLabel.setVisible(true);

		} catch (BusinessException e) {
			dispatcher.renderError(e);
		} catch (ViewException e) {
			e.printStackTrace();
			throw new ViewException();
		}
		
	}
	
	@FXML
	private void signupAction(ActionEvent event) {
		Utente utente = new Utente();
		utente.setUsername("utente");
		utente.setPassword("123456");

		utenteGenericoService.setSituation(ViewSituations.register);
		dispatcher.registerView("RegisterView/user_detail", utente);
	}

}
