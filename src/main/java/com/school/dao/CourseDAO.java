package com.school.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import com.school.model.Course;
import com.school.util.DatabaseConnection;

public class CourseDAO {

    public List<Course> getAllCourses() {
        List<Course> courses = new ArrayList<>();
        String sql = "SELECT c.*, u.full_name as teacher_name, " +
                     "(SELECT COUNT(*) FROM enrollments e WHERE e.course_id = c.course_id AND e.status = 'ACTIVE') as enrolled_count " +
                     "FROM courses c " +
                     "LEFT JOIN users u ON c.teacher_id = u.user_id " +
                     "ORDER BY c.course_code";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                courses.add(extractCourseFromResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return courses;
    }

    public Course getCourseById(int courseId) {
        String sql = "SELECT c.*, u.full_name as teacher_name, " +
                     "(SELECT COUNT(*) FROM enrollments e WHERE e.course_id = c.course_id AND e.status = 'ACTIVE') as enrolled_count " +
                     "FROM courses c " +
                     "LEFT JOIN users u ON c.teacher_id = u.user_id " +
                     "WHERE c.course_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, courseId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return extractCourseFromResultSet(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Course> getCoursesByTeacher(int teacherId) {
        List<Course> courses = new ArrayList<>();
        String sql = "SELECT c.*, u.full_name as teacher_name, " +
                     "(SELECT COUNT(*) FROM enrollments e WHERE e.course_id = c.course_id AND e.status = 'ACTIVE') as enrolled_count " +
                     "FROM courses c " +
                     "LEFT JOIN users u ON c.teacher_id = u.user_id " +
                     "WHERE c.teacher_id = ? " +
                     "ORDER BY c.course_code";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, teacherId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                courses.add(extractCourseFromResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return courses;
    }

    public List<Course> getAvailableCourses() {
        List<Course> courses = new ArrayList<>();
        String sql = "SELECT c.*, u.full_name as teacher_name, " +
                     "(SELECT COUNT(*) FROM enrollments e WHERE e.course_id = c.course_id AND e.status = 'ACTIVE') as enrolled_count " +
                     "FROM courses c " +
                     "LEFT JOIN users u ON c.teacher_id = u.user_id " +
                     "HAVING enrolled_count < c.max_students " +
                     "ORDER BY c.course_code";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                courses.add(extractCourseFromResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return courses;
    }

    public boolean addCourse(Course course) {
        String sql = "INSERT INTO courses (course_code, course_name, description, credits, teacher_id, max_students, schedule_days, schedule_time, room_number) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, course.getCourseCode());
            stmt.setString(2, course.getCourseName());
            stmt.setString(3, course.getDescription());
            stmt.setInt(4, course.getCredits());
            
            if (course.getTeacherId() > 0) {
                stmt.setInt(5, course.getTeacherId());
            } else {
                stmt.setNull(5, Types.INTEGER);
            }
            
            stmt.setInt(6, course.getMaxStudents());
            stmt.setString(7, course.getScheduleDays());
            stmt.setString(8, course.getScheduleTime());
            stmt.setString(9, course.getRoomNumber());
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean updateCourse(Course course) {
        String sql = "UPDATE courses SET course_code = ?, course_name = ?, description = ?, " +
                     "credits = ?, teacher_id = ?, max_students = ?, schedule_days = ?, schedule_time = ?, room_number = ? WHERE course_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, course.getCourseCode());
            stmt.setString(2, course.getCourseName());
            stmt.setString(3, course.getDescription());
            stmt.setInt(4, course.getCredits());
            
            if (course.getTeacherId() > 0) {
                stmt.setInt(5, course.getTeacherId());
            } else {
                stmt.setNull(5, Types.INTEGER);
            }
            
            stmt.setInt(6, course.getMaxStudents());
            stmt.setString(7, course.getScheduleDays());
            stmt.setString(8, course.getScheduleTime());
            stmt.setString(9, course.getRoomNumber());
            stmt.setInt(10, course.getCourseId());
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean deleteCourse(int courseId) {
        String sql = "DELETE FROM courses WHERE course_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, courseId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private Course extractCourseFromResultSet(ResultSet rs) throws SQLException {
        Course course = new Course();
        course.setCourseId(rs.getInt("course_id"));
        course.setCourseCode(rs.getString("course_code"));
        course.setCourseName(rs.getString("course_name"));
        course.setDescription(rs.getString("description"));
        course.setCredits(rs.getInt("credits"));
        course.setTeacherId(rs.getInt("teacher_id"));
        course.setTeacherName(rs.getString("teacher_name"));
        course.setMaxStudents(rs.getInt("max_students"));
        course.setEnrolledCount(rs.getInt("enrolled_count"));
        course.setScheduleDays(rs.getString("schedule_days"));
        course.setScheduleTime(rs.getString("schedule_time"));
        course.setRoomNumber(rs.getString("room_number"));
        course.setCreatedAt(rs.getTimestamp("created_at"));
        course.setUpdatedAt(rs.getTimestamp("updated_at"));
        return course;
    }
}
