package com.project.artconnect.dao.impl;

import com.project.artconnect.dao.ExhibitionDao;
import com.project.artconnect.model.Exhibition;
import com.project.artconnect.model.Gallery;
import com.project.artconnect.util.ConnectionManager;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ExhibitionDaoImpl implements ExhibitionDao {

    @Override
    public List<Exhibition> findAll() {
        List<Exhibition> exhibitions = new ArrayList<>();
        String sql = "SELECT * FROM exhibition";

        try (Connection conn = ConnectionManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                exhibitions.add(mapRowToExhibition(rs, conn));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return exhibitions;
    }

    @Override
    public void save(Exhibition exhibition) {
        String sql = "INSERT INTO exhibition (title, start_date, end_date, description, gallery_name, curator_name, theme) VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, exhibition.getTitle());

            // Convert LocalDate to java.sql.Date
            pstmt.setDate(2, exhibition.getStartDate() != null ? Date.valueOf(exhibition.getStartDate()) : null);
            pstmt.setDate(3, exhibition.getEndDate() != null ? Date.valueOf(exhibition.getEndDate()) : null);

            pstmt.setString(4, exhibition.getDescription());

            // Relational link (Foreign Key)
            if (exhibition.getGallery() != null) {
                pstmt.setString(5, exhibition.getGallery().getName());
            } else {
                pstmt.setNull(5, Types.VARCHAR);
            }

            pstmt.setString(6, exhibition.getCuratorName());
            pstmt.setString(7, exhibition.getTheme());

            pstmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void update(Exhibition exhibition) {
        String sql = "UPDATE exhibition SET start_date = ?, end_date = ?, description = ?, gallery_name = ?, curator_name = ?, theme = ? WHERE title = ?";

        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setDate(1, exhibition.getStartDate() != null ? Date.valueOf(exhibition.getStartDate()) : null);
            pstmt.setDate(2, exhibition.getEndDate() != null ? Date.valueOf(exhibition.getEndDate()) : null);
            pstmt.setString(3, exhibition.getDescription());

            if (exhibition.getGallery() != null) {
                pstmt.setString(4, exhibition.getGallery().getName());
            } else {
                pstmt.setNull(4, Types.VARCHAR);
            }

            pstmt.setString(5, exhibition.getCuratorName());
            pstmt.setString(6, exhibition.getTheme());

            pstmt.setString(7, exhibition.getTitle()); // WHERE condition

            pstmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void delete(String title) {
        String sql = "DELETE FROM exhibition WHERE title = ?";

        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, title);
            pstmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Helper method to map a ResultSet row to an Exhibition object and reconstruct its object graph.
     */
    private Exhibition mapRowToExhibition(ResultSet rs, Connection conn) throws SQLException {
        Exhibition exhibition = new Exhibition();

        exhibition.setTitle(rs.getString("title"));

        Date startDate = rs.getDate("start_date");
        if (startDate != null) {
            exhibition.setStartDate(startDate.toLocalDate());
        }

        Date endDate = rs.getDate("end_date");
        if (endDate != null) {
            exhibition.setEndDate(endDate.toLocalDate());
        }

        exhibition.setDescription(rs.getString("description"));
        exhibition.setCuratorName(rs.getString("curator_name"));
        exhibition.setTheme(rs.getString("theme"));

        // Object Graph Reconstruction: Fetch the associated Gallery
        String galleryName = rs.getString("gallery_name");
        if (galleryName != null) {
            exhibition.setGallery(fetchGalleryByName(galleryName, conn));
        }

        /* * Note on Object Graph Reconstruction:
         * To fully reconstruct the OOP graph as per the project requirements,
         * you would additionally query the 'exhibition_artwork' junction table
         * to populate the `exhibition.getArtworks()` list here.
         */

        return exhibition;
    }

    /**
     * Helper method to fetch a Gallery by name to fulfill the OOP bidirectional link requirements.
     */
    private Gallery fetchGalleryByName(String name, Connection conn) {
        String sql = "SELECT * FROM gallery WHERE name = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, name);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    Gallery gallery = new Gallery();
                    gallery.setName(rs.getString("name"));
                    gallery.setAddress(rs.getString("address"));
                    gallery.setOwnerName(rs.getString("owner_name"));
                    gallery.setOpeningHours(rs.getString("opening_hours"));
                    gallery.setContactPhone(rs.getString("contact_phone"));
                    gallery.setRating(rs.getDouble("rating"));
                    gallery.setWebsite(rs.getString("website"));

                    return gallery;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}