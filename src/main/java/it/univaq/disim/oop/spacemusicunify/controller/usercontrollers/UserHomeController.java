package it.univaq.disim.oop.spacemusicunify.controller.usercontrollers;

import it.univaq.disim.oop.spacemusicunify.controller.DataInitializable;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;

import java.net.URL;
import java.util.Locale;
import java.util.ResourceBundle;

import it.univaq.disim.oop.spacemusicunify.domain.User;

public class UserHomeController implements Initializable, DataInitializable<User> {

	@FXML
	private Label benvenutoLabel;
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		
	}
	@Override
	public void initializeData(User utente) {
		benvenutoLabel.setText("Benvenuto " +  utente.getUsername().substring(0, 1).toUpperCase(Locale.ROOT).concat(utente.getUsername().substring(1)) + "!");
	}
}
