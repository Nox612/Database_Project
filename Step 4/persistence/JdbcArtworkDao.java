package com.project.artconnect.persistence;

import com.project.artconnect.dao.ArtworkDao;
import com.project.artconnect.model.Artist;
import com.project.artconnect.model.Artwork;
import com.project.artconnect.util.ConnectionManager;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class JdbcArtworkDao implements ArtworkDao {

    @Override
    public List<Artwork> findAll() {
        List<Artwork> artworks = new ArrayList<>();
        // Join ARTWORK to ARTIST to get the Artist name for the Java object
        String sql = "SELECT aw.Title, aw.Type, aw.Price, aw.Status, a.Name as ArtistName " +
                "FROM ARTWORK aw LEFT JOIN ARTIST a ON aw.ArtistID = a.ArtistID";

        try (Connection conn = ConnectionManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                artworks.add(mapRowToArtwork(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return artworks;
    }

    @Override
    public void save(Artwork artwork) {
        String sql = "INSERT INTO ARTWORK (Title, ArtistID, Type, Price, Status) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, artwork.getTitle());

            Integer artistId = getArtistIdByName(conn, artwork.getArtist() != null ? artwork.getArtist().getName() : null);
            if (artistId != null) {
                pstmt.setInt(2, artistId);
            } else {
                throw new SQLException("Cannot save artwork: Artist must exist in the database first.");
            }

            pstmt.setString(3, artwork.getType());
            pstmt.setDouble(4, artwork.getPrice());
            pstmt.setString(5, artwork.getStatus() != null ? artwork.getStatus().name() : "Available");

            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void update(Artwork artwork) {
        String sql = "UPDATE ARTWORK SET Type = ?, Price = ?, Status = ? WHERE Title = ?";
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, artwork.getType());
            pstmt.setDouble(2, artwork.getPrice());
            pstmt.setString(3, artwork.getStatus() != null ? artwork.getStatus().name() : "Available");
            pstmt.setString(4, artwork.getTitle());

            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void delete(String title) {
        String sql = "DELETE FROM ARTWORK WHERE Title = ?";
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
        String sql = "SELECT aw.Title, aw.Type, aw.Price, aw.Status, a.Name as ArtistName " +
                "FROM ARTWORK aw JOIN ARTIST a ON aw.ArtistID = a.ArtistID WHERE a.Name = ?";
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, artistName);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    artworks.add(mapRowToArtwork(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return artworks;
    }

    private Artwork mapRowToArtwork(ResultSet rs) throws SQLException {
        Artwork artwork = new Artwork();
        artwork.setTitle(rs.getString("Title"));
        artwork.setType(rs.getString("Type"));
        artwork.setPrice(rs.getDouble("Price"));

        String statusStr = rs.getString("Status");
        try {
            if (statusStr != null) artwork.setStatus(Artwork.Status.valueOf(statusStr.toUpperCase()));
        } catch (IllegalArgumentException e) {
            artwork.setStatus(Artwork.Status.FOR_SALE);
        }

        String artistName = rs.getString("ArtistName");
        if (artistName != null) {
            Artist artist = new Artist();
            artist.setName(artistName);
            artwork.setArtist(artist);
        }

        return artwork;
    }

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