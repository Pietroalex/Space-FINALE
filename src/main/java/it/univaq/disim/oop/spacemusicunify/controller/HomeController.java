package it.univaq.disim.oop.spacemusicunify.controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;

import java.net.URL;
import java.util.Locale;
import java.util.ResourceBundle;

import it.univaq.disim.oop.spacemusicunify.domain.GeneralUser;
import it.univaq.disim.oop.spacemusicunify.domain.User;

public class HomeController implements Initializable, DataInitializable<GeneralUser> {

	@FXML
	private Label welcomeLabel;
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {}
	@Override
	public void initializeData(GeneralUser user) {
		welcomeLabel.setText("Welcome " +  user.getUsername().substring(0, 1).toUpperCase(Locale.ROOT).concat(user.getUsername().substring(1)) + "!");
	}
}
