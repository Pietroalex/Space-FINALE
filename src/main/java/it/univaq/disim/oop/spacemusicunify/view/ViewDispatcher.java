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
	private static final String RESOURCE_BASE = "/viste/";
	private static final String FXML_SUFFIX = ".fxml";
	
	private Stage stage;
	private BorderPane layout;
	
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
		stage.getIcons().add(new Image("/viste/AdministratorViews/AdministratorHomeView/icon/logo.png"));
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

	
	public void loggedIn(GeneralUser utenteGenerico) throws ViewException {
		//definire se caricare la vista utente o amministratore
		
		//vista utente
		if(utenteGenerico instanceof User) {
		try{
			View<User> layoutView = loadView("UserViews/UserHomeView/layout");
			layout = (BorderPane) layoutView.getView();
			DataInitializable<User> layoutController = layoutView.getController();
			layoutController.initializeData((User) utenteGenerico);
			

			renderView("UserViews/UserHomeView/home",(User) utenteGenerico);
			Scene scene = new Scene(layout);
			stage.setScene(scene);
	} catch (ViewException e) {
		e.printStackTrace();
		renderError(e);
	}
		} else {
			try{
				View<Administrator> layoutView = loadView("AdministratorViews/AdministratorHomeView/layout");
				DataInitializable<Administrator> layoutController = layoutView.getController();
				layoutController.initializeData((Administrator) utenteGenerico);
				
				layout = (BorderPane) layoutView.getView();
				renderView("AdministratorViews/AdministratorHomeView/home",(Administrator) utenteGenerico);
				Scene scene = new Scene(layout);
				stage.setScene(scene);
			} catch (ViewException e) {
				e.printStackTrace();
				renderError(e);
			}
		}
	}
	public <T> void renderView(String viewName, T data) {
		try {
			View<T> view = loadView(viewName);
			DataInitializable<T> controller = view.getController();
			controller.initializeData(data);
			layout.setCenter(view.getView());
		} catch (ViewException e) {
			renderError(e);
		}
	}
	public <T> void renderPlayer(String viewName, T data) {
		try {
			View<T> view = loadView(viewName);
			DataInitializable<T> controller = view.getController();
			controller.initializeData(data);
			layout.setBottom(view.getView());
		} catch (ViewException e) {
			renderError(e);
		}
	}
	public <T> void renderPlaylists(String viewName, T data) {
		try {
			View<T> view = loadView(viewName);
			DataInitializable<T> controller = view.getController();
			controller.initializeData(data);
			layout.setLeft(view.getView());
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


	public <T> void registerView(String s, T utente) {
		try {
			View<T> view = loadView(s);
			DataInitializable<T> controller = view.getController();
			controller.initializeData(utente);
			Scene scene = new Scene(view.getView());
			stage.setScene(scene);
		} catch (ViewException e) {
			renderError(e);
		}
	}
}
