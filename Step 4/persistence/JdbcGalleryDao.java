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

    @Override
    public void save(Gallery gallery) {
        String sql = "INSERT INTO GALLERY (Name, Address, Rating) VALUES (?, ?, ?)";
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, gallery.getName());
            pstmt.setString(2, gallery.getAddress());
            pstmt.setDouble(3, gallery.getRating());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void update(Gallery gallery) {
        String sql = "UPDATE GALLERY SET Address = ?, Rating = ? WHERE Name = ?";
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, gallery.getAddress());
            pstmt.setDouble(2, gallery.getRating());
            pstmt.setString(3, gallery.getName());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void delete(String name) {
        String sql = "DELETE FROM GALLERY WHERE Name = ?";
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, name);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}