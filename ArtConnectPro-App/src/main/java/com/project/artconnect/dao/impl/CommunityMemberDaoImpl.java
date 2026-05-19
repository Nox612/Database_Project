package com.project.artconnect.dao.impl;

import com.project.artconnect.dao.CommunityMemberDao;
import com.project.artconnect.model.CommunityMember;
import com.project.artconnect.util.ConnectionManager;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CommunityMemberDaoImpl implements CommunityMemberDao {

    @Override
    public Optional<CommunityMember> findById(Long id) {
        String sql = "SELECT * FROM community_member WHERE id = ?";

        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setLong(1, id);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRowToCommunityMember(rs));
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return Optional.empty();
    }

    @Override
    public List<CommunityMember> findAll() {
        List<CommunityMember> members = new ArrayList<>();
        String sql = "SELECT * FROM community_member";

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

    /**
     * Helper method to map a ResultSet row to a CommunityMember object.
     */
    private CommunityMember mapRowToCommunityMember(ResultSet rs) throws SQLException {
        CommunityMember member = new CommunityMember();

        member.setName(rs.getString("name"));
        member.setEmail(rs.getString("email"));

        // Handle potentially null integers for birth_year
        int birthYear = rs.getInt("birth_year");
        member.setBirthYear(rs.wasNull() ? null : birthYear);

        member.setPhone(rs.getString("phone"));
        member.setCity(rs.getString("city"));
        member.setMembershipType(rs.getString("membership_type"));

        /* * Note on Object Graph Reconstruction:
         * To fully reconstruct the OOP graph as per the project requirements,
         * you would additionally query the 'booking', 'review', and 'favorite_discipline'
         * junction tables/entities here and attach them to the 'member' object
         * (e.g., fetching all reviews made by this member's ID).
         */

        return member;
    }
}