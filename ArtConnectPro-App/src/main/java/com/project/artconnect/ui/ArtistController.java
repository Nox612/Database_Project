package com.project.artconnect.ui;

import com.project.artconnect.model.Artist;
import com.project.artconnect.model.Discipline;
import com.project.artconnect.service.ArtistService;
import com.project.artconnect.util.ServiceProvider;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

public class ArtistController {
    @FXML private TextField searchField;
    @FXML private ComboBox<Discipline> disciplineFilter;

    @FXML private TableView<Artist> artistTable;
    @FXML private TableColumn<Artist, String> nameColumn;
    @FXML private TableColumn<Artist, String> cityColumn;
    @FXML private TableColumn<Artist, String> emailColumn;
    @FXML private TableColumn<Artist, Integer> yearColumn;

    // New FXML elements for CRUD
    @FXML private TextField nameInput;
    @FXML private TextField cityInput;
    @FXML private TextField emailInput;
    @FXML private TextField yearInput;

    private final ArtistService artistService = ServiceProvider.getArtistService();

    @FXML
    public void initialize() {
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        cityColumn.setCellValueFactory(new PropertyValueFactory<>("city"));
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("contactEmail"));
        yearColumn.setCellValueFactory(new PropertyValueFactory<>("birthYear"));

        disciplineFilter.setItems(FXCollections.observableArrayList(artistService.getAllDisciplines()));

        // Listen for selection changes and show the artist details in the form
        artistTable.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> showArtistDetails(newValue));

        refreshTable();
    }

    private void showArtistDetails(Artist artist) {
        if (artist != null) {
            nameInput.setText(artist.getName());
            cityInput.setText(artist.getCity());
            emailInput.setText(artist.getContactEmail());
            yearInput.setText(artist.getBirthYear() != null ? String.valueOf(artist.getBirthYear()) : "");
        } else {
            nameInput.clear();
            cityInput.clear();
            emailInput.clear();
            yearInput.clear();
        }
    }

    @FXML
    private void handleAdd() {
        Artist newArtist = new Artist();
        newArtist.setName(nameInput.getText());
        newArtist.setCity(cityInput.getText());
        newArtist.setContactEmail(emailInput.getText());
        try {
            newArtist.setBirthYear(Integer.parseInt(yearInput.getText()));
        } catch (NumberFormatException e) {
            newArtist.setBirthYear(null);
        }

        // Persist to Database
        artistService.createArtist(newArtist);
        refreshTable();
        showArtistDetails(null); // Clear form
    }

    @FXML
    private void handleUpdate() {
        Artist selectedArtist = artistTable.getSelectionModel().getSelectedItem();
        if (selectedArtist != null) {
            selectedArtist.setName(nameInput.getText());
            selectedArtist.setCity(cityInput.getText());
            selectedArtist.setContactEmail(emailInput.getText());
            try {
                selectedArtist.setBirthYear(Integer.parseInt(yearInput.getText()));
            } catch (NumberFormatException e) {
                selectedArtist.setBirthYear(null);
            }

            // Update in Database
            artistService.updateArtist(selectedArtist);
            refreshTable();
        }
    }

    @FXML
    private void handleDelete() {
        Artist selectedArtist = artistTable.getSelectionModel().getSelectedItem();
        if (selectedArtist != null) {
            // Delete from Database
            artistService.deleteArtist(selectedArtist.getName());
            refreshTable();
        }
    }

    @FXML
    private void handleSearch() {
        String query = searchField.getText();
        Discipline d = disciplineFilter.getValue();
        String dName = (d != null) ? d.getName() : null;
        artistTable.setItems(FXCollections.observableArrayList(artistService.searchArtists(query, dName, null)));
    }

    @FXML
    private void handleReset() {
        searchField.clear();
        disciplineFilter.setValue(null);
        refreshTable();
    }

    private void refreshTable() {
        // Fetches fresh data from the Database
        artistTable.setItems(FXCollections.observableArrayList(artistService.getAllArtists()));
    }
}