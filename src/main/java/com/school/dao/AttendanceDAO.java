package com.school.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.time.LocalDate;

import com.school.model.Attendance;
import com.school.util.DatabaseConnection;

public class AttendanceDAO {

    public List<Attendance> getAttendanceByEnrollment(int enrollmentId) {
        List<Attendance> attendances = new ArrayList<>();
        String sql = "SELECT * FROM attendance WHERE enrollment_id = ? ORDER BY attendance_date DESC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, enrollmentId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                attendances.add(extractAttendanceFromResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return attendances;
    }

    public List<Attendance> getAttendanceByCourseAndDate(int courseId, LocalDate date) {
        List<Attendance> attendances = new ArrayList<>();
        String sql = "SELECT a.* FROM attendance a " +
                     "JOIN enrollments e ON a.enrollment_id = e.enrollment_id " +
                     "WHERE e.course_id = ? AND a.attendance_date = ? " +
                     "ORDER BY e.student_id";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, courseId);
            stmt.setDate(2, Date.valueOf(date));
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                attendances.add(extractAttendanceFromResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return attendances;
    }

    public Attendance getAttendance(int enrollmentId, LocalDate date) {
        String sql = "SELECT * FROM attendance WHERE enrollment_id = ? AND attendance_date = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, enrollmentId);
            stmt.setDate(2, Date.valueOf(date));
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return extractAttendanceFromResultSet(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean recordAttendance(Attendance attendance) {
        String sql = "INSERT INTO attendance (enrollment_id, attendance_date, status, notes) " +
                     "VALUES (?, ?, ?, ?) " +
                     "ON DUPLICATE KEY UPDATE status = ?, notes = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, attendance.getEnrollmentId());
            stmt.setDate(2, Date.valueOf(attendance.getAttendanceDate()));
            stmt.setString(3, attendance.getStatus().name());
            stmt.setString(4, attendance.getNotes());
            stmt.setString(5, attendance.getStatus().name());
            stmt.setString(6, attendance.getNotes());
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean updateAttendance(int attendanceId, Attendance.AttendanceStatus status, String notes) {
        String sql = "UPDATE attendance SET status = ?, notes = ? WHERE attendance_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, status.name());
            stmt.setString(2, notes);
            stmt.setInt(3, attendanceId);
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public int getAbsentCount(int enrollmentId) {
        String sql = "SELECT COUNT(*) FROM attendance WHERE enrollment_id = ? AND status = 'ABSENT'";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, enrollmentId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public int getPresentCount(int enrollmentId) {
        String sql = "SELECT COUNT(*) FROM attendance WHERE enrollment_id = ? AND status = 'PRESENT'";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, enrollmentId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public double getAttendanceRate(int enrollmentId) {
        String sql = "SELECT " +
                     "COUNT(*) as total, " +
                     "SUM(CASE WHEN status IN ('PRESENT', 'LATE', 'EXCUSED') THEN 1 ELSE 0 END) as attended " +
                     "FROM attendance WHERE enrollment_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, enrollmentId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                int total = rs.getInt("total");
                int attended = rs.getInt("attended");
                
                if (total == 0) return 100.0;
                return (attended * 100.0) / total;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0.0;
    }

    public boolean deleteAttendance(int attendanceId) {
        String sql = "DELETE FROM attendance WHERE attendance_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, attendanceId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private Attendance extractAttendanceFromResultSet(ResultSet rs) throws SQLException {
        Attendance attendance = new Attendance();
        attendance.setId(rs.getInt("attendance_id"));
        attendance.setEnrollmentId(rs.getInt("enrollment_id"));
        attendance.setAttendanceDate(rs.getDate("attendance_date").toLocalDate());
        attendance.setStatus(Attendance.AttendanceStatus.valueOf(rs.getString("status")));
        attendance.setNotes(rs.getString("notes"));
        attendance.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        attendance.setUpdatedAt(rs.getTimestamp("updated_at").toLocalDateTime());
        return attendance;
    }
}
