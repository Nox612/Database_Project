package com.project.artconnect.dao.impl;

import com.project.artconnect.dao.WorkshopDao;
import com.project.artconnect.model.Artist;
import com.project.artconnect.model.Workshop;
import com.project.artconnect.util.ConnectionManager;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class WorkshopDaoImpl implements WorkshopDao {

    @Override
    public Optional<Workshop> findById(Long id) {
        String sql = "SELECT * FROM workshop WHERE id = ?";

        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setLong(1, id);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRowToWorkshop(rs, conn));
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return Optional.empty();
    }

    @Override
    public List<Workshop> findAll() {
        List<Workshop> workshops = new ArrayList<>();
        String sql = "SELECT * FROM workshop";

        try (Connection conn = ConnectionManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                workshops.add(mapRowToWorkshop(rs, conn));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return workshops;
    }

    /**
     * Helper method to map a ResultSet row to a Workshop object and reconstruct its object graph.
     */
    private Workshop mapRowToWorkshop(ResultSet rs, Connection conn) throws SQLException {
        Workshop workshop = new Workshop();

        workshop.setTitle(rs.getString("title"));

        // Convert SQL TIMESTAMP to java.time.LocalDateTime
        Timestamp timestamp = rs.getTimestamp("date");
        if (timestamp != null) {
            workshop.setDate(timestamp.toLocalDateTime());
        }

        workshop.setDurationMinutes(rs.getInt("duration_minutes"));
        workshop.setMaxParticipants(rs.getInt("max_participants"));
        workshop.setPrice(rs.getDouble("price"));
        workshop.setLocation(rs.getString("location"));
        workshop.setDescription(rs.getString("description"));
        workshop.setLevel(rs.getString("level"));

        // Object Graph Reconstruction: Fetch the associated Artist (Instructor)
        String instructorName = rs.getString("instructor_name");
        if (instructorName != null) {
            workshop.setInstructor(fetchInstructorByName(instructorName, conn));
        }

        return workshop;
    }

    /**
     * Helper method to fetch an Artist by name to fulfill the OOP bidirectional link requirements.
     */
    private Artist fetchInstructorByName(String name, Connection conn) {
        String sql = "SELECT * FROM artist WHERE name = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, name);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    Artist artist = new Artist();
                    artist.setName(rs.getString("name"));
                    artist.setBio(rs.getString("bio"));

                    int birthYear = rs.getInt("birth_year");
                    artist.setBirthYear(rs.wasNull() ? null : birthYear);

                    artist.setContactEmail(rs.getString("contact_email"));
                    artist.setPhone(rs.getString("phone"));
                    artist.setCity(rs.getString("city"));
                    artist.setWebsite(rs.getString("website"));
                    artist.setSocialMedia(rs.getString("social_media"));
                    artist.setActive(rs.getBoolean("is_active"));

                    return artist;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null; // Return null if the instructor could not be fetched
    }
}