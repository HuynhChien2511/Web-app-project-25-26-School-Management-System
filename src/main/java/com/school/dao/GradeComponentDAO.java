package com.school.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.school.model.GradeComponent;
import com.school.util.DatabaseConnection;

public class GradeComponentDAO {

    public List<GradeComponent> getAllGradeComponents() {
        List<GradeComponent> components = new ArrayList<>();
        String sql = "SELECT gc.*, c.course_name, c.course_code, s.semester_name " +
                     "FROM grade_components gc " +
                     "JOIN courses c ON gc.course_id = c.course_id " +
                     "JOIN semesters s ON gc.semester_id = s.semester_id " +
                     "ORDER BY s.start_date DESC, c.course_code";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                components.add(extractGradeComponentFromResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return components;
    }

    public GradeComponent getGradeComponent(int courseId, int semesterId) {
        String sql = "SELECT * FROM grade_components WHERE course_id = ? AND semester_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, courseId);
            stmt.setInt(2, semesterId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return extractGradeComponentFromResultSet(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public GradeComponent getGradeComponentById(int componentId) {
        String sql = "SELECT * FROM grade_components WHERE component_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, componentId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return extractGradeComponentFromResultSet(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<GradeComponent> getGradeComponentsBySemester(int semesterId) {
        List<GradeComponent> components = new ArrayList<>();
        String sql = "SELECT gc.*, c.course_name, c.course_code " +
                     "FROM grade_components gc " +
                     "JOIN courses c ON gc.course_id = c.course_id " +
                     "WHERE gc.semester_id = ? " +
                     "ORDER BY c.course_code";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, semesterId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                components.add(extractGradeComponentFromResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return components;
    }

    public boolean createGradeComponent(GradeComponent component) {
        String sql = "INSERT INTO grade_components (course_id, semester_id, " +
                     "inclass_percentage, midterm_percentage, final_percentage) " +
                     "VALUES (?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, component.getCourseId());
            stmt.setInt(2, component.getSemesterId());
            stmt.setBigDecimal(3, component.getInclassPercentage());
            stmt.setBigDecimal(4, component.getMidtermPercentage());
            stmt.setBigDecimal(5, component.getFinalPercentage());
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean updateGradeComponent(GradeComponent component) {
        String sql = "UPDATE grade_components SET inclass_percentage = ?, " +
                     "midterm_percentage = ?, final_percentage = ? " +
                     "WHERE component_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setBigDecimal(1, component.getInclassPercentage());
            stmt.setBigDecimal(2, component.getMidtermPercentage());
            stmt.setBigDecimal(3, component.getFinalPercentage());
            stmt.setInt(4, component.getId());
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean deleteGradeComponent(int componentId) {
        String sql = "DELETE FROM grade_components WHERE component_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, componentId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean gradeComponentExists(int courseId, int semesterId) {
        String sql = "SELECT COUNT(*) FROM grade_components WHERE course_id = ? AND semester_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, courseId);
            stmt.setInt(2, semesterId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private GradeComponent extractGradeComponentFromResultSet(ResultSet rs) throws SQLException {
        GradeComponent component = new GradeComponent();
        component.setId(rs.getInt("component_id"));
        component.setCourseId(rs.getInt("course_id"));
        component.setSemesterId(rs.getInt("semester_id"));
        component.setInclassPercentage(rs.getBigDecimal("inclass_percentage"));
        component.setMidtermPercentage(rs.getBigDecimal("midterm_percentage"));
        component.setFinalPercentage(rs.getBigDecimal("final_percentage"));
        return component;
    }
}
