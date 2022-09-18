package it.univaq.disim.oop.spacemusicunify.controller.administratorcontrollers;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import it.univaq.disim.oop.spacemusicunify.business.*;
import it.univaq.disim.oop.spacemusicunify.controller.DataInitializable;
import it.univaq.disim.oop.spacemusicunify.domain.*;
import it.univaq.disim.oop.spacemusicunify.view.ViewDispatcher;
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

public class AdministratorManageArtistsController implements Initializable, DataInitializable<Administrator> {
	private final SPACEMusicUnifyService spaceMusicUnifyService;
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
	public AdministratorManageArtistsController(){
		dispatcher = ViewDispatcher.getInstance();
		SpacemusicunifyBusinessFactory factory = SpacemusicunifyBusinessFactory.getInstance();
		spaceMusicUnifyService = factory.getSPACEMusicUnifyService();
	}

	@Override
	public void initialize(URL url, ResourceBundle resourceBundle) {
		id.setCellValueFactory(new PropertyValueFactory<>("id"));
		stageName.setCellValueFactory(new PropertyValueFactory<>("stageName"));
		yearsofActivity.setCellValueFactory(new PropertyValueFactory<>("yearsOfActivity"));
		biography.setCellValueFactory(new PropertyValueFactory<>("biography"));
		nationality.setCellValueFactory(new PropertyValueFactory<>("nationality"));


		viewmodify.setStyle("-fx-alignment: CENTER;");
		viewmodify.setCellValueFactory((TableColumn.CellDataFeatures<Artist, Button> param) -> {
			final Button modify = new Button("Detail");
			modify.setCursor(Cursor.HAND);
			modify.setOnAction((ActionEvent event) -> {
				spaceMusicUnifyService.setSituation(ViewSituations.detail);
				dispatcher.renderView("AdministratorViews/ManageArtistsView/artist_detail", param.getValue());
			});
			return new SimpleObjectProperty<Button>(modify);
		});
		viewalbums.setStyle("-fx-alignment: CENTER;");
		viewalbums.setCellValueFactory((TableColumn.CellDataFeatures<Artist, Button> param) -> {
			final Button modify = new Button("Albums");
			modify.setCursor(Cursor.HAND);
			modify.setOnAction((ActionEvent event) -> {
				spaceMusicUnifyService.setSituation(ViewSituations.detail);
				dispatcher.renderView("AdministratorViews/ManageArtistsView/ManageAlbumsView/manage_albums", param.getValue().getDiscography());
			});
			return new SimpleObjectProperty<Button>(modify);
		});
		songNumber.setCellValueFactory((TableColumn.CellDataFeatures<Artist, String> param) -> {
			int count = 0;

			for (Album album : param.getValue().getDiscography()) {
				count = count + album.getSongList().size();
			}
			String number = String.valueOf(count);
			return new SimpleStringProperty(number);
		});

	}

	@Override
	public void initializeData(Administrator amministratore) {
			List<Artist> artisti = spaceMusicUnifyService.getAllArtists();
			ObservableList<Artist> artistaData = FXCollections.observableArrayList(artisti);
			artistList.setItems(artistaData);
		

	}



	@FXML
	public void newArtist(ActionEvent event) {
		Artist artista = new Artist();
		artista.setStageName("test group");
		artista.setBiography("test bio");
		artista.setNationality(Nationality.british);
		artista.setYearsOfActivity(1);

		this.spaceMusicUnifyService.setSituation(ViewSituations.newobject);
		dispatcher.renderView("AdministratorViews/ManageArtistsView/artist_detail", artista);
	}


}