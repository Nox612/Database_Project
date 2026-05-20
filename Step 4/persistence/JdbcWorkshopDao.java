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

        return workshop;
    }

    @Override
    public void save(Workshop workshop) {
        String infoSql = "INSERT INTO WORKSHOP_INFO (Title, InstructorID, Price, Level) VALUES (?, ?, ?, ?)";
        String schedSql = "INSERT INTO WORKSHOP_SCHEDULE (WS_ID, Date) VALUES (?, ?)";
        try (Connection conn = ConnectionManager.getConnection()) {
            conn.setAutoCommit(false);
            try {
                int wsId = -1;
                try (PreparedStatement infoStmt = conn.prepareStatement(infoSql, Statement.RETURN_GENERATED_KEYS)) {
                    infoStmt.setString(1, workshop.getTitle());

                    Integer artistId = getArtistIdByName(conn, workshop.getInstructor() != null ? workshop.getInstructor().getName() : null);
                    if (artistId != null) infoStmt.setInt(2, artistId);
                    else throw new SQLException("Instructor must exist in the DB.");

                    infoStmt.setDouble(3, workshop.getPrice());
                    infoStmt.setString(4, workshop.getLevel());
                    infoStmt.executeUpdate();

                    try (ResultSet keys = infoStmt.getGeneratedKeys()) {
                        if (keys.next()) wsId = keys.getInt(1);
                    }
                }

                if (wsId != -1 && workshop.getDate() != null) {
                    try (PreparedStatement scheduleStmt = conn.prepareStatement(schedSql)) {
                        scheduleStmt.setInt(1, wsId);
                        scheduleStmt.setDate(2, Date.valueOf(workshop.getDate().toLocalDate()));
                        scheduleStmt.executeUpdate();
                    }
                }
                conn.commit();
            } catch (SQLException ex) {
                conn.rollback();
                ex.printStackTrace();
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void update(Workshop workshop) {
        String sql = "UPDATE WORKSHOP_INFO SET Price = ?, Level = ? WHERE Title = ?";
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setDouble(1, workshop.getPrice());
            pstmt.setString(2, workshop.getLevel());
            pstmt.setString(3, workshop.getTitle());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void delete(String title) {
        String sql = "DELETE FROM WORKSHOP_INFO WHERE Title = ?";
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, title);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Helper method (Required for Save to find InstructorID)
    private Integer getArtistIdByName(Connection conn, String name) throws SQLException {
        if (name == null || name.trim().isEmpty()) return null;
        try (PreparedStatement stmt = conn.prepareStatement("SELECT ArtistID FROM ARTIST WHERE Name = ?")) {
            stmt.setString(1, name);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return rs.getInt("ArtistID");
            }
        }
        return null;
    }
}