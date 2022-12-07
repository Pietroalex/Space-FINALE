package it.univaq.disim.oop.spacemusicunify.controller.administratorcontrollers;

import java.net.URL;
import java.util.HashSet;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Set;

import it.univaq.disim.oop.spacemusicunify.business.*;
import it.univaq.disim.oop.spacemusicunify.controller.DataInitializable;
import it.univaq.disim.oop.spacemusicunify.domain.*;
import it.univaq.disim.oop.spacemusicunify.view.ViewDispatcher;
import it.univaq.disim.oop.spacemusicunify.view.ViewSituations;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Cursor;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

public class ManageArtistsController implements Initializable, DataInitializable<Administrator> {
	private final ArtistService artistService;
	@FXML
	private TableView<Artist> artistList;
	@FXML
	private TableColumn<Artist, String> id;

	@FXML
	private TableColumn<Artist, String> stageName;
	@FXML
	private TableColumn<Artist, Integer> yearsofActivity;

	@FXML
	private TableColumn<Artist, String> nationality;
	@FXML
	private TableColumn<Artist, String> songNumber;
	@FXML
	private TableColumn<Artist, String> biography;
	@FXML
	private TableColumn<Artist, Button> viewmodify;
	@FXML
	private TableColumn<Artist, Button> viewalbums;

	private ViewDispatcher dispatcher;
	public ManageArtistsController(){
		dispatcher = ViewDispatcher.getInstance();
		SpacemusicunifyBusinessFactory factory = SpacemusicunifyBusinessFactory.getInstance();
		artistService = factory.getArtistService();
	}

	@Override
	public void initialize(URL url, ResourceBundle resourceBundle) {
		id.setCellValueFactory(new PropertyValueFactory<>("id"));
		stageName.setCellValueFactory(new PropertyValueFactory<>("name"));
		yearsofActivity.setCellValueFactory(new PropertyValueFactory<>("yearsOfActivity"));
		biography.setCellValueFactory(new PropertyValueFactory<>("biography"));
		nationality.setCellValueFactory(new PropertyValueFactory<>("nationality"));


		viewmodify.setStyle("-fx-alignment: CENTER;");
		viewmodify.setCellValueFactory((TableColumn.CellDataFeatures<Artist, Button> param) -> {
			final Button modify = new Button("Detail");
			modify.setId("b3");
			modify.setCursor(Cursor.HAND);
			modify.setOnAction((ActionEvent event) -> {
				dispatcher.setSituation(ViewSituations.detail);
				dispatcher.renderView("AdministratorViews/ManageArtistsView/artist_detail", param.getValue());
			});
			return new SimpleObjectProperty<Button>(modify);
		});
		viewalbums.setStyle("-fx-alignment: CENTER;");
		viewalbums.setCellValueFactory((TableColumn.CellDataFeatures<Artist, Button> param) -> {
			final Button modify = new Button("Albums");
			modify.setId("b3");
			modify.setCursor(Cursor.HAND);
			modify.setOnAction((ActionEvent event) -> {
				Set<Artist> artists = new HashSet<>();
				artists.add(param.getValue());
				SpacemusicunifyBusinessFactory.getInstance().getAlbumService().setChosenArtists(artists);
				dispatcher.setSituation(ViewSituations.detail);
				dispatcher.renderView("AdministratorViews/ManageArtistsView/ManageAlbumsView/manage_albums", param.getValue());
			});
			return new SimpleObjectProperty<Button>(modify);
		});
		songNumber.setCellValueFactory((TableColumn.CellDataFeatures<Artist, String> param) -> {
			int count = 0;

			try {
				for (Production production : artistService.findAllProductions(param.getValue())) {
					count = count + production.getAlbum().getSongs().size();
				}
			} catch (BusinessException e) {
				dispatcher.renderError(e);
			}
			String number = String.valueOf(count);
			return new SimpleStringProperty(number);
		});

	}

	@Override
	public void initializeData(Administrator admin) {
		Set<Artist> artists = null;
		try {
			artists = artistService.getArtistList();
		} catch (BusinessException e) {
			dispatcher.renderError(e);
		}
		ObservableList<Artist> artistaData = FXCollections.observableArrayList(artists);
			artistList.setItems(artistaData);
		

	}



	@FXML
	public void newArtist(ActionEvent event) {
		Artist artist = new Artist();
		artist.setName("test group");
		artist.setYearsOfActivity(1);
		artist.setBiography("test bio");
		Set<Picture> pictures = new HashSet<>();
		artist.setPictures(pictures);
		artist.setNationality(Nationality.british);
		Set<Artist> artists = new HashSet<>();
		artist.setBandMembers(artists);


		dispatcher.setSituation(ViewSituations.newobject);
		dispatcher.renderView("AdministratorViews/ManageArtistsView/artist_modify", artist);
	}


}