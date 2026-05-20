package com.project.artconnect.persistence;

import com.project.artconnect.dao.CommunityMemberDao;
import com.project.artconnect.model.CommunityMember;
import com.project.artconnect.util.ConnectionManager;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class JdbcCommunityMemberDao implements CommunityMemberDao {

    @Override
    public Optional<CommunityMember> findById(Long id) {
        String sql = "SELECT m.Name, m.Email, c.CityName FROM MEMBER m LEFT JOIN CITY c ON m.CityID = c.CityID WHERE m.MemberID = ?";
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) return Optional.of(mapRowToCommunityMember(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    @Override
    public List<CommunityMember> findAll() {
        List<CommunityMember> members = new ArrayList<>();
        String sql = "SELECT m.Name, m.Email, c.CityName FROM MEMBER m LEFT JOIN CITY c ON m.CityID = c.CityID";
        try (Connection conn = ConnectionManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                members.add(mapRowToCommunityMember(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return members;
    }

    private CommunityMember mapRowToCommunityMember(ResultSet rs) throws SQLException {
        CommunityMember member = new CommunityMember();
        member.setName(rs.getString("Name"));
        member.setEmail(rs.getString("Email"));
        member.setCity(rs.getString("CityName"));
        return member;
    }

    @Override
    public void save(CommunityMember member) {
        String sql = "INSERT INTO MEMBER (Name, Email, CityID) VALUES (?, ?, ?)";
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, member.getName());
            pstmt.setString(2, member.getEmail());

            Integer cityId = getOrCreateCityId(conn, member.getCity());
            if(cityId != null) pstmt.setInt(3, cityId);
            else pstmt.setNull(3, Types.INTEGER);

            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void update(CommunityMember member) {
        String sql = "UPDATE MEMBER SET Email = ?, CityID = ? WHERE Name = ?";
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, member.getEmail());

            Integer cityId = getOrCreateCityId(conn, member.getCity());
            if(cityId != null) pstmt.setInt(2, cityId);
            else pstmt.setNull(2, Types.INTEGER);

            pstmt.setString(3, member.getName());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void delete(String name) {
        String sql = "DELETE FROM MEMBER WHERE Name = ?";
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, name);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Helper method (Required for Save/Update to handle CityID Foreign Key)
    private Integer getOrCreateCityId(Connection conn, String cityName) throws SQLException {
        if (cityName == null || cityName.trim().isEmpty()) return null;
        try (PreparedStatement select = conn.prepareStatement("SELECT CityID FROM CITY WHERE CityName = ?")) {
            select.setString(1, cityName);
            try (ResultSet rs = select.executeQuery()) {
                if (rs.next()) return rs.getInt("CityID");
            }
        }
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