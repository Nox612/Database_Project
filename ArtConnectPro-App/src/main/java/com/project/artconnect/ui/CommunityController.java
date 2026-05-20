package com.project.artconnect.ui;

import com.project.artconnect.model.CommunityMember;
import com.project.artconnect.service.CommunityService;
import com.project.artconnect.util.ServiceProvider;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.TextField;

public class CommunityController {
    @FXML
    private TableView<CommunityMember> memberTable;
    @FXML
    private TableColumn<CommunityMember, String> nameColumn;
    @FXML
    private TableColumn<CommunityMember, String> emailColumn;
    @FXML
    private TableColumn<CommunityMember, String> cityColumn;
    @FXML
    private TextField nameInput;
    @FXML
    private TextField emailInput;
    @FXML
    private TextField cityInput;

    private final CommunityService communityService = ServiceProvider.getCommunityService();

    @FXML
    public void initialize() {
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
        cityColumn.setCellValueFactory(new PropertyValueFactory<>("city"));

        memberTable.setItems(FXCollections.observableArrayList(communityService.getAllMembers()));

        memberTable.getSelectionModel().selectedItemProperty().addListener((obs, old, newV) -> showMemberDetails(newV));
    }

    private void showMemberDetails(CommunityMember member) {
        if (member != null) {
            nameInput.setText(member.getName());
            emailInput.setText(member.getEmail());
            cityInput.setText(member.getCity());
        } else {
            nameInput.clear(); emailInput.clear(); cityInput.clear();
        }
    }

    @FXML private void handleAdd() {
        CommunityMember m = new CommunityMember();
        m.setName(nameInput.getText());
        m.setEmail(emailInput.getText());
        m.setCity(cityInput.getText());
        communityService.createMember(m);
        refreshData(); showMemberDetails(null);
    }

    @FXML private void handleUpdate() {
        CommunityMember m = memberTable.getSelectionModel().getSelectedItem();
        if (m != null) {
            m.setEmail(emailInput.getText());
            m.setCity(cityInput.getText());
            communityService.updateMember(m);
            refreshData();
        }
    }

    @FXML private void handleDelete() {
        CommunityMember m = memberTable.getSelectionModel().getSelectedItem();
        if (m != null) {
            communityService.deleteMember(m.getName());
            refreshData();
        }
    }

    private void refreshData() {
        // Fetch directly from the Database using the Exhibition Service!
        memberTable.setItems(FXCollections.observableArrayList(communityService.getAllMembers()));
    }
}
