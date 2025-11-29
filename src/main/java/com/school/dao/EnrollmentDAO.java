package com.school.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.school.model.Course;
import com.school.model.Enrollment;
import com.school.util.DatabaseConnection;

public class EnrollmentDAO {

    public List<Enrollment> getEnrollmentsByStudent(int studentId) {
        List<Enrollment> enrollments = new ArrayList<>();
        String sql = "SELECT e.*, c.course_name, c.course_code, c.credits, c.max_students, " +
                     "c.schedule_days, c.schedule_time, c.room_number, " +
                     "u.full_name as teacher_name, s.semester_name " +
                     "FROM enrollments e " +
                     "JOIN courses c ON e.course_id = c.course_id " +
                     "LEFT JOIN users u ON c.teacher_id = u.user_id " +
                     "LEFT JOIN semesters s ON e.semester_id = s.semester_id " +
                     "WHERE e.student_id = ? " +
                     "ORDER BY e.enrollment_date DESC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, studentId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                enrollments.add(extractEnrollmentFromResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return enrollments;
    }

    public List<Enrollment> getEnrollmentsByCourse(int courseId) {
        List<Enrollment> enrollments = new ArrayList<>();
        String sql = "SELECT e.*, u.full_name as student_name, u.email, c.course_name, c.course_code, s.semester_name " +
                     "FROM enrollments e " +
                     "JOIN users u ON e.student_id = u.user_id " +
                     "JOIN courses c ON e.course_id = c.course_id " +
                     "LEFT JOIN semesters s ON e.semester_id = s.semester_id " +
                     "WHERE e.course_id = ? " +
                     "ORDER BY u.full_name";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, courseId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                enrollments.add(extractEnrollmentFromResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return enrollments;
    }

    public Enrollment getEnrollment(int studentId, int courseId) {
        String sql = "SELECT e.*, c.course_name, c.course_code, u.full_name as student_name, s.semester_name " +
                     "FROM enrollments e " +
                     "JOIN courses c ON e.course_id = c.course_id " +
                     "JOIN users u ON e.student_id = u.user_id " +
                     "LEFT JOIN semesters s ON e.semester_id = s.semester_id " +
                     "WHERE e.student_id = ? AND e.course_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, studentId);
            stmt.setInt(2, courseId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return extractEnrollmentFromResultSet(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean enrollStudent(int studentId, int courseId) {
        String sql = "INSERT INTO enrollments (student_id, course_id, semester_id, status) " +
                     "SELECT ?, ?, semester_id, 'ACTIVE' FROM semesters WHERE is_active = TRUE LIMIT 1";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, studentId);
            stmt.setInt(2, courseId);
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean updateEnrollmentGrade(int enrollmentId, String grade) {
        String sql = "UPDATE enrollments SET grade = ? WHERE enrollment_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, grade);
            stmt.setInt(2, enrollmentId);
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean updateEnrollmentStatus(int enrollmentId, Enrollment.EnrollmentStatus status) {
        String sql = "UPDATE enrollments SET status = ? WHERE enrollment_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, status.name());
            stmt.setInt(2, enrollmentId);
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean dropEnrollment(int enrollmentId) {
        String sql = "UPDATE enrollments SET status = 'DROPPED' WHERE enrollment_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, enrollmentId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean deleteEnrollment(int enrollmentId) {
        String sql = "DELETE FROM enrollments WHERE enrollment_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, enrollmentId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean isStudentEnrolled(int studentId, int courseId) {
        String sql = "SELECT COUNT(*) FROM enrollments WHERE student_id = ? AND course_id = ? AND status = 'ACTIVE'";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, studentId);
            stmt.setInt(2, courseId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private Enrollment extractEnrollmentFromResultSet(ResultSet rs) throws SQLException {
        Enrollment enrollment = new Enrollment();
        enrollment.setEnrollmentId(rs.getInt("enrollment_id"));
        enrollment.setStudentId(rs.getInt("student_id"));
        enrollment.setCourseId(rs.getInt("course_id"));
        
        // Set semester_id if available
        try {
            enrollment.setSemesterId(rs.getInt("semester_id"));
        } catch (SQLException e) {
            // semester_id not present in this query
        }
        
        enrollment.setEnrollmentDate(rs.getTimestamp("enrollment_date"));
        enrollment.setGrade(rs.getString("grade"));
        enrollment.setStatus(Enrollment.EnrollmentStatus.valueOf(rs.getString("status")));
        
        // Optional fields that might not always be present
        try {
            enrollment.setStudentName(rs.getString("student_name"));
        } catch (SQLException e) {
            // Field not present in this query
        }
        
        try {
            enrollment.setCourseName(rs.getString("course_name"));
            enrollment.setCourseCode(rs.getString("course_code"));
        } catch (SQLException e) {
            // Fields not present in this query
        }
        
        // Create Course object if course details are available
        try {
            String courseName = rs.getString("course_name");
            if (courseName != null) {
                Course course = new Course();
                course.setCourseId(rs.getInt("course_id"));
                course.setCourseCode(rs.getString("course_code"));
                course.setCourseName(courseName);
                
                try {
                    course.setCredits(rs.getInt("credits"));
                    course.setMaxStudents(rs.getInt("max_students"));
                    course.setScheduleDays(rs.getString("schedule_days"));
                    course.setScheduleTime(rs.getString("schedule_time"));
                    course.setRoomNumber(rs.getString("room_number"));
                } catch (SQLException e) {
                    // Some fields not present
                }
                
                try {
                    course.setTeacherName(rs.getString("teacher_name"));
                } catch (SQLException e) {
                    // Teacher name not present
                }
                
                enrollment.setCourse(course);
            }
        } catch (SQLException e) {
            // Course fields not present in this query
        }
        
        return enrollment;
    }
}
