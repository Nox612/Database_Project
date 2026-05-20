package com.project.artconnect.ui;

import com.project.artconnect.model.Exhibition;
import com.project.artconnect.service.ExhibitionService;
import com.project.artconnect.util.ServiceProvider;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import java.time.LocalDate;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import com.project.artconnect.model.Gallery;

public class ExhibitionController {
    @FXML
    private TableView<Exhibition> exhibitionTable;
    @FXML
    private TableColumn<Exhibition, String> titleColumn;
    @FXML
    private TableColumn<Exhibition, LocalDate> dateColumn;
    @FXML
    private TableColumn<Exhibition, String> themeColumn;
    @FXML
    private TableColumn<Exhibition, String> galleryColumn;
    @FXML
    private TextField titleInput;
    @FXML
    private TextField themeInput;
    @FXML
    private DatePicker dateInput;
    @FXML
    private TextField galleryInput;

    // Use ExhibitionService instead of GalleryService!
    private final ExhibitionService exhibitionService = ServiceProvider.getExhibitionService();

    @FXML
    public void initialize() {
        titleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("startDate"));
        themeColumn.setCellValueFactory(new PropertyValueFactory<>("theme"));

        galleryColumn.setCellValueFactory(cellData -> new SimpleStringProperty(
                cellData.getValue().getGallery() != null ? cellData.getValue().getGallery().getName() : "Unknown"));

        refreshData();
        exhibitionTable.getSelectionModel().selectedItemProperty().addListener((obs, oldV, newV) -> showExhibitionDetails(newV));
    }

    private void refreshData() {
        // Fetch directly from the Database using the Exhibition Service!
        exhibitionTable.setItems(FXCollections.observableArrayList(exhibitionService.getAllExhibitions()));
    }

    private void showExhibitionDetails(Exhibition exhibition) {
        if (exhibition != null) {
            titleInput.setText(exhibition.getTitle());
            themeInput.setText(exhibition.getTheme());
            dateInput.setValue(exhibition.getStartDate());
            galleryInput.setText(exhibition.getGallery() != null ? exhibition.getGallery().getName() : "");
        } else {
            titleInput.clear(); themeInput.clear(); dateInput.setValue(null); galleryInput.clear();
        }
    }

    @FXML private void handleAdd() {
        Exhibition e = new Exhibition();
        e.setTitle(titleInput.getText());
        e.setTheme(themeInput.getText());
        e.setStartDate(dateInput.getValue());
        Gallery g = new Gallery(); g.setName(galleryInput.getText());
        e.setGallery(g);
        exhibitionService.createExhibition(e);
        refreshData(); showExhibitionDetails(null);
    }

    @FXML private void handleUpdate() {
        Exhibition e = exhibitionTable.getSelectionModel().getSelectedItem();
        if (e != null) {
            e.setTheme(themeInput.getText());
            exhibitionService.updateExhibition(e);
            refreshData();
        }
    }

    @FXML private void handleDelete() {
        Exhibition e = exhibitionTable.getSelectionModel().getSelectedItem();
        if (e != null) {
            exhibitionService.deleteExhibition(e.getTitle());
            refreshData();
        }
    }
}