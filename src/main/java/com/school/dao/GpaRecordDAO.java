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
        // Build per-semester rows joined with cumulative snapshot, plus attempted/earned credits
        String sql =
            "SELECT " +
            "  sem.gpa_id AS sem_gpa_id, sem.student_id, sem.semester_id, sem.gpa AS semester_gpa, sem.total_credits AS semester_credits, sem.updated_at AS sem_updated_at, " +
            "  s.semester_name, s.academic_year, " +
            "  cum.gpa AS cumulative_gpa, cum.total_credits AS total_credits, cum.updated_at AS cum_updated_at, " +
            "  (SELECT COALESCE(SUM(c.credits),0) FROM enrollments e JOIN courses c ON c.course_id=e.course_id WHERE e.student_id = sem.student_id AND e.semester_id = sem.semester_id) AS credits_attempted, " +
            "  (SELECT COALESCE(SUM(c.credits),0) FROM enrollments e JOIN courses c ON c.course_id=e.course_id JOIN grades g ON g.enrollment_id=e.enrollment_id WHERE e.student_id = sem.student_id AND e.semester_id = sem.semester_id AND g.grade_point > 0) AS credits_earned " +
            "FROM gpa_records sem " +
            "JOIN semesters s ON s.semester_id = sem.semester_id " +
            "LEFT JOIN gpa_records cum ON cum.student_id = sem.student_id AND cum.is_cumulative = TRUE " +
            "WHERE sem.student_id = ? AND sem.is_cumulative = FALSE " +
            "ORDER BY s.start_date DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, studentId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                records.add(mapCombinedRecord(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return records;
    }

    public GpaRecord getGpaRecord(int studentId, int semesterId) {
        String sql =
            "SELECT " +
            "  sem.gpa_id AS sem_gpa_id, sem.student_id, sem.semester_id, sem.gpa AS semester_gpa, sem.total_credits AS semester_credits, sem.updated_at AS sem_updated_at, " +
            "  s.semester_name, s.academic_year, " +
            "  cum.gpa AS cumulative_gpa, cum.total_credits AS total_credits, cum.updated_at AS cum_updated_at, " +
            "  (SELECT COALESCE(SUM(c.credits),0) FROM enrollments e JOIN courses c ON c.course_id=e.course_id WHERE e.student_id = sem.student_id AND e.semester_id = sem.semester_id) AS credits_attempted, " +
            "  (SELECT COALESCE(SUM(c.credits),0) FROM enrollments e JOIN courses c ON c.course_id=e.course_id JOIN grades g ON g.enrollment_id=e.enrollment_id WHERE e.student_id = sem.student_id AND e.semester_id = sem.semester_id AND g.grade_point > 0) AS credits_earned " +
            "FROM gpa_records sem " +
            "JOIN semesters s ON s.semester_id = sem.semester_id " +
            "LEFT JOIN gpa_records cum ON cum.student_id = sem.student_id AND cum.is_cumulative = TRUE " +
            "WHERE sem.student_id = ? AND sem.semester_id = ? AND sem.is_cumulative = FALSE " +
            "LIMIT 1";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, studentId);
            stmt.setInt(2, semesterId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return mapCombinedRecord(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public GpaRecord getLatestGpaRecord(int studentId) {
        String sql =
            "SELECT " +
            "  sem.gpa_id AS sem_gpa_id, sem.student_id, sem.semester_id, sem.gpa AS semester_gpa, sem.total_credits AS semester_credits, sem.updated_at AS sem_updated_at, " +
            "  s.semester_name, s.academic_year, " +
            "  cum.gpa AS cumulative_gpa, cum.total_credits AS total_credits, cum.updated_at AS cum_updated_at, " +
            "  (SELECT COALESCE(SUM(c.credits),0) FROM enrollments e JOIN courses c ON c.course_id=e.course_id WHERE e.student_id = sem.student_id AND e.semester_id = sem.semester_id) AS credits_attempted, " +
            "  (SELECT COALESCE(SUM(c.credits),0) FROM enrollments e JOIN courses c ON c.course_id=e.course_id JOIN grades g ON g.enrollment_id=e.enrollment_id WHERE e.student_id = sem.student_id AND e.semester_id = sem.semester_id AND g.grade_point > 0) AS credits_earned " +
            "FROM gpa_records sem " +
            "JOIN semesters s ON s.semester_id = sem.semester_id " +
            "LEFT JOIN gpa_records cum ON cum.student_id = sem.student_id AND cum.is_cumulative = TRUE " +
            "WHERE sem.student_id = ? AND sem.is_cumulative = FALSE " +
            "ORDER BY s.start_date DESC LIMIT 1";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, studentId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return mapCombinedRecord(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // Deprecated: schema changed; kept for compatibility if needed
    private boolean createGpaRecord(GpaRecord record) { return false; }

    private boolean updateGpaRecord(GpaRecord record) { return false; }

    public boolean calculateAndSaveGpa(int studentId, int semesterId) {
        // Calculate semester GPA
        String semesterSql = "SELECT SUM(g.grade_point * c.credits) AS total_points, " +
                             "SUM(c.credits) AS total_credits " +
                             "FROM grades g " +
                             "JOIN enrollments e ON g.enrollment_id = e.enrollment_id " +
                             "JOIN courses c ON e.course_id = c.course_id " +
                             "WHERE e.student_id = ? AND e.semester_id = ? " +
                             "AND g.grade_point IS NOT NULL";

        // Calculate cumulative GPA
        String cumulativeSql = "SELECT SUM(g.grade_point * c.credits) AS total_points, " +
                               "SUM(c.credits) AS total_credits " +
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
            BigDecimal semesterPoints = BigDecimal.ZERO;

            if (semRs.next()) {
                BigDecimal totalPoints = semRs.getBigDecimal("total_points");
                semesterCredits = semRs.getInt("total_credits");
                if (totalPoints != null) semesterPoints = totalPoints;
                if (totalPoints != null && semesterCredits > 0) {
                    semesterGpa = GpaRecord.calculateGPA(totalPoints, semesterCredits);
                }
            }

            // Get cumulative GPA
            cumStmt.setInt(1, studentId);
            ResultSet cumRs = cumStmt.executeQuery();

            BigDecimal cumulativeGpa = BigDecimal.ZERO;
            int totalCredits = 0;
            BigDecimal totalPoints = BigDecimal.ZERO;

            if (cumRs.next()) {
                BigDecimal cumPoints = cumRs.getBigDecimal("total_points");
                totalCredits = cumRs.getInt("total_credits");
                if (cumPoints != null) totalPoints = cumPoints;
                if (cumPoints != null && totalCredits > 0) {
                    cumulativeGpa = GpaRecord.calculateGPA(cumPoints, totalCredits);
                }
            }

            // Upsert semester row (is_cumulative = false)
            upsertGpaRecord(conn, studentId, semesterId, false, semesterGpa, semesterCredits, semesterPoints);

            // Upsert cumulative row (is_cumulative = true, semester_id NULL)
            upsertGpaRecord(conn, studentId, null, true, cumulativeGpa, totalCredits, totalPoints);

            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean deleteGpaRecord(int recordId) {
        String sql = "DELETE FROM gpa_records WHERE gpa_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, recordId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private GpaRecord mapCombinedRecord(ResultSet rs) throws SQLException {
        GpaRecord record = new GpaRecord();
        record.setId(safeGetInt(rs, "sem_gpa_id"));
        record.setStudentId(safeGetInt(rs, "student_id"));
        record.setSemesterId(safeGetInt(rs, "semester_id"));
        record.setSemesterName(safeGetString(rs, "semester_name"));
        record.setAcademicYear(safeGetString(rs, "academic_year"));
        record.setSemesterGpa(rs.getBigDecimal("semester_gpa"));
        record.setSemesterCredits(safeGetInt(rs, "semester_credits"));
        record.setCumulativeGpa(rs.getBigDecimal("cumulative_gpa"));
        record.setTotalCredits(safeGetInt(rs, "total_credits"));
        java.sql.Timestamp ts = rs.getTimestamp("sem_updated_at");
        if (ts == null) ts = rs.getTimestamp("cum_updated_at");
        if (ts != null) record.setCalculatedAt(ts.toLocalDateTime());
        // Optional metrics for admin view
        record.setCreditsAttempted(safeGetIntObj(rs, "credits_attempted"));
        record.setCreditsEarned(safeGetIntObj(rs, "credits_earned"));
        return record;
    }

    private void upsertGpaRecord(Connection conn, int studentId, Integer semesterId, boolean isCumulative,
                                 BigDecimal gpa, int totalCredits, BigDecimal totalGradePoints) throws SQLException {
        // Try update first
        String update = "UPDATE gpa_records SET gpa = ?, total_credits = ?, total_grade_points = ?, updated_at = CURRENT_TIMESTAMP " +
                        "WHERE student_id = ? AND " + (isCumulative ? "is_cumulative = TRUE AND semester_id IS NULL" : "semester_id = ? AND is_cumulative = FALSE");
        try (PreparedStatement stmt = conn.prepareStatement(update)) {
            stmt.setBigDecimal(1, gpa);
            stmt.setInt(2, totalCredits);
            stmt.setBigDecimal(3, totalGradePoints);
            stmt.setInt(4, studentId);
            if (!isCumulative) {
                stmt.setInt(5, semesterId);
            }
            int updated = stmt.executeUpdate();
            if (updated > 0) return; // done
        }

        // Insert if not updated
        String insert = "INSERT INTO gpa_records (student_id, semester_id, gpa, total_credits, total_grade_points, is_cumulative) " +
                        "VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(insert)) {
            stmt.setInt(1, studentId);
            if (isCumulative) {
                stmt.setNull(2, java.sql.Types.INTEGER);
            } else {
                stmt.setInt(2, semesterId);
            }
            stmt.setBigDecimal(3, gpa);
            stmt.setInt(4, totalCredits);
            stmt.setBigDecimal(5, totalGradePoints);
            stmt.setBoolean(6, isCumulative);
            stmt.executeUpdate();
        }
    }

    private int safeGetInt(ResultSet rs, String col) throws SQLException {
        int v = rs.getInt(col);
        return rs.wasNull() ? 0 : v;
    }

    private Integer safeGetIntObj(ResultSet rs, String col) throws SQLException {
        int v = rs.getInt(col);
        return rs.wasNull() ? null : v;
    }
    
    private String safeGetString(ResultSet rs, String col) throws SQLException {
        String v = rs.getString(col);
        return v;
    }
}
