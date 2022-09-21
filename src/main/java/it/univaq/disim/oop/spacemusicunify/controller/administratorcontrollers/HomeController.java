package it.univaq.disim.oop.spacemusicunify.controller.administratorcontrollers;

import it.univaq.disim.oop.spacemusicunify.controller.DataInitializable;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;

import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.Locale;
import java.util.ResourceBundle;

import it.univaq.disim.oop.spacemusicunify.domain.Administrator;
import javafx.stage.FileChooser;


public class HomeController implements Initializable, DataInitializable<Administrator> {

	@FXML
	private Label benvenutoLabel;
	@Override
	public void initialize(URL location, ResourceBundle resources) {
	}

	@Override
	public void initializeData(Administrator amministratore) {
		benvenutoLabel.setText("Benvenuto " + amministratore.getUsername().substring(0, 1).toUpperCase(Locale.ROOT).concat(amministratore.getUsername().substring(1)) + "!");
/*		FileChooser fileChoose = new FileChooser();
		File file =  fileChoose.showOpenDialog(null);

		if(file != null) {
			String path = file.getPath();
			if (path.endsWith(".png") || path.endsWith(".jpg")) {
				ByteArrayOutputStream outStreamObj = new ByteArrayOutputStream();
				try {
					outStreamObj.writeBytes(Files.readAllBytes(Paths.get(file.getPath())));
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
				try {
					Files.write(Paths.get("src" + File.separator + "main" + File.separator + "resources" + File.separator + "dati"+File.separator+"immagini"+File.separator+"foto.jpg"), outStreamObj.toByteArray());
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
			}
		}*/
	}

}
