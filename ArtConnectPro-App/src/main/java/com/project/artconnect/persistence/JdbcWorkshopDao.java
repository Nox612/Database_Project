package com.project.artconnect.persistence;

import com.project.artconnect.dao.WorkshopDao;
import com.project.artconnect.model.Artist;
import com.project.artconnect.model.Workshop;
import com.project.artconnect.util.ConnectionManager;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class JdbcWorkshopDao implements WorkshopDao {

    @Override
    public Optional<Workshop> findById(Long id) {
        String sql = "SELECT w.Title, w.Price, w.Level, s.Date, a.Name AS InstructorName " +
                "FROM WORKSHOP_INFO w " +
                "LEFT JOIN WORKSHOP_SCHEDULE s ON w.WS_ID = s.WS_ID " +
                "LEFT JOIN ARTIST a ON w.InstructorID = a.ArtistID " +
                "WHERE w.WS_ID = ?";

        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setLong(1, id);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRowToWorkshop(rs));
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
        String sql = "SELECT w.Title, w.Price, w.Level, s.Date, a.Name AS InstructorName " +
                "FROM WORKSHOP_INFO w " +
                "LEFT JOIN WORKSHOP_SCHEDULE s ON w.WS_ID = s.WS_ID " +
                "LEFT JOIN ARTIST a ON w.InstructorID = a.ArtistID";

        try (Connection conn = ConnectionManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                workshops.add(mapRowToWorkshop(rs));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return workshops;
    }

    /**
     * Helper method to map a ResultSet row to a Workshop object and reconstruct its object graph.
     */
    private Workshop mapRowToWorkshop(ResultSet rs) throws SQLException {
        Workshop workshop = new Workshop();

        // Map columns that exist in WORKSHOP_INFO
        workshop.setTitle(rs.getString("Title"));
        workshop.setPrice(rs.getDouble("Price"));
        workshop.setLevel(rs.getString("Level"));

        // Map the date from WORKSHOP_SCHEDULE
        Date sqlDate = rs.getDate("Date");
        if (sqlDate != null) {
            // Convert java.sql.Date to java.time.LocalDateTime (using start of day as time isn't in your DB)
            workshop.setDate(sqlDate.toLocalDate().atStartOfDay());
        }

        // Object Graph Reconstruction: Create a lightweight Artist object for the instructor
        String instructorName = rs.getString("InstructorName");
        if (instructorName != null) {
            Artist instructor = new Artist();
            instructor.setName(instructorName);
            workshop.setInstructor(instructor);
        }

        /* Fields that DO NOT exist in the database schema:
         * durationMinutes, maxParticipants, location, description.
         * We do not attempt to read them from the ResultSet to avoid SQL errors.
         * They will default to 0 or null in the Java object.
         */

        return workshop;
    }

    // --- Note for complete CRUD ---
    // If you need to implement save() or update() in the future, remember that you will need to
    // INSERT into WORKSHOP_INFO first, get the generated WS_ID, and then INSERT the Date into WORKSHOP_SCHEDULE.
}