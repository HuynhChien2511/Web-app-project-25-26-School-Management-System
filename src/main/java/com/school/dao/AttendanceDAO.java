package com.school.dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.school.model.Attendance;
import com.school.model.AttendanceSummary;
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

    public List<AttendanceSummary> getAttendanceSummaryByCourse(int courseId) {
        List<AttendanceSummary> list = new ArrayList<>();
        String sql = "SELECT e.enrollment_id, e.student_id, u.full_name AS student_name, " +
                "SUM(CASE WHEN a.status = 'PRESENT' THEN 1 ELSE 0 END) AS present_count, " +
                "SUM(CASE WHEN a.status = 'ABSENT' THEN 1 ELSE 0 END) AS absent_count, " +
                "SUM(CASE WHEN a.status = 'LATE' THEN 1 ELSE 0 END) AS late_count, " +
                "SUM(CASE WHEN a.status = 'EXCUSED' THEN 1 ELSE 0 END) AS excused_count, " +
                "COUNT(a.attendance_id) AS total_count " +
                "FROM enrollments e " +
                "JOIN users u ON e.student_id = u.user_id " +
                "LEFT JOIN attendance a ON a.enrollment_id = e.enrollment_id " +
                "WHERE e.course_id = ? " +
                "GROUP BY e.enrollment_id, e.student_id, u.full_name " +
                "ORDER BY u.full_name";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, courseId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                AttendanceSummary s = new AttendanceSummary();
                s.setEnrollmentId(rs.getInt("enrollment_id"));
                s.setStudentId(rs.getInt("student_id"));
                s.setStudentName(rs.getString("student_name"));
                int present = rs.getInt("present_count");
                int absent = rs.getInt("absent_count");
                int late = rs.getInt("late_count");
                int excused = rs.getInt("excused_count");
                int total = rs.getInt("total_count");
                s.setPresentCount(present);
                s.setAbsentCount(absent);
                s.setLateCount(late);
                s.setExcusedCount(excused);
                int attended = present + late + excused;
                s.setAttendanceRate(total == 0 ? 100.0 : (attended * 100.0) / total);
                list.add(s);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
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
