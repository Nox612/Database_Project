package com.project.artconnect.persistence;

import com.project.artconnect.dao.ExhibitionDao;
import com.project.artconnect.model.Exhibition;
import com.project.artconnect.model.Gallery;
import com.project.artconnect.util.ConnectionManager;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class JdbcExhibitionDao implements ExhibitionDao {

    @Override
    public List<Exhibition> findAll() {
        List<Exhibition> exhibitions = new ArrayList<>();

        // JOIN EXHIBITION_INFO, EXHIBITION_SCHEDULE, and GALLERY to get all the data
        String sql = "SELECT e.Title, e.Theme, s.StartDate, g.Name AS GalleryName " +
                "FROM EXHIBITION_INFO e " +
                "LEFT JOIN EXHIBITION_SCHEDULE s ON e.ExhibID = s.ExhibID " +
                "LEFT JOIN GALLERY g ON s.GalleryID = g.GalleryID";

        try (Connection conn = ConnectionManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                exhibitions.add(mapRowToExhibition(rs));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return exhibitions;
    }

    @Override
    public void save(Exhibition exhibition) {
        String infoSql = "INSERT INTO EXHIBITION_INFO (Title, Theme) VALUES (?, ?)";
        String scheduleSql = "INSERT INTO EXHIBITION_SCHEDULE (ExhibID, GalleryID, StartDate) VALUES (?, ?, ?)";

        try (Connection conn = ConnectionManager.getConnection()) {
            // Using a transaction because we need to insert into TWO tables
            conn.setAutoCommit(false);

            try {
                int exhibId = -1;
                // 1. Insert into EXHIBITION_INFO and retrieve the auto-generated ID
                try (PreparedStatement infoStmt = conn.prepareStatement(infoSql, Statement.RETURN_GENERATED_KEYS)) {
                    infoStmt.setString(1, exhibition.getTitle());
                    infoStmt.setString(2, exhibition.getTheme());
                    infoStmt.executeUpdate();

                    try (ResultSet keys = infoStmt.getGeneratedKeys()) {
                        if (keys.next()) exhibId = keys.getInt(1);
                    }
                }

                // 2. Insert into EXHIBITION_SCHEDULE
                if (exhibId != -1 && exhibition.getGallery() != null) {
                    Integer galleryId = getGalleryIdByName(conn, exhibition.getGallery().getName());
                    if (galleryId != null) {
                        try (PreparedStatement scheduleStmt = conn.prepareStatement(scheduleSql)) {
                            scheduleStmt.setInt(1, exhibId);
                            scheduleStmt.setInt(2, galleryId);

                            if (exhibition.getStartDate() != null) {
                                scheduleStmt.setDate(3, Date.valueOf(exhibition.getStartDate()));
                            } else {
                                scheduleStmt.setNull(3, Types.DATE);
                            }

                            scheduleStmt.executeUpdate();
                        }
                    }
                }

                conn.commit(); // Success! Save changes.
            } catch (SQLException ex) {
                conn.rollback(); // Failed! Undo changes.
                ex.printStackTrace();
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void update(Exhibition exhibition) {
        // Since Title is the identifier in the Java model, we update the Theme
        String sql = "UPDATE EXHIBITION_INFO SET Theme = ? WHERE Title = ?";
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, exhibition.getTheme());
            pstmt.setString(2, exhibition.getTitle());
            pstmt.executeUpdate();

            // Note: Updating the Schedule (Gallery/Date) is more complex as it requires
            // resolving the ExhibID and GalleryID. Keeping it simple here for the INFO update.

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void delete(String title) {
        // Because of "ON DELETE CASCADE" in your database schema, deleting from
        // EXHIBITION_INFO will automatically delete the linked EXHIBITION_SCHEDULE!
        String sql = "DELETE FROM EXHIBITION_INFO WHERE Title = ?";

        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, title);
            pstmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Helper method to map a ResultSet row to an Exhibition object.
     */
    private Exhibition mapRowToExhibition(ResultSet rs) throws SQLException {
        Exhibition exhibition = new Exhibition();

        // Map columns that exist in EXHIBITION_INFO
        exhibition.setTitle(rs.getString("Title"));
        exhibition.setTheme(rs.getString("Theme"));

        // Map the StartDate from EXHIBITION_SCHEDULE
        Date startDate = rs.getDate("StartDate");
        if (startDate != null) {
            exhibition.setStartDate(startDate.toLocalDate());
        }

        // Reconstruct the associated Gallery object
        String galleryName = rs.getString("GalleryName");
        if (galleryName != null) {
            Gallery gallery = new Gallery();
            gallery.setName(galleryName);
            exhibition.setGallery(gallery);
        }

        /* * Fields that DO NOT exist in the database schema:
         * endDate, description, curatorName, artworks.
         * We ignore them here to avoid SQLExceptions.
         */

        return exhibition;
    }

    // --- Helper to handle the GALLERY table foreign key relationship ---
    private Integer getGalleryIdByName(Connection conn, String name) throws SQLException {
        if (name == null || name.trim().isEmpty()) return null;
        try (PreparedStatement stmt = conn.prepareStatement("SELECT GalleryID FROM GALLERY WHERE Name = ?")) {
            stmt.setString(1, name);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return rs.getInt("GalleryID");
            }
        }
        return null;
    }
}