package com.school.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.school.model.Announcement;
import com.school.util.DatabaseConnection;

public class AnnouncementDAO {

    // Get all school-wide announcements
    public List<Announcement> getSchoolWideAnnouncements() {
        List<Announcement> announcements = new ArrayList<>();
        String sql = "SELECT a.*, u.full_name as author_name, u.user_type " +
                     "FROM announcements a " +
                     "JOIN users u ON a.author_id = u.user_id " +
                     "WHERE a.course_id IS NULL " +
                     "ORDER BY a.is_important DESC, a.created_at DESC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                announcements.add(extractAnnouncementFromResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return announcements;
    }

    // Get announcements for a specific course
    public List<Announcement> getCourseAnnouncements(int courseId) {
        List<Announcement> announcements = new ArrayList<>();
        String sql = "SELECT a.*, u.full_name as author_name, u.user_type, c.course_name, c.course_code " +
                     "FROM announcements a " +
                     "JOIN users u ON a.author_id = u.user_id " +
                     "JOIN courses c ON a.course_id = c.course_id " +
                     "WHERE a.course_id = ? " +
                     "ORDER BY a.is_important DESC, a.created_at DESC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, courseId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                announcements.add(extractAnnouncementFromResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return announcements;
    }

    // Get all announcements for a student (school-wide + their enrolled courses)
    public List<Announcement> getAnnouncementsForStudent(int studentId) {
        List<Announcement> announcements = new ArrayList<>();
        String sql = "SELECT a.*, u.full_name as author_name, u.user_type, c.course_name, c.course_code " +
                     "FROM announcements a " +
                     "JOIN users u ON a.author_id = u.user_id " +
                     "LEFT JOIN courses c ON a.course_id = c.course_id " +
                     "WHERE a.course_id IS NULL " +
                     "   OR a.course_id IN (SELECT course_id FROM enrollments WHERE student_id = ? AND status = 'ACTIVE') " +
                     "ORDER BY a.is_important DESC, a.created_at DESC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, studentId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                announcements.add(extractAnnouncementFromResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return announcements;
    }

    // Get all announcements for a student with pagination
    public List<Announcement> getAnnouncementsForStudentWithPagination(int studentId, int page, int pageSize) {
        List<Announcement> announcements = new ArrayList<>();
        int offset = (page - 1) * pageSize;
        String sql = "SELECT a.*, u.full_name as author_name, u.user_type, c.course_name, c.course_code " +
                     "FROM announcements a " +
                     "JOIN users u ON a.author_id = u.user_id " +
                     "LEFT JOIN courses c ON a.course_id = c.course_id " +
                     "WHERE a.course_id IS NULL " +
                     "   OR a.course_id IN (SELECT course_id FROM enrollments WHERE student_id = ? AND status = 'ACTIVE') " +
                     "ORDER BY a.is_important DESC, a.created_at DESC LIMIT ? OFFSET ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, studentId);
            stmt.setInt(2, pageSize);
            stmt.setInt(3, offset);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                announcements.add(extractAnnouncementFromResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return announcements;
    }

    // Get total count of announcements for a student
    public int getTotalAnnouncementCountForStudent(int studentId) {
        String sql = "SELECT COUNT(*) FROM announcements a " +
                     "WHERE a.course_id IS NULL " +
                     "   OR a.course_id IN (SELECT course_id FROM enrollments WHERE student_id = ? AND status = 'ACTIVE')";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, studentId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    // Get announcements created by a specific teacher
    public List<Announcement> getAnnouncementsByTeacher(int teacherId) {
        List<Announcement> announcements = new ArrayList<>();
        String sql = "SELECT a.*, u.full_name as author_name, u.user_type, c.course_name, c.course_code " +
                     "FROM announcements a " +
                     "JOIN users u ON a.author_id = u.user_id " +
                     "LEFT JOIN courses c ON a.course_id = c.course_id " +
                     "WHERE a.author_id = ? " +
                     "ORDER BY a.created_at DESC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, teacherId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                announcements.add(extractAnnouncementFromResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return announcements;
    }

    // Get all announcements (for admin)
    public List<Announcement> getAllAnnouncements() {
        List<Announcement> announcements = new ArrayList<>();
        String sql = "SELECT a.*, u.full_name as author_name, u.user_type, c.course_name, c.course_code " +
                     "FROM announcements a " +
                     "JOIN users u ON a.author_id = u.user_id " +
                     "LEFT JOIN courses c ON a.course_id = c.course_id " +
                     "ORDER BY a.is_important DESC, a.created_at DESC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                announcements.add(extractAnnouncementFromResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return announcements;
    }

    // Get all announcements with pagination (for admin)
    public List<Announcement> getAllAnnouncementsWithPagination(int page, int pageSize) {
        List<Announcement> announcements = new ArrayList<>();
        int offset = (page - 1) * pageSize;
        String sql = "SELECT a.*, u.full_name as author_name, u.user_type, c.course_name, c.course_code " +
                     "FROM announcements a " +
                     "JOIN users u ON a.author_id = u.user_id " +
                     "LEFT JOIN courses c ON a.course_id = c.course_id " +
                     "ORDER BY a.is_important DESC, a.created_at DESC LIMIT ? OFFSET ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, pageSize);
            stmt.setInt(2, offset);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                announcements.add(extractAnnouncementFromResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return announcements;
    }

    // Get total count of all announcements
    public int getTotalAnnouncementCount() {
        String sql = "SELECT COUNT(*) FROM announcements";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    // Get announcement by ID
    public Announcement getAnnouncementById(int announcementId) {
        String sql = "SELECT a.*, u.full_name as author_name, u.user_type, c.course_name, c.course_code " +
                     "FROM announcements a " +
                     "JOIN users u ON a.author_id = u.user_id " +
                     "LEFT JOIN courses c ON a.course_id = c.course_id " +
                     "WHERE a.announcement_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, announcementId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return extractAnnouncementFromResultSet(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // Create a new announcement
    public boolean createAnnouncement(Announcement announcement) {
        String sql = "INSERT INTO announcements (title, content, author_id, course_id, is_important) " +
                     "VALUES (?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, announcement.getTitle());
            stmt.setString(2, announcement.getContent());
            stmt.setInt(3, announcement.getAuthorId());
            if (announcement.getCourseId() != null) {
                stmt.setInt(4, announcement.getCourseId());
            } else {
                stmt.setNull(4, java.sql.Types.INTEGER);
            }
            stmt.setBoolean(5, announcement.isImportant());
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Update announcement
    public boolean updateAnnouncement(Announcement announcement) {
        String sql = "UPDATE announcements SET title = ?, content = ?, is_important = ? " +
                     "WHERE announcement_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, announcement.getTitle());
            stmt.setString(2, announcement.getContent());
            stmt.setBoolean(3, announcement.isImportant());
            stmt.setInt(4, announcement.getAnnouncementId());
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Delete announcement
    public boolean deleteAnnouncement(int announcementId) {
        String sql = "DELETE FROM announcements WHERE announcement_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, announcementId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Helper method to extract announcement from ResultSet
    private Announcement extractAnnouncementFromResultSet(ResultSet rs) throws SQLException {
        Announcement announcement = new Announcement();
        announcement.setAnnouncementId(rs.getInt("announcement_id"));
        announcement.setTitle(rs.getString("title"));
        announcement.setContent(rs.getString("content"));
        announcement.setAuthorId(rs.getInt("author_id"));
        announcement.setAuthorName(rs.getString("author_name"));
        announcement.setAuthorType(rs.getString("user_type"));
        
        // Handle nullable course_id
        int courseId = rs.getInt("course_id");
        if (!rs.wasNull()) {
            announcement.setCourseId(courseId);
            announcement.setCourseName(rs.getString("course_name"));
            announcement.setCourseCode(rs.getString("course_code"));
        }
        
        announcement.setImportant(rs.getBoolean("is_important"));
        announcement.setCreatedAt(rs.getTimestamp("created_at"));
        announcement.setUpdatedAt(rs.getTimestamp("updated_at"));
        
        return announcement;
    }
}
