package com.school.dao;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.school.model.GpaRecord;
import com.school.util.DatabaseConnection;

public class GpaRecordDAO {

    public List<GpaRecord> getGpaRecordsByStudent(int studentId) {
        List<GpaRecord> records = new ArrayList<>();
        String sql = "SELECT gpa.*, s.semester_name, s.academic_year " +
                     "FROM gpa_records gpa " +
                     "JOIN semesters s ON gpa.semester_id = s.semester_id " +
                     "WHERE gpa.student_id = ? " +
                     "ORDER BY s.start_date DESC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, studentId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                records.add(extractGpaRecordFromResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return records;
    }

    public GpaRecord getGpaRecord(int studentId, int semesterId) {
        String sql = "SELECT * FROM gpa_records WHERE student_id = ? AND semester_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, studentId);
            stmt.setInt(2, semesterId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return extractGpaRecordFromResultSet(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public GpaRecord getLatestGpaRecord(int studentId) {
        String sql = "SELECT gpa.* FROM gpa_records gpa " +
                     "JOIN semesters s ON gpa.semester_id = s.semester_id " +
                     "WHERE gpa.student_id = ? " +
                     "ORDER BY s.start_date DESC LIMIT 1";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, studentId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return extractGpaRecordFromResultSet(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean createGpaRecord(GpaRecord record) {
        String sql = "INSERT INTO gpa_records (student_id, semester_id, semester_gpa, " +
                     "semester_credits, cumulative_gpa, total_credits) " +
                     "VALUES (?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, record.getStudentId());
            stmt.setInt(2, record.getSemesterId());
            stmt.setBigDecimal(3, record.getSemesterGpa());
            stmt.setInt(4, record.getSemesterCredits());
            stmt.setBigDecimal(5, record.getCumulativeGpa());
            stmt.setInt(6, record.getTotalCredits());
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean updateGpaRecord(GpaRecord record) {
        String sql = "UPDATE gpa_records SET semester_gpa = ?, semester_credits = ?, " +
                     "cumulative_gpa = ?, total_credits = ? " +
                     "WHERE gpa_record_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setBigDecimal(1, record.getSemesterGpa());
            stmt.setInt(2, record.getSemesterCredits());
            stmt.setBigDecimal(3, record.getCumulativeGpa());
            stmt.setInt(4, record.getTotalCredits());
            stmt.setInt(5, record.getId());
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean calculateAndSaveGpa(int studentId, int semesterId) {
        // Calculate semester GPA
        String semesterSql = "SELECT SUM(g.grade_point * c.credits) as total_points, " +
                            "SUM(c.credits) as total_credits " +
                            "FROM grades g " +
                            "JOIN enrollments e ON g.enrollment_id = e.enrollment_id " +
                            "JOIN courses c ON e.course_id = c.course_id " +
                            "WHERE e.student_id = ? AND e.semester_id = ? " +
                            "AND g.grade_point IS NOT NULL";
        
        // Calculate cumulative GPA
        String cumulativeSql = "SELECT SUM(g.grade_point * c.credits) as total_points, " +
                              "SUM(c.credits) as total_credits " +
                              "FROM grades g " +
                              "JOIN enrollments e ON g.enrollment_id = e.enrollment_id " +
                              "JOIN courses c ON e.course_id = c.course_id " +
                              "WHERE e.student_id = ? AND g.grade_point IS NOT NULL";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement semStmt = conn.prepareStatement(semesterSql);
             PreparedStatement cumStmt = conn.prepareStatement(cumulativeSql)) {
            
            // Get semester GPA
            semStmt.setInt(1, studentId);
            semStmt.setInt(2, semesterId);
            ResultSet semRs = semStmt.executeQuery();
            
            BigDecimal semesterGpa = BigDecimal.ZERO;
            int semesterCredits = 0;
            
            if (semRs.next()) {
                BigDecimal totalPoints = semRs.getBigDecimal("total_points");
                semesterCredits = semRs.getInt("total_credits");
                
                if (totalPoints != null && semesterCredits > 0) {
                    semesterGpa = GpaRecord.calculateGPA(totalPoints, semesterCredits);
                }
            }
            
            // Get cumulative GPA
            cumStmt.setInt(1, studentId);
            ResultSet cumRs = cumStmt.executeQuery();
            
            BigDecimal cumulativeGpa = BigDecimal.ZERO;
            int totalCredits = 0;
            
            if (cumRs.next()) {
                BigDecimal totalPoints = cumRs.getBigDecimal("total_points");
                totalCredits = cumRs.getInt("total_credits");
                
                if (totalPoints != null && totalCredits > 0) {
                    cumulativeGpa = GpaRecord.calculateGPA(totalPoints, totalCredits);
                }
            }
            
            // Save or update GPA record
            GpaRecord record = getGpaRecord(studentId, semesterId);
            if (record == null) {
                record = new GpaRecord();
                record.setStudentId(studentId);
                record.setSemesterId(semesterId);
                record.setSemesterGpa(semesterGpa);
                record.setSemesterCredits(semesterCredits);
                record.setCumulativeGpa(cumulativeGpa);
                record.setTotalCredits(totalCredits);
                return createGpaRecord(record);
            } else {
                record.setSemesterGpa(semesterGpa);
                record.setSemesterCredits(semesterCredits);
                record.setCumulativeGpa(cumulativeGpa);
                record.setTotalCredits(totalCredits);
                return updateGpaRecord(record);
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean deleteGpaRecord(int recordId) {
        String sql = "DELETE FROM gpa_records WHERE gpa_record_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, recordId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private GpaRecord extractGpaRecordFromResultSet(ResultSet rs) throws SQLException {
        GpaRecord record = new GpaRecord();
        record.setId(rs.getInt("gpa_record_id"));
        record.setStudentId(rs.getInt("student_id"));
        record.setSemesterId(rs.getInt("semester_id"));
        record.setSemesterGpa(rs.getBigDecimal("semester_gpa"));
        record.setSemesterCredits(rs.getInt("semester_credits"));
        record.setCumulativeGpa(rs.getBigDecimal("cumulative_gpa"));
        record.setTotalCredits(rs.getInt("total_credits"));
        record.setCalculatedAt(rs.getTimestamp("calculated_at").toLocalDateTime());
        return record;
    }
}
