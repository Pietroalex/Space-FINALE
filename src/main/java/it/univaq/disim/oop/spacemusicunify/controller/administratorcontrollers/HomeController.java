package it.univaq.disim.oop.spacemusicunify.controller.administratorcontrollers;

import it.univaq.disim.oop.spacemusicunify.controller.DataInitializable;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;

import java.net.URL;
import java.util.Locale;
import java.util.ResourceBundle;

import it.univaq.disim.oop.spacemusicunify.domain.Administrator;

public class HomeController implements Initializable, DataInitializable<Administrator> {

	@FXML
	private Label entryLabel;
	@Override
	public void initialize(URL location, ResourceBundle resources) {}

	@Override
	public void initializeData(Administrator admin) {
		entryLabel.setText("Benvenuto " + admin.getUsername().substring(0, 1).toUpperCase(Locale.ROOT).concat(admin.getUsername().substring(1)) + "!");
	}

}
