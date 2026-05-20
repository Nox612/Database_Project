package com.project.artconnect.ui;

import com.project.artconnect.model.Gallery;
import com.project.artconnect.service.GalleryService;
import com.project.artconnect.util.ServiceProvider;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;

public class GalleryController {
    @FXML
    private ListView<Gallery> galleryList;

    // CRUD text fields
    @FXML private TextField nameInput;
    @FXML private TextField addressInput;
    @FXML private TextField ratingInput;

    private final GalleryService galleryService = ServiceProvider.getGalleryService();

    @FXML
    public void initialize() {
        refreshData();

        // Custom cell factory to show more info
        galleryList.setCellFactory(lv -> new javafx.scene.control.ListCell<>() {
            @Override
            protected void updateItem(Gallery item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.getName() + " - " + item.getAddress() + " (" + item.getRating() + "/5.0)");
                }
            }
        });

        // Listen for selection changes and show details in the form
        galleryList.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> showGalleryDetails(newValue));
    }

    private void showGalleryDetails(Gallery gallery) {
        if (gallery != null) {
            nameInput.setText(gallery.getName());
            addressInput.setText(gallery.getAddress());
            ratingInput.setText(String.valueOf(gallery.getRating()));
        } else {
            nameInput.clear();
            addressInput.clear();
            ratingInput.clear();
        }
    }

    @FXML
    private void handleAdd() {
        Gallery g = new Gallery();
        g.setName(nameInput.getText());
        g.setAddress(addressInput.getText());
        try {
            g.setRating(Double.parseDouble(ratingInput.getText()));
        } catch (Exception ex) {
            g.setRating(0);
        }

        galleryService.createGallery(g);
        refreshData();
        showGalleryDetails(null);
    }

    @FXML
    private void handleUpdate() {
        // FIXED: Using galleryList instead of galleryTable
        Gallery g = galleryList.getSelectionModel().getSelectedItem();
        if (g != null) {
            g.setAddress(addressInput.getText());
            try {
                g.setRating(Double.parseDouble(ratingInput.getText()));
            } catch (Exception ex) {
                g.setRating(0);
            }

            galleryService.updateGallery(g);
            refreshData();
        }
    }

    @FXML
    private void handleDelete() {
        // FIXED: Using galleryList instead of galleryTable
        Gallery g = galleryList.getSelectionModel().getSelectedItem();
        if (g != null) {
            galleryService.deleteGallery(g.getName());
            refreshData();
        }
    }

    // Helper method to reload data from DB
    private void refreshData() {
        galleryList.setItems(FXCollections.observableArrayList(galleryService.getAllGalleries()));
    }
}