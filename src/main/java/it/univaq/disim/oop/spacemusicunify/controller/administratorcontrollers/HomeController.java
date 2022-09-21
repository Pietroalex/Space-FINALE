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
	private Label benvenutoLabel;
	@Override
	public void initialize(URL location, ResourceBundle resources) {
	}

	@Override
	public void initializeData(Administrator amministratore) {
		benvenutoLabel.setText("Benvenuto " + amministratore.getUsername().substring(0, 1).toUpperCase(Locale.ROOT).concat(amministratore.getUsername().substring(1)) + "!");
	}

}
