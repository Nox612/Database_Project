package com.project.artconnect.persistence;

import com.project.artconnect.dao.ArtistDao;
import com.project.artconnect.model.Artist;
import com.project.artconnect.util.ConnectionManager;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class JdbcArtistDao implements ArtistDao {

    @Override
    public List<Artist> findAll() {
        List<Artist> artists = new ArrayList<>();
        // Join ARTIST and CITY to get the CityName string for the Java model
        String sql = "SELECT a.Name, a.Email, a.BirthYear, c.CityName " +
                "FROM ARTIST a LEFT JOIN CITY c ON a.CityID = c.CityID";

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
        String sql = "INSERT INTO ARTIST (Name, Email, BirthYear, CityID) VALUES (?, ?, ?, ?)";
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, artist.getName());
            pstmt.setString(2, artist.getContactEmail()); // Assuming contactEmail maps to Email

            if (artist.getBirthYear() != null) {
                pstmt.setInt(3, artist.getBirthYear());
            } else {
                pstmt.setNull(3, Types.INTEGER);
            }

            // Handle City Foreign Key
            Integer cityId = getOrCreateCityId(conn, artist.getCity());
            if (cityId != null) {
                pstmt.setInt(4, cityId);
            } else {
                pstmt.setNull(4, Types.INTEGER);
            }

            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void update(Artist artist) {
        String sql = "UPDATE ARTIST SET Email = ?, BirthYear = ?, CityID = ? WHERE Name = ?";
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, artist.getContactEmail());
            if (artist.getBirthYear() != null) {
                pstmt.setInt(2, artist.getBirthYear());
            } else {
                pstmt.setNull(2, Types.INTEGER);
            }

            Integer cityId = getOrCreateCityId(conn, artist.getCity());
            if (cityId != null) {
                pstmt.setInt(3, cityId);
            } else {
                pstmt.setNull(3, Types.INTEGER);
            }

            pstmt.setString(4, artist.getName());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void delete(String artistName) {
        String sql = "DELETE FROM ARTIST WHERE Name = ?";
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
        String sql = "SELECT a.Name, a.Email, a.BirthYear, c.CityName " +
                "FROM ARTIST a JOIN CITY c ON a.CityID = c.CityID WHERE c.CityName = ?";
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
        artist.setName(rs.getString("Name"));
        artist.setContactEmail(rs.getString("Email"));

        int birthYear = rs.getInt("BirthYear");
        artist.setBirthYear(rs.wasNull() ? null : birthYear);

        artist.setCity(rs.getString("CityName"));
        return artist;
    }

    // --- Helper to handle the CITY table foreign key relationship ---
    private Integer getOrCreateCityId(Connection conn, String cityName) throws SQLException {
        if (cityName == null || cityName.trim().isEmpty()) return null;

        // Try to find it
        try (PreparedStatement select = conn.prepareStatement("SELECT CityID FROM CITY WHERE CityName = ?")) {
            select.setString(1, cityName);
            try (ResultSet rs = select.executeQuery()) {
                if (rs.next()) return rs.getInt("CityID");
            }
        }
        // If not found, insert a dummy record (since state is required in your DB)
        try (PreparedStatement insert = conn.prepareStatement("INSERT INTO CITY (CityName, State) VALUES (?, 'Unknown')", Statement.RETURN_GENERATED_KEYS)) {
            insert.setString(1, cityName);
            insert.executeUpdate();
            try (ResultSet keys = insert.getGeneratedKeys()) {
                if (keys.next()) return keys.getInt(1);
            }
        }
        return null;
    }
}