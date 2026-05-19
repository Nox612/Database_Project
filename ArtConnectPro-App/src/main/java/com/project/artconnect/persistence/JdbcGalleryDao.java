package com.project.artconnect.persistence;

import com.project.artconnect.dao.GalleryDao;
import com.project.artconnect.model.Gallery;
import com.project.artconnect.util.ConnectionManager;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class JdbcGalleryDao implements GalleryDao {

    @Override
    public Optional<Gallery> findById(Long id) {
        String sql = "SELECT * FROM GALLERY WHERE GalleryID = ?";

        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setLong(1, id);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRowToGallery(rs));
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return Optional.empty();
    }

    @Override
    public List<Gallery> findAll() {
        List<Gallery> galleries = new ArrayList<>();
        String sql = "SELECT * FROM GALLERY";

        try (Connection conn = ConnectionManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                galleries.add(mapRowToGallery(rs));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return galleries;
    }

    /**
     * Helper method to map a ResultSet row to a Gallery object.
     */
    private Gallery mapRowToGallery(ResultSet rs) throws SQLException {
        Gallery gallery = new Gallery();

        // Only mapping columns that actually exist in the SQL schema
        gallery.setName(rs.getString("Name"));
        gallery.setAddress(rs.getString("Address"));

        double rating = rs.getDouble("Rating");
        if (!rs.wasNull()) {
            gallery.setRating(rating);
        }

        // Fields like ownerName, openingHours, contactPhone, and website
        // do not exist in the database schema, so we skip them to avoid SQL errors.
        // They will safely default to null in the Java object.

        return gallery;
    }
}