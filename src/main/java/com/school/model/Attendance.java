package com.school.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class Attendance {
    private int id;
    private int enrollmentId;
    private LocalDate attendanceDate;
    private AttendanceStatus status;
    private String notes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    public enum AttendanceStatus {
        PRESENT,
        ABSENT,
        LATE,
        EXCUSED
    }
    
    // Constructors
    public Attendance() {
    }
    
    public Attendance(int id, int enrollmentId, LocalDate attendanceDate,
                     AttendanceStatus status, String notes) {
        this.id = id;
        this.enrollmentId = enrollmentId;
        this.attendanceDate = attendanceDate;
        this.status = status;
        this.notes = notes;
    }
    
    // Getters and Setters
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public int getEnrollmentId() {
        return enrollmentId;
    }
    
    public void setEnrollmentId(int enrollmentId) {
        this.enrollmentId = enrollmentId;
    }
    
    public LocalDate getAttendanceDate() {
        return attendanceDate;
    }
    
    public void setAttendanceDate(LocalDate attendanceDate) {
        this.attendanceDate = attendanceDate;
    }
    
    public AttendanceStatus getStatus() {
        return status;
    }
    
    public void setStatus(AttendanceStatus status) {
        this.status = status;
    }
    
    public String getNotes() {
        return notes;
    }
    
    public void setNotes(String notes) {
        this.notes = notes;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
    
    // Helper methods
    public boolean isAbsent() {
        return status == AttendanceStatus.ABSENT;
    }
    
    public boolean isPresent() {
        return status == AttendanceStatus.PRESENT;
    }
    
    public boolean countsAsPresent() {
        // PRESENT, LATE, and EXCUSED count as attendance
        return status != AttendanceStatus.ABSENT;
    }
    
    @Override
    public String toString() {
        return "Attendance{" +
                "id=" + id +
                ", enrollmentId=" + enrollmentId +
                ", date=" + attendanceDate +
                ", status=" + status +
                '}';
    }
}
