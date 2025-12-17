package com.school.model;

public class AttendanceSummary {
    private int enrollmentId;
    private int studentId;
    private String studentName;
    private int presentCount;
    private int absentCount;
    private int lateCount;
    private int excusedCount;
    private double attendanceRate;

    public AttendanceSummary() {}

    public int getEnrollmentId() { return enrollmentId; }
    public void setEnrollmentId(int enrollmentId) { this.enrollmentId = enrollmentId; }

    public int getStudentId() { return studentId; }
    public void setStudentId(int studentId) { this.studentId = studentId; }

    public String getStudentName() { return studentName; }
    public void setStudentName(String studentName) { this.studentName = studentName; }

    public int getPresentCount() { return presentCount; }
    public void setPresentCount(int presentCount) { this.presentCount = presentCount; }

    public int getAbsentCount() { return absentCount; }
    public void setAbsentCount(int absentCount) { this.absentCount = absentCount; }

    public int getLateCount() { return lateCount; }
    public void setLateCount(int lateCount) { this.lateCount = lateCount; }

    public int getExcusedCount() { return excusedCount; }
    public void setExcusedCount(int excusedCount) { this.excusedCount = excusedCount; }

    public double getAttendanceRate() { return attendanceRate; }
    public void setAttendanceRate(double attendanceRate) { this.attendanceRate = attendanceRate; }
}
