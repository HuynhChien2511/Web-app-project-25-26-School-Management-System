package com.school.dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.school.model.Semester;
import com.school.util.DatabaseConnection;

public class SemesterDAO {

    public List<Semester> getAllSemesters() {
        List<Semester> semesters = new ArrayList<>();
        String sql = "SELECT * FROM semesters ORDER BY start_date DESC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                semesters.add(extractSemesterFromResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return semesters;
    }

    public List<Semester> getSemestersByAcademicYear(String academicYear) {
        List<Semester> semesters = new ArrayList<>();
        String sql = "SELECT * FROM semesters WHERE academic_year = ? ORDER BY semester_type";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, academicYear);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                semesters.add(extractSemesterFromResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return semesters;
    }

    public Semester getActiveSemester() {
        String sql = "SELECT * FROM semesters WHERE is_active = TRUE LIMIT 1";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return extractSemesterFromResultSet(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Semester getSemesterById(int semesterId) {
        String sql = "SELECT * FROM semesters WHERE semester_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, semesterId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return extractSemesterFromResultSet(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean createSemester(Semester semester) {
        String sql = "INSERT INTO semesters (semester_name, semester_type, academic_year, " +
                     "start_date, end_date, weeks, is_active) VALUES (?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, semester.getSemesterName());
            stmt.setString(2, semester.getSemesterType().name());
            stmt.setString(3, semester.getAcademicYear());
            stmt.setDate(4, Date.valueOf(semester.getStartDate()));
            stmt.setDate(5, Date.valueOf(semester.getEndDate()));
            stmt.setInt(6, semester.getWeeks());
            stmt.setBoolean(7, semester.isActive());
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean updateSemester(Semester semester) {
        String sql = "UPDATE semesters SET semester_name = ?, semester_type = ?, " +
                     "academic_year = ?, start_date = ?, end_date = ?, weeks = ?, " +
                     "is_active = ? WHERE semester_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, semester.getSemesterName());
            stmt.setString(2, semester.getSemesterType().name());
            stmt.setString(3, semester.getAcademicYear());
            stmt.setDate(4, Date.valueOf(semester.getStartDate()));
            stmt.setDate(5, Date.valueOf(semester.getEndDate()));
            stmt.setInt(6, semester.getWeeks());
            stmt.setBoolean(7, semester.isActive());
            stmt.setInt(8, semester.getId());
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean setActiveSemester(int semesterId) {
        String sql1 = "UPDATE semesters SET is_active = FALSE";
        String sql2 = "UPDATE semesters SET is_active = TRUE WHERE semester_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false);
            
            try (PreparedStatement stmt1 = conn.prepareStatement(sql1);
                 PreparedStatement stmt2 = conn.prepareStatement(sql2)) {
                
                stmt1.executeUpdate();
                stmt2.setInt(1, semesterId);
                stmt2.executeUpdate();
                
                conn.commit();
                return true;
            } catch (SQLException e) {
                conn.rollback();
                e.printStackTrace();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean deleteSemester(int semesterId) {
        String sql = "DELETE FROM semesters WHERE semester_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, semesterId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private Semester extractSemesterFromResultSet(ResultSet rs) throws SQLException {
        Semester semester = new Semester();
        semester.setId(rs.getInt("semester_id"));
        semester.setSemesterName(rs.getString("semester_name"));
        semester.setSemesterType(Semester.SemesterType.valueOf(rs.getString("semester_type")));
        semester.setAcademicYear(rs.getString("academic_year"));
        semester.setStartDate(rs.getDate("start_date").toLocalDate());
        semester.setEndDate(rs.getDate("end_date").toLocalDate());
        semester.setWeeks(rs.getInt("weeks"));
        semester.setActive(rs.getBoolean("is_active"));
        return semester;
    }
}
