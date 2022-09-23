package it.univaq.disim.oop.spacemusicunify;

import it.univaq.disim.oop.spacemusicunify.business.SpacemusicunifyBusinessFactory;
import it.univaq.disim.oop.spacemusicunify.view.ViewDispatcher;
import javafx.application.Application;
import javafx.stage.Stage;

public class SpaceMusicUniFyApplication extends Application {

	public static void main(String[] args) {
		SpacemusicunifyBusinessFactory factory = SpacemusicunifyBusinessFactory.getInstance();
		/*UserService UserService = factory.getUserService();
		UserService.setAllDefaults();*/
		
		launch(args);
	}

	@Override
	public void start(Stage stage) throws Exception {
		ViewDispatcher dispatcher = ViewDispatcher.getInstance();
		dispatcher.loginView(stage);
	}

}
