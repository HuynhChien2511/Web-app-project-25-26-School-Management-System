package com.school.model;

import java.time.LocalDate;

public class Semester {
    private int id;
    private String semesterName;
    private SemesterType semesterType;
    private String academicYear;
    private LocalDate startDate;
    private LocalDate endDate;
    private int weeks;
    private boolean isActive;
    
    public enum SemesterType {
        SEMESTER_1,  // 16 weeks
        SEMESTER_2,  // 16 weeks
        SEMESTER_3   // 8 weeks (summer)
    }
    
    // Constructors
    public Semester() {
    }
    
    public Semester(int id, String semesterName, SemesterType semesterType, 
                   String academicYear, LocalDate startDate, LocalDate endDate, 
                   int weeks, boolean isActive) {
        this.id = id;
        this.semesterName = semesterName;
        this.semesterType = semesterType;
        this.academicYear = academicYear;
        this.startDate = startDate;
        this.endDate = endDate;
        this.weeks = weeks;
        this.isActive = isActive;
    }
    
    // Getters and Setters
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public String getSemesterName() {
        return semesterName;
    }
    
    public void setSemesterName(String semesterName) {
        this.semesterName = semesterName;
    }
    
    public SemesterType getSemesterType() {
        return semesterType;
    }
    
    public void setSemesterType(SemesterType semesterType) {
        this.semesterType = semesterType;
    }
    
    public String getAcademicYear() {
        return academicYear;
    }
    
    public void setAcademicYear(String academicYear) {
        this.academicYear = academicYear;
    }
    
    public LocalDate getStartDate() {
        return startDate;
    }
    
    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }
    
    public LocalDate getEndDate() {
        return endDate;
    }
    
    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }
    
    public int getWeeks() {
        return weeks;
    }
    
    public void setWeeks(int weeks) {
        this.weeks = weeks;
    }
    
    public boolean isActive() {
        return isActive;
    }
    
    public void setActive(boolean active) {
        isActive = active;
    }
    
    // Helper methods
    public boolean isSummerSemester() {
        return semesterType == SemesterType.SEMESTER_3;
    }
    
    public int getExpectedWeeks() {
        return semesterType == SemesterType.SEMESTER_3 ? 8 : 16;
    }
    
    @Override
    public String toString() {
        return "Semester{" +
                "id=" + id +
                ", semesterName='" + semesterName + '\'' +
                ", semesterType=" + semesterType +
                ", academicYear='" + academicYear + '\'' +
                ", weeks=" + weeks +
                ", isActive=" + isActive +
                '}';
    }
}
