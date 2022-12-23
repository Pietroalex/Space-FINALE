package it.univaq.disim.oop.spacemusicunify.view;

import java.io.IOException;
import it.univaq.disim.oop.spacemusicunify.controller.DataInitializable;
import it.univaq.disim.oop.spacemusicunify.domain.Administrator;
import it.univaq.disim.oop.spacemusicunify.domain.User;
import it.univaq.disim.oop.spacemusicunify.domain.GeneralUser;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class ViewDispatcher {
	private static final String RESOURCE_BASE = "/views/";
	private static final String FXML_SUFFIX = ".fxml";
	
	private Stage stage;
	private BorderPane layout;
	private ViewSituations situation;
	public BorderPane getLayout(){
		return layout;
	}
	private static ViewDispatcher instance = new ViewDispatcher();
	
	private ViewDispatcher() { //costruttore privato in modo tale che nessuna classe possa istanziare un nuovo ViewDispatcher
	}
	
	public static ViewDispatcher getInstance() {
		return instance;
	}

	public void loginView(Stage stage) throws ViewException {
		this.stage = stage;
		Parent login = loadView("LoginView/login").getView();
		Scene scene = new Scene(login);
		stage.setScene(scene);
		stage.setResizable(false);
		stage.setTitle("Space Music UNIFY");
		stage.getIcons().add(new Image("/views/AdministratorViews/LayoutView/icon/logo.png"));
		stage.show();
	}

	private <T> View<T> loadView(String view) throws ViewException {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource(RESOURCE_BASE + view + FXML_SUFFIX));
			Parent parent = (Parent) loader.load();
			return new View<>(parent, loader.getController());
		} catch (IOException e) {
			throw new ViewException(e);
		}
	}
	
	public void renderError(Exception e) {
		e.printStackTrace();
		System.exit(1);
	}

	
	public void loggedIn(GeneralUser generalUser) throws ViewException {
		//definire se caricare la vista utente o amministratore
		try{
			
			if(generalUser instanceof User) {
				//vista utente
				View<User> layoutView = loadView("UserViews/LayoutView/layout");
				layout = (BorderPane) layoutView.getView();
				DataInitializable<User> layoutController = layoutView.getController();
				layoutController.initializeData((User) generalUser);
			} else {
				//vista amministratore	
				View<Administrator> layoutView = loadView("AdministratorViews/LayoutView/layout");
				DataInitializable<Administrator> layoutController = layoutView.getController();
				layoutController.initializeData((Administrator) generalUser);
				layout = (BorderPane) layoutView.getView();
			}
			renderView("HomeView/home", generalUser);
			Scene scene = new Scene(layout);
			stage.setScene(scene);
		} catch (ViewException e) {
			renderError(e);
		}
	}
	public <T> void renderView(String viewName, T data) {
		try {
			View<T> view = loadView(viewName);
			DataInitializable<T> controller = view.getController();
			controller.initializeData(data);
			String[] str = viewName.split("/");
			switch (str[str.length-2]+"/"+str[str.length-1]){
				case "LayoutView/playerPane":
					layout.setBottom(view.getView());
					break;

				case "LayoutView/playlistPane":
					layout.setLeft(view.getView());
					break;

				case "RegisterView/user_detail":
					Scene scene = new Scene(view.getView());
					stage.setScene(scene);
					break;

				default:
					layout.setCenter(view.getView());
					break;
			}
		} catch (ViewException e) {
			renderError(e);
		}
	}

	public void logout() {
		try {
			Parent loginView = loadView("LoginView/login").getView();
			Scene scene = new Scene(loginView);
			stage.setScene(scene);
		} catch (ViewException e) {
			renderError(e);
		}
	}
	public void setSituation(ViewSituations sit) {
		situation = sit;
	}
	public ViewSituations getSituation() {
		return situation;
	}
}
