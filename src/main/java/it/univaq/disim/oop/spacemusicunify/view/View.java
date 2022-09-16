package it.univaq.disim.oop.spacemusicunify.view;

import it.univaq.disim.oop.spacemusicunify.controller.DataInitializable;
import javafx.scene.Parent;

public class View<T> extends Parent {
	
	private Parent view;
	private DataInitializable<T> controller;
	
	public View(Parent view, DataInitializable<T> controller) {
		super();
		this.view = view;
		this.controller = controller;
	}

	public Parent getView() {
		return view;
	}
	public void setView(Parent view) {
		this.view = view;
	}

	public DataInitializable<T> getController() {
		return controller;
	}
	
	public void setController(DataInitializable<T> controller) {
		this.controller = controller;
	}
	
}