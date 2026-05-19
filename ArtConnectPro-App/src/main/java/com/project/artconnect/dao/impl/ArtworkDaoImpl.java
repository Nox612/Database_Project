package com.project.artconnect.dao.impl;

import com.project.artconnect.dao.ArtworkDao;
import com.project.artconnect.model.Artist;
import com.project.artconnect.model.Artwork;
import com.project.artconnect.util.ConnectionManager;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ArtworkDaoImpl implements ArtworkDao {

    @Override
    public List<Artwork> findAll() {
        List<Artwork> artworks = new ArrayList<>();
        String sql = "SELECT * FROM artwork";

        try (Connection conn = ConnectionManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                artworks.add(mapRowToArtwork(rs, conn));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return artworks;
    }

    @Override
    public void save(Artwork artwork) {
        String sql = "INSERT INTO artwork (title, creation_year, type, medium, dimensions, description, price, status, artist_name) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, artwork.getTitle());

            if (artwork.getCreationYear() != null) {
                pstmt.setInt(2, artwork.getCreationYear());
            } else {
                pstmt.setNull(2, Types.INTEGER);
            }

            pstmt.setString(3, artwork.getType());
            pstmt.setString(4, artwork.getMedium());
            pstmt.setString(5, artwork.getDimensions());
            pstmt.setString(6, artwork.getDescription());
            pstmt.setDouble(7, artwork.getPrice());
            pstmt.setString(8, artwork.getStatus() != null ? artwork.getStatus().name() : Artwork.Status.FOR_SALE.name());

            // Relational link (Foreign Key)
            if (artwork.getArtist() != null) {
                pstmt.setString(9, artwork.getArtist().getName());
            } else {
                pstmt.setNull(9, Types.VARCHAR);
            }

            pstmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void update(Artwork artwork) {
        String sql = "UPDATE artwork SET creation_year = ?, type = ?, medium = ?, dimensions = ?, description = ?, price = ?, status = ?, artist_name = ? WHERE title = ?";

        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            if (artwork.getCreationYear() != null) {
                pstmt.setInt(1, artwork.getCreationYear());
            } else {
                pstmt.setNull(1, Types.INTEGER);
            }

            pstmt.setString(2, artwork.getType());
            pstmt.setString(3, artwork.getMedium());
            pstmt.setString(4, artwork.getDimensions());
            pstmt.setString(5, artwork.getDescription());
            pstmt.setDouble(6, artwork.getPrice());
            pstmt.setString(7, artwork.getStatus() != null ? artwork.getStatus().name() : Artwork.Status.FOR_SALE.name());

            if (artwork.getArtist() != null) {
                pstmt.setString(8, artwork.getArtist().getName());
            } else {
                pstmt.setNull(8, Types.VARCHAR);
            }

            pstmt.setString(9, artwork.getTitle()); // WHERE condition

            pstmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void delete(String title) {
        String sql = "DELETE FROM artwork WHERE title = ?";

        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, title);
            pstmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<Artwork> findByArtistName(String artistName) {
        List<Artwork> artworks = new ArrayList<>();
        String sql = "SELECT * FROM artwork WHERE artist_name = ?";

        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, artistName);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    artworks.add(mapRowToArtwork(rs, conn));
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return artworks;
    }

    /**
     * Helper method to map a ResultSet row to an Artwork object and reconstruct its object graph.
     */
    private Artwork mapRowToArtwork(ResultSet rs, Connection conn) throws SQLException {
        Artwork artwork = new Artwork();

        artwork.setTitle(rs.getString("title"));

        int creationYear = rs.getInt("creation_year");
        artwork.setCreationYear(rs.wasNull() ? null : creationYear);

        artwork.setType(rs.getString("type"));
        artwork.setMedium(rs.getString("medium"));
        artwork.setDimensions(rs.getString("dimensions"));
        artwork.setDescription(rs.getString("description"));
        artwork.setPrice(rs.getDouble("price"));

        String statusStr = rs.getString("status");
        if (statusStr != null) {
            artwork.setStatus(Artwork.Status.valueOf(statusStr));
        }

        // Object Graph Reconstruction: Fetch the associated Artist
        String artistName = rs.getString("artist_name");
        if (artistName != null) {
            artwork.setArtist(fetchArtistByName(artistName, conn));
        }

        return artwork;
    }

    /**
     * Helper method to fetch an Artist by name to fulfill the OOP bidirectional link requirements.
     */
    private Artist fetchArtistByName(String name, Connection conn) {
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
        return null; // Return null if the artist could not be fetched
    }
}