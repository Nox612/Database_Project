package com.project.artconnect.dao.impl;

import com.project.artconnect.dao.ArtistDao;
import com.project.artconnect.model.Artist;
import com.project.artconnect.util.ConnectionManager;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
public class ArtistDaoImpl implements ArtistDao {

    @Override
    public List<Artist> findAll() {
        List<Artist> artists = new ArrayList<>();
        String sql = "SELECT * FROM artist";

        try (Connection conn = ConnectionManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                artists.add(mapRowToArtist(rs));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return artists;
    }

    @Override
    public void save(Artist artist) {
        String sql = "INSERT INTO artist (name, bio, birth_year, contact_email, phone, city, website, social_media, is_active) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, artist.getName());
            pstmt.setString(2, artist.getBio());

            if (artist.getBirthYear() != null) {
                pstmt.setInt(3, artist.getBirthYear());
            } else {
                pstmt.setNull(3, Types.INTEGER);
            }

            pstmt.setString(4, artist.getContactEmail());
            pstmt.setString(5, artist.getPhone());
            pstmt.setString(6, artist.getCity());
            pstmt.setString(7, artist.getWebsite());
            pstmt.setString(8, artist.getSocialMedia());
            pstmt.setBoolean(9, artist.isActive());

            pstmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void update(Artist artist) {
        String sql = "UPDATE artist SET bio = ?, birth_year = ?, contact_email = ?, phone = ?, city = ?, website = ?, social_media = ?, is_active = ? WHERE name = ?";

        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, artist.getBio());

            if (artist.getBirthYear() != null) {
                pstmt.setInt(2, artist.getBirthYear());
            } else {
                pstmt.setNull(2, Types.INTEGER);
            }

            pstmt.setString(3, artist.getContactEmail());
            pstmt.setString(4, artist.getPhone());
            pstmt.setString(5, artist.getCity());
            pstmt.setString(6, artist.getWebsite());
            pstmt.setString(7, artist.getSocialMedia());
            pstmt.setBoolean(8, artist.isActive());
            pstmt.setString(9, artist.getName()); // WHERE condition

            pstmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void delete(String artistName) {
        String sql = "DELETE FROM artist WHERE name = ?";

        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, artistName);
            pstmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<Artist> findByCity(String city) {
        List<Artist> artists = new ArrayList<>();
        String sql = "SELECT * FROM artist WHERE city = ?";

        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, city);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    artists.add(mapRowToArtist(rs));
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return artists;
    }

    private Artist mapRowToArtist(ResultSet rs) throws SQLException {
        Artist artist = new Artist();

        artist.setName(rs.getString("name"));
        artist.setBio(rs.getString("bio"));

        // Handle potentially null integers carefully
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
