package com.school.dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
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

    public List<Semester> getSemestersWithPagination(int page, int pageSize) {
        List<Semester> semesters = new ArrayList<>();
        int offset = (page - 1) * pageSize;
        // Sort by: ACTIVE status first (DESC), then by start_date DESC
        String sql = "SELECT * FROM semesters ORDER BY is_active DESC, start_date DESC LIMIT ? OFFSET ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, pageSize);
            stmt.setInt(2, offset);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                semesters.add(extractSemesterFromResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return semesters;
    }

    public int getTotalSemesterCount() {
        String sql = "SELECT COUNT(*) FROM semesters";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
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
        // First auto-create any missing semesters for current and next year
        autoCreateSemesters();
        
        // Then check and auto-activate semester based on current date
        autoActivateSemester();
        
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

    /**
     * Checks if a semester exists for the given academic year and type.
     */
    private boolean semesterExists(String academicYear, Semester.SemesterType semesterType) {
        String sql = "SELECT COUNT(*) FROM semesters WHERE academic_year = ? AND semester_type = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, academicYear);
            stmt.setString(2, semesterType.name());
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Auto-creates semesters for current and next academic year if they don't exist.
     * Fall semester (SEMESTER_1): September 5 - December 20 (16 weeks)
     * Spring semester (SEMESTER_2): January 15 - May 15 (16 weeks)
     * Summer semester (SEMESTER_3): June 1 - July 31 (8 weeks)
     */
    public void autoCreateSemesters() {
        LocalDate today = LocalDate.now();
        int currentYear = today.getYear();
        
        // Determine current and next academic year
        // Academic year starts in September (e.g., 2025-2026 starts Sept 2025)
        int academicStartYear = today.getMonthValue() >= 9 ? currentYear : currentYear - 1;
        int academicEndYear = academicStartYear + 1;
        
        String currentAcademicYear = academicStartYear + "-" + academicEndYear;
        String nextAcademicYear = academicEndYear + "-" + (academicEndYear + 1);
        
        // Create semesters for current academic year
        createSemesterIfNotExists(currentAcademicYear, academicStartYear, academicEndYear);
        
        // Create semesters for next academic year
        createSemesterIfNotExists(nextAcademicYear, academicEndYear, academicEndYear + 1);
    }

    /**
     * Creates semesters for a given academic year if they don't exist.
     */
    private void createSemesterIfNotExists(String academicYear, int startYear, int endYear) {
        // Fall Semester (SEMESTER_1): September 5 - December 20
        if (!semesterExists(academicYear, Semester.SemesterType.SEMESTER_1)) {
            Semester fall = new Semester();
            fall.setSemesterName("Fall " + startYear);
            fall.setSemesterType(Semester.SemesterType.SEMESTER_1);
            fall.setAcademicYear(academicYear);
            fall.setStartDate(LocalDate.of(startYear, 9, 5));
            fall.setEndDate(LocalDate.of(startYear, 12, 20));
            fall.setWeeks(16);
            fall.setActive(false);
            createSemester(fall);
        }
        
        // Spring Semester (SEMESTER_2): January 15 - May 15
        if (!semesterExists(academicYear, Semester.SemesterType.SEMESTER_2)) {
            Semester spring = new Semester();
            spring.setSemesterName("Spring " + endYear);
            spring.setSemesterType(Semester.SemesterType.SEMESTER_2);
            spring.setAcademicYear(academicYear);
            spring.setStartDate(LocalDate.of(endYear, 1, 15));
            spring.setEndDate(LocalDate.of(endYear, 5, 15));
            spring.setWeeks(16);
            spring.setActive(false);
            createSemester(spring);
        }
        
        // Summer Semester (SEMESTER_3): June 1 - July 31
        if (!semesterExists(academicYear, Semester.SemesterType.SEMESTER_3)) {
            Semester summer = new Semester();
            summer.setSemesterName("Summer " + endYear);
            summer.setSemesterType(Semester.SemesterType.SEMESTER_3);
            summer.setAcademicYear(academicYear);
            summer.setStartDate(LocalDate.of(endYear, 6, 1));
            summer.setEndDate(LocalDate.of(endYear, 7, 31));
            summer.setWeeks(8);
            summer.setActive(false);
            createSemester(summer);
        }
    }

    /**
     * Auto-activates semester based on current date.
     * Checks if current date falls within any semester's date range and activates it.
     */
    public void autoActivateSemester() {
        LocalDate today = LocalDate.now();
        String sql = "SELECT semester_id FROM semesters WHERE ? BETWEEN start_date AND end_date LIMIT 1";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setDate(1, Date.valueOf(today));
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                int semesterIdToActivate = rs.getInt("semester_id");
                
                // Check if this semester is already active
                String checkSql = "SELECT semester_id FROM semesters WHERE is_active = TRUE LIMIT 1";
                try (PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {
                    ResultSet checkRs = checkStmt.executeQuery();
                    
                    // Only activate if different semester or no active semester
                    if (!checkRs.next() || checkRs.getInt("semester_id") != semesterIdToActivate) {
                        setActiveSemester(semesterIdToActivate);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
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
