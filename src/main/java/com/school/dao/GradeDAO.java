package com.school.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.school.model.Grade;
import com.school.model.GradeComponent;
import com.school.util.DatabaseConnection;

public class GradeDAO {

    public List<Grade> getGradesByStudent(int studentId) {
        List<Grade> grades = new ArrayList<>();
        String sql = "SELECT g.* FROM grades g " +
                     "JOIN enrollments e ON g.enrollment_id = e.enrollment_id " +
                     "WHERE e.student_id = ? " +
                     "ORDER BY g.updated_at DESC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, studentId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                grades.add(extractGradeFromResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return grades;
    }

    public Grade getGradeByEnrollment(int enrollmentId) {
        String sql = "SELECT * FROM grades WHERE enrollment_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, enrollmentId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return extractGradeFromResultSet(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean createGrade(Grade grade) {
        String sql = "INSERT INTO grades (enrollment_id, inclass_score, midterm_score, " +
                     "final_score, total_score, letter_grade, grade_point) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, grade.getEnrollmentId());
            stmt.setBigDecimal(2, grade.getInclassScore());
            stmt.setBigDecimal(3, grade.getMidtermScore());
            stmt.setBigDecimal(4, grade.getFinalScore());
            stmt.setBigDecimal(5, grade.getTotalScore());
            stmt.setString(6, grade.getLetterGrade());
            stmt.setBigDecimal(7, grade.getGradePoint());
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean updateGrade(Grade grade) {
        String sql = "UPDATE grades SET inclass_score = ?, midterm_score = ?, " +
                     "final_score = ?, total_score = ?, letter_grade = ?, " +
                     "grade_point = ? WHERE grade_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setBigDecimal(1, grade.getInclassScore());
            stmt.setBigDecimal(2, grade.getMidtermScore());
            stmt.setBigDecimal(3, grade.getFinalScore());
            stmt.setBigDecimal(4, grade.getTotalScore());
            stmt.setString(5, grade.getLetterGrade());
            stmt.setBigDecimal(6, grade.getGradePoint());
            stmt.setInt(7, grade.getId());
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean saveOrUpdateGrade(int enrollmentId, Grade grade, GradeComponent component) {
        // Calculate final grade with component percentages
        grade.calculateFinalGrade(component);
        
        Grade existingGrade = getGradeByEnrollment(enrollmentId);
        if (existingGrade != null) {
            grade.setId(existingGrade.getId());
            return updateGrade(grade);
        } else {
            return createGrade(grade);
        }
    }

    public boolean updateInclassScore(int enrollmentId, java.math.BigDecimal score) {
        String sql = "UPDATE grades SET inclass_score = ? WHERE enrollment_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setBigDecimal(1, score == null ? null : score.setScale(0, java.math.RoundingMode.HALF_UP));
            stmt.setInt(2, enrollmentId);
            
            int updated = stmt.executeUpdate();
            if (updated == 0) {
                // Create new grade record if it doesn't exist
                String insertSql = "INSERT INTO grades (enrollment_id, inclass_score) VALUES (?, ?)";
                try (PreparedStatement insertStmt = conn.prepareStatement(insertSql)) {
                    insertStmt.setInt(1, enrollmentId);
                    insertStmt.setBigDecimal(2, score == null ? null : score.setScale(0, java.math.RoundingMode.HALF_UP));
                    return insertStmt.executeUpdate() > 0;
                }
            }
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean updateMidtermScore(int enrollmentId, java.math.BigDecimal score) {
        String sql = "UPDATE grades SET midterm_score = ? WHERE enrollment_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setBigDecimal(1, score == null ? null : score.setScale(0, java.math.RoundingMode.HALF_UP));
            stmt.setInt(2, enrollmentId);
            
            int updated = stmt.executeUpdate();
            if (updated == 0) {
                String insertSql = "INSERT INTO grades (enrollment_id, midterm_score) VALUES (?, ?)";
                try (PreparedStatement insertStmt = conn.prepareStatement(insertSql)) {
                    insertStmt.setInt(1, enrollmentId);
                    insertStmt.setBigDecimal(2, score == null ? null : score.setScale(0, java.math.RoundingMode.HALF_UP));
                    return insertStmt.executeUpdate() > 0;
                }
            }
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean updateFinalScore(int enrollmentId, java.math.BigDecimal score) {
        String sql = "UPDATE grades SET final_score = ? WHERE enrollment_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setBigDecimal(1, score == null ? null : score.setScale(0, java.math.RoundingMode.HALF_UP));
            stmt.setInt(2, enrollmentId);
            
            int updated = stmt.executeUpdate();
            if (updated == 0) {
                String insertSql = "INSERT INTO grades (enrollment_id, final_score) VALUES (?, ?)";
                try (PreparedStatement insertStmt = conn.prepareStatement(insertSql)) {
                    insertStmt.setInt(1, enrollmentId);
                    insertStmt.setBigDecimal(2, score == null ? null : score.setScale(0, java.math.RoundingMode.HALF_UP));
                    return insertStmt.executeUpdate() > 0;
                }
            }
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean recalculateGrade(int enrollmentId, GradeComponent component) {
        Grade grade = getGradeByEnrollment(enrollmentId);
        if (grade == null || component == null) {
            return false;
        }

        // Recalculate whenever all three component scores are present
        if (grade.hasAllComponentScores()) {
            grade.calculateFinalGrade(component);
            return updateGrade(grade);
        }
        return false;
    }

    public boolean deleteGrade(int gradeId) {
        String sql = "DELETE FROM grades WHERE grade_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, gradeId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private Grade extractGradeFromResultSet(ResultSet rs) throws SQLException {
        Grade grade = new Grade();
        grade.setId(rs.getInt("grade_id"));
        grade.setEnrollmentId(rs.getInt("enrollment_id"));
        grade.setInclassScore(rs.getBigDecimal("inclass_score"));
        grade.setMidtermScore(rs.getBigDecimal("midterm_score"));
        grade.setFinalScore(rs.getBigDecimal("final_score"));
        grade.setTotalScore(rs.getBigDecimal("total_score"));
        grade.setLetterGrade(rs.getString("letter_grade"));
        grade.setGradePoint(rs.getBigDecimal("grade_point"));
        grade.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        grade.setUpdatedAt(rs.getTimestamp("updated_at").toLocalDateTime());
        return grade;
    }
}
