package com.project.artconnect.ui;

import com.project.artconnect.model.Workshop;
import com.project.artconnect.service.WorkshopService;
import com.project.artconnect.util.ServiceProvider;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import java.time.LocalDateTime;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import com.project.artconnect.model.Artist;

public class WorkshopController {
    @FXML
    private TableView<Workshop> workshopTable;
    @FXML
    private TableColumn<Workshop, String> titleColumn;
    @FXML
    private TableColumn<Workshop, LocalDateTime> dateColumn;
    @FXML
    private TableColumn<Workshop, String> instructorColumn;
    @FXML
    private TableColumn<Workshop, Double> priceColumn;
    @FXML
    private TableColumn<Workshop, String> levelColumn;
    @FXML
    private TextField titleInput;
    @FXML
    private TextField priceInput;
    @FXML
    private TextField levelInput;
    @FXML
    private DatePicker dateInput;
    @FXML
    private TextField instructorInput;

    private final WorkshopService workshopService = ServiceProvider.getWorkshopService();

    @FXML
    public void initialize() {
        titleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));
        priceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));
        levelColumn.setCellValueFactory(new PropertyValueFactory<>("level"));

        instructorColumn.setCellValueFactory(cellData -> new SimpleStringProperty(
                cellData.getValue().getInstructor() != null ? cellData.getValue().getInstructor().getName()
                        : "Unknown"));

        workshopTable.setItems(FXCollections.observableArrayList(workshopService.getAllWorkshops()));

        workshopTable.getSelectionModel().selectedItemProperty().addListener((obs, old, newV) -> showWorkshopDetails(newV));
    }

    private void showWorkshopDetails(Workshop workshop) {
        if (workshop != null) {
            titleInput.setText(workshop.getTitle());
            priceInput.setText(String.valueOf(workshop.getPrice()));
            levelInput.setText(workshop.getLevel());
            dateInput.setValue(workshop.getDate() != null ? workshop.getDate().toLocalDate() : null);
            instructorInput.setText(workshop.getInstructor() != null ? workshop.getInstructor().getName() : "");
        } else {
            titleInput.clear(); priceInput.clear(); levelInput.clear(); dateInput.setValue(null); instructorInput.clear();
        }
    }

    @FXML private void handleAdd() {
        Workshop w = new Workshop();
        w.setTitle(titleInput.getText());
        w.setLevel(levelInput.getText());
        try { w.setPrice(Double.parseDouble(priceInput.getText())); } catch (Exception e) { w.setPrice(0); }
        if (dateInput.getValue() != null) w.setDate(dateInput.getValue().atStartOfDay());
        Artist a = new Artist(); a.setName(instructorInput.getText());
        w.setInstructor(a);
        workshopService.createWorkshop(w);
        refreshData(); showWorkshopDetails(null);
    }

    @FXML private void handleUpdate() {
        Workshop w = workshopTable.getSelectionModel().getSelectedItem();
        if (w != null) {
            w.setLevel(levelInput.getText());
            try { w.setPrice(Double.parseDouble(priceInput.getText())); } catch (Exception e) { w.setPrice(0); }
            workshopService.updateWorkshop(w);
            refreshData();
        }
    }

    @FXML private void handleDelete() {
        Workshop w = workshopTable.getSelectionModel().getSelectedItem();
        if (w != null) {
            workshopService.deleteWorkshop(w.getTitle());
            refreshData();
        }
    }

    private void refreshData() {
        // Fetch directly from the Database using the Exhibition Service!
        workshopTable.setItems(FXCollections.observableArrayList(workshopService.getAllWorkshops()));
    }
}
