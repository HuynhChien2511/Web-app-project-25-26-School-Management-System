package com.school.model;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

public class GpaRecord {
    private int id;
    private int studentId;
    private int semesterId;
    private String semesterName;
    private String academicYear;
    private BigDecimal semesterGpa;
    private int semesterCredits;
    private BigDecimal cumulativeGpa;
    private int totalCredits;
    private LocalDateTime calculatedAt;
    // Optional analytics for admin views
    private Integer creditsAttempted;
    private Integer creditsEarned;
    
    // Constructors
    public GpaRecord() {
    }
    
    public GpaRecord(int id, int studentId, int semesterId,
                    BigDecimal semesterGpa, int semesterCredits,
                    BigDecimal cumulativeGpa, int totalCredits) {
        this.id = id;
        this.studentId = studentId;
        this.semesterId = semesterId;
        this.semesterGpa = semesterGpa;
        this.semesterCredits = semesterCredits;
        this.cumulativeGpa = cumulativeGpa;
        this.totalCredits = totalCredits;
    }
    
    // Getters and Setters
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public int getStudentId() {
        return studentId;
    }
    
    public void setStudentId(int studentId) {
        this.studentId = studentId;
    }
    
    public int getSemesterId() {
        return semesterId;
    }
    
    public void setSemesterId(int semesterId) {
        this.semesterId = semesterId;
    }
    
    public String getSemesterName() {
        return semesterName;
    }
    
    public void setSemesterName(String semesterName) {
        this.semesterName = semesterName;
    }
    
    public String getAcademicYear() {
        return academicYear;
    }
    
    public void setAcademicYear(String academicYear) {
        this.academicYear = academicYear;
    }
    
    public BigDecimal getSemesterGpa() {
        return semesterGpa;
    }
    
    public void setSemesterGpa(BigDecimal semesterGpa) {
        this.semesterGpa = semesterGpa;
    }
    
    public int getSemesterCredits() {
        return semesterCredits;
    }
    
    public void setSemesterCredits(int semesterCredits) {
        this.semesterCredits = semesterCredits;
    }
    
    public BigDecimal getCumulativeGpa() {
        return cumulativeGpa;
    }
    
    public void setCumulativeGpa(BigDecimal cumulativeGpa) {
        this.cumulativeGpa = cumulativeGpa;
    }
    
    public int getTotalCredits() {
        return totalCredits;
    }
    
    public void setTotalCredits(int totalCredits) {
        this.totalCredits = totalCredits;
    }
    
    public LocalDateTime getCalculatedAt() {
        return calculatedAt;
    }
    
    public void setCalculatedAt(LocalDateTime calculatedAt) {
        this.calculatedAt = calculatedAt;
    }

    public Date getCalculatedAtDate() {
        return calculatedAt == null ? null : Date.from(calculatedAt.atZone(ZoneId.systemDefault()).toInstant());
    }
    
    public Integer getCreditsAttempted() {
        return creditsAttempted;
    }
    
    public void setCreditsAttempted(Integer creditsAttempted) {
        this.creditsAttempted = creditsAttempted;
    }
    
    public Integer getCreditsEarned() {
        return creditsEarned;
    }
    
    public void setCreditsEarned(Integer creditsEarned) {
        this.creditsEarned = creditsEarned;
    }
    
    // Helper methods
    public static BigDecimal calculateGPA(BigDecimal totalGradePoints, int totalCredits) {
        if (totalCredits == 0) {
            return BigDecimal.ZERO;
        }
        
        return totalGradePoints.divide(new BigDecimal(totalCredits), 2, RoundingMode.HALF_UP);
    }
    
    public String getAcademicStanding() {
        if (cumulativeGpa == null) {
            return "N/A";
        }
        
        double gpa = cumulativeGpa.doubleValue();
        
        if (gpa >= 3.70) return "Dean's List";
        if (gpa >= 3.50) return "Honors";
        if (gpa >= 3.00) return "Good Standing";
        if (gpa >= 2.00) return "Satisfactory";
        if (gpa >= 1.50) return "Academic Warning";
        return "Academic Probation";
    }
    
    public boolean isGoodStanding() {
        return cumulativeGpa != null && cumulativeGpa.compareTo(new BigDecimal("2.00")) >= 0;
    }
    
    @Override
    public String toString() {
        return "GpaRecord{" +
                "id=" + id +
                ", studentId=" + studentId +
                ", semesterId=" + semesterId +
                ", semesterGpa=" + semesterGpa +
                ", cumulativeGpa=" + cumulativeGpa +
                ", totalCredits=" + totalCredits +
                '}';
    }
}
