package com.project.artconnect.ui;

import com.project.artconnect.model.Artwork;
import com.project.artconnect.service.ArtworkService;
import com.project.artconnect.util.ServiceProvider;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.TextField;
import javafx.scene.control.ComboBox;
import com.project.artconnect.model.Artist;

public class ArtworkController {
    @FXML
    private TableView<Artwork> artworkTable;
    @FXML
    private TableColumn<Artwork, String> titleColumn;
    @FXML
    private TableColumn<Artwork, String> typeColumn;
    @FXML
    private TableColumn<Artwork, Double> priceColumn;
    @FXML
    private TableColumn<Artwork, String> statusColumn;
    @FXML
    private TableColumn<Artwork, String> artistColumn;
    @FXML
    private TextField titleInput;
    @FXML
    private TextField typeInput;
    @FXML
    private TextField priceInput;
    @FXML
    private ComboBox<Artwork.Status> statusInput;
    @FXML
    private TextField artistInput;

    private final ArtworkService artworkService = ServiceProvider.getArtworkService();

    @FXML
    public void initialize() {
        titleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        typeColumn.setCellValueFactory(new PropertyValueFactory<>("type"));
        priceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));

        artistColumn.setCellValueFactory(cellData -> new SimpleStringProperty(
                cellData.getValue().getArtist() != null ? cellData.getValue().getArtist().getName() : "Unknown"));

        artworkTable.setItems(FXCollections.observableArrayList(artworkService.getAllArtworks()));
        statusInput.setItems(FXCollections.observableArrayList(Artwork.Status.values()));
        artworkTable.getSelectionModel().selectedItemProperty().addListener((obs, oldV, newV) -> showArtworkDetails(newV));
    }

    private void showArtworkDetails(Artwork artwork) {
        if (artwork != null) {
            titleInput.setText(artwork.getTitle());
            typeInput.setText(artwork.getType());
            priceInput.setText(String.valueOf(artwork.getPrice()));
            statusInput.setValue(artwork.getStatus());
            artistInput.setText(artwork.getArtist() != null ? artwork.getArtist().getName() : "");
        } else {
            titleInput.clear(); typeInput.clear(); priceInput.clear();
            statusInput.setValue(null); artistInput.clear();
        }
    }

    @FXML private void handleAdd() {
        Artwork artwork = new Artwork();
        artwork.setTitle(titleInput.getText());
        artwork.setType(typeInput.getText());
        try { artwork.setPrice(Double.parseDouble(priceInput.getText())); } catch (Exception e) { artwork.setPrice(0); }
        artwork.setStatus(statusInput.getValue());
        Artist artist = new Artist(); artist.setName(artistInput.getText());
        artwork.setArtist(artist);
        artworkService.createArtwork(artwork);
        refreshData(); showArtworkDetails(null);
    }

    @FXML private void handleUpdate() {
        Artwork artwork = artworkTable.getSelectionModel().getSelectedItem();
        if (artwork != null) {
            artwork.setType(typeInput.getText());
            try { artwork.setPrice(Double.parseDouble(priceInput.getText())); } catch (Exception e) { artwork.setPrice(0); }
            artwork.setStatus(statusInput.getValue());
            Artist artist = new Artist(); artist.setName(artistInput.getText());
            artwork.setArtist(artist);
            artworkService.updateArtwork(artwork);
            refreshData();
        }
    }

    @FXML private void handleDelete() {
        Artwork artwork = artworkTable.getSelectionModel().getSelectedItem();
        if (artwork != null) {
            artworkService.deleteArtwork(artwork.getTitle());
            refreshData();
        }
    }

    private void refreshData() {
        // Fetch directly from the Database using the Exhibition Service!
        artworkTable.setItems(FXCollections.observableArrayList(artworkService.getAllArtworks()));
    }
}
